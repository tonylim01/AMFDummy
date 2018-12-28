/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RmqProcOutgoingHangupReq.java
 * @author Tony Lim
 *
 */


package media.platform.amf.rmqif.handler;

import media.platform.amf.config.AmfConfig;
import media.platform.amf.config.UserConfig;
import media.platform.amf.redundant.RedundantClient;
import media.platform.amf.redundant.RedundantMessage;
import media.platform.amf.rmqif.handler.base.RmqOutgoingMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import media.platform.amf.AppInstance;
import media.platform.amf.rmqif.types.RmqMessageType;
import media.platform.amf.session.SessionInfo;
import media.platform.amf.session.SessionState;
import media.platform.amf.session.SessionStateManager;

public class RmqProcOutgoingHangupReq extends RmqOutgoingMessage {
    private static final Logger logger = LoggerFactory.getLogger(RmqProcOutgoingHangupReq.class);

    public RmqProcOutgoingHangupReq(String sessionId, String transactionId) {
        super(sessionId, transactionId);
        setType(RmqMessageType.RMQ_MSG_STR_HANGUP_REQ);
    }

    /**
     * Sends a HangupReq to the given queue
     * @param queueName
     * @return
     */
    public boolean send(String queueName) {

        SessionInfo sessionInfo = checkAndGetSession(getSessionId());
        if (sessionInfo == null) {
            return sendTo(queueName);
        }

        boolean result = sendTo(queueName);
        if (result) {
            SessionStateManager.getInstance().setState(getSessionId(), SessionState.RELEASE);
        }

        return result;
    }

    /**
     * Sends a message to MCUD
     * @return
     */
    public boolean sendToMcud() {
        UserConfig config = AppInstance.getInstance().getUserConfig();
        if (config == null) {
            logger.error("[{}] Null config", getSessionId());
            return false;
        }

        return sendTo(config.getMcudName());
    }
}
