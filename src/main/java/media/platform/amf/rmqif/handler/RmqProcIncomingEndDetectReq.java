/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RmqProcIncomingHangupReq.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.handler;

import media.platform.amf.AppInstance;
import media.platform.amf.common.AppId;
import media.platform.amf.engine.handler.EngineProcAudioBranchReq;
import media.platform.amf.redundant.RedundantClient;
import media.platform.amf.redundant.RedundantMessage;
import media.platform.amf.rmqif.handler.base.RmqIncomingMessageHandler;
import media.platform.amf.rmqif.types.RmqMessage;
import media.platform.amf.session.SessionInfo;
import media.platform.amf.session.SessionState;
import media.platform.amf.session.SessionStateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RmqProcIncomingEndDetectReq extends RmqIncomingMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcIncomingEndDetectReq.class);

    @Override
    public boolean handle(RmqMessage msg) {
        if (msg == null || msg.getHeader() == null) {
            return false;
        }

        logger.info("[{}] EndDetectReq", msg.getSessionId());

        SessionInfo sessionInfo = validateSessionId(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom());
        if (sessionInfo == null) {
            logger.error("[{}] Session not found", msg.getSessionId());
            return false;
        }

        if (AppInstance.getInstance().getUserConfig().getRedundantConfig().isActive()) {
            RedundantClient.getInstance().sendMessageSimple(RedundantMessage.RMT_SN_END_DETECT_REQ, msg.getSessionId());
        }

        EngineProcAudioBranchReq branchReq = new EngineProcAudioBranchReq(AppId.newId());
        branchReq.setData(sessionInfo, true);
        branchReq.send();


        return false;
    }

    @Override
    public void sendResponse(String sessionId, String transactionId, String queueName, int reasonCode, String reasonStr) {

    }
}

