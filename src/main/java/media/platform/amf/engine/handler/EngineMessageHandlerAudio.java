package media.platform.amf.engine.handler;

import media.platform.amf.common.AppId;
import media.platform.amf.engine.types.EngineMessageType;
import media.platform.amf.engine.types.EngineReportMessage;
import media.platform.amf.engine.types.EngineResponseMessage;
import media.platform.amf.rmqif.handler.RmqProcOutgoingEndDetectReq;
import media.platform.amf.room.RoomInfo;
import media.platform.amf.room.RoomManager;
import media.platform.amf.session.SessionInfo;
import media.platform.amf.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EngineMessageHandlerAudio extends DefaultEngineMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(EngineMessageHandlerAudio.class);

    public void handle(EngineResponseMessage msg) {
        if (msg == null || msg.getHeader() == null) {
            logger.warn("Null response message");
            return;
        }

        if (compareString(msg.getHeader().getCmd(), EngineMessageType.MSG_CMD_CREATE)) {
            procAudioCreateRes(msg);
        }
        else if (compareString(msg.getHeader().getCmd(), EngineMessageType.MSG_CMD_DELETE)) {
            procAudioDeleteRes(msg);
        }
        else if (compareString(msg.getHeader().getCmd(), EngineMessageType.MSG_CMD_BRANCH)) {
            procAudioBranchRes(msg);
        }
        else {
            logger.warn("Unsupported cmd [{}]", msg.getHeader().getCmd());
        }

    }

    public void handle(EngineReportMessage msg) {
        if (msg == null || msg.getHeader() == null) {
            logger.warn("Null response message");
            return;
        }

        if (compareString(msg.getHeader().getCmd(), EngineMessageType.MSG_CMD_BRANCH)) {
            procAudioBranchRpt(msg);
        }
        else {
            logger.warn("Unsupported cmd [{}]", msg.getHeader().getCmd());
        }
    }

    private void procAudioCreateRes(EngineResponseMessage msg) {
        if (msg == null || msg.getHeader() == null) {
            logger.warn("Null response message");
            return;
        }

        if (compareString(msg.getHeader().getResult(), EngineMessageType.MSG_RESULT_OK) ||
                compareString(msg.getHeader().getResult(), EngineMessageType.MSG_RESULT_SUCCESS)) {
            // Success
            if (msg.getHeader().getAppId() == null) {
                logger.warn("Null appId in response message");
                return;
            }

            String sessionId = AppId.getInstance().get(msg.getHeader().getAppId());
            if (sessionId == null) {
                logger.warn("No sessionId for appId=[{}]", msg.getHeader().getAppId());
                return;
            }

            SessionInfo sessionInfo = SessionManager.getInstance().getSession(sessionId);
            if (sessionInfo == null) {
                logger.warn("Cannot find session for appId=[{}]", msg.getHeader().getAppId());
                return;
            }

            sessionInfo.setAudioCreated(true);

            //
            // TODO
            //
        }
        else {
            logger.warn("Undefined result [{}]", msg.getHeader().getResult());
        }

    }

    private void procAudioDeleteRes(EngineResponseMessage msg) {
        if (msg == null || msg.getHeader() == null) {
            logger.warn("Null response message");
            return;
        }

        if (compareString(msg.getHeader().getResult(), EngineMessageType.MSG_RESULT_OK) ||
            compareString(msg.getHeader().getResult(), EngineMessageType.MSG_RESULT_SUCCESS)) {
            // Success
            if (msg.getHeader().getAppId() == null) {
                logger.warn("Null appId in response message");
                return;
            }

            String sessionId = AppId.getInstance().get(msg.getHeader().getAppId());
            if (sessionId == null) {
                logger.warn("No sessionId for appId=[{}]", msg.getHeader().getAppId());
                return;
            }

            SessionInfo sessionInfo = SessionManager.getInstance().getSession(sessionId);
            if (sessionInfo == null) {
                logger.warn("Cannot find session for appId=[{}]", msg.getHeader().getAppId());
                return;
            }

            sessionInfo.setAudioCreated(false);
        }
        else {
            logger.warn("Undefined result [{}]", msg.getHeader().getResult());
        }

    }

    private void procAudioBranchRes(EngineResponseMessage msg) {
        if (msg == null || msg.getHeader() == null) {
            logger.warn("Null response message");
            return;
        }

        if (compareString(msg.getHeader().getResult(), EngineMessageType.MSG_RESULT_OK) ||
                compareString(msg.getHeader().getResult(), EngineMessageType.MSG_RESULT_SUCCESS)) {
            // Success
            if (msg.getHeader().getAppId() == null) {
                logger.warn("Null appId in response message");
                return;
            }

            String sessionId = AppId.getInstance().get(msg.getHeader().getAppId());
            if (sessionId == null) {
                logger.warn("No sessionId for appId=[{}]", msg.getHeader().getAppId());
                return;
            }

            SessionInfo sessionInfo = SessionManager.getInstance().getSession(sessionId);
            if (sessionInfo == null) {
                logger.warn("Cannot find session for appId=[{}]", msg.getHeader().getAppId());
                return;
            }

            //
            // TODO
            //
            // Nothing to do:w


        }
        else {
            logger.warn("Undefined result [{}]", msg.getHeader().getResult());
        }

    }

    private void procAudioBranchRpt(EngineReportMessage msg) {
        if (msg == null || msg.getHeader() == null) {
            logger.warn("Null response message");
            return;
        }

        if (msg.getHeader().getAppId() == null) {
            logger.warn("Null appId in response message");
            return;
        }

        String sessionId = AppId.getInstance().get(msg.getHeader().getAppId());
        if (sessionId == null) {
            logger.warn("No sessionId for appId=[{}]", msg.getHeader().getAppId());
            return;
        }

        SessionInfo sessionInfo = SessionManager.getInstance().getSession(sessionId);
        if (sessionInfo == null) {
            logger.warn("Cannot find session for appId=[{}]", msg.getHeader().getAppId());
            return;
        }

        if (sessionInfo.getConferenceId() != null) {
            RoomInfo roomInfo = RoomManager.getInstance().getRoomInfo(sessionInfo.getConferenceId());
            if (roomInfo != null && roomInfo.getAwfQueueName() != null) {

                RmqProcOutgoingEndDetectReq req = new RmqProcOutgoingEndDetectReq(sessionInfo.getSessionId(), AppId.newId());
                req.send(roomInfo.getAwfQueueName(), sessionInfo.isCaller() ? 1 : 2);
            }
            else {
                logger.warn("[{}] Invalid roomInfo. cnfId [{}] awf [{}]", sessionInfo.getSessionId(),
                        sessionInfo.getConferenceId(), (roomInfo != null) ? roomInfo.getAwfQueueName() : "no room");
            }
        }
    }
}
