package media.platform.amf.engine.messages;

import media.platform.amf.engine.types.EngineResponseHeader;

public class SysHeartbeatRes {

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
