/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RmqProcLogInRes.java
 * @author Tony Lim
 *
 */


package media.platform.amf.rmqif.handler;

import media.platform.amf.rmqif.handler.base.RmqOutgoingMessage;
import media.platform.amf.rmqif.types.RmqMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RmqProcLogInRes extends RmqOutgoingMessage {

    private static final Logger logger = LoggerFactory.getLogger( RmqProcLogInRes.class);

    public RmqProcLogInRes(String sessionId, String transactionId) {
        super(sessionId, transactionId);
        setType( RmqMessageType.RMQ_MSG_STR_LOGIN_RES);
    }

    public boolean send(String queueName) {
        return sendTo(queueName);
    }
}
