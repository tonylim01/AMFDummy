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
import media.platform.amf.redundant.RedundantClient;
import media.platform.amf.redundant.RedundantMessage;
import media.platform.amf.rmqif.handler.base.RmqIncomingMessageHandler;
import media.platform.amf.rmqif.messages.NegoDoneReq;
import media.platform.amf.rmqif.types.RmqMessage;
import media.platform.amf.rmqif.types.RmqMessageType;
import media.platform.amf.session.SessionInfo;
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

        if (req.getSdp() != null) {
            SdpInfo sdpInfo = SdpParser.parseSdp( req.getSdp());

            sessionInfo.setSdpInfo(sdpInfo);
        }

        if (req.getDeviceSdp() != null) {
            SdpInfo sdpDeviceInfo = SdpParser.parseSdp(req.getDeviceSdp());
            sessionInfo.setSdpDeviceInfo(sdpDeviceInfo);
        }

        SessionStateManager.getInstance().setState(msg.getSessionId(), SessionState.PREPARE);

        sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom());

        if (AppInstance.getInstance().getConfig().getRedundantConfig().isRun()) {
            String json = new JsonMessage(SessionInfo.class).build(sessionInfo);
            RedundantClient.getInstance().sendMessage(RedundantMessage.RMT_SN_NEGO_DONE_REQ, json);
        }

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

