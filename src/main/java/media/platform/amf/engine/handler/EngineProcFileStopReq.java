package media.platform.amf.engine.handler;

import media.platform.amf.AppInstance;
import media.platform.amf.config.AmfConfig;
import media.platform.amf.engine.handler.base.EngineOutgoingMessage;
import media.platform.amf.engine.messages.FileStopReq;
import media.platform.amf.session.SessionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EngineProcFileStopReq extends EngineOutgoingMessage {
    private static final Logger logger = LoggerFactory.getLogger(EngineProcFileStopReq.class);

    private String appId;
    private FileStopReq data;

    public EngineProcFileStopReq(String appId) {

        super("file", "stop", appId);
        this.appId = appId;
    }

    public void setData(SessionInfo sessionInfo, int toolId, int mediaType, int[] dstIds) {

        if (sessionInfo == null) {
            logger.error("Null sessionInfo");
            return;
        }

        AmfConfig config = AppInstance.getInstance().getConfig();
        if (config == null) {
            return;
        }

        data = new FileStopReq();
        data.setId(toolId);
        data.setType(mediaType);
        data.setDstIds(dstIds);

        setBody(data, FileStopReq.class);
    }

    public Object getData() {
        return data;
    }

    public boolean send() {

        return sendTo(false);
    }
}
