/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RmqProcIncomingCommandReq.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.handler;

import media.platform.amf.rmqif.handler.base.RmqIncomingMessageHandler;
import media.platform.amf.rmqif.messages.CommandStartReq;
import media.platform.amf.rmqif.messages.FileData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import media.platform.amf.rmqif.module.RmqData;
import media.platform.amf.rmqif.types.RmqMessage;
import media.platform.amf.rmqif.types.RmqMessageType;
import media.platform.amf.session.SessionInfo;
import media.platform.amf.session.SessionState;
import media.platform.amf.session.SessionStateManager;

public class RmqProcIncomingCommandReq extends RmqIncomingMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcIncomingCommandReq.class);

    @Override
    public boolean handle(RmqMessage msg) {
        if (msg == null || msg.getHeader() == null) {
            logger.error("[{}] Invalid message");
            return false;
        }

        SessionInfo sessionInfo = validateSessionId(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom());
        if (sessionInfo == null) {
            logger.error("[{}] Session not found", msg.getSessionId());
            return false;
        }

        RmqData<CommandStartReq> data = new RmqData<>( CommandStartReq.class);
        CommandStartReq req = data.parse(msg);

        if (req == null) {
            logger.error("[{}] CommandReq: parsing failed", msg.getSessionId());
            sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom(),
                    RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_FAILURE,
                    "PARSING FAILURE");
            return false;
        }

        if (req.getType() == null) {
            logger.error("[{}] CommandReq: null types", msg.getSessionId());
            sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom(),
                    RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_FAILURE,
                    "INVALID TYPE");
            return false;
        }

        FileData file = req.getData();
        if (file == null) {
            logger.error("[{}] CommandReq: no data field", msg.getSessionId());
            sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom(),
                    RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_FAILURE,
                    "NO DATA");
            return false;
        }

        logger.info("[{}] CommandReq: cmd types [{}] channel [{}] file types [{}] file [{}] def [{}] mix [{}] media [{}]",
                msg.getSessionId(), req.getType(), req.getChannel(),
                file.getPlayType(), file.getPlayFile(), file.getDefVolume(), file.getMixVolume(), file.getMediaType());

        sessionInfo.setFromQueue(msg.getHeader().getMsgFrom());

        file.setChannel(req.getChannel());

        if (req.getType().equals(CommandStartReq.CMD_TYPE_MEDIA_PLAY)) {
            SessionStateManager.getInstance().setState(msg.getSessionId(), SessionState.PLAY_START, file);

            sessionInfo.setVolumeMin(req.getData().getMixVolume());
            sessionInfo.setVolumeMax(req.getData().getDefVolume());
        }
        else if (req.getType().equals(CommandStartReq.CMD_TYPE_MEDIA_STOP)) {
            SessionStateManager.getInstance().setState(msg.getSessionId(), SessionState.PLAY_STOP, file);
        }
        else {
            logger.warn("[{}] CommandReq: Unsupported types [{}]", msg.getSessionId(), req.getType());
        }

        sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom());

        return false;
    }

    @Override
    public void sendResponse(String sessionId, String transactionId, String queueName, int reasonCode, String reasonStr) {

        RmqProcOutgoingCommandRes res = new RmqProcOutgoingCommandRes(sessionId, transactionId);

        res.setReasonCode(reasonCode);
        res.setReasonStr(reasonStr);

        if (res.send(queueName) == false) {
            // TODO
        }

    }
}
