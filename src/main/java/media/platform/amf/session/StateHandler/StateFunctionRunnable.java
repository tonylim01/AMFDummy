package media.platform.amf.session.StateHandler;

import media.platform.amf.session.SessionInfo;

public class StateFunctionRunnable implements Runnable {

    private StateFunction stateFunction;
    private SessionInfo sessionId;
    private Object obj;

    public StateFunctionRunnable(StateFunction stateFunction, SessionInfo sessionId, Object obj) {
        this.stateFunction = stateFunction;
        this.sessionId = sessionId;
        this.obj = obj;
    }

    @Override
    public void run() {
        if (stateFunction != null) {
            stateFunction.run(sessionId, obj);
        }
    }
}
