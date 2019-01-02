package media.platform.amf.session.StateHandler;

import media.platform.amf.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import media.platform.amf.session.SessionInfo;
import media.platform.amf.session.SessionState;

public class ReleaseStateFunction implements StateFunction {
    private static final Logger logger = LoggerFactory.getLogger(ReleaseStateFunction.class);

    @Override
    public void run(SessionInfo sessionInfo, Object arg) {
        if (sessionInfo == null) {
            return;
        }

        logger.debug("[{}] ReleaseStateFunction", sessionInfo.getSessionId());

        if (sessionInfo.getServiceState() != SessionState.RELEASE) {
            sessionInfo.setServiceState(SessionState.RELEASE);
            sessionInfo.updateT4Time(SessionManager.TIMER_HANGUP_T4);
        }

        sessionInfo.setLastSentTime();
        sessionInfo.updateT2Time(SessionManager.TIMER_HANGUP_T2);

        sessionInfo.setEndOfState(SessionState.RELEASE);
    }
}
