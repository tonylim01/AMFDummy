/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RmqProcCommandStopReq.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.handler;

import media.platform.amf.rmqif.handler.base.RmqIncomingMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import media.platform.amf.rmqif.types.RmqMessage;
import media.platform.amf.session.SessionInfo;

public class RmqProcCommandStopReq extends RmqIncomingMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcCommandStopReq.class);

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

//        RmqData<CommandStopReq> data = new RmqData<>(CommandStopReq.class);
//        CommandStopReq req = data.parse(msg);
//
//        if (req == null) {
//            logger.error("[{}] CommandStopReq: parsing failed", msg.getSessionId());
//            sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom(),
//                    RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_FAILURE,
//                    "PARSING FAILURE");
//            return false;
//        }

        logger.info("[{}] CommandStopReq:", msg.getSessionId());

        //
        // TODO
        //

        sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom());

        return false;
    }

    @Override
    public void sendResponse(String sessionId, String transactionId, String queueName, int reasonCode, String reasonStr) {

        RmqProcCommandStopRes res = new RmqProcCommandStopRes(sessionId, transactionId);

        res.setReasonCode(reasonCode);
        res.setReasonStr(reasonStr);

        if (res.send(queueName) == false) {
            // TODO
        }

    }
}
