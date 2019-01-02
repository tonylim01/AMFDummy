package media.platform.amf.engine.types;

public class EngineReportHeader {
    public String type;
    public String cmd;
    public String appId;
    public String event;
    public String value;

    public EngineReportHeader(String type, String cmd, String appId, String event, String value) {
        this.type = type;
        this.cmd = cmd;
        this.appId = appId;
        this.event = event;
        this.value = value;
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

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
