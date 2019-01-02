package media.platform.amf.engine.handler;

import media.platform.amf.AppInstance;
import media.platform.amf.config.AmfConfig;
import media.platform.amf.engine.handler.base.EngineOutgoingMessage;
import media.platform.amf.engine.messages.WakeupStartReq;
import media.platform.amf.session.SessionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EngineProcWakeupStartReq extends EngineOutgoingMessage {
    private static final Logger logger = LoggerFactory.getLogger(EngineProcWakeupStartReq.class);

    public static final int DEFAULT_TIMEOUT_MSEC = 600000;

    private String appId;
    private WakeupStartReq data;

    public EngineProcWakeupStartReq(String appId) {

        super("wakeup", "start", appId);
        this.appId = appId;
    }

    public void setData(SessionInfo sessionInfo, int toolId, int timeout) {

        if (sessionInfo == null) {
            logger.error("Null sessionInfo");
            return;
        }

        AmfConfig config = AppInstance.getInstance().getConfig();
        if (config == null) {
            return;
        }
        data = new WakeupStartReq();
        data.setId(toolId);
        data.setTimeout(timeout);

        setBody(data, WakeupStartReq.class);
    }

    public Object getData() {
        return data;
    }

    public boolean send() {

        return sendTo();
    }
}
