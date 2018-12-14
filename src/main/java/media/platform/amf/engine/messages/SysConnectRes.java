package media.platform.amf.engine.messages;

import media.platform.amf.engine.types.EngineResponseHeader;

public class SysConnectRes {

    private int total;
    private int available;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }
}
