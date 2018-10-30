package media.platform.amf.engine.messages.common;

public class SendVocoderInfo {
    private boolean enabled;
    private CodecInfo vocoder;
    private int[] dstIds;

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

    public int[] getDstIds() {
        return dstIds;
    }

    public void setDstIds(int[] dstIds) {
        this.dstIds = dstIds;
    }
}
