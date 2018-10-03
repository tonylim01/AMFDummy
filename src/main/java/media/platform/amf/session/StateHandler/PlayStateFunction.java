package media.platform.amf.session.StateHandler;

import media.platform.amf.room.RoomInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import media.platform.amf.session.SessionInfo;

public class PlayStateFunction {
    private static final Logger logger = LoggerFactory.getLogger(PlayStateFunction.class);

    protected boolean stopPlay(SessionInfo sessionInfo, RoomInfo roomInfo, int toolId) {
        if (sessionInfo == null) {
            logger.error("No session");
            return false;
        }

        if (roomInfo == null) {
            logger.error("[{}] Invalid argument", sessionInfo.getSessionId());
            return false;
        }

        logger.debug("[{}] Stop play: toolId [{}]", sessionInfo.getSessionId(), toolId);

        String json;
        int groupId = roomInfo.getGroupId();
        int mixerId = roomInfo.getMixerId();

        if (groupId < 0 || mixerId < 0) {
            return false;
        }


        return true;
    }
}
