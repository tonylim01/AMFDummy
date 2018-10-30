package media.platform.amf.engine.messages;

public class RequestHeader {
    private String type;
    private String cmd;
    private String appId;

    public RequestHeader(String type, String cmd, String appId) {
        this.type = type;
        this.cmd = cmd;
        this.appId = appId;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
}
