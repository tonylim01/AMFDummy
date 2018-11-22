package media.platform.amf.engine;

import com.google.gson.Gson;
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
        logger.debug("<- Engine: json {}", json);

        //
        // TODO
        //
        Gson gson = new Gson();
        EngineResponseMessage msg = gson.fromJson(json, EngineResponseMessage.class);

        if (msg == null || msg.getHeader() == null) {
            return;
        }
    }
}
