/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RmqProcDtmfDetectReq.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.handler;

import media.platform.amf.rmqif.handler.base.RmqOutgoingMessage;
import media.platform.amf.rmqif.messages.DtmfDetectReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import media.platform.amf.rmqif.types.RmqMessageType;
import media.platform.amf.session.SessionInfo;

public class RmqProcDtmfDetectReq extends RmqOutgoingMessage {
    private static final Logger logger = LoggerFactory.getLogger(RmqProcDtmfDetectReq.class);

    public RmqProcDtmfDetectReq(String sessionId, String transactionId) {
        super(sessionId, transactionId);
        setType(RmqMessageType.RMQ_MSG_STR_DTMF_DETECT_REQ);
    }

    public void setDtmfInfo(int digit) {
        SessionInfo sessionInfo = checkAndGetSession(getSessionId());
        if (sessionInfo == null) {
            return;
        }

        DtmfDetectReq req = new DtmfDetectReq();
        req.setDtmf(digit);

        setBody(req, DtmfDetectReq.class);

    }

    /**
     * Sends a DtmfDetReq to the given queue
     * @param queueName
     * @return
     */
    public boolean send(String queueName) {
        return sendTo(queueName);
    }
}
