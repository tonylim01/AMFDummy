package media.platform.amf.session.StateHandler;

import media.platform.amf.core.sdp.SdpInfo;
import media.platform.amf.AppInstance;
import media.platform.amf.config.AmfConfig;
import media.platform.amf.config.SdpConfig;
import media.platform.amf.core.socket.JitterSender;
import media.platform.amf.core.socket.packets.Vocoder;
import media.platform.amf.engine.EngineClient;
import media.platform.amf.engine.handler.EngineProcAudioCreateReq;
import media.platform.amf.engine.messages.SysConnectReq;
import media.platform.amf.rmqif.messages.FileData;
import media.platform.amf.room.RoomInfo;
import media.platform.amf.room.RoomManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import media.platform.amf.session.SessionInfo;
import media.platform.amf.session.SessionState;
import media.platform.amf.session.SessionStateManager;

import java.util.UUID;

public class PrepareStateFunction implements StateFunction {
    private static final Logger logger = LoggerFactory.getLogger(PrepareStateFunction.class);

    @Override
    public void run(SessionInfo sessionInfo, Object arg) {
        if (sessionInfo == null) {
            return;
        }

        logger.debug("{} PREPARE state", sessionInfo.getSessionId());

        if (sessionInfo.getServiceState() != SessionState.PREPARE) {
            sessionInfo.setServiceState(SessionState.PREPARE);
        }


        logger.debug("{} SDP payload {}", sessionInfo.getSessionId(), sessionInfo.getSdpInfo().getPayloadId());
        logger.debug("{} SDP payload {}", sessionInfo.getSessionId(), sessionInfo.getSdpDeviceInfo().getPayloadId());

        if (sessionInfo.isCaller()) {
            openCallerRelayResource(sessionInfo);

        }
        else {
            openCalleeRelayResource(sessionInfo);
        }

        // TEST
        String appId = UUID.randomUUID().toString();
        EngineProcAudioCreateReq audioCreateReq = new EngineProcAudioCreateReq(appId);
        audioCreateReq.setData(sessionInfo);

        if (audioCreateReq.send()) {
            EngineClient.getInstance().pushSentQueue(appId, SysConnectReq.class, audioCreateReq.getData());
        }
    }


    /**
     * (Caller) -> (Relay) --> (Callee)
     *
     * @param sessionInfo
     * @return
     */
    private boolean openCallerRelayResource(SessionInfo sessionInfo) {

        if (sessionInfo == null) {
            return false;
        }

        logger.debug("[{}] Open caller relay resources", sessionInfo.getSessionId());

        SdpInfo sdpInfo = sessionInfo.getSdpDeviceInfo();
        if (sdpInfo == null) {
            return false;
        }

        logger.debug("[{}] Caller Relay: remote ({}:{}) <- local ({})", sessionInfo.getSessionId(),
                     sdpInfo.getRemoteIp(), sdpInfo.getRemotePort(), sessionInfo.getSrcLocalPort());

        sessionInfo.rtpClient = AppInstance.getInstance().getNettyRTPServer().addConnectPort(sdpInfo.getRemoteIp(), sdpInfo.getRemotePort());

        if (!AppInstance.getInstance().getConfig().isRelayMode()) {
            sessionInfo.udpClient = AppInstance.getInstance().getNettyRTPServer().addConnectPort("127.0.0.1", sessionInfo.getEnginePort());
        }

        openJitterSender(sessionInfo);

        return true;
    }

    /**
     * (Callee) -> (Relay) --> (Caller)
     *
     * @param sessionInfo
     * @return
     */
    private boolean openCalleeRelayResource(SessionInfo sessionInfo) {

        if (sessionInfo == null) {
            return false;
        }

        logger.debug("[{}] Open callee relay resources", sessionInfo.getSessionId());

        RoomInfo roomInfo = RoomManager.getInstance().getRoomInfo( sessionInfo.getConferenceId());
        if (roomInfo == null) {
            logger.error("[{}] No roomInfo found", sessionInfo.getSessionId());
            return false;
        }

        SdpInfo sdpInfo = sessionInfo.getSdpDeviceInfo();
        if (sdpInfo == null) {
            logger.error("[{}] sdpInfo null", sessionInfo.getSessionId());
            return false;
        }

        //sessionInfo.channel = AppInstance.getInstance().getNettyRTPServer().addBindPort( sdpConfig.getLocalIpAddress(), sessionInfo.getSrcLocalPort());
        sessionInfo.rtpClient = AppInstance.getInstance().getNettyRTPServer().addConnectPort(sdpInfo.getRemoteIp(), sdpInfo.getRemotePort());

        if (!AppInstance.getInstance().getConfig().isRelayMode()) {
            sessionInfo.udpClient = AppInstance.getInstance().getNettyRTPServer().addConnectPort("127.0.0.1", sessionInfo.getEnginePort());
        }

        openJitterSender(sessionInfo);


        return true;
    }

