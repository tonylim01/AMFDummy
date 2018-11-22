package media.platform.amf.engine.messages;

import media.platform.amf.engine.types.EngineRequestHeader;

public class FileStopReq {

    private EngineRequestHeader request;

    public EngineRequestHeader getRequest() {
        return request;
    }

    public void setRequest(String type, String cmd, String appId) {
        this.request = new EngineRequestHeader(type, cmd, appId);
    }

    public void setRequest(EngineRequestHeader request) {
        this.request = request;
    }
}
