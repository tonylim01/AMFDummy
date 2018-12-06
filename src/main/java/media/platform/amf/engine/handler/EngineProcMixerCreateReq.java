package media.platform.amf.engine.handler;

import media.platform.amf.AppInstance;
import media.platform.amf.config.AmfConfig;
import media.platform.amf.engine.EngineManager;
import media.platform.amf.engine.handler.base.EngineOutgoingMessage;
import media.platform.amf.engine.messages.MixerCreateReq;
import media.platform.amf.session.SessionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EngineProcMixerCreateReq extends EngineOutgoingMessage {
    private static final Logger logger = LoggerFactory.getLogger(EngineProcMixerCreateReq.class);

    private String appId;
    private MixerCreateReq data;

    public EngineProcMixerCreateReq(String appId) {

        super("mixer", "create", appId);
        this.appId = appId;
    }

    public void setData(SessionInfo sessionInfo, int mixerId, int max) {

        if (sessionInfo == null) {
            logger.error("Null sessionInfo");
            return;
        }

        AmfConfig config = AppInstance.getInstance().getConfig();
        if (config == null) {
            return;
        }

        data = new MixerCreateReq();
        data.setId(mixerId);
        data.setMax(max);
        data.setFrom(sessionInfo.getFromNo());
        data.setTo(sessionInfo.getToNo());

        setBody(data, MixerCreateReq.class);
    }

    public Object getData() {
        return data;
    }

    public boolean send() {

        return sendTo();
    }
}
