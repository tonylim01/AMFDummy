package media.platform.amf.session.StateHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import media.platform.amf.session.SessionInfo;
import media.platform.amf.session.SessionState;

public class ReadyStateFunction implements StateFunction {
    private static final Logger logger = LoggerFactory.getLogger(ReadyStateFunction.class);

    @Override
    public void run(SessionInfo sessionInfo, Object arg) {
        if (sessionInfo == null) {
            return;
        }

        if (sessionInfo.getServiceState() != SessionState.READY) {
            sessionInfo.setServiceState(SessionState.READY);
        }

        //
        // TODO
        //

        sessionInfo.setEndOfState(SessionState.READY);
    }
}
