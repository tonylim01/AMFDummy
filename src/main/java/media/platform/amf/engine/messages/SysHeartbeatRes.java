package media.platform.amf.engine.messages;

import media.platform.amf.engine.types.EngineResponseHeader;

public class SysHeartbeatRes {

    public class Data {
        private int total;
        private int busy;
        private int idle;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getBusy() {
            return busy;
        }

        public void setBusy(int busy) {
            this.busy = busy;
        }

        public int getIdle() {
            return idle;
        }

        public void setIdle(int idle) {
            this.idle = idle;
        }
    }

    private EngineResponseHeader response;
    private Data data;

    public EngineResponseHeader getResponse() {
        return response;
    }

    public void setResposne(String type, String cmd, String appId, String result, String reason) {
        this.response = new EngineResponseHeader(type, cmd, appId, result, reason);
    }

    public void setResponse(EngineResponseHeader response) {
        this.response = response;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
