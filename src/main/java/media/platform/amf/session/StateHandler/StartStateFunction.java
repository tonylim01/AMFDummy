package media.platform.amf.session.StateHandler;

import media.platform.amf.common.AppId;
import media.platform.amf.common.AppUtil;
import media.platform.amf.engine.EngineClient;
import media.platform.amf.engine.EngineServiceManager;
import media.platform.amf.engine.handler.EngineProcWakeupStartReq;
import media.platform.amf.engine.handler.EngineProcWakeupStopReq;
import media.platform.amf.engine.messages.AudioCreateReq;
import media.platform.amf.engine.messages.WakeupStartReq;
import media.platform.amf.engine.messages.WakeupStopReq;
import media.platform.amf.rmqif.handler.RmqProcWakeupStatusRes;
import media.platform.amf.rmqif.types.RmqMessageType;
import media.platform.amf.room.RoomInfo;
import media.platform.amf.room.RoomManager;
import media.platform.amf.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import media.platform.amf.session.SessionInfo;
import media.platform.amf.session.SessionState;

public class StartStateFunction implements StateFunction {
    private static final Logger logger = LoggerFactory.getLogger(StartStateFunction.class);

    @Override
    public void run(SessionInfo sessionInfo, Object arg) {
        if (sessionInfo == null) {
            return;
        }

        boolean isPush = false;

        int count = 0;
        if ((sessionInfo.getEndOfState() != SessionState.PREPARE && sessionInfo.getEndOfState() != SessionState.START) ||
                sessionInfo.isAudioCreated() == false) {
            logger.warn("[{}] State mismatch. state [{}] endState [{}] audio [{}]", sessionInfo.getSessionId(),
                    sessionInfo.getServiceState(), sessionInfo.getEndOfState(), sessionInfo.isAudioCreated());

            isPush = true;
        }

        /*

            do {
                AppUtil.trySleep(100);
                count++;

                logger.warn("[{}] State mismatch. state [{}] endState [{}] audio [{}] try [{}]", sessionInfo.getSessionId(),
                        sessionInfo.getServiceState(), sessionInfo.getEndOfState(), sessionInfo.isAudioCreated(), count);

            } while (((sessionInfo.getEndOfState() != SessionState.PREPARE &&  sessionInfo.getEndOfState() != SessionState.START)
                    || sessionInfo.isAudioCreated() == false) && (count < 10));

            if((sessionInfo.getEndOfState() != SessionState.PREPARE && sessionInfo.getEndOfState() != SessionState.START)
                || sessionInfo.isAudioCreated() == false) {
                logger.warn("[{}] State break. state [{}] endState [{}] audio [{}]", sessionInfo.getSessionId(),
                        sessionInfo.getServiceState(), sessionInfo.getEndOfState(), sessionInfo.isAudioCreated());

                if (sessionInfo.getConferenceId() != null) {
                    RoomInfo roomInfo = RoomManager.getInstance().getRoomInfo(sessionInfo.getConferenceId());

                    if (roomInfo != null) {
                        RmqProcWakeupStatusRes res = new RmqProcWakeupStatusRes(sessionInfo.getSessionId(), roomInfo.getLastTransactionId());

                        res.setReasonCode(RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_FAILURE);
                        res.setReasonStr("Engine not ready");

                        if (res.send(roomInfo.getAwfQueueName(), false, false) == false) {
                        }
                    }
                }

                return;
            }
        }
        */

        logger.warn("[{}] State [{}] endState [{}] audio [{}]", sessionInfo.getSessionId(),
                sessionInfo.getServiceState(), sessionInfo.getEndOfState(), sessionInfo.isAudioCreated());

        if (sessionInfo.getServiceState() != SessionState.START) {
            sessionInfo.setServiceState(SessionState.START);
        }

        int wakeupStatus = 0;
        if (sessionInfo.getConferenceId() != null) {
            RoomInfo roomInfo = RoomManager.getInstance().getRoomInfo(sessionInfo.getConferenceId());
            if (roomInfo != null) {
                wakeupStatus = roomInfo.getWakeupStatus();
            }
        }

        logger.warn("[{}] isCaller [{}] caller [{}] callee [{}] wakeupStatus [{}]", sessionInfo.getSessionId(),
                sessionInfo.isCaller(),
                sessionInfo.isCallerWakeupStatus(), sessionInfo.isCalleeWakeupStatus(),
                wakeupStatus);

        if (sessionInfo.getEngineToolId() == 0) {
            count = 0;
            do {
                AppUtil.trySleep(100);
                count++;
            } while ((sessionInfo.getEngineToolId() == 0) && (count < 10));

            if (sessionInfo.getEngineToolId() > 0) {
                isPush = false;
            }
        }

        if (sessionInfo.isCaller() && ((wakeupStatus & 0x4) > 0)) {
            if (sessionInfo.isCallerWakeupStatus()) {
                sendWakeupStartReqToEngine(sessionInfo, sessionInfo.getEngineToolId(), isPush);
            }
            else if (!sessionInfo.isCallerWakeupStatus()) {
                sendWakeupStopReqToEngine(sessionInfo, sessionInfo.getEngineToolId(), isPush);
            }
        }
        else if (!sessionInfo.isCaller() && ((wakeupStatus & 0x1) > 0)) {
            if (sessionInfo.isCalleeWakeupStatus()) {
                sendWakeupStartReqToEngine(sessionInfo, sessionInfo.getEngineToolId(), isPush);
            }
            else if (!sessionInfo.isCalleeWakeupStatus()) {
                sendWakeupStopReqToEngine(sessionInfo, sessionInfo.getEngineToolId(), isPush);
            }
        }

        SessionInfo otherSessionInfo = SessionManager.findOtherSession(sessionInfo);
        if (otherSessionInfo != null) {
            logger.warn("[{}] isCaller [{}] caller [{}] callee [{}] wakeupStatus [{}]", otherSessionInfo.getSessionId(),
                    otherSessionInfo.isCaller(),
                    otherSessionInfo.isCallerWakeupStatus(), otherSessionInfo.isCalleeWakeupStatus(),
                    wakeupStatus);

            if (otherSessionInfo.getEngineToolId() == 0) {
                count = 0;
                do {
                    AppUtil.trySleep(100);
                    count++;
                } while ((otherSessionInfo.getEngineToolId() == 0) && (count < 10));

                if (otherSessionInfo.getEngineToolId() > 0) {
                    isPush = false;
                }
            }

            if (otherSessionInfo.isCaller() && ((wakeupStatus & 0x4) > 0)) {
                if (otherSessionInfo.isCallerWakeupStatus()) {
                    sendWakeupStartReqToEngine(otherSessionInfo, otherSessionInfo.getEngineToolId(), isPush);
                }
                else if (!sessionInfo.isCallerWakeupStatus()) {
                    sendWakeupStopReqToEngine(otherSessionInfo, otherSessionInfo.getEngineToolId(), isPush);
                }
            }
            else if (!otherSessionInfo.isCaller() && ((wakeupStatus & 0x1) > 0)) {
                if (otherSessionInfo.isCalleeWakeupStatus()) {
                    sendWakeupStartReqToEngine(otherSessionInfo, otherSessionInfo.getEngineToolId(), isPush);
                }
                else if (!sessionInfo.isCalleeWakeupStatus()) {
                    sendWakeupStopReqToEngine(otherSessionInfo, otherSessionInfo.getEngineToolId(), isPush);
                }
            }
        }

        logger.debug("[{}] End of start", sessionInfo.getSessionId());

        sessionInfo.setEndOfState(SessionState.START);
    }

