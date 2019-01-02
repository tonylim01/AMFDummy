package media.platform.amf.session.StateHandler;

import media.platform.amf.common.AppId;
import media.platform.amf.common.AppUtil;
import media.platform.amf.engine.EngineClient;
import media.platform.amf.engine.handler.EngineProcWakeupStartReq;
import media.platform.amf.engine.messages.AudioCreateReq;
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

        int count = 0;
        if (sessionInfo.getEndOfState() != SessionState.PREPARE || sessionInfo.isAudioCreated() == false) {
            logger.warn("[{}] State mismatch. state [{}] endState [{}] audio [{}]", sessionInfo.getSessionId(),
                    sessionInfo.getServiceState(), sessionInfo.getEndOfState(), sessionInfo.isAudioCreated());

            do {
                AppUtil.trySleep(100);
                count++;

            } while ((sessionInfo.getEndOfState() != SessionState.PREPARE || sessionInfo.isAudioCreated() == false) && (count < 10));

            if(sessionInfo.getEndOfState() != SessionState.PREPARE || sessionInfo.isAudioCreated() == false) {
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

        if (sessionInfo.getServiceState() != SessionState.START) {
            sessionInfo.setServiceState(SessionState.START);
        }

//        logger.info("[{}] openRmqRelayChannel [{}]");
//        openRmqRelayChannel(sessionInfo);

        if (sessionInfo.isCallerWakeupStatus()) {
            sendWakeupStartReqToEngine(sessionInfo, sessionInfo.getEngineToolId());
        }

        if (sessionInfo.isCalleeWakeupStatus()) {
            SessionInfo otherSessionInfo = SessionManager.findOtherSession(sessionInfo);
            if (otherSessionInfo != null) {
                sendWakeupStartReqToEngine(otherSessionInfo, otherSessionInfo.getEngineToolId());
            }
        }

        sessionInfo.setEndOfState(SessionState.START);
    }

    private void sendWakeupStartReqToEngine(SessionInfo sessionInfo, int toolId) {

        if (sessionInfo == null) {
            return;
        }

        logger.info("[{}] Send WakeupStartReq. toolId [{}]", sessionInfo.getSessionId(), toolId);

        String appId = AppId.newId();
        EngineProcWakeupStartReq wakeupStartReq = new EngineProcWakeupStartReq(appId);
        wakeupStartReq.setData(sessionInfo, toolId, EngineProcWakeupStartReq.DEFAULT_TIMEOUT_MSEC);

        if (wakeupStartReq.send()) {
            EngineClient.getInstance().pushSentQueue(appId, AudioCreateReq.class, wakeupStartReq.getData());
            if (sessionInfo.getSessionId() != null) {
                AppId.getInstance().push(appId, sessionInfo.getSessionId());
            }
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
