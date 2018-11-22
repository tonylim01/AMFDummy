package media.platform.amf.engine.types;

public class EngineResponseHeader {
    public String type;
    public String cmd;
    public String appId;
    public String result;
    public String reason;

    public EngineResponseHeader(String type, String cmd, String appId, String result, String reason) {
        this.type = type;
        this.cmd = cmd;
        this.appId = appId;
        this.result = result;
        this.reason = reason;
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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
