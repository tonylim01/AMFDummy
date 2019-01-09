package media.platform.amf.engine.handler;

import media.platform.amf.AppInstance;
import media.platform.amf.config.AmfConfig;
import media.platform.amf.engine.handler.base.EngineOutgoingMessage;
import media.platform.amf.engine.messages.AudioCreateReq;
import media.platform.amf.engine.messages.AudioDeleteReq;
import media.platform.amf.engine.messages.common.CodecInfo;
import media.platform.amf.engine.messages.common.NetIP4Address;
import media.platform.amf.session.SessionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EngineProcAudioDeleteReq extends EngineOutgoingMessage {
    private static final Logger logger = LoggerFactory.getLogger(EngineProcAudioDeleteReq.class);

    private String appId;
    private AudioDeleteReq data;

    public EngineProcAudioDeleteReq(String appId) {

        super("audio", "delete", appId);
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

        logger.debug("[{}] AudioDeleteReq. toolId [{}", sessionInfo.getSessionId(), sessionInfo.getEngineToolId());

        data = new AudioDeleteReq();
        data.setId(sessionInfo.getEngineToolId());      // tool id

        setBody(data, AudioDeleteReq.class);
    }

    public Object getData() {
        return data;
    }

    public boolean send() {

        return sendTo();
    }
}
