/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RmqProcMediaPlayRes.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.handler;

import media.platform.amf.rmqif.handler.base.RmqOutgoingMessage;
import media.platform.amf.rmqif.types.RmqMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RmqProcMediaPlayRes extends RmqOutgoingMessage {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcMediaPlayRes.class);

    public RmqProcMediaPlayRes(String sessionId, String transactionId) {
        super(sessionId, transactionId);
        setType(RmqMessageType.RMQ_MSG_STR_MEDIA_PLAY_RES);
    }

    public boolean send(String queueName) {
        return sendTo(queueName);
    }
}
