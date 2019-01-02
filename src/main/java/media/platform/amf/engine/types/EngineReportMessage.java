package media.platform.amf.engine.types;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class EngineReportMessage {

    private EngineReportHeader report;
    private JsonElement data = null;

    public EngineReportMessage(String type, String cmd, String appId, String event, String value) {
        this.report = new EngineReportHeader(type, cmd, appId, event, value);
    }

    public EngineReportMessage(EngineReportHeader header)  {
        this.report = new EngineReportHeader(header.getType(), header.getCmd(), header.getAppId(),
                header.getEvent(), header.getValue());
    }

    public EngineReportHeader getHeader() {
        return report;
    }

    public void setHeader(EngineReportHeader header) {
        this.report = header;
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
