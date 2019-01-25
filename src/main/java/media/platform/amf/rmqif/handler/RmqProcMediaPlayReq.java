/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RmqProcMediaPlayReq.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.handler;

import media.platform.amf.AppInstance;
import media.platform.amf.common.AppId;
import media.platform.amf.config.PromptConfig;
import media.platform.amf.engine.EngineClient;
import media.platform.amf.engine.handler.EngineProcFilePlayReq;
import media.platform.amf.engine.messages.FilePlayReq;
import media.platform.amf.oam.StatManager;
import media.platform.amf.rmqif.handler.base.RmqIncomingMessageHandler;
import media.platform.amf.rmqif.messages.MediaPlayReq;
import media.platform.amf.rmqif.module.RmqData;
import media.platform.amf.rmqif.types.RmqMessage;
import media.platform.amf.rmqif.types.RmqMessageType;
import media.platform.amf.room.RoomInfo;
import media.platform.amf.room.RoomManager;
import media.platform.amf.session.SessionInfo;
import media.platform.amf.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RmqProcMediaPlayReq extends RmqIncomingMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcMediaPlayReq.class);

    @Override
    public boolean handle(RmqMessage msg) {
        if (msg == null || msg.getHeader() == null) {
            return false;
        }

        logger.info("[{}] MediaPlayReq", msg.getSessionId());

        SessionInfo sessionInfo = validateSessionId(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom());
        if (sessionInfo == null) {
            logger.error("[{}] Session not found", msg.getSessionId());
            return false;
        }

        RmqData<MediaPlayReq> data = new RmqData<>(MediaPlayReq.class);
        MediaPlayReq req = data.parse(msg);

        if (req == null) {
            logger.error("[{}] MediaPlayReq: parsing failed", msg.getSessionId());
            sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom(),
                    RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_FAILURE,
                    "PARSING FAILURE");
            return false;
        }

        logger.debug("[{}] MediaPlayReq: dir [{}] channelId [{}] mediaType [{}] defVol [{}] minVol [{}] media [{}]", sessionInfo.getSessionId(),
                req.getDir(), req.getMentOrMusic(), req.getMediaType(), req.getDefVolume(), req.getMinVolume(), req.getMediaUrl());

        sessionInfo.setMediaDir(req.getDir());

//        setData(SessionInfo sessionInfo, int toolId, int mediaType, int[] dstIds, boolean hasContainer, String[] filenames, int defVolume, int lowVolume)

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
        if (req.getDir() == MediaPlayReq.DIR_BOTH) {
            dstIds = new int[2];
            dstIds[0] = sessionInfo.getEngineToolId();
            dstIds[1] = otherToolId;
        }
        else if (req.getDir() == MediaPlayReq.DIR_CALLER) {
            dstIds = new int[1];
            dstIds[0] = sessionInfo.isCaller() ? sessionInfo.getEngineToolId() : otherToolId;
        }
        else if (req.getDir() == MediaPlayReq.DIR_CALLEE) {
            dstIds = new int[1];
            dstIds[0] = sessionInfo.isCaller() ? otherToolId : sessionInfo.getEngineToolId();
        }
        else {
            logger.error("[{}] Undefined dir [{}]", sessionInfo.getSessionId(), req.getDir());
            return false;
        }

        boolean hasContainer = false;
        String filename = req.getMediaUrl();
        if (filename != null) {
            if (!filename.startsWith("/") && !filename.startsWith("http")) {
                PromptConfig promptConfig = AppInstance.getInstance().getPromptConfig();
                if (promptConfig != null) {
                    filename = promptConfig.getPromptDir() + filename;
                }
            }

            if (filename.endsWith("wav")) {
                hasContainer = true;
            }
        }

        String [] filenames = new String[1];
        filenames[0] = filename;

        String appId = AppId.newId();

        EngineProcFilePlayReq filePlayReq = new EngineProcFilePlayReq(appId);
        filePlayReq.setData(sessionInfo,
                mixerId,
                (req.getMentOrMusic() == MediaPlayReq.MEDIA_MENT) ? 0 : 1,
                dstIds, hasContainer, filenames, req.getDefVolume(), req.getMinVolume());

        if (filePlayReq.send()) {
            EngineClient.getInstance().pushSentQueue(appId, FilePlayReq.class, filePlayReq.getData());
            if (sessionInfo.getSessionId() != null) {
                AppId.getInstance().push(appId, sessionInfo.getSessionId());
            }
        }

        sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom());

        StatManager.getInstance().incCount(StatManager.SVC_PLAY_REQ);

        return false;
    }

    @Override
    public void sendResponse(String sessionId, String transactionId, String queueName, int reasonCode, String reasonStr) {

        RmqProcMediaPlayRes res = new RmqProcMediaPlayRes(sessionId, transactionId);

        res.setReasonCode(reasonCode);
        res.setReasonStr(reasonStr);

        if (res.send(queueName) == false) {
            // TODO
        }

        StatManager.getInstance().incCount(StatManager.SVC_PLAY_RES);

    }
}