package media.platform.amf.engine.types;

public class EngineSQueueMessage {
    private String type;
    private String cmd;
    private String appId;
    private String msg;

    public EngineSQueueMessage(String type, String cmd, String appId, String msg) {
        this.type = type;
        this.cmd = cmd;
        this.appId = appId;
        this.msg = msg;
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

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
