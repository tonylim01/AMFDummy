package media.platform.amf.engine.handler;

import media.platform.amf.AppInstance;
import media.platform.amf.config.AmfConfig;
import media.platform.amf.engine.handler.base.EngineOutgoingMessage;
import media.platform.amf.engine.messages.SysConnectReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EngineProcSysConnectReq extends EngineOutgoingMessage {
    private static final Logger logger = LoggerFactory.getLogger(EngineProcSysConnectReq.class);

    private String appId;
    private SysConnectReq data;

    public EngineProcSysConnectReq(String appId) {

        super("sys", "connect", appId);
        this.appId = appId;
        setData();
    }

    public void setData() {

        AmfConfig config = AppInstance.getInstance().getConfig();
        if (config == null) {
            return;
        }

        data = new SysConnectReq();
        data.setPort(config.getEngineLocalPort());

        setBody(data, SysConnectReq.class);

    }

    public Object getData() {
        return data;
    }

    public boolean send() {

        return sendTo();
    }
}
