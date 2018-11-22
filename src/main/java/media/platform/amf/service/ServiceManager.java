package media.platform.amf.service;

import media.platform.amf.AppInstance;
import media.platform.amf.common.NetUtil;
import media.platform.amf.config.AmfConfig;
import media.platform.amf.engine.EngineServer;
import media.platform.amf.redundant.RedundantServer;
import media.platform.amf.rmqif.handler.RmqProcLogInReq;
import media.platform.amf.rmqif.module.RmqClient;
import media.platform.amf.rmqif.module.RmqServer;
import media.platform.amf.room.RoomManager;
import media.platform.amf.session.SessionInfo;
import media.platform.amf.session.SessionManager;
import media.platform.amf.rtpcore.Process.NettyUDPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class ServiceManager {

    private static final Logger logger = LoggerFactory.getLogger(ServiceManager.class);

    private static final boolean USE_PING = false;

    private static ServiceManager serviceManager = null;

    public static ServiceManager getInstance() {
        if (serviceManager == null) {
            serviceManager = new ServiceManager();
        }

        return serviceManager;
    }

    private RmqServer rmqServer;
    private SessionManager sessionManager;
    private HeartbeatManager heartbeatManager;
    private RedundantServer redundantServer;
    private EngineServer engineServer;

    private boolean isQuit = false;


    /**
     * Reads a config file in the constructor
     */
    public ServiceManager() {
        AppInstance instance = AppInstance.getInstance();
        instance.setConfig(new AmfConfig( instance.getInstanceId(), instance.getConfigFile()));
        instance.loadPromptConfig();

        AmfConfig config = instance.getConfig();

        if (config.getLogPath() != null && config.getLogTime() > 0) {
            org.apache.log4j.xml.DOMConfigurator.configureAndWatch(config.getLogPath(), config.getLogTime());
        }
    }

    /**
     * Main loop
     */
    public void loop() {

        AmfConfig config = AppInstance.getInstance().getConfig();

        if (USE_PING && !pingRmqServer(config.getRmqHost())) {
            return;
        }

        startService();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {

            logger.warn("Process is about to quit (Ctrl+C)");
            isQuit = true;

            stopService();
            }));

        while (!isQuit) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("Process End");
    }

    /**
     * Returns a ping result to a rabbitmq server
     * @param host
     * @return
     */
    private boolean pingRmqServer(String host) {
        logger.info("Checking RMQ target [{}]", host);
        boolean rmqAvailable = NetUtil.ping( host, 1000);
        logger.info("Host [{}] is {}", host, rmqAvailable ? "reachable" : "NOT reachable");

        return rmqAvailable;
    }

    /**
     * Initializes pre-process
     * @return
     */
    private boolean startService() {

        AmfConfig config = AppInstance.getInstance().getConfig();

        rmqServer = new RmqServer();
        rmqServer.start();

        sessionManager = SessionManager.getInstance();
        sessionManager.start();

        if (config.getRedundantConfig().getLocalPort() > 0) {
            redundantServer = new RedundantServer(config.getRedundantConfig().getLocalPort());
            redundantServer.start();
        }

        if (config.getEngineLocalPort() > 0) {
            engineServer = new EngineServer(config.getEngineLocalPort());
            engineServer.start();
        }

        if(config.getHeartbeat().equals( "true" ))
        {
            heartbeatManager = heartbeatManager.getInstance();
            heartbeatManager.start();
        }

        this.amfLoginToA2S();

        try {
            NettyUDPServer nettyUDPServer = new NettyUDPServer( );

            nettyUDPServer.run();

            AppInstance.getInstance().setNettyUDPServer( nettyUDPServer );
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * Finalizes all the resources
     */
    private void stopService() {

        if (rmqServer != null) {
            rmqServer.stop();
        }

        if (redundantServer != null) {
            redundantServer.stop();
        }

        if (engineServer != null) {
            engineServer.stop();
        }

//        heartbeatManager.stop();
        sessionManager.stop();

        AmfConfig config = AppInstance.getInstance().getConfig();

        if (RmqClient.hasInstance( config.getMcudName())) {
            RmqClient.getInstance(config.getMcudName()).closeSender();
        }
    }

    public boolean releaseResource(String sessionId) {
        if (sessionId == null) {
            return false;
        }

        if (sessionManager == null) {
            return false;
        }

        SessionInfo sessionInfo = sessionManager.getSession(sessionId);

        if (sessionInfo == null) {
            logger.warn("[{}] No session found", sessionId);
            return false;
        }

        if (sessionInfo.getJitterSender() != null) {
            sessionInfo.getJitterSender().stop();
        }

        if(sessionInfo.rtpChannel != null) {
            sessionInfo.rtpChannel.close();
        }

        if(sessionInfo.udpChannel != null) {
            sessionInfo.udpChannel.close();
        }

        logger.warn("Netty session Close : [{}]", sessionId);

        if(sessionInfo.rtpClient != null) {
            sessionInfo.rtpClient.close();
        }

        if(sessionInfo.udpClient != null) {
            sessionInfo.udpClient.close();
        }


        logger.warn("Netty UDP session Close : [{}]", sessionId);

        if (sessionInfo.getConferenceId() != null) {
            RoomManager.getInstance().removeSession(sessionInfo.getConferenceId(), sessionInfo.getSessionId());
        }

        sessionManager.deleteSession(sessionId);


        return true;
    }

    private void amfLoginToA2S() {
        AmfConfig config = AppInstance.getInstance().getConfig();

        String thisSessionId = UUID.randomUUID().toString();

        RmqProcLogInReq req = new RmqProcLogInReq( thisSessionId, UUID.randomUUID().toString());

        req.send(config.getMcudName());
    }
}
