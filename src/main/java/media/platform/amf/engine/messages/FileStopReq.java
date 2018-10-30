package media.platform.amf.engine.messages;

import media.platform.amf.engine.messages.RequestHeader;

public class FileStopReq {

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
