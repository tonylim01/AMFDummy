package media.platform.amf.engine.messages;

import media.platform.amf.engine.types.EngineRequestHeader;

import java.util.Arrays;

public class FileStopReq {

    private int id;
    private Integer type;
    private int[] dstIds;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int[] getDstIds() {
        return (dstIds != null) ? Arrays.copyOf(dstIds, dstIds.length) : null;
    }

    public void setDstIds(int[] dstIds) {
        this.dstIds = (dstIds != null) ? Arrays.copyOf(dstIds, dstIds.length) : null;
    }
}
