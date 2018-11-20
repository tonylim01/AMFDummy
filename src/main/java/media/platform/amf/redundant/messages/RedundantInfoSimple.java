package media.platform.amf.redundant.messages;

public class RedundantInfoSimple {

    private String sessionId;

    public RedundantInfoSimple(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
