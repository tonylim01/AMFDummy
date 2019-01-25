package media.platform.amf.engine.handler;

import media.platform.amf.common.AppId;
import media.platform.amf.common.AppUtil;
import media.platform.amf.engine.EngineClient;
import media.platform.amf.engine.EngineServiceManager;
import media.platform.amf.engine.messages.WakeupStartReq;
import media.platform.amf.engine.types.EngineMessageType;
import media.platform.amf.engine.types.EngineReportMessage;
import media.platform.amf.engine.types.EngineResponseMessage;
import media.platform.amf.oam.StatManager;
import media.platform.amf.rmqif.handler.RmqProcOutgoingAiServiceCancelReq;
import media.platform.amf.rmqif.handler.RmqProcOutgoingEndDetectReq;
import media.platform.amf.room.RoomInfo;
import media.platform.amf.room.RoomManager;
import media.platform.amf.session.SessionInfo;
import media.platform.amf.session.SessionManager;
import media.platform.amf.session.SessionState;
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
            logger.debug("[{}] Audio created", sessionId);

            //
            // TODO
            //
            SessionInfo otherSessionInfo = SessionManager.findOtherSession(sessionInfo);
            if (otherSessionInfo != null) {
                logger.debug("[{}] Other session found", sessionId);

                if (otherSessionInfo.isAudioCreated()) {
                    //
                    // TODO
                    //
//                    EngineServiceManager.getInstance().popAndSendMessage();
                }
                else {
                    logger.debug("[{}] Other session audio not created", otherSessionInfo.getSessionId());
                }

                logger.debug("[{}] Other session audio created [{}]", sessionId, otherSessionInfo.getSessionId());
            }

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

                if (compareString(msg.getHeader().event, EngineMessageType.MSG_EVENT_TIMEOUT)) {

                    RmqProcOutgoingAiServiceCancelReq req = new RmqProcOutgoingAiServiceCancelReq(sessionInfo.getSessionId(), AppId.newId());
                    req.send(roomInfo.getAwfQueueName(), sessionInfo.isCaller() ? 1 : 2);

                    StatManager.getInstance().incCount(StatManager.SVC_OUT_AI_CANCEL);
                }
                else {
                    // Nothing to do
                }


                // Restart wakeup
                int wakeupStatus = roomInfo.getWakeupStatus();

                if ((sessionInfo.isCaller() && ((sessionInfo.isCallerWakeupStatus() ? 0x8 : 0x0) != (wakeupStatus & 0xc))) ||
                        (!sessionInfo.isCaller() && ((sessionInfo.isCalleeWakeupStatus() ? 0x2 : 0x0) != (wakeupStatus & 0x3)))) {
                    roomInfo.setWakeupStatus(sessionInfo.isCaller(), RoomInfo.WAKEUP_STATUS_PREPARE);
                }

                if (sessionInfo.isCaller() && sessionInfo.isCallerWakeupStatus() && ((wakeupStatus & 0x4) > 0)) {
                    sendWakeupStartReqToEngine(sessionInfo, sessionInfo.getEngineToolId());
                }
                else if (!sessionInfo.isCaller() && sessionInfo.isCalleeWakeupStatus() && ((wakeupStatus & 0x1) > 0)) {
                    sendWakeupStartReqToEngine(sessionInfo, sessionInfo.getEngineToolId());
                }

                SessionInfo otherSessionInfo = SessionManager.findOtherSession(sessionInfo);
                if (otherSessionInfo != null) {

                    if ((otherSessionInfo.isCaller() && ((otherSessionInfo.isCallerWakeupStatus() ? 0x8 : 0x0) != (wakeupStatus & 0xc))) ||
                            (!otherSessionInfo.isCaller() && ((otherSessionInfo.isCalleeWakeupStatus() ? 0x2 : 0x0) != (wakeupStatus & 0x3)))) {
                        roomInfo.setWakeupStatus(otherSessionInfo.isCaller(), RoomInfo.WAKEUP_STATUS_PREPARE);
                    }

                    logger.warn("[{}] isCaller [{}] caller [{}] callee [{}] wakeupStatus [{}]", otherSessionInfo.getSessionId(),
                            otherSessionInfo.isCaller(),
                            otherSessionInfo.isCallerWakeupStatus(), otherSessionInfo.isCalleeWakeupStatus(),
                            wakeupStatus);

                    if (otherSessionInfo.isCaller() && otherSessionInfo.isCallerWakeupStatus() && ((wakeupStatus & 0x4) > 0)) {
                        sendWakeupStartReqToEngine(otherSessionInfo, otherSessionInfo.getEngineToolId());
                    }
                    else if (!otherSessionInfo.isCaller() && otherSessionInfo.isCalleeWakeupStatus() && ((wakeupStatus & 0x1) > 0)) {
                        sendWakeupStartReqToEngine(otherSessionInfo, otherSessionInfo.getEngineToolId());
                    }
                }

            }
            else {
                logger.warn("[{}] Invalid roomInfo. cnfId [{}] awf [{}]", sessionInfo.getSessionId(),
                        sessionInfo.getConferenceId(), (roomInfo != null) ? roomInfo.getAwfQueueName() : "no room");
            }
        }
    }

    private void sendWakeupStartReqToEngine(SessionInfo sessionInfo, int toolId) {

        if (sessionInfo == null) {
            return;
        }

        logger.info("[{}] Send WakeupStartReq. toolId [{}]", sessionInfo.getSessionId(), toolId);

        String appId = AppId.newId();
        EngineProcWakeupStartReq wakeupStartReq = new EngineProcWakeupStartReq(appId);
        wakeupStartReq.setData(sessionInfo, toolId, EngineProcWakeupStartReq.DEFAULT_TIMEOUT_MSEC);

        EngineClient.getInstance().pushSentQueue(appId, WakeupStartReq.class, wakeupStartReq.getData());
        if (sessionInfo.getSessionId() != null) {
            AppId.getInstance().push(appId, sessionInfo.getSessionId());
        }

        if (!wakeupStartReq.send(false)) {
            // ERROR
//            AppId.getInstance().remove(appId);
//            EngineClient.getInstance().removeSentQueue(appId);
        }
    }

}
