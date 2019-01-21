package media.platform.amf.engine;

import media.platform.amf.AppInstance;
import media.platform.amf.config.AmfConfig;
import media.platform.amf.config.UserConfig;
import media.platform.amf.engine.handler.EngineProcSysConnectReq;
import media.platform.amf.engine.handler.EngineProcSysHeartbeatReq;
import media.platform.amf.engine.messages.SysConnectReq;
import media.platform.amf.engine.messages.SysHeartbeatReq;
import media.platform.amf.engine.types.SentMessageInfo;
import media.platform.amf.rtpcore.Process.UdpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EngineClient {

    private static final Logger logger = LoggerFactory.getLogger(EngineClient.class);

    private static final int MAX_RETRANSMISSION_COUNT = 3;

    private UdpClient udpClient;
    private String ip;
    private int port;

    private volatile static EngineClient engineClient = null;
    private boolean connected;
    private boolean isQuit;

    private Thread clientThread = null;
    private Map<String, SentMessageInfo> sentQueue;

    public static EngineClient getInstance() {
        if (engineClient == null) {
            UserConfig config = AppInstance.getInstance().getUserConfig();
            if (config.getEngineIp() != null && config.getEngineRemotePort() > 0) {
                engineClient = new EngineClient(config.getEngineIp(), config.getEngineRemotePort());
            }
            else {
                logger.error("Media engine not defined");
            }
        }
        return engineClient;
    }

    public EngineClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.connected = false;

        sentQueue = new HashMap<>();

        initClient();
    }

    public void stop() {
        isQuit = true;
    }

    private void initClient() {
        try {
            InetAddress addr = InetAddress.getByName(ip);
            udpClient = new UdpClient(addr, port);
        } catch (Exception e) {
            e.printStackTrace();
        }

        clientThread = new Thread(new EngineClientRunnable());
        clientThread.start();
    }

    private boolean sendBytes(byte[] msg) {
        if (msg == null) {
            return false;
        }

        boolean result = false;

        boolean isRepeat;
        int repeatCount = 0;

        do {
            isRepeat = false;

            if (udpClient == null) {
                initClient();
            }

            if (udpClient != null) {
                try {
                    udpClient.send(msg);
                    result = true;

                } catch (Exception e) {
                    e.printStackTrace();

                    udpClient.close();
                    udpClient = null;

                    if (repeatCount < 1) {
                        isRepeat = true;
                        repeatCount++;
                    }
                }

            }
        } while (isRepeat);

        return result;
    }

    public boolean sendMessage(String msg, boolean printLog) {

        boolean result = sendBytes(msg.getBytes(Charset.defaultCharset()));
        if (printLog) {
            logger.debug("-> Engine: msg [{}] result [{}]", msg, result);
        }

        return result;
    }

    public boolean sendMessage(String msg) {

        return sendMessage(msg, true);
    }

    private synchronized boolean isConnected() {
        return connected;
    }

    class EngineClientRunnable implements Runnable {
        @Override
        public void run() {

            logger.info("EngineClient startScheduler");
            AmfConfig config = AppInstance.getInstance().getConfig();

            if (config == null) {
                return;
            }

            int retryCount = 0;

            while (!isQuit) {

                try {
                    Thread.sleep(config.getTimerEngineTE());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //
                // TODO
                //
                String appId = UUID.randomUUID().toString();

                if (!isConnected()) {
                    EngineProcSysConnectReq sysConnectReq = new EngineProcSysConnectReq(appId);

                    if (sysConnectReq.send()) {
                        pushSentQueue(appId, SysConnectReq.class, sysConnectReq.getData());
                    }
                }
                else if (checkHeartbeat()) {
                    if (retryCount > 0) {
                        retryCount = 0;
                    }

                    EngineProcSysHeartbeatReq sysHeartbeatReq = new EngineProcSysHeartbeatReq(appId);
                    if (sysHeartbeatReq.send()) {
                        pushSentQueue(appId, SysHeartbeatReq.class, sysHeartbeatReq.getData());
                    }
                }
                else {
                    retryCount++;

                    if (retryCount >= MAX_RETRANSMISSION_COUNT) {
                        setConnected(false);
                        retryCount = 0;
                    }
                }
            }

            logger.info("EngineClient end");
        }
    }

    public synchronized void pushSentQueue(String appId, Class clss, Object obj) {
        boolean isImmediate = false;
        if (sentQueue.size() == 0) {
            isImmediate = true;
        }

        SentMessageInfo msgInfo = new SentMessageInfo(System.currentTimeMillis(), clss, obj);
        sentQueue.put(appId, msgInfo);

        if (isImmediate) {

        }
    }

    public synchronized void setConnected(boolean connected) {
        this.connected = connected;
    }

    private synchronized boolean checkHeartbeat() {
        boolean result = true;

        for (Map.Entry<String, SentMessageInfo> entry: sentQueue.entrySet()) {
            SentMessageInfo msgInfo = entry.getValue();

            if (entry.getKey() == null || msgInfo == null) {
                continue;
            }

            if (msgInfo.getClss() == SysHeartbeatReq.class) {
                logger.info("Heartbeat found in queue [{}]", entry.getKey());
                sentQueue.remove(entry.getKey());
                result = false;
                break;
            }
        }

        return result;
    }

    public synchronized void checkHeartbeat(String appId) {

        if (appId == null) {
            return;
        }

        AmfConfig config = AppInstance.getInstance().getConfig();

        if (config == null) {
            return;
        }

        for (Map.Entry<String, SentMessageInfo> entry: sentQueue.entrySet()) {
            String id = entry.getKey();
            SentMessageInfo msgInfo = entry.getValue();

            if (id == null || msgInfo == null) {
                continue;
            }

            if (msgInfo.getClss() == SysHeartbeatReq.class && appId.equals(id)) {

                long currentTimestamp = System.currentTimeMillis();

                if (currentTimestamp - msgInfo.getTimestamp() > config.getTimerEngineTH()) {
                    logger.warn("Heartbeat response timeout [{}]ms appId [{}]", currentTimestamp - msgInfo.getTimestamp(), id);
                }
                else {
                    sentQueue.remove(entry.getKey());
                }

                break;
            }
        }
    }

    public synchronized SentMessageInfo getSentQueue(String appId) {
        if (appId == null) {
            return null;
        }

        SentMessageInfo messageInfo = null;
        if (sentQueue.containsKey(appId)) {
            messageInfo = sentQueue.get(appId);
        }

        return messageInfo;
    }

    public synchronized boolean removeSentQueue(String appId) {
        if (appId == null) {
            return false;
        }

        boolean result = false;

        if (sentQueue.containsKey(appId)) {
            sentQueue.remove(appId);
            result = true;
        }

        return result;
    }
}
