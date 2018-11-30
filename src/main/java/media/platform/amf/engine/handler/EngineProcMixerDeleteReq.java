package media.platform.amf.engine.handler;

import media.platform.amf.AppInstance;
import media.platform.amf.config.AmfConfig;
import media.platform.amf.engine.handler.base.EngineOutgoingMessage;
import media.platform.amf.engine.messages.MixerDeleteReq;
import media.platform.amf.session.SessionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EngineProcMixerDeleteReq extends EngineOutgoingMessage {
    private static final Logger logger = LoggerFactory.getLogger(EngineProcMixerDeleteReq.class);

    private String appId;
    private MixerDeleteReq data;

    public EngineProcMixerDeleteReq(String appId) {

        super("mixer", "delete", appId);
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

        data = new MixerDeleteReq();
        data.setId(sessionInfo.getEngineToolId());      // tool id

        setBody(data, MixerDeleteReq.class);
    }

    public Object getData() {
        return data;
    }

    public boolean send() {

        return sendTo();
    }
}
