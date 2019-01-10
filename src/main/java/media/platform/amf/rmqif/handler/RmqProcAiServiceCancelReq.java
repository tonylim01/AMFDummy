/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RmqProcAiServiceRes.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.handler;

import media.platform.amf.common.AppId;
import media.platform.amf.engine.handler.EngineProcAudioBranchReq;
import media.platform.amf.rmqif.handler.base.RmqIncomingMessageHandler;
import media.platform.amf.rmqif.messages.AiServiceCancelReq;
import media.platform.amf.rmqif.messages.AiServiceRes;
import media.platform.amf.rmqif.module.RmqData;
import media.platform.amf.rmqif.types.RmqMessage;
import media.platform.amf.room.RoomInfo;
import media.platform.amf.room.RoomManager;
import media.platform.amf.session.SessionInfo;
import media.platform.amf.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RmqProcAiServiceCancelReq extends RmqIncomingMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcAiServiceCancelReq.class);

    @Override
    public boolean handle(RmqMessage msg) {
        if (msg == null || msg.getHeader() == null) {
            return false;
        }

        logger.info("[{}] AiServiceCancelReq", msg.getSessionId());

        SessionInfo sessionInfo = validateSessionId(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom());
        if (sessionInfo == null) {
            logger.error("[{}] Session not found", msg.getSessionId());
            return false;
        }

        RmqData<AiServiceCancelReq> data = new RmqData<>(AiServiceCancelReq.class);
        AiServiceCancelReq req = data.parse(msg);
        
        SessionInfo reqSessionInfo = null;

        if (req.getDir() == 1) {
            if (sessionInfo.isCaller()) {
                reqSessionInfo = sessionInfo;
            }
            else {
                SessionInfo otherSessionInfo = SessionManager.findOtherSession(sessionInfo);
                if (otherSessionInfo != null) {
                    reqSessionInfo = otherSessionInfo;
                }
            }
        }
        else {
            if (!sessionInfo.isCaller()) {
                reqSessionInfo = sessionInfo;
            }
            else {
                SessionInfo otherSessionInfo = SessionManager.findOtherSession(sessionInfo);
                if (otherSessionInfo != null) {
                    reqSessionInfo = otherSessionInfo;
                }
            }
        }

        if (reqSessionInfo != null) {

            EngineProcAudioBranchReq branchReq = new EngineProcAudioBranchReq(AppId.newId());
            branchReq.setData(reqSessionInfo, true);
            branchReq.send();
        }

        return false;
    }

    @Override
    public void sendResponse(String sessionId, String transactionId, String queueName, int reasonCode, String reasonStr) {

    }
}

