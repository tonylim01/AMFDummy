/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RmqProcIncomingHangupReq.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.handler;

import media.platform.amf.AppInstance;
import media.platform.amf.redundant.RedundantClient;
import media.platform.amf.redundant.RedundantMessage;
import media.platform.amf.rmqif.handler.base.RmqIncomingMessageHandler;
import media.platform.amf.rmqif.types.RmqMessage;
import media.platform.amf.session.SessionInfo;
import media.platform.amf.session.SessionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import media.platform.amf.session.SessionStateManager;

public class RmqProcIncomingHangupReq extends RmqIncomingMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcIncomingHangupReq.class);

    @Override
    public boolean handle(RmqMessage msg) {
        if (msg == null || msg.getHeader() == null) {
            return false;
        }

        logger.info("[{}] HangupReq", msg.getSessionId());

        SessionInfo sessionInfo = validateSessionId(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom());
        if (sessionInfo == null) {
            logger.error("[{}] Session not found", msg.getSessionId());
            return false;
        }

        SessionStateManager.getInstance().setState(msg.getSessionId(), SessionState.IDLE);

        sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom());

        if (AppInstance.getInstance().getConfig().getRedundantConfig().isRun()) {
            RedundantClient.getInstance().sendMessageSimple(RedundantMessage.RMT_SN_HANGUP_REQ, msg.getSessionId());
        }

        return false;
    }

    @Override
    public void sendResponse(String sessionId, String transactionId, String queueName, int reasonCode, String reasonStr) {

        RmqProcIncomingHangupRes res = new RmqProcIncomingHangupRes(sessionId, transactionId);

        res.setReasonCode(reasonCode);
        res.setReasonStr(reasonStr);

        if (res.send(queueName) == false) {
            // TODO
        }

    }
}

