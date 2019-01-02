package media.platform.amf.engine.messages;

import media.platform.amf.engine.messages.common.CodecInfo;
import media.platform.amf.engine.messages.common.NetIP4Address;

import java.util.Arrays;

public class AudioBranchReq {

    private int id;

    private NetIP4Address remote;
    private Integer local;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public NetIP4Address getRemote() {
        return remote;
    }

    public void setRemote(NetIP4Address remote) {
        this.remote = remote;
    }

    public int getLocal() {
        return local;
    }

    public void setLocal(int local) {
        this.local = local;
    }
}
