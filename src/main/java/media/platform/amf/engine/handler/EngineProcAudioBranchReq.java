package media.platform.amf.engine.handler;

import media.platform.amf.AppInstance;
import media.platform.amf.config.AmfConfig;
import media.platform.amf.engine.handler.base.EngineOutgoingMessage;
import media.platform.amf.engine.messages.AudioBranchReq;
import media.platform.amf.engine.messages.common.NetIP4Address;
import media.platform.amf.session.SessionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EngineProcAudioBranchReq extends EngineOutgoingMessage {
    private static final Logger logger = LoggerFactory.getLogger(EngineProcAudioBranchReq.class);

    private String appId;
    private AudioBranchReq data;

    public EngineProcAudioBranchReq(String appId) {

        super("audio", "branch", appId);
        this.appId = appId;
    }

    public void setData(SessionInfo sessionInfo) {

        if (sessionInfo == null) {
            logger.error("Null sessionInfo");
            return;
        }

        AmfConfig config = AppInstance.getInstance().getConfig();
        if (config == null) {
            return;
        }

        data = new AudioBranchReq();
        data.setId(sessionInfo.getEngineToolId());

        data.setRemote(new NetIP4Address(sessionInfo.getAiifIp(), sessionInfo.getAiifPort()));
        data.setLocal(sessionInfo.getEnginePort());   // On engine side

        setBody(data, AudioBranchReq.class);
    }

    public Object getData() {
        return data;
    }

    public boolean send() {

        return sendTo();
    }
}
