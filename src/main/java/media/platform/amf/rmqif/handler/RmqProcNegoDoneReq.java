/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RmqProcNegoDoneReq.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.handler;

import media.platform.amf.AppInstance;
import media.platform.amf.common.JsonMessage;
import media.platform.amf.core.sdp.SdpInfo;
import media.platform.amf.core.sdp.SdpParser;
import media.platform.amf.oam.StatManager;
import media.platform.amf.redundant.RedundantClient;
import media.platform.amf.redundant.RedundantMessage;
import media.platform.amf.rmqif.handler.base.RmqIncomingMessageHandler;
import media.platform.amf.rmqif.messages.NegoDoneReq;
import media.platform.amf.rmqif.types.RmqMessage;
import media.platform.amf.rmqif.types.RmqMessageType;
import media.platform.amf.room.RoomInfo;
import media.platform.amf.room.RoomManager;
import media.platform.amf.session.SessionInfo;
import media.platform.amf.session.SessionManager;
import media.platform.amf.session.SessionState;
import media.platform.amf.session.SessionStateManager;
import media.platform.amf.rmqif.module.RmqData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RmqProcNegoDoneReq extends RmqIncomingMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcNegoDoneReq.class);

    @Override
    public boolean handle(RmqMessage msg) {
        if (msg == null || msg.getHeader() == null) {
            logger.error("[{}] Invalid message");
            return false;
        }

        SessionInfo sessionInfo = validateSessionId(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom());
        if (sessionInfo == null) {
            logger.error("[{}] Session not found", msg.getSessionId());
            return false;
        }

        RmqData<NegoDoneReq> data = new RmqData<>( NegoDoneReq.class);
        NegoDoneReq req = data.parse(msg);

        if (req == null) {
            logger.error("[{}] NegoDoneReq: parsing failed", msg.getSessionId());
            sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom(),
                    RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_FAILURE,
                    "PARSING FAILURE");
            return false;
        }

        logger.info("[{}] NegoDoneReq: sdp [{}]", msg.getSessionId(), req.getSdp());

        //sessionInfo.getSdpInfo();

        SdpInfo sdpInfo = null;
        SdpInfo sdpDeviceInfo = null;

        if (req.getSdp() != null) {
            sdpInfo = SdpParser.parseSdp(req.getSdp());
            sessionInfo.setSdpInfo(sdpInfo);
        }

        if (req.getDeviceSdp() != null) {
            sdpDeviceInfo = SdpParser.parseSdp(req.getDeviceSdp());
            sessionInfo.setSdpDeviceInfo(sdpDeviceInfo);
        }

        if (req.getInOutFlag() != sessionInfo.getOutbound()) {
            sessionInfo.setOutbound(req.getInOutFlag());
        }

        if (sessionInfo.getConferenceId() != null &&
                ((req.getPeerSdp() != null) || (req.getPeerDeviceSdp() != null))) {
            RoomInfo roomInfo = RoomManager.getInstance().getRoomInfo(sessionInfo.getConferenceId());

            if (roomInfo != null) {
                String peerSessionId = roomInfo.getOtherSession(sessionInfo.getSessionId());
                if (peerSessionId != null) {
                    SessionInfo peerSessionInfo = SessionManager.getInstance().getSession(peerSessionId);
                    if (peerSessionInfo != null) {
                        if (peerSessionInfo.getSdpInfo() == null && req.getPeerSdp() != null) {
                            peerSessionInfo.setSdpInfo(SdpParser.parseSdp(req.getPeerSdp()));
                        }

                        if (peerSessionInfo.getSdpInfo() != null && sdpInfo != null &&
                                peerSessionInfo.getSdpInfo().getPayloadId() != sdpInfo.getPayloadId()) {

                            if (peerSessionInfo.getSdpInfo().getPriority() < sdpInfo.getPriority()) {
                                // Change peer
                                peerSessionInfo.setSdpInfo(SdpParser.parseSdp(req.getPeerSdp(), sdpInfo.getPriority()));
                            }
                            else {
                                // Change mine
                                sessionInfo.setSdpInfo(SdpParser.parseSdp(req.getSdp(), peerSessionInfo.getSdpInfo().getPriority()));
                            }
                        }

                        if (peerSessionInfo.getSdpDeviceInfo() == null && req.getPeerDeviceSdp() != null) {
                            peerSessionInfo.setSdpDeviceInfo(SdpParser.parseSdp(req.getPeerDeviceSdp()));
                        }

                        if (peerSessionInfo.getSdpDeviceInfo() != null && sdpDeviceInfo != null &&
                                peerSessionInfo.getSdpDeviceInfo().getPayloadId() != sdpDeviceInfo.getPayloadId()) {

                            if (peerSessionInfo.getSdpDeviceInfo().getPriority() < sdpDeviceInfo.getPriority()) {
                                // Change peer
                                peerSessionInfo.setSdpDeviceInfo(SdpParser.parseSdp(req.getPeerDeviceSdp(), sdpDeviceInfo.getPriority()));
                            }
                            else {
                                // Change mine
                                sessionInfo.setSdpDeviceInfo(SdpParser.parseSdp(req.getDeviceSdp(), peerSessionInfo.getSdpDeviceInfo().getPriority()));
                            }
                        }
                    }
                }
            }

        }

        SessionStateManager.getInstance().setState(msg.getSessionId(), SessionState.PREPARE);

        sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom());

        if (AppInstance.getInstance().getUserConfig().getRedundantConfig().isActive()) {
            String json = new JsonMessage(SessionInfo.class).build(sessionInfo);
            RedundantClient.getInstance().sendMessage(RedundantMessage.RMT_SN_NEGO_DONE_REQ, json);
        }

        StatManager.getInstance().incCount(StatManager.SVC_ANSWER);

        return false;
    }

    @Override
    public void sendResponse(String sessionId, String transactionId, String queueName, int reasonCode, String reasonStr) {

        RmqProcNegoDoneRes res = new RmqProcNegoDoneRes(sessionId, transactionId);

        res.setReasonCode(reasonCode);
        res.setReasonStr(reasonStr);

        if (res.send(queueName) == false) {
            // TODO
        }
    }

}

