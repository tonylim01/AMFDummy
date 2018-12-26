package media.platform.amf.engine.messages.common;

public class SendVocoderInfo {
    private boolean enabled;
    private CodecInfo vocoder;

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
}
