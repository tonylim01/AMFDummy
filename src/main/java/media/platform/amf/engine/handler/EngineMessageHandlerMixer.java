package media.platform.amf.engine.handler;

import media.platform.amf.common.AppId;
import media.platform.amf.engine.types.EngineMessageType;
import media.platform.amf.engine.types.EngineResponseMessage;
import media.platform.amf.room.RoomInfo;
import media.platform.amf.room.RoomManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EngineMessageHandlerMixer extends DefaultEngineMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(EngineMessageHandlerMixer.class);

    public void handle(EngineResponseMessage msg) {
        if (msg == null || msg.getHeader() == null) {
            logger.warn("Null response message");
            return;
        }

        if (compareString(msg.getHeader().getCmd(), EngineMessageType.MSG_CMD_CREATE)) {
            procMixerCreateRes(msg);
        }
        else {
            logger.warn("Unsupported cmd [{}]", msg.getHeader().getCmd());
        }

    }

    private void procMixerCreateRes(EngineResponseMessage msg) {
        if (msg == null || msg.getHeader() == null) {
            logger.warn("Null response message");
            return;
        }

        if (compareString(msg.getHeader().getResult(), EngineMessageType.MSG_RESULT_OK) ||
            compareString(msg.getHeader().getResult(),EngineMessageType.MSG_RESULT_SUCCESS)) {
            // Success
            if (msg.getHeader().getAppId() == null) {
                logger.warn("Null appId in response message");
                return;
            }

            String roomId = AppId.getInstance().get(msg.getHeader().getAppId());
            if (roomId == null) {
                logger.warn("No roomId for appId=[{}]", msg.getHeader().getAppId());
                return;
            }

            RoomInfo roomInfo = RoomManager.getInstance().getRoomInfo(roomId);
            if (roomInfo == null) {
                logger.warn("Cannot find room for appId=[{}]", msg.getHeader().getAppId());
                return;
            }

            roomInfo.setMixerAvailable(true);

            //
            // TODO
            //
        }
        else {
            logger.warn("Undefined result [{}]", msg.getHeader().getResult());
        }

    }
}
