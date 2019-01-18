package media.platform.amf.engine;

import media.platform.amf.engine.types.EngineSQueueMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

public class EngineServiceManager {

    private static final Logger logger = LoggerFactory.getLogger(EngineServiceManager.class);

    private static final int THREAD_POOL_SIZE = 4096;   // Number of channels
    private static final int QUEUE_SIZE = 128;

    private volatile static EngineServiceManager engineServiceManager = null;

    public static EngineServiceManager getInstance() {
        if (engineServiceManager == null) {
            engineServiceManager = new EngineServiceManager();
        }
        return engineServiceManager;
    }

    private BlockingQueue<EngineSQueueMessage> msgQueue;
    private EngineClient client;

    public EngineServiceManager() {
        msgQueue = new LinkedBlockingQueue<>(QUEUE_SIZE);

    }

    public void start() {

        client = EngineClient.getInstance();

        logger.info("SessionStateManager started");
    }

    public void stop() {

    }

    /**
     * Adds a msg into the end of the queue which is to send sequentially
     * @param type
     * @param cmd
     * @param appId
     * @param msg
     */
    public void pushMessage(String type, String cmd, String appId, String msg) {
        EngineSQueueMessage msgInfo = new EngineSQueueMessage(type,cmd, appId, msg);
        msgQueue.add(msgInfo);
    }

    public boolean popAndSendMessage() {
        boolean result = false;

        do {
            try {
                EngineSQueueMessage msg = msgQueue.take();
                handleMessage(msg);

                result = true;

            } catch (Exception e) {
                logger.warn("Exception [{}] [{}]", e.getClass(), e.getMessage());
                logger.error("Exception desc: {}", e);

                if (e.getClass() == InterruptedException.class || e.getClass() == SocketException.class) {
                }
                else {
                    e.printStackTrace();
                }
            }

        } while (!msgQueue.isEmpty());

        return result;
    }

    private void handleMessage(EngineSQueueMessage msg) {
        if (msg == null) {
            return;
        }

        if (client == null) {
            client = EngineClient.getInstance();

            if (client == null) {
                logger.error("Fail to get engine client");
                return;
            }
        }

        boolean result = client.sendMessage(msg.getMsg(), msg.getCmd().equals("heartbeat") ? false : true);;
        if (!result) {
            logger.error("Fail to send a engine msg");
            client = EngineClient.getInstance();

            if (client != null) {
                logger.error("Fail to get engine client");
                return;
            }

            result = client.sendMessage(msg.getMsg(), msg.getCmd().equals("heartbeat") ? false : true);;
            if (!result) {
                logger.error("Fail to resend a engine msg");
            }
        }
    }
}