    private void openJitterSender(SessionInfo sessionInfo) {
        if (sessionInfo == null) {
            return;
        }

        int vocoder = 0;
        if (sessionInfo.getSdpDeviceInfo().getCodecStr() != null) {
            if (sessionInfo.getSdpDeviceInfo().getCodecStr().equals("AMR-WB")) {
                vocoder = Vocoder.VOCODER_AMR_WB;
            }
            else if (sessionInfo.getSdpDeviceInfo().getCodecStr().equals("AMR-NB")) {
                vocoder = Vocoder.VOCODER_AMR_NB;
            }
        }

        if (vocoder == 0) {
            switch (sessionInfo.getSdpDeviceInfo().getPayloadId()) {
                case 0: vocoder = Vocoder.VOCODER_ULAW; break;
                case 8: vocoder = Vocoder.VOCODER_ALAW; break;
            }
        }

        if (vocoder == 0) {
            vocoder = Vocoder.VOCODER_ALAW;
        }

        int payloadId = sessionInfo.getSdpDeviceInfo().getPayloadId();
        int payloadSize = (vocoder == Vocoder.VOCODER_AMR_WB) ? 320 : 160;

        JitterSender jitterSender = new JitterSender(vocoder, Vocoder.MODE_NA, payloadId, 20, 3, payloadSize);
        jitterSender.setUdpClient(sessionInfo.rtpClient);
        jitterSender.setSessionId(sessionInfo.getSessionId());
        jitterSender.setRelay(AppInstance.getInstance().getConfig().isRelayMode());

        jitterSender.start();

        sessionInfo.setJitterSender(jitterSender);

    }

    private boolean openMixerResource(RoomInfo roomInfo, String sessionId) {
        if (roomInfo == null) {
            return false;
        }


        int groupId = roomInfo.getGroupId();

        if (groupId < 0) {
            return false;
        }

        logger.debug("({}) Allocates mixer on group [{}]", roomInfo.getRoomId(), groupId);


        return true;
    }

    private boolean openCallerResource(SessionInfo sessionInfo, RoomInfo roomInfo) {
        SdpConfig sdpConfig = AppInstance.getInstance().getConfig().getSdpConfig();

        if (sessionInfo == null) {
            return false;
        }

        logger.debug("[{}] Allocates caller DSP resources", sessionInfo.getSessionId());

        String json;
        int groupId = roomInfo.getGroupId();
        int mixerId = roomInfo.getMixerId();

        if (groupId < 0 || mixerId < 0) {
            return false;
        }

        return true;
    }

    private boolean openCalleeResource(SessionInfo sessionInfo, RoomInfo roomInfo) {
        if (sessionInfo == null) {
            return false;
        }

        logger.debug("{} Allocates callee DSP resources", sessionInfo.getSessionId());

        String json;
        int groupId = roomInfo.getGroupId();
        int mixerId = roomInfo.getMixerId();

        if (groupId < 0 || mixerId < 0) {
            return false;
        }

        AmfConfig config = AppInstance.getInstance().getConfig();


        return true;
    }

    private boolean openPlayResource(SessionInfo sessionInfo, RoomInfo roomInfo) {
        if (sessionInfo == null) {
            return false;
        }

        logger.debug("{} Allocates file play DSP resources", sessionInfo.getSessionId());

        String json;
        int groupId = roomInfo.getGroupId();
        int mixerId = roomInfo.getMixerId();

        if (groupId < 0 || mixerId < 0) {
            return false;
        }


        return true;
    }

    private boolean playDemoAudio(SessionInfo sessionInfo, RoomInfo roomInfo) {
        if (sessionInfo == null) {
            return false;
        }

        logger.debug("[{}] Play demo audio", sessionInfo.getSessionId());

        int groupId = roomInfo.getGroupId();
        int mixerId = roomInfo.getMixerId();

        if (groupId < 0 || mixerId < 0) {
            return false;
        }

        FileData file = new FileData();
        file.setChannel(FileData.CHANNEL_BGM);
        file.setPlayFile("Heize_rain_and.wav");
        SessionStateManager.getInstance().setState(sessionInfo.getSessionId(), SessionState.PLAY_START, file);

        return true;
    }
 }
