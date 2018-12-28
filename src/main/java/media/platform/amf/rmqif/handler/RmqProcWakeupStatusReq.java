/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RmqProcServiceStartReq.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.handler;

import media.platform.amf.rmqif.handler.base.RmqIncomingMessageHandler;
import media.platform.amf.rmqif.messages.WakeupStatusReq;
import media.platform.amf.rmqif.module.RmqData;
import media.platform.amf.rmqif.types.RmqMessage;
import media.platform.amf.rmqif.types.RmqMessageType;
import media.platform.amf.session.MediaFileInfo;
import media.platform.amf.session.SessionInfo;
import media.platform.amf.session.SessionState;
import media.platform.amf.session.SessionStateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RmqProcWakeupStatusReq extends RmqIncomingMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcWakeupStatusReq.class);

    @Override
    public boolean handle(RmqMessage msg) {
        if (msg == null || msg.getHeader() == null) {
            return false;
        }

        logger.info("[{}] WakeupStatusReq", msg.getSessionId());

        SessionInfo sessionInfo = validateSessionId(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom());
        if (sessionInfo == null) {
            logger.error("[{}] Session not found", msg.getSessionId());
            return false;
        }

        RmqData<WakeupStatusReq> data = new RmqData<>(WakeupStatusReq.class);
        WakeupStatusReq req = data.parse(msg);

        if (req == null) {
            logger.error("[{}] WakeupStatusReq: parsing failed", msg.getSessionId());
            sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom(),
                    RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_FAILURE,
                    "PARSING FAILURE");
            return false;
        }

        sessionInfo.setCallerWakeupStatus(req.getCallerWakeupStatus());
        sessionInfo.setCalleeWakeupStatus(req.getCalleeWakeupStatus());

        sessionInfo.setSuccessMedia((req.getSuccess() != null) ? req.getSuccess().getMediaFileInfo() : null);
        sessionInfo.setFailureMedia((req.getFail() != null) ? req.getFail().getMediaFileInfo() : null);

        SessionStateManager.getInstance().setState(msg.getSessionId(), SessionState.START);

        sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom());

        return false;
    }

    @Override
    public void sendResponse(String sessionId, String transactionId, String queueName, int reasonCode, String reasonStr) {

        RmqProcWakeupStatusRes res = new RmqProcWakeupStatusRes(sessionId, transactionId);

        res.setReasonCode(reasonCode);
        res.setReasonStr(reasonStr);

        if (res.send(queueName) == false) {
            // TODO
        }

    }
}