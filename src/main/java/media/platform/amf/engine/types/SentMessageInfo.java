package media.platform.amf.engine.types;

public class SentMessageInfo {
    private long timestamp;
    private Class clss;
    private Object obj;

    public SentMessageInfo() {
    }

    public SentMessageInfo(long timestamp, Class clss, Object obj) {
        this.timestamp = timestamp;
        this.clss = clss;
        this.obj = obj;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Class getClss() {
        return clss;
    }

    public void setClss(Class clss) {
        this.clss = clss;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }
}
