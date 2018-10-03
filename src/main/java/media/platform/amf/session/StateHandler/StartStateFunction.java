package media.platform.amf.session.StateHandler;

import media.platform.amf.room.RoomInfo;
import media.platform.amf.room.RoomManager;
import media.platform.amf.session.SessionManager;
import media.platform.amf.simulator.BiUdpRelayManager;
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

        if (sessionInfo.getServiceState() != SessionState.START) {
            sessionInfo.setServiceState(SessionState.START);
        }
        logger.info("[{}] openRmqRelayChannel [{}]");
        openRmqRelayChannel(sessionInfo);
    }

    private void openRmqRelayChannel(SessionInfo sessionInfo) {

        if (sessionInfo == null) {
            return;
        }

        BiUdpRelayManager udpRelayManager = BiUdpRelayManager.getInstance();
        udpRelayManager.openDstDupQueue(sessionInfo.getSessionId(), sessionInfo.getAiifName());

        RoomInfo roomInfo = RoomManager.getInstance().getRoomInfo( sessionInfo.getConferenceId());
        if (roomInfo == null) {
            return;
        }

        String otherSessionId = roomInfo.getOtherSession(sessionInfo.getSessionId());
        if (otherSessionId == null) {
            return;
        }

        SessionInfo otherSession = SessionManager.findSession( otherSessionId);
        if (otherSession == null) {
            return;
        }

        udpRelayManager.openDstDupQueue(otherSession.getSessionId(), null);


    }
}
