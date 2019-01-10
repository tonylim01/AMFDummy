/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RmqProcServiceStartRes.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.handler;

import media.platform.amf.rmqif.handler.base.RmqOutgoingMessage;
import media.platform.amf.rmqif.messages.InboundGetAnswerRes;
import media.platform.amf.rmqif.messages.WakeupStatusRes;
import media.platform.amf.rmqif.types.RmqMessageType;
import media.platform.amf.session.SessionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RmqProcWakeupStatusRes extends RmqOutgoingMessage {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcWakeupStatusRes.class);

    public RmqProcWakeupStatusRes(String sessionId, String transactionId) {
        super(sessionId, transactionId);
        setType(RmqMessageType.RMQ_MSG_STR_WAKEUP_STATUS_RES);
    }

    public boolean send(String queueName, boolean callerStatus, boolean calleeStatus) {
        SessionInfo sessionInfo = checkAndGetSession(getSessionId());
        if (sessionInfo == null) {
            return sendTo(queueName);
        }

        WakeupStatusRes res = new WakeupStatusRes();

        //
        // TODO
        //
        res.setCallerWakeupStatus(callerStatus ? 1 : 0);
        res.setCalleeWakeupStatus(calleeStatus ? 1 : 0);

        setBody(res, WakeupStatusRes.class);

        return sendTo(queueName);
    }
}
