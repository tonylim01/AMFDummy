package media.platform.amf.engine.handler;

import media.platform.amf.common.AppId;
import media.platform.amf.engine.types.EngineMessageType;
import media.platform.amf.engine.types.EngineReportMessage;
import media.platform.amf.engine.types.EngineResponseMessage;
import media.platform.amf.oam.StatManager;
import media.platform.amf.rmqif.handler.RmqProcAiServiceReq;
import media.platform.amf.rmqif.handler.RmqProcWakeupStatusRes;
import media.platform.amf.rmqif.types.RmqMessageType;
import media.platform.amf.room.RoomInfo;
import media.platform.amf.room.RoomManager;
import media.platform.amf.session.SessionInfo;
import media.platform.amf.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EngineMessageHandlerWakeup extends DefaultEngineMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(EngineMessageHandlerWakeup.class);

    public void handle(EngineResponseMessage msg) {
        if (msg == null || msg.getHeader() == null) {
            logger.warn("Null response message");
            return;
        }

        if (compareString(msg.getHeader().getCmd(), EngineMessageType.MSG_CMD_START)) {
            procWakeupStartRes(msg);
        }
        else if (compareString(msg.getHeader().getCmd(), EngineMessageType.MSG_CMD_STOP)) {
            procWakeupStopRes(msg);
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

        if (compareString(msg.getHeader().getCmd(), EngineMessageType.MSG_CMD_START)) {
            procWakeupStartRpt(msg);
        }
        else {
            logger.warn("Unsupported cmd [{}]", msg.getHeader().getCmd());
        }
    }

    private void procWakeupStartRes(EngineResponseMessage msg) {
        if (msg == null || msg.getHeader() == null) {
            logger.warn("Null response message");
            return;
        }

        if (compareString(msg.getHeader().getResult(), EngineMessageType.MSG_RESULT_OK)) {
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

            if (sessionInfo.getConferenceId() != null) {
                RoomInfo roomInfo = RoomManager.getInstance().getRoomInfo(sessionInfo.getConferenceId());
                if (roomInfo != null) {
                    int wakeupStatus = roomInfo.setWakeupStatus(sessionInfo.isCaller() ? true : false, RoomInfo.WAKEUP_STATUS_READY);
                    if ((wakeupStatus & 0x5) == 0) {
                        // Sends a response to AWF
                        RmqProcWakeupStatusRes res = new RmqProcWakeupStatusRes(sessionId, roomInfo.getLastTransactionId());

                        res.setReasonCode(RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_SUCCESS);
                        res.setReasonStr(null);

                        if (res.send(roomInfo.getAwfQueueName(),
                                ((wakeupStatus & 0x8) > 0) ? true : false,
                                ((wakeupStatus & 0x2) > 0) ? true : false) == false) {
                            // TODO
                        }
                    }
                }
            }

            if (sessionInfo.isCaller()) {
                StatManager.getInstance().incCount(StatManager.SVC_CG_WAKEUP_OK);
            }
            else {
                StatManager.getInstance().incCount(StatManager.SVC_CD_WAKEUP_OK);
            }
        }
        else {
            logger.warn("Undefined result [{}]", msg.getHeader().getResult());
        }

    }

    private void procWakeupStopRes(EngineResponseMessage msg) {
        if (msg == null || msg.getHeader() == null) {
            logger.warn("Null response message");
            return;
        }

        if (compareString(msg.getHeader().getResult(), EngineMessageType.MSG_RESULT_OK)) {
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

            if (sessionInfo.getConferenceId() != null) {
                RoomInfo roomInfo = RoomManager.getInstance().getRoomInfo(sessionInfo.getConferenceId());
                if (roomInfo != null) {
                    int wakeupStatus = roomInfo.setWakeupStatus(sessionInfo.isCaller() ? true : false, RoomInfo.WAKEUP_STATUS_NONE);
                    if ((wakeupStatus & 0x5) == 0) {
                        // Sends a response to AWF
                        RmqProcWakeupStatusRes res = new RmqProcWakeupStatusRes(sessionId, roomInfo.getLastTransactionId());

                        res.setReasonCode(RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_SUCCESS);
                        res.setReasonStr(null);

                        if (res.send(roomInfo.getAwfQueueName(),
                                ((wakeupStatus & 0x8) > 0) ? true : false,
                                ((wakeupStatus & 0x2) > 0) ? true : false) == false) {
                            // TODO
                        }
                    }
                }
            }
        }
        else {
            logger.warn("Undefined result [{}]", msg.getHeader().getResult());
        }

    }

    private void procWakeupStartRpt(EngineReportMessage msg) {
        if (msg == null || msg.getHeader() == null) {
            logger.warn("Null response message");
            return;
        }

        if (compareString(msg.getHeader().getEvent(), EngineMessageType.MSG_EVENT_DETECTED)) {
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

            if (sessionInfo.getConferenceId() != null) {
                RoomInfo roomInfo = RoomManager.getInstance().getRoomInfo(sessionInfo.getConferenceId());
                if (roomInfo != null && roomInfo.getAwfQueueName() != null) {

                    RmqProcAiServiceReq req = new RmqProcAiServiceReq(sessionInfo.getSessionId(), AppId.newId());
                    req.send(roomInfo.getAwfQueueName(), sessionInfo.isCaller() ? 1 : 2);

                    StatManager.getInstance().incCount(StatManager.SVC_AI_REQ);
                }
            }
        }
        else {
            logger.warn("Undefined event [{}]", msg.getHeader().getEvent());
        }

    }
}
