package media.platform.amf.engine;

import com.google.gson.*;
import media.platform.amf.engine.handler.EngineMessageHandlerAudio;
import media.platform.amf.engine.handler.EngineMessageHandlerMixer;
import media.platform.amf.engine.handler.EngineMessageHandlerWakeup;
import media.platform.amf.engine.handler.EngineMessageHandlerFile;
import media.platform.amf.engine.messages.SysHeartbeatRes;
import media.platform.amf.engine.types.EngineMessageType;
import media.platform.amf.engine.types.EngineReportMessage;
import media.platform.amf.engine.types.EngineResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.concurrent.BlockingQueue;

public class EngineConsumer implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(EngineConsumer.class);

    private BlockingQueue<byte[]> queue;
    private boolean isQuit = false;
    private Gson gson;

    public EngineConsumer(BlockingQueue<byte[]> queue) {
        this.queue = queue;
        gson = new GsonBuilder().setLenient().create();
    }

    @Override
    public void run() {
        logger.debug("EngineConsumer startScheduler");

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

    private String getJsonType(String json) {
        if (json == null) {
            return null;
        }

        int st = json.indexOf("\"");
        int et = json.indexOf("\"", st + 1);

        String key = null;
        if (st > 0 && et > 0) {
            key = json.substring(st + 1, et);
        }

        return key;
    }

    private void handleMessage(byte[] data) {
        if (data == null || data.length == 0) {
            return;
        }

        String json = new String(data, Charset.defaultCharset());
        json = json.trim();

        String typeStr = getJsonType(json);
        if (typeStr == null) {
            return;
        }

        if (compareString(typeStr, EngineMessageType.MSG_TYPE_RESPONSE)) {

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

            if (compareString(msg.getHeader().getType(), EngineMessageType.HDR_TYPE_SYS)) {
                handleSysResponse(msg);
            }
            else if (compareString(msg.getHeader().getType(), EngineMessageType.HDR_TYPE_AUDIO)) {
                logger.debug("<- Engine: json {}", json);
                EngineMessageHandlerAudio mixer = new EngineMessageHandlerAudio();
                mixer.handle(msg);
            }
            else if (compareString(msg.getHeader().getType(), EngineMessageType.HDR_TYPE_MIXER)) {
                logger.debug("<- Engine: json {}", json);
                EngineMessageHandlerMixer mixer = new EngineMessageHandlerMixer();
                mixer.handle(msg);
            }
            else if (compareString(msg.getHeader().getType(), EngineMessageType.HDR_TYPE_FILE)) {
                logger.debug("<- Engine: json {}", json);
                EngineMessageHandlerFile wakeup = new EngineMessageHandlerFile();
                wakeup.handle(msg);
            }
            else if (compareString(msg.getHeader().getType(), EngineMessageType.HDR_TYPE_WAKEUP)) {
                logger.debug("<- Engine: json {}", json);
                EngineMessageHandlerWakeup wakeup = new EngineMessageHandlerWakeup();
                wakeup.handle(msg);
            }
            else {
                logger.debug("<- Engine: json {}", json);
            }
        }
        else if (compareString(typeStr, EngineMessageType.MSG_TYPE_REPORT)) {

            EngineReportMessage msg = gson.fromJson(json, EngineReportMessage.class);

            if (msg == null || msg.getHeader() == null) {
                logger.debug("<- Engine: json {}", json);
                return;
            }

            if (msg.getHeader().getAppId() == null) {
                logger.debug("<- Engine: json {}", json);
                logger.warn("No appId in engine message");
                return;
            }

            if (compareString(msg.getHeader().getType(), EngineMessageType.HDR_TYPE_AUDIO)) {
                logger.debug("<- Engine: json {}", json);
                EngineMessageHandlerAudio audio = new EngineMessageHandlerAudio();
                audio.handle(msg);
            }
            else if (compareString(msg.getHeader().getType(), EngineMessageType.HDR_TYPE_FILE)) {
                logger.debug("<- Engine: json {}", json);
                EngineMessageHandlerFile wakeup = new EngineMessageHandlerFile();
                wakeup.handle(msg);
            }
            else if (compareString(msg.getHeader().getType(), EngineMessageType.HDR_TYPE_WAKEUP)) {
                logger.debug("<- Engine: json {}", json);
                EngineMessageHandlerWakeup wakeup = new EngineMessageHandlerWakeup();
                wakeup.handle(msg);
            }
            else {
                logger.debug("<- Engine: json {}", json);
            }

        }
        else {
            logger.warn("<- Engine: Undefined msg type [{}] json [{}]", typeStr, json);
        }
    }

    private boolean compareString(String src, String dst) {
        return (src != null && dst != null && src.equals(dst)) ? true : false;
    }

    private void handleSysResponse(EngineResponseMessage msg) {

        if (msg == null || msg.getHeader() == null) {
            return;
        }

        EngineClient engineClient = EngineClient.getInstance();

        if (compareString(msg.getHeader().getCmd(), EngineMessageType.MSG_CMD_CONNECT)) {

            if (compareString(msg.getHeader().getResult(), EngineMessageType.MSG_RESULT_OK) ||
                compareString(msg.getHeader().getResult(), EngineMessageType.MSG_RESULT_SUCCESS)) {
                // Ok
                engineClient.setConnected(true);
            }
            else {
                // Error
                engineClient.setConnected(false);
            }
        }
        else if (compareString(msg.getHeader().getCmd(), EngineMessageType.MSG_CMD_HEARTBEAT)) {

            engineClient.checkHeartbeat(msg.getHeader().appId);

            SysHeartbeatRes heartbeatRes = gson.fromJson(msg.getBody(), SysHeartbeatRes.class);
            if (heartbeatRes != null && EngineManager.getInstance().isResourceChanged(heartbeatRes.getTotal(), heartbeatRes.getBusy(), heartbeatRes.getIdle())) {
                logger.debug("Heart resource: total [{}] busy [{}] idle [{}]", heartbeatRes.getTotal(), heartbeatRes.getBusy(), heartbeatRes.getIdle());
                EngineManager.getInstance().setResourceCount(heartbeatRes.getTotal(), heartbeatRes.getBusy(), heartbeatRes.getIdle());
            }
        }
    }

}