    private void sendWakeupStartReqToEngine(SessionInfo sessionInfo, int toolId, boolean push) {

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

        if (!wakeupStartReq.send(push)) {
           // ERROR
//            AppId.getInstance().remove(appId);
//            EngineClient.getInstance().removeSentQueue(appId);
        }
    }

    private void sendWakeupStopReqToEngine(SessionInfo sessionInfo, int toolId, boolean push) {

        if (sessionInfo == null) {
            return;
        }

        logger.info("[{}] Send WakeupStopReq. toolId [{}]", sessionInfo.getSessionId(), toolId);

        String appId = AppId.newId();
        EngineProcWakeupStopReq wakeupStopReq = new EngineProcWakeupStopReq(appId);
        wakeupStopReq.setData(sessionInfo, toolId);

        EngineClient.getInstance().pushSentQueue(appId, WakeupStopReq.class, wakeupStopReq.getData());
        if (sessionInfo.getSessionId() != null) {
            AppId.getInstance().push(appId, sessionInfo.getSessionId());
        }

        if (!wakeupStopReq.send(push)) {
            // ERROR
        }
    }


    private void openRmqRelayChannel(SessionInfo sessionInfo) {

        if (sessionInfo == null) {
            return;
        }

//        BiUdpRelayManager udpRelayManager = BiUdpRelayManager.getInstance();
//        udpRelayManager.openDstDupQueue(sessionInfo.getSessionId(), sessionInfo.getAiifName());
//
//        RoomInfo roomInfo = RoomManager.getInstance().getRoomInfo( sessionInfo.getConferenceId());
//        if (roomInfo == null) {
//            return;
//        }
//
//        String otherSessionId = roomInfo.getOtherSession(sessionInfo.getSessionId());
//        if (otherSessionId == null) {
//            return;
//        }
//
//        SessionInfo otherSession = SessionManager.findSession( otherSessionId);
//        if (otherSession == null) {
//            return;
//        }
//
//        udpRelayManager.openDstDupQueue(otherSession.getSessionId(), null);


    }
}
