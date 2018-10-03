package media.platform.amf.session.StateHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import media.platform.amf.service.ServiceManager;
import media.platform.amf.session.SessionInfo;
import media.platform.amf.session.SessionState;

public class IdleStateFunction implements StateFunction {
    private static final Logger logger = LoggerFactory.getLogger(IdleStateFunction.class);

    @Override
    public void run(SessionInfo sessionInfo, Object arg) {
        if (sessionInfo == null) {
            return;
        }

        logger.debug("[{}] IdleStateFunction", sessionInfo.getSessionId());

//        BiUdpRelayManager.getInstance().close(sessionInfo.getSessionId());

        if (sessionInfo.getServiceState() != SessionState.IDLE) {
            sessionInfo.setServiceState(SessionState.IDLE);
        }

        ServiceManager.getInstance().releaseResource(sessionInfo.getSessionId());
    }
}
