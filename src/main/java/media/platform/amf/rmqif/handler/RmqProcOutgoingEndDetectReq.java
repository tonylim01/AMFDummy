/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RmqProcOutgoingEndDetectReq.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.handler;

import media.platform.amf.rmqif.handler.base.RmqOutgoingMessage;
import media.platform.amf.rmqif.messages.EndDetectReq;
import media.platform.amf.rmqif.messages.MediaPlayDoneReq;
import media.platform.amf.rmqif.types.RmqMessageType;
import media.platform.amf.session.SessionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RmqProcOutgoingEndDetectReq extends RmqOutgoingMessage {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcOutgoingEndDetectReq.class);

    public RmqProcOutgoingEndDetectReq(String sessionId, String transactionId) {
        super(sessionId, transactionId);
        setType(RmqMessageType.RMQ_MSG_STR_END_DETECT_REQ);
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

        EndDetectReq req = new EndDetectReq();
        req.setDir(dir);

        setBody(req, EndDetectReq.class);

        return sendTo(queueName);
    }

}
