/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RmqProcInboundSetOfferReq.java
 * @author Tony Lim
 *
 */


package media.platform.amf.rmqif.handler;

import media.platform.amf.AppInstance;
import media.platform.amf.common.JsonMessage;
import media.platform.amf.config.SdpConfig;
import media.platform.amf.core.sdp.SdpInfo;
import media.platform.amf.core.sdp.SdpParser;
import media.platform.amf.oam.StatManager;
import media.platform.amf.redundant.RedundantClient;
import media.platform.amf.redundant.RedundantMessage;
import media.platform.amf.rmqif.handler.base.RmqIncomingMessageHandler;
import media.platform.amf.rmqif.messages.InboundSetOfferReq;
import media.platform.amf.session.SessionManager;
import media.platform.amf.rmqif.types.RmqMessage;
import media.platform.amf.rmqif.types.RmqMessageType;
import media.platform.amf.room.RoomManager;
import media.platform.amf.session.SessionInfo;
import media.platform.amf.rmqif.module.RmqData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import media.platform.amf.session.SessionState;
import media.platform.amf.session.SessionStateManager;
import media.platform.amf.simulator.BiUdpRelayManager;

public class RmqProcInboundSetOfferReq extends RmqIncomingMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcInboundSetOfferReq.class);

    @Override
    public boolean handle(RmqMessage msg) {
        if (msg == null || msg.getHeader() == null) {
            return false;
        }

        RmqData<InboundSetOfferReq> data = new RmqData<>( InboundSetOfferReq.class);
        InboundSetOfferReq req = data.parse(msg);

        if (req == null) {
            logger.error("InboundSetOfferReq: parsing failed");
            return false;
        }

        logger.info("[{}] InboundSetOfferReq: from [{}] to [{}] cnfid [{}]", msg.getSessionId(),
                req.getFromNo(), req.getToNo(), req.getConferenceId());

        // SessionId and ConferenceId are mandatory fields in the service
        if (msg.getSessionId() == null) {
            logger.error("[{}] No sessionId found");
            sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom(),
                    RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_WRONG_PARAM,
                    "NO SESSION ID");
            return  false;
        }

        if (req.getConferenceId() == null) {
            logger.warn("[{}] InboundSetOfferReq: No conferenceId", msg.getSessionId());
            sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom(),
                    RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_WRONG_PARAM,
                    "NO CONFERENCE ID");
            return false;
        }

        // sdpInfo can be null for a no-sdp case
        SdpInfo sdpInfo = SdpParser.parseSdp(req.getSdp());

        int parCount;
        parCount = setRoomInfo(req.getConferenceId(), msg.getSessionId());
        if (parCount == 0) {
            sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom(),
                    RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_FAILURE,
                    "ROOM-SESSION ERROR");
            return false;
        }

        // Creates a sessionInfo and set things following with the offerReq
        SessionManager sessionManager = SessionManager.getInstance();
        SessionInfo sessionInfo = sessionManager.createSession(msg.getSessionId());

        if (sessionInfo == null) {
            logger.warn("[{}] Cannot create session", msg.getSessionId());
            RoomManager.getInstance().removeSession(req.getConferenceId(), msg.getSessionId());

            sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom(),
                    RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_FAILURE,
                    "SESSION ERROR");
            return false;
        }

        sessionInfo.setSdpInfo(sdpInfo);
        sessionInfo.setSdpDeviceInfo(sdpInfo);  // 20180916 Hong add: What is this, SdpDeviceInfo?
        sessionInfo.setConferenceId(req.getConferenceId());
        sessionInfo.setFromNo(req.getFromNo());
        sessionInfo.setToNo(req.getToNo());
        sessionInfo.setCaller((parCount == 1) ? true : false);
        sessionInfo.setOutbound(req.getOutbound());
        sessionInfo.setRemoteRmqName(msg.getHeader().getMsgFrom());

        BiUdpRelayManager udpRelayManager = BiUdpRelayManager.getInstance();
        SdpConfig sdpConfig = AppInstance.getInstance().getUserConfig().getSdpConfig();

        boolean isError = false;
        int localPort = 0;
        do {
            try {
                localPort = udpRelayManager.getNextLocalPort();
                sessionInfo.rtpChannel = AppInstance.getInstance().getNettyRTPServer().addBindPort(sdpConfig.getLocalIpAddress(), localPort);
                sessionInfo.setSrcLocalPort(localPort);
                isError = false;
            } catch (Exception e) {
                logger.error("Exception rtp channel [{}] [{}] port [{}]", e.getClass(), e.getMessage(), localPort);
                isError = true;
            }

        } while (isError == true);

        sessionInfo.setDstLocalPort(udpRelayManager.getNextLocalPort());
        sessionInfo.setEnginePort(udpRelayManager.getNextLocalPort());

        try {
            sessionInfo.udpChannel = AppInstance.getInstance().getNettyUDPServer().addBindPort("127.0.0.1", sessionInfo.getDstLocalPort());

        } catch (Exception e) {
            logger.error("Exception udp channel [{}] [{}] port [{}]", e.getClass(), e.getMessage(), sessionInfo.getDstLocalPort());
        }

        logger.debug("[{}] Local port: rtp side [{}] udp side [{}]", msg.getSessionId(),
                sessionInfo.getSrcLocalPort(), sessionInfo.getDstLocalPort());
        logger.debug("[{}] Participant count [{}]", msg.getSessionId(), parCount);
        //
        // TODO
        //

        SessionStateManager.getInstance().setState(msg.getSessionId(), SessionState.OFFER);

        sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom());

        if (AppInstance.getInstance().getUserConfig().getRedundantConfig().isActive()) {
            String json = new JsonMessage(SessionInfo.class).build(sessionInfo);
            logger.debug("[{}] JSON: {}", msg.getSessionId(), json);

            RedundantClient.getInstance().sendMessage(RedundantMessage.RMT_SN_INBOUND_SET_OFFER_REQ, json);
        }

        StatManager.getInstance().incCount(sessionInfo.isCaller() ? StatManager.SVC_IN_CALL : StatManager.SVC_OUT_CALL);

        return true;
    }

    @Override
    public void sendResponse(String sessionId, String transactionId, String queueName, int reasonCode, String reasonStr) {

        RmqProcInboundSetOfferRes res = new RmqProcInboundSetOfferRes(sessionId, transactionId);

        res.setReasonCode(reasonCode);
        res.setReasonStr(reasonStr);

        if (res.send(queueName) == false) {
            // TODO
        }
    }

    /**
     * Checks a room with the key is a conferenceId.
     * If a room is not found, creates new room and put the sessionId into the room
     * @param conferenceId
     * @param sessionId
     * @return
     */
    private int setRoomInfo(String conferenceId, String sessionId) {
        if (conferenceId == null || sessionId == null) {
            return 0;
        }

        int result = 0;

        RoomManager roomManager = RoomManager.getInstance();
        if (roomManager.hasSession(conferenceId, sessionId)) {
            logger.warn("[{}] Already existed in room [{}]", sessionId, conferenceId);
        }
        else {
            result = roomManager.addSession(conferenceId, sessionId);
            logger.debug("[{}] Room addSession [{}] size [{}]", conferenceId, sessionId, result);
        }

        return result;
    }
}

