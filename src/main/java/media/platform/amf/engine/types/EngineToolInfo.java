package media.platform.amf.engine.types;

public class EngineToolInfo {

    private EngineToolState state;
    private String sessionId;
    private long allocTime;
    private long idleTime;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public EngineToolState getState() {
        return state;
    }

    public void setState(EngineToolState state) {
        this.state = state;
        this.allocTime = System.currentTimeMillis();
    }

    public long getAllocTime() {
        return allocTime;
    }

    public void setAllocTime(long allocTime) {
        this.allocTime = allocTime;
    }

    public long getIdleTime() {
        return idleTime;
    }

    public void setIdleTime(long idleTime) {
        this.idleTime = idleTime;
    }
}
