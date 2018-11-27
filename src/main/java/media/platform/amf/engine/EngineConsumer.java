package media.platform.amf.engine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import media.platform.amf.engine.messages.SysHeartbeatRes;
import media.platform.amf.engine.types.EngineResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketException;
import java.util.concurrent.BlockingQueue;

public class EngineConsumer implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(EngineConsumer.class);

    private BlockingQueue<byte[]> queue;
    private boolean isQuit = false;

    public EngineConsumer(BlockingQueue<byte[]> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        logger.debug("RedundantConsumer start");

        while (!isQuit) {
            try {
                byte[] buf = queue.take();

                handleMessage(buf);

            } catch (Exception e) {
                logger.warn("Exception [{}] [{}]", e.getClass(), e.getMessage());
                if (e.getClass() == InterruptedException.class || e.getClass() == SocketException.class) {
                    isQuit = true;
                }
            }
        }

        logger.debug("EngineConsumer end");
    }

    private void handleMessage(byte[] data) {
        if (data == null || (data != null && data.length == 0)) {
            return;
        }

        String json = new String(data);
        json = json.trim();

        //
        // TODO
        //
        //Gson gson = new Gson();
        Gson gson = new GsonBuilder().setLenient().create();
        EngineResponseMessage msg = gson.fromJson(json, EngineResponseMessage.class);

        if (msg == null || msg.getHeader() == null) {
            logger.debug("<- Engine: json {}", json);
            return;
        }

        if (msg.getHeader().getAppId() == null) {
            logger.debug("<- Engine: json {}", json);
            logger.warn("No appId in engine message");
            return;
        }

        EngineClient engineClient = EngineClient.getInstance();

        if (compareString(msg.getHeader().getType(), "sys")) {
            if (compareString(msg.getHeader().getCmd(), "connect")) {

                if (compareString(msg.getHeader().getResult(), "success")) {
                    // Ok
                    engineClient.setConnected(true);
                }
                else {
                    // Error
                    engineClient.setConnected(false);
                }
            }
            else if (compareString(msg.getHeader().getCmd(), "heartbeat")) {

                engineClient.checkHeartbeat(msg.getHeader().appId);

                SysHeartbeatRes heartbeatRes = gson.fromJson(msg.getBody(), SysHeartbeatRes.class);
                if (heartbeatRes != null && EngineManager.getInstance().isResourceChanged(heartbeatRes.getTotal(), heartbeatRes.getBusy(), heartbeatRes.getIdle())) {
                    logger.debug("Heart resource: total [{}] busy [{}] idle [{}]", heartbeatRes.getTotal(), heartbeatRes.getBusy(), heartbeatRes.getIdle());
                    EngineManager.getInstance().setResourceCount(heartbeatRes.getTotal(), heartbeatRes.getBusy(), heartbeatRes.getIdle());
                }
            }
        }
        else {
            logger.debug("<- Engine: json {}", json);
        }
    }

    private boolean compareString(String src, String dst) {
        return (src != null && dst != null && src.equals(dst)) ? true : false;
    }
}
