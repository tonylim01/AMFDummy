package media.platform.amf.engine.types;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class EngineResponseMessage {

    private EngineResponseHeader header;
    private JsonElement body = null;

    public EngineResponseMessage(String type, String cmd, String appId, String result, String reason) {
        this.header = new EngineResponseHeader(type, cmd, appId,result, reason);
    }

    public EngineResponseMessage(EngineResponseHeader header)  {
        this.header = new EngineResponseHeader(header.getType(), header.getCmd(), header.getAppId(),
                header.getResult(), header.getReason());
    }

    public EngineResponseHeader getHeader() {
        return header;
    }

    public void setHeader(EngineResponseHeader header) {
        this.header = header;
    }

    public JsonElement getBody() {
        return body;
    }

    public void setBody(JsonElement body) {
        this.body = body;
    }

    public void setBody(String body) {
        Gson gson = new Gson();
        this.body = gson.toJsonTree(body);
    }
}
