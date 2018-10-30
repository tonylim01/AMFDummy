package media.platform.amf.engine.messages;

public class FilePlayRes {

    private ResponseHeader response;

    public ResponseHeader getResponse() {
        return response;
    }

    public void setResposne(String type, String cmd, String appId, String result, String reason) {
        this.response = new ResponseHeader(type, cmd, appId, result, reason);
    }

    public void setResponse(ResponseHeader response) {
        this.response = response;
    }

}
