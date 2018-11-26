package media.platform.amf.engine.handler;

import media.platform.amf.AppInstance;
import media.platform.amf.config.AmfConfig;
import media.platform.amf.engine.handler.base.EngineOutgoingMessage;
import media.platform.amf.engine.messages.SysConnectReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EngineProcSysHeartbeatReq extends EngineOutgoingMessage {
    private static final Logger logger = LoggerFactory.getLogger(EngineProcSysHeartbeatReq.class);

    private String appId;
    private SysConnectReq data;

    public EngineProcSysHeartbeatReq(String appId) {

        super("sys", "heartbeat", appId);
        this.appId = appId;

        setData();
    }

    public void setData() {

        AmfConfig config = AppInstance.getInstance().getConfig();
        if (config == null) {
            return;
        }

        // No data
    }

    public Object getData() {
        return data;
    }

    public boolean send() {

        return sendTo();
    }
}
