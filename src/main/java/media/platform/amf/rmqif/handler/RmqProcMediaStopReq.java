/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RmqProcMediaPlayReq.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.handler;

import media.platform.amf.common.AppId;
import media.platform.amf.engine.EngineClient;
import media.platform.amf.engine.handler.EngineProcFilePlayReq;
import media.platform.amf.engine.handler.EngineProcFileStopReq;
import media.platform.amf.engine.messages.FilePlayReq;
import media.platform.amf.engine.messages.FileStopReq;
import media.platform.amf.oam.StatManager;
import media.platform.amf.rmqif.handler.base.RmqIncomingMessageHandler;
import media.platform.amf.rmqif.messages.MediaPlayReq;
import media.platform.amf.rmqif.messages.MediaStopReq;
import media.platform.amf.rmqif.module.RmqData;
import media.platform.amf.rmqif.types.RmqMessage;
import media.platform.amf.rmqif.types.RmqMessageType;
import media.platform.amf.room.RoomInfo;
import media.platform.amf.room.RoomManager;
import media.platform.amf.session.SessionInfo;
import media.platform.amf.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RmqProcMediaStopReq extends RmqIncomingMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcMediaStopReq.class);

    @Override
    public boolean handle(RmqMessage msg) {
        if (msg == null || msg.getHeader() == null) {
            return false;
        }

        logger.info("[{}] MediaStopReq", msg.getSessionId());

        SessionInfo sessionInfo = validateSessionId(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom());
        if (sessionInfo == null) {
            logger.error("[{}] Session not found", msg.getSessionId());
            return false;
        }

        RmqData<MediaStopReq> data = new RmqData<>(MediaStopReq.class);
        MediaStopReq req = data.parse(msg);

        if (req == null) {
            logger.error("[{}] MediaStopReq: parsing failed", msg.getSessionId());
            sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom(),
                    RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_FAILURE,
                    "PARSING FAILURE");
            return false;
        }

        logger.debug("[{}] MediaStopReq: channelId [{}]", sessionInfo.getSessionId(), req.getMentOrMusic());

        int dir = sessionInfo.getMediaDir();

        int otherToolId = -1;
        int mixerId = -1;
        RoomInfo roomInfo = RoomManager.getInstance().getRoomInfo(sessionInfo.getConferenceId());
        if (roomInfo != null) {
            mixerId = roomInfo.getMixerId();
            String otherSessionId = roomInfo.getOtherSession(sessionInfo.getSessionId());
            if (otherSessionId != null) {
                SessionInfo otherSession = SessionManager.findSession(otherSessionId);
                if (otherSession != null) {
                    otherToolId = otherSession.getEngineToolId();
                }
            }
        }
        else {
            mixerId = sessionInfo.getMixerToolId();
        }

        int [] dstIds =  null;
        if (dir == MediaPlayReq.DIR_BOTH) {
            dstIds = new int[2];
            dstIds[0] = sessionInfo.getEngineToolId();
            dstIds[1] = otherToolId;
        }
        else if (dir == MediaPlayReq.DIR_CALLER) {
            dstIds = new int[1];
            dstIds[0] = sessionInfo.isCaller() ? sessionInfo.getEngineToolId() : otherToolId;
        }
        else if (dir == MediaPlayReq.DIR_CALLEE) {
            dstIds = new int[1];
            dstIds[0] = sessionInfo.isCaller() ? otherToolId : sessionInfo.getEngineToolId();
        }
        else {
            logger.error("[{}] Undefined dir [{}]", sessionInfo.getSessionId(), dir);
            return false;
        }

//        String appId = AppId.newId();

        EngineProcFileStopReq fileStopReq = new EngineProcFileStopReq(msg.getHeader().getTransactionId());
        fileStopReq.setData(sessionInfo,
                mixerId,
                (req.getMentOrMusic() == MediaPlayReq.MEDIA_MENT) ? 0 : 1,
                dstIds);

        if (fileStopReq.send()) {
            EngineClient.getInstance().pushSentQueue(msg.getHeader().getTransactionId(), FileStopReq.class, fileStopReq.getData());
            if (sessionInfo.getSessionId() != null) {
                AppId.getInstance().push(msg.getHeader().getTransactionId(), sessionInfo.getSessionId());
            }
        }

//        sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom());

        StatManager.getInstance().incCount(StatManager.SVC_PLAY_STOP_REQ);

        return false;
    }

    @Override
    public void sendResponse(String sessionId, String transactionId, String queueName, int reasonCode, String reasonStr) {

        RmqProcMediaStopRes res = new RmqProcMediaStopRes(sessionId, transactionId);

        res.setReasonCode(reasonCode);
        res.setReasonStr(reasonStr);

        if (res.send(queueName) == false) {
            // TODO
        }

        StatManager.getInstance().incCount(StatManager.SVC_PLAY_STOP_RES);

    }
}