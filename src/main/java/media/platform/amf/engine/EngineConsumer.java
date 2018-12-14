package media.platform.amf.engine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import media.platform.amf.common.AppId;
import media.platform.amf.engine.messages.SysHeartbeatRes;
import media.platform.amf.engine.types.EngineResponseMessage;
import media.platform.amf.room.RoomInfo;
import media.platform.amf.room.RoomManager;
import media.platform.amf.session.SessionInfo;
import media.platform.amf.session.SessionManager;
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
        if (data == null || data.length == 0) {
            return;
        }

        String json = new String(data, Charset.defaultCharset());
        json = json.trim();

        //
        // TODO
        //

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

        if (compareString(msg.getHeader().getType(), "sys")) {
            handleSysResponse(msg);
        }
        else if (compareString(msg.getHeader().getType(), "audio")) {
            logger.debug("<- Engine: json {}", json);
            handleAudioResponse(msg);
        }
        else if (compareString(msg.getHeader().getType(), "mixer")) {
            logger.debug("<- Engine: json {}", json);
            handleMixerResponse(msg);
        }
        else {
            logger.debug("<- Engine: json {}", json);
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

        if (compareString(msg.getHeader().getCmd(), "connect")) {

            if (compareString(msg.getHeader().getResult(), "ok") ||
                compareString(msg.getHeader().getResult(), "success")) {
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

    private void handleAudioResponse(EngineResponseMessage msg) {

        if (msg == null || msg.getHeader() == null) {
            logger.warn("Null response message");
            return;
        }

        if (compareString(msg.getHeader().getCmd(), "create")) {

            if (compareString(msg.getHeader().getResult(), "ok") ||
                    compareString(msg.getHeader().getResult(), "success")) {
                // Success
                if (msg.getHeader().getAppId() == null) {
                    logger.warn("Null appId in response message");
                    return;
                }

                String sessionId = AppId.getInstance().get(msg.getHeader().getAppId());
                if (sessionId == null) {
                    logger.warn("No sessionId for appId=[{}]", msg.getHeader().getAppId());
                    return;
                }

                SessionInfo sessionInfo = SessionManager.getInstance().getSession(sessionId);
                if (sessionInfo == null) {
                    logger.warn("Cannot find session for appId=[{}]", msg.getHeader().getAppId());
                    return;
                }

                sessionInfo.setAudioCreated(true);

                //
                // TODO
                //
            }
            else {
                logger.warn("Undefined result [{}]", msg.getHeader().getResult());
            }
        }
        else {
            logger.warn("Unsupported cmd [{}]", msg.getHeader().getCmd());
        }

    }

    private void handleMixerResponse(EngineResponseMessage msg) {

        if (msg == null || msg.getHeader() == null) {
            logger.warn("Null response message");
            return;
        }

        if (compareString(msg.getHeader().getCmd(), "create")) {

            if (compareString(msg.getHeader().getResult(), "ok") ||
                compareString(msg.getHeader().getResult(), "success")) {
                // Success
                if (msg.getHeader().getAppId() == null) {
                    logger.warn("Null appId in response message");
                    return;
                }

                String roomId = AppId.getInstance().get(msg.getHeader().getAppId());
                if (roomId == null) {
                    logger.warn("No roomId for appId=[{}]", msg.getHeader().getAppId());
                    return;
                }

                RoomInfo roomInfo = RoomManager.getInstance().getRoomInfo(roomId);
                if (roomInfo == null) {
                    logger.warn("Cannot find room for appId=[{}]", msg.getHeader().getAppId());
                    return;
                }

                roomInfo.setMixerAvailable(true);

                //
                // TODO
                //
            }
            else {
                logger.warn("Undefined result [{}]", msg.getHeader().getResult());
            }
        }
        else {
            logger.warn("Unsupported cmd [{}]", msg.getHeader().getCmd());
        }

    }
}

