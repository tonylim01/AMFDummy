package media.platform.amf.engine.messages;

import media.platform.amf.engine.messages.common.FileInfo;
import media.platform.amf.engine.messages.common.RecvVocoderInfo;

public class FileRecordReq {

    public class Data {
        private int id;
        private FileInfo file;
        private RecvVocoderInfo audio;
        private RecvVocoderInfo video;
    }

    private RequestHeader request;
    private Data data;

    public RequestHeader getRequest() {
        return request;
    }

    public void setRequest(String type, String cmd, String appId) {
        this.request = new RequestHeader(type, cmd, appId);
    }

    public void setRequest(RequestHeader request) {
        this.request = request;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
