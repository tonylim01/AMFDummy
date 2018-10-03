package media.platform.amf.session.StateHandler;

import media.platform.amf.session.SessionInfo;

public interface StateFunction {
    void run(SessionInfo sessionInfo, Object arg);
}
