/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RmqProcInboundGetAnswerRes.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.handler;

import media.platform.amf.rmqif.handler.base.RmqOutgoingMessage;
import media.platform.amf.rmqif.messages.AiServiceReq;
import media.platform.amf.rmqif.types.RmqMessageType;
import media.platform.amf.session.SessionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RmqProcAiServiceReq extends RmqOutgoingMessage {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcAiServiceReq.class);

    public RmqProcAiServiceReq(String sessionId, String transactionId) {
        super(sessionId, transactionId);
        setType(RmqMessageType.RMQ_MSG_STR_AI_SERVICE_REQ);
    }

    /**
     * Makes a response body and sends the message to AWF
     * @return
     */
    public boolean send(String queueName, int dir) {

        SessionInfo sessionInfo = checkAndGetSession(getSessionId());
        if (sessionInfo == null) {
            return sendTo(queueName);
        }

        AiServiceReq req = new AiServiceReq();
        req.setDir(dir);

        setBody(req, AiServiceReq.class);

        return sendTo(queueName);
    }

}
