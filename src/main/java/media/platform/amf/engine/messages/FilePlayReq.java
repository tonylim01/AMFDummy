package media.platform.amf.engine.messages;

import media.platform.amf.engine.messages.common.FileInfos;
import media.platform.amf.engine.types.EngineRequestHeader;
import media.platform.amf.engine.messages.common.SendVocoderInfo;

public class FilePlayReq {

    public class Data {
        private int id;
        private FileInfos file;
        private SendVocoderInfo audio;
        private SendVocoderInfo video;
    }

    private EngineRequestHeader request;
    private Data data;

    public EngineRequestHeader getRequest() {
        return request;
    }

    public void setRequest(String type, String cmd, String appId) {
        this.request = new EngineRequestHeader(type, cmd, appId);
    }

    public void setRequest(EngineRequestHeader request) {
        this.request = request;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
