package media.platform.amf.engine.handler;

import com.google.common.base.Strings;
import media.platform.amf.AppInstance;
import media.platform.amf.config.AmfConfig;
import media.platform.amf.engine.EngineManager;
import media.platform.amf.engine.handler.base.EngineOutgoingMessage;
import media.platform.amf.engine.messages.AudioCreateReq;
import media.platform.amf.engine.messages.FilePlayReq;
import media.platform.amf.engine.messages.common.*;
import media.platform.amf.session.SessionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EngineProcFilePlayReq extends EngineOutgoingMessage {
    private static final Logger logger = LoggerFactory.getLogger(EngineProcFilePlayReq.class);

    private String appId;
    private FilePlayReq data;

    public EngineProcFilePlayReq(String appId) {

        super("file", "play", appId);
        this.appId = appId;
    }

    public void setData(SessionInfo sessionInfo, int toolId, int mediaType, int[] dstIds, boolean hasContainer, String[] filenames, int defVolume, int lowVolume) {

        if (sessionInfo == null) {
            logger.error("Null sessionInfo");
            return;
        }

        AmfConfig config = AppInstance.getInstance().getConfig();
        if (config == null) {
            return;
        }

        /*
        int toolId = EngineManager.getInstance().getIdleToolId();
        if (toolId < 0) {
            // Error
            logger.error("[{}] No available tools", sessionInfo.getSessionId());
            return;
        }
        */

        data = new FilePlayReq();
        data.setId(toolId);
        data.setType(mediaType);
        data.setDstIds(dstIds);

        FileInfos fileInfos = new FileInfos();
        fileInfos.setContainer(hasContainer);
        fileInfos.setList(filenames);

        data.setFile(fileInfos);

        SendVocoderInfo vocoder = new SendVocoderInfo();
        vocoder.setEnabled(true);

        data.setAudio(vocoder);

        VolumeInfo volume = new VolumeInfo();
        volume.setDef(defVolume);
        volume.setLow(lowVolume);

        setBody(data, FilePlayReq.class);
    }

    public Object getData() {
        return data;
    }

    public boolean send() {

        return sendTo();
    }
}
