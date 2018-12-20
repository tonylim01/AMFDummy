package media.platform.amf.engine.handler;

import media.platform.amf.AppInstance;
import media.platform.amf.config.AmfConfig;
import media.platform.amf.core.sdp.SdpInfo;
import media.platform.amf.engine.EngineManager;
import media.platform.amf.engine.handler.base.EngineOutgoingMessage;
import media.platform.amf.engine.messages.AudioCreateReq;
import media.platform.amf.engine.messages.common.CodecInfo;
import media.platform.amf.engine.messages.common.NetIP4Address;
import media.platform.amf.session.SessionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EngineProcAudioCreateReq extends EngineOutgoingMessage {
    private static final Logger logger = LoggerFactory.getLogger(EngineProcAudioCreateReq.class);

    private String appId;
    private AudioCreateReq data;

    public EngineProcAudioCreateReq(String appId) {

        super("audio", "create", appId);
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

        int toolId = EngineManager.getInstance().getIdleToolId();
        if (toolId < 0) {
            // Error
            logger.error("[{}] No available tools", sessionInfo.getSessionId());
            return;
        }

        sessionInfo.setEngineToolId(toolId);

        data = new AudioCreateReq();
//        data.setId(EngineManager.getInstance().getIdleToolId());      // tool id
        data.setId(toolId);
        data.setFrom(sessionInfo.getFromNo());
        data.setTo(sessionInfo.getToNo());

        /*
        int[] dstIds = new int[1];
        dstIds[0] = 2;      // dest ids;

        data.setDstIds(dstIds);
        */

        data.setRemote(new NetIP4Address("127.0.0.1", sessionInfo.getDstLocalPort()));    // On amf side
        data.setLocal(sessionInfo.getEnginePort());   // On engine side

        // decoder & encoder
        CodecInfo coderInfo = new CodecInfo();
        String codec = null;
        String packing = null;
        String rate = null;
        SdpInfo sdpInfo;
        if (sessionInfo.getSdpDeviceInfo() != null) {
            sdpInfo = sessionInfo.getSdpDeviceInfo();
        }
        else if (sessionInfo.getSdpInfo() != null) {
            sdpInfo = sessionInfo.getSdpInfo();
        }
        else {
            logger.error("[{}] No sdpInfo found", sessionInfo.getSessionId());
            return;
        }

        if (sdpInfo.getCodecStr() != null) {
            if (sdpInfo.getCodecStr().equals("AMR-WB")) {
                codec = "AMR_WB";
                packing = "OA";
                rate = "23.85";
            }
            else if (sdpInfo.getCodecStr().equals("AMR-NB")) {
                codec = "AMR_NB";
                packing = "OA";
                rate = "12.2";
            }
            else {
                codec = sessionInfo.getSdpDeviceInfo().getCodecStr();
            }
        }
        else if (sdpInfo.getCodecStr() == null) {
            if (sdpInfo.getPayloadId() == 0) {
                codec = "ulaw";
            }
            else {
                codec = "alaw";
            }
        }

        coderInfo.setCodec(codec);
        coderInfo.setDuration(20);
        coderInfo.setPacking(packing);
        coderInfo.setRate(rate);

        data.setDecoder(coderInfo);
        data.setEncoder(coderInfo);

        data.setDtmf(true);

        setBody(data, AudioCreateReq.class);
    }

    public Object getData() {
        return data;
    }

    public boolean send() {

        return sendTo();
    }
}
