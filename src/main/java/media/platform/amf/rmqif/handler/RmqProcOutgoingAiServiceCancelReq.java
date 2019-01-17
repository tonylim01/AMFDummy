/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RmqProcOutgoingEndDetectReq.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.handler;

import media.platform.amf.rmqif.handler.base.RmqOutgoingMessage;
import media.platform.amf.rmqif.messages.AiServiceCancelReq;
import media.platform.amf.rmqif.messages.EndDetectReq;
import media.platform.amf.rmqif.types.RmqMessageType;
import media.platform.amf.session.SessionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RmqProcOutgoingAiServiceCancelReq extends RmqOutgoingMessage {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcOutgoingAiServiceCancelReq.class);

    public RmqProcOutgoingAiServiceCancelReq(String sessionId, String transactionId) {
        super(sessionId, transactionId);
        setType(RmqMessageType.RMQ_MSG_STR_AI_SERVICE_CANCEL_REQ);
    }

    /**
     * Makes a body and sends the message to AWF
     * @return
     */
    public boolean send(String queueName, int dir) {

        SessionInfo sessionInfo = checkAndGetSession(getSessionId());
        if (sessionInfo == null) {
            return sendTo(queueName);
        }

        AiServiceCancelReq req = new AiServiceCancelReq();
        req.setDir(dir);

        setBody(req, AiServiceCancelReq.class);

        return sendTo(queueName);
    }

}
