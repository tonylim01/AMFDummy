package media.platform.amf.engine.messages;

import media.platform.amf.engine.types.EngineResponseHeader;

public class AudioCreateRes {

    private EngineResponseHeader response;

    public EngineResponseHeader getResponse() {
        return response;
    }

    public void setResposne(String type, String cmd, String appId, String result, String reason) {
        this.response = new EngineResponseHeader(type, cmd, appId, result, reason);
    }

    public void setResponse(EngineResponseHeader response) {
        this.response = response;
    }

}
