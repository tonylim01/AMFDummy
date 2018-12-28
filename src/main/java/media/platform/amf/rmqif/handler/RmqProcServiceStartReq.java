/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RmqProcServiceStartReq.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.handler;

import media.platform.amf.rmqif.handler.base.RmqIncomingMessageHandler;
import media.platform.amf.rmqif.messages.ServiceStartReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import media.platform.amf.AppInstance;
import media.platform.amf.rmqif.module.RmqData;
import media.platform.amf.rmqif.types.RmqMessage;
import media.platform.amf.rmqif.types.RmqMessageType;
import media.platform.amf.session.SessionInfo;
import media.platform.amf.session.SessionState;
import media.platform.amf.session.SessionStateManager;

public class RmqProcServiceStartReq extends RmqIncomingMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcServiceStartReq.class);

    private static final String DEFAULT_AIIF_QUEUE_FMT = "aiif%d_aiifd_u";

    @Override
    public boolean handle(RmqMessage msg) {
        if (msg == null || msg.getHeader() == null) {
            return false;
        }

        logger.info("[{}] ServiceStartReq", msg.getSessionId());

        SessionInfo sessionInfo = validateSessionId(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom());
        if (sessionInfo == null) {
            logger.error("[{}] Session not found", msg.getSessionId());
            return false;
        }

        RmqData<ServiceStartReq> data = new RmqData<>( ServiceStartReq.class);
        ServiceStartReq req = data.parse(msg);

        if (req == null) {
            logger.error("[{}] ServiceStartReq: parsing failed", msg.getSessionId());
            sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom(),
                    RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_FAILURE,
                    "PARSING FAILURE");
            return false;
        }

        /*
        String aiifName = AppInstance.getInstance().getConfig().getRmqAiif(req.getAiifId());
        if (aiifName == null) {
            logger.error("[{}] ServiceStartReq: Invalid aiifId [{}]", msg.getSessionId(), req.getAiifId());
            sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom(),
                    RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_FAILURE,
                    "INVALID AIIFID");
            return false;
        }
        */
        String aiifFmt = AppInstance.getInstance().getUserConfig().getRmqAiifFmt();
        if (aiifFmt == null) {
            aiifFmt = DEFAULT_AIIF_QUEUE_FMT;
        }

        String aiifName = String.format(aiifFmt, req.getAiifId());

        sessionInfo.setAiifName(aiifName);

        SessionStateManager.getInstance().setState(msg.getSessionId(), SessionState.START);

        sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom());

        return false;
    }

    @Override
    public void sendResponse(String sessionId, String transactionId, String queueName, int reasonCode, String reasonStr) {

        RmqProcServiceStartRes res = new RmqProcServiceStartRes(sessionId, transactionId);

        res.setReasonCode(reasonCode);
        res.setReasonStr(reasonStr);

        if (res.send(queueName) == false) {
            // TODO
        }

    }
}