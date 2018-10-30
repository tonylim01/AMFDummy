package media.platform.amf.engine.messages.common;

public class RecvVocoderInfo {
    private boolean enabled;
    private CodecInfo vocoder;
    private int srcId;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public CodecInfo getVocoder() {
        return vocoder;
    }

    public void setVocoder(CodecInfo vocoder) {
        this.vocoder = vocoder;
    }

    public int getSrcId() {
        return srcId;
    }

    public void setSrcId(int srcId) {
        this.srcId = srcId;
    }
}
