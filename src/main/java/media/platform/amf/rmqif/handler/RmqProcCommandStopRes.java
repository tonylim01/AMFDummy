/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RmqProcCommandStopRes.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.handler;

import media.platform.amf.rmqif.handler.base.RmqOutgoingMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import media.platform.amf.rmqif.types.RmqMessageType;

public class RmqProcCommandStopRes extends RmqOutgoingMessage {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcCommandStopRes.class);

    public RmqProcCommandStopRes(String sessionId, String transactionId) {
        super(sessionId, transactionId);
        setType(RmqMessageType.RMQ_MSG_STR_COMMAND_STOP_RES);
    }

    public boolean send(String queueName) {
        return sendTo(queueName);
    }

}
