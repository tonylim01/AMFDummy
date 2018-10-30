package media.platform.amf.engine.messages;

import media.platform.amf.engine.messages.common.CodecInfo;
import media.platform.amf.engine.messages.common.NetIP4Address;

public class AudioUpdateReq {

    public class Data {
        private int id;
        private int[] dstIds;
        private NetIP4Address remote;
        private int local;
        private CodecInfo decoder;
        private CodecInfo encoder;
        private boolean dtmf;
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
