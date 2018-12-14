package media.platform.amf.engine.messages;

import media.platform.amf.engine.messages.common.CodecInfo;
import media.platform.amf.engine.messages.common.NetIP4Address;
import media.platform.amf.engine.types.EngineRequestHeader;

import java.util.Arrays;

public class AudioCreateReq {

    private int id;
    private Integer[] dstIds;

    private String from;
    private String to;

    private NetIP4Address remote;
    private int local;
    private CodecInfo decoder;
    private CodecInfo encoder;
    private boolean dtmf;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer[] getDstIds() {
        return dstIds;
    }

    public void setDstIds(Integer[] dstIds) {
        this.dstIds = dstIds;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
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

    public CodecInfo getDecoder() {
        return decoder;
    }

    public void setDecoder(CodecInfo decoder) {
        this.decoder = decoder;
    }

    public CodecInfo getEncoder() {
        return encoder;
    }

    public void setEncoder(CodecInfo encoder) {
        this.encoder = encoder;
    }

    public boolean isDtmf() {
        return dtmf;
    }

    public void setDtmf(boolean dtmf) {
        this.dtmf = dtmf;
    }
}
