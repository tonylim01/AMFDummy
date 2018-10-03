/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RmqIncomingMessageHandler.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.handler.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import media.platform.amf.rmqif.types.RmqMessageType;
import media.platform.amf.session.SessionInfo;
import media.platform.amf.session.SessionManager;

public abstract class RmqIncomingMessageHandler implements RmqIncomingMessageInterface {

    private static final Logger logger = LoggerFactory.getLogger(RmqIncomingMessageHandler.class);

    /**
     * Calls a sendResponse() with the default queueName
     * @param sessionId
     * @param transactionId
     */
    @Override
    public void sendResponse(String sessionId, String transactionId) {
        sendResponse(sessionId, transactionId, null, RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_SUCCESS, null);
    }

    /**
     * Pre-implements to send a response as a successs
     * @param sessionId
     * @param transactionId
     */
    @Override
    public void sendResponse(String sessionId, String transactionId, String queueName) {
        sendResponse(sessionId, transactionId, queueName, RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_SUCCESS, null);
    }

    /**
     * Calls a sendResponse() with the default queueName
     * @param sessionId
     * @param transactionId
     * @param reasonCode
     * @param reasonStr
     */
    @Override
    public void sendResponse(String sessionId, String transactionId, int reasonCode, String reasonStr) {
        sendResponse(sessionId, transactionId, null, reasonCode, reasonStr);
    }

    /**
     * Checks whether sessionId is valid or not.
     * If invalid, sends an response back after setting the proper reason
     * @param sessionId
     * @param transactionId
     * @param msgFrom
     * @return
     */
    protected SessionInfo validateSessionId(String sessionId, String transactionId, String msgFrom) {
        if (sessionId == null) {
            logger.error("[{}] No sessionId found");
            sendResponse(sessionId, transactionId, msgFrom,
                    RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_WRONG_PARAM,
                    "NO SESSION ID");
            return null;
        }

        SessionInfo sessionInfo = SessionManager.findSession(sessionId);
        if (sessionInfo == null) {
            logger.error("[{}] No sessionInfo found", sessionId);
            sendResponse(sessionId, transactionId, msgFrom,
                    RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_FAILURE,
                    "NO SESSION");
            return null;
        }

        return sessionInfo;
    }
}
