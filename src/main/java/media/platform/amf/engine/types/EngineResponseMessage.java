package media.platform.amf.engine.types;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class EngineResponseMessage {

    private EngineResponseHeader response;
    private JsonElement data = null;

    public EngineResponseMessage(String type, String cmd, String appId, String result, String reason) {
        this.response = new EngineResponseHeader(type, cmd, appId,result, reason);
    }

    public EngineResponseMessage(EngineResponseHeader header)  {
        this.response = new EngineResponseHeader(header.getType(), header.getCmd(), header.getAppId(),
                header.getResult(), header.getReason());
    }

    public EngineResponseHeader getHeader() {
        return response;
    }

    public void setHeader(EngineResponseHeader header) {
        this.response = header;
    }

    public JsonElement getBody() {
        return data;
    }

    public void setBody(JsonElement body) {
        this.data = body;
    }

    public void setBody(String body) {
        Gson gson = new Gson();
        this.data = gson.toJsonTree(body);
    }
}
