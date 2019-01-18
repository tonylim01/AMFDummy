package media.platform.amf.engine.handler;

import media.platform.amf.engine.handler.base.EngineOutgoingMessage;
import media.platform.amf.engine.messages.WakeupStopReq;
import media.platform.amf.session.SessionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EngineProcWakeupStopReq extends EngineOutgoingMessage {
    private static final Logger logger = LoggerFactory.getLogger(EngineProcWakeupStopReq.class);

    private String appId;
    private WakeupStopReq data;

    public EngineProcWakeupStopReq(String appId) {

        super("wakeup", "stop", appId);
        this.appId = appId;
    }

    public void setData(SessionInfo sessionInfo, int toolId) {

        if (sessionInfo == null) {
            logger.error("Null sessionInfo");
            return;
        }

        data = new WakeupStopReq();
        data.setId(toolId);

        setBody(data, WakeupStopReq.class);
    }

    public Object getData() {
        return data;
    }

    public boolean send(boolean push) {

        return sendTo(push);
    }
}
