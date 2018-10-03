package media.platform.amf.session;

public class SessionStateMessage {

    private String sessionId;
    private SessionState state;
    private Object data;

    public SessionStateMessage() {
    }

    public SessionStateMessage(String sessionId, SessionState state, Object data) {
        this.sessionId = sessionId;
        this.state = state;
        this.data = data;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public SessionState getState() {
        return state;
    }

    public void setState(SessionState state) {
        this.state = state;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
