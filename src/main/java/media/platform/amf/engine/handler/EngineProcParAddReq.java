package media.platform.amf.engine.handler;

import media.platform.amf.AppInstance;
import media.platform.amf.config.AmfConfig;
import media.platform.amf.engine.handler.base.EngineOutgoingMessage;
import media.platform.amf.engine.messages.MixerDeleteReq;
import media.platform.amf.engine.messages.ParAddReq;
import media.platform.amf.room.RoomInfo;
import media.platform.amf.room.RoomManager;
import media.platform.amf.session.SessionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EngineProcParAddReq extends EngineOutgoingMessage {
    private static final Logger logger = LoggerFactory.getLogger(EngineProcParAddReq.class);

    private String appId;
    private ParAddReq data;

    public EngineProcParAddReq(String appId) {

        super("par", "add", appId);
        this.appId = appId;
    }

    public void setData(SessionInfo sessionInfo) {

        if (sessionInfo == null) {
            logger.error("Null sessionInfo");
            return;
        }

        if (sessionInfo.getEngineToolId() < 0) {
            logger.warn("[{}] Tool not defined", sessionInfo.getSessionId());
            return;
        }

        AmfConfig config = AppInstance.getInstance().getConfig();
        if (config == null) {
            return;
        }

        int mixerId;

        RoomInfo roomInfo = RoomManager.getInstance().getRoomInfo(sessionInfo.getConferenceId());
        if (roomInfo != null) {
            mixerId = roomInfo.getMixerId();
        }
        else {
            mixerId = sessionInfo.getMixerToolId();
        }

        data = new ParAddReq();
        data.setId(mixerId);      // tool id

        int[] srcIds = new int[1];
        srcIds[0] = sessionInfo.getEngineToolId();

        data.setSrcIds(srcIds);

        setBody(data, ParAddReq.class);
    }

    public Object getData() {
        return data;
    }

    public boolean send() {

        return sendTo(false);
    }
}
