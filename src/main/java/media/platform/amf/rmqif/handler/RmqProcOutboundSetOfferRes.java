/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RmqProcOutboundSetOfferRes.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.handler;

import media.platform.amf.rmqif.handler.base.RmqOutgoingMessage;
import media.platform.amf.rmqif.types.RmqMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RmqProcOutboundSetOfferRes extends RmqOutgoingMessage {

    private static final Logger logger = LoggerFactory.getLogger( RmqProcOutboundSetOfferRes.class);

    public RmqProcOutboundSetOfferRes(String sessionId, String transactionId) {
        super(sessionId, transactionId);
        setType( RmqMessageType.RMQ_MSG_STR_OUTBOUND_GET_OFFER_RES);
    }

    public boolean send(String queueName) {
        return sendTo(queueName);
    }
}
