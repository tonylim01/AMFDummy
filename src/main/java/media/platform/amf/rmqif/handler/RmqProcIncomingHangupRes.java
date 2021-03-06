/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RmqProcIncomingHangupRes.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.handler;

import media.platform.amf.rmqif.handler.base.RmqOutgoingMessage;
import media.platform.amf.rmqif.messages.HangupRes;
import media.platform.amf.rmqif.types.RmqMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RmqProcIncomingHangupRes extends RmqOutgoingMessage {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcIncomingHangupRes.class);

    public RmqProcIncomingHangupRes(String sessionId, String transactionId) {
        super(sessionId, transactionId);
        setType(RmqMessageType.RMQ_MSG_STR_HANGUP_RES);
    }

    public boolean send(String queueName) {

        HangupRes res = new HangupRes();

        res.setConferenceId(null);  // TODO
        res.setParticipantCount(0); // TODO

        setBody(res, HangupRes.class);

        return sendTo(queueName);
    }
}
