package media.platform.amf.session.StateHandler;

import io.netty.channel.socket.DatagramChannel;
import media.platform.amf.core.sdp.SdpInfo;
import media.platform.amf.AppInstance;
import media.platform.amf.config.AmfConfig;
import media.platform.amf.config.SdpConfig;
import media.platform.amf.core.socket.JitterSender;
import media.platform.amf.core.socket.packets.Vocoder;
import media.platform.amf.rmqif.messages.FileData;
import media.platform.amf.room.RoomInfo;
import media.platform.amf.room.RoomManager;
import media.platform.amf.simulator.BiUdpRelayManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import media.platform.amf.session.SessionInfo;
import media.platform.amf.session.SessionState;
import media.platform.amf.session.SessionStateManager;

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
    }


    /**
     * (Caller) -> (Relay) --> (Callee)
     *
     * @param sessionInfo
     * @return
     */
    private boolean openCallerRelayResource(SessionInfo sessionInfo) {
        SdpConfig sdpConfig = AppInstance.getInstance().getConfig().getSdpConfig();

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


        //sessionInfo.channel = AppInstance.getInstance().getNettyUDPServer().addBindPort( sdpConfig.getLocalIpAddress(), sessionInfo.getSrcLocalPort());

        DatagramChannel datagramChannel = null;

        sessionInfo.udpClient = AppInstance.getInstance().getNettyUDPServer().addConnectPort( sdpInfo.getRemoteIp(), sdpInfo.getRemotePort());

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
        SdpConfig sdpConfig = AppInstance.getInstance().getConfig().getSdpConfig();
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

        BiUdpRelayManager udpRelayManager = BiUdpRelayManager.getInstance();

        //sessionInfo.channel = AppInstance.getInstance().getNettyUDPServer().addBindPort( sdpConfig.getLocalIpAddress(), sessionInfo.getSrcLocalPort());
        sessionInfo.udpClient = AppInstance.getInstance().getNettyUDPServer().addConnectPort( sdpInfo.getRemoteIp(), sdpInfo.getRemotePort());

        //JitterSender jitterSender = new JitterSender(Vocoder.VOCODER_ALAW, Vocoder.MODE_NA, 8, 20, 3, 160);
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
        jitterSender.setUdpClient(sessionInfo.udpClient);
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
