/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RmqProcAiServiceRes.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.handler;

import media.platform.amf.common.AppId;
import media.platform.amf.engine.EngineClient;
import media.platform.amf.engine.handler.EngineProcAudioBranchReq;
import media.platform.amf.engine.messages.AudioBranchReq;
import media.platform.amf.oam.StatManager;
import media.platform.amf.rmqif.handler.base.RmqIncomingMessageHandler;
import media.platform.amf.rmqif.messages.AiServiceRes;
import media.platform.amf.rmqif.module.RmqData;
import media.platform.amf.rmqif.types.RmqMessage;
import media.platform.amf.session.SessionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RmqProcAiServiceRes extends RmqIncomingMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcAiServiceRes.class);

    @Override
    public boolean handle(RmqMessage msg) {
        if (msg == null || msg.getHeader() == null) {
            return false;
        }

        logger.info("[{}] AiServiceRes", msg.getSessionId());

        SessionInfo sessionInfo = validateSessionId(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom());
        if (sessionInfo == null) {
            logger.error("[{}] Session not found", msg.getSessionId());
            return false;
        }

        RmqData<AiServiceRes> data = new RmqData<>(AiServiceRes.class);
        AiServiceRes res = data.parse(msg);

        sessionInfo.setAiifIp(res.getIp());
        sessionInfo.setAiifPort(res.getPort());

        StatManager.getInstance().incCount(StatManager.SVC_AI_RES);

        String appId = AppId.newId();

        EngineProcAudioBranchReq branchReq = new EngineProcAudioBranchReq(appId);
        branchReq.setData(sessionInfo, false);

        EngineClient.getInstance().pushSentQueue(appId, AudioBranchReq.class, branchReq.getData());
        if (sessionInfo.getSessionId() != null) {
            AppId.getInstance().push(appId, sessionInfo.getSessionId());
        }

        if (!branchReq.send()) {
            // ERROR
//            EngineClient.getInstance().removeSentQueue(appId);
        }

        return false;
    }

    @Override
    public void sendResponse(String sessionId, String transactionId, String queueName, int reasonCode, String reasonStr) {

    }
}

