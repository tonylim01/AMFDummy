/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RmqProcNegoDoneRes.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.handler;

import media.platform.amf.rmqif.handler.base.RmqOutgoingMessage;
import media.platform.amf.rmqif.messages.NegoDoneRes;
import media.platform.amf.rmqif.types.RmqMessageType;
import media.platform.amf.session.SessionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RmqProcNegoDoneRes extends RmqOutgoingMessage {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcNegoDoneRes.class);

    public RmqProcNegoDoneRes(String sessionId, String transactionId) {
        super(sessionId, transactionId);
        setType(RmqMessageType.RMQ_MSG_STR_NEGO_DONE_RES);
    }

    public boolean send(String queueName) {

        SessionInfo sessionInfo = checkAndGetSession(getSessionId());
        if (sessionInfo == null) {
            return sendTo(queueName);
        }

        NegoDoneRes res = new NegoDoneRes();
        res.setInOutFlag(sessionInfo.getOutbound());

        setBody(res, NegoDoneRes.class);

        return sendTo(queueName);
    }
}
