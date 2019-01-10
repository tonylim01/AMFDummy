package media.platform.amf.engine.handler.base;

import media.platform.amf.common.AppId;
import media.platform.amf.engine.handler.DefaultEngineMessageHandler;
import media.platform.amf.engine.types.EngineMessageType;
import media.platform.amf.engine.types.EngineReportMessage;
import media.platform.amf.engine.types.EngineResponseMessage;
import media.platform.amf.rmqif.handler.RmqProcAiServiceReq;
import media.platform.amf.rmqif.handler.RmqProcMediaPlayDoneReq;
import media.platform.amf.room.RoomInfo;
import media.platform.amf.room.RoomManager;
import media.platform.amf.session.SessionInfo;
import media.platform.amf.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EngineMessageHandlerFile extends DefaultEngineMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(EngineMessageHandlerFile.class);

    public void handle(EngineResponseMessage msg) {
        if (msg == null || msg.getHeader() == null) {
            logger.warn("Null response message");
            return;
        }

        if (compareString(msg.getHeader().getCmd(), EngineMessageType.MSG_CMD_PLAY)) {
            procFilePlayRes(msg);
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

        if (compareString(msg.getHeader().getCmd(), EngineMessageType.MSG_CMD_PLAY)) {
            procFilePlayRpt(msg);
        }
        else {
            logger.warn("Unsupported cmd [{}]", msg.getHeader().getCmd());
        }
    }

    private void procFilePlayRes(EngineResponseMessage msg) {
        if (msg == null || msg.getHeader() == null) {
            logger.warn("Null response message");
            return;
        }

        if (compareString(msg.getHeader().getResult(), EngineMessageType.MSG_RESULT_OK)) {
        }
        else {
            logger.warn("Undefined result [{}]", msg.getHeader().getResult());
        }

    }

    private void procFilePlayRpt(EngineReportMessage msg) {
        if (msg == null || msg.getHeader() == null) {
            logger.warn("Null response message");
            return;
        }

        boolean isSuccess = false;

        if (compareString(msg.getHeader().getEvent(), EngineMessageType.MSG_EVENT_DONE) ||
            compareString(msg.getHeader().getEvent(), EngineMessageType.MSG_EVENT_STOPPED)) {

            isSuccess = true;
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

                RmqProcMediaPlayDoneReq req = new RmqProcMediaPlayDoneReq(sessionInfo.getSessionId(), AppId.newId());
                if (!isSuccess) {
                    req.setReasonCode(1);
                    req.setReasonStr(msg.getHeader().getValue());
                }
                req.send(roomInfo.getAwfQueueName(), sessionInfo.getMediaDir());
            }
        }
        else {
            logger.warn("Undefined event [{}]", msg.getHeader().getEvent());
        }

    }
}
