package media.platform.amf.redundant.messages;

public class JitterSenderInfo {

    private String sessionId;
    private int seq;
    private long ssrc;
    private long timestamp;

    public JitterSenderInfo(String sessionId, int seq, long ssrc, long timestamp) {
        this.sessionId = sessionId;
        this.seq = seq;
        this.ssrc = ssrc;
        this.timestamp = timestamp;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public long getSsrc() {
        return ssrc;
    }

    public void setSsrc(long ssrc) {
        this.ssrc = ssrc;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
