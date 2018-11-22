package media.platform.amf.engine.types;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class EngineRequestMessage {

    private EngineRequestHeader request;
    private JsonElement data = null;

    public EngineRequestMessage(String type, String cmd, String appId) {
        this.request = new EngineRequestHeader(type, cmd, appId);
    }

    public EngineRequestMessage(EngineRequestHeader header)  {
        this.request = new EngineRequestHeader(header.getType(), header.getCmd(), header.getAppId());
    }

    public EngineRequestHeader getHeader() {
        return request;
    }

    public void setHeader(EngineRequestHeader header) {
        this.request = header;
    }

    public JsonElement getData() {
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
