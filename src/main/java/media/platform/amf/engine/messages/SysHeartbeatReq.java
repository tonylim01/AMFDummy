package media.platform.amf.engine.messages;

public class SysHeartbeatReq {

    private RequestHeader request;

    public RequestHeader getRequest() {
        return request;
    }

    public void setRequest(String type, String cmd, String appId) {
        this.request = new RequestHeader(type, cmd, appId);
    }

    public void setRequest(RequestHeader request) {
        this.request = request;
    }
}
