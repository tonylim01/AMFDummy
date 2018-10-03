package media.platform.amf.session.StateHandler;

import media.platform.amf.AppInstance;
import media.platform.amf.rmqif.messages.FileData;
import media.platform.amf.room.RoomInfo;
import media.platform.amf.room.RoomManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import media.platform.amf.session.SessionInfo;

public class UpdateStateFunction implements StateFunction {
    private static final Logger logger = LoggerFactory.getLogger(UpdateStateFunction.class);

    @Override
    public void run(SessionInfo sessionInfo, Object arg) {
        if (sessionInfo == null) {
            return;
        }

        // NOT to change the status value with UPDATE

        //
        // TODO
        //
        RoomInfo roomInfo = RoomManager.getInstance().getRoomInfo( sessionInfo.getConferenceId());
        if (roomInfo == null) {
            logger.error("[{}] No roomInfo found", sessionInfo.getSessionId());
            return;
        }

        if (roomInfo.getGroupId() < 0) {
            logger.error("[{}] No channel group found", sessionInfo.getSessionId());
            return;
        }

        if (arg != null && arg instanceof Boolean) {
            boolean isLow = (Boolean)arg;
            updateVolume(sessionInfo, roomInfo, isLow);
            /*
            if (roomInfo.isVolumeMin() != isLow) {
                roomInfo.setVolumeMin(isLow);
                updateVolume(sessionInfo, roomInfo, isLow);
            }
            */
        }


    }

    private boolean updateVolume(SessionInfo sessionInfo, RoomInfo roomInfo, boolean isLow) {
        if (sessionInfo == null) {
            return false;
        }

        logger.debug("[{}] Update play volume: isLow [{}]", sessionInfo.getSessionId(), isLow);

        String json;
        int groupId = roomInfo.getGroupId();
        int mixerId = roomInfo.getMixerId();

        if (groupId < 0 || mixerId < 0) {
            return false;
        }

        boolean isBgm = false;
        boolean isMent = false;


        return true;
    }

}
