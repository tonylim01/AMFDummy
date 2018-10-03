package media.platform.amf.rtpcore.core.spi;

public enum ComponentType {
    DTMF_DETECTOR(0), DTMF_GENERATOR(1), PLAYER(2), RECORDER(3), SIGNAL_DETECTOR(4), SIGNAL_GENERATOR(5), SINE(6), SPECTRA_ANALYZER(7), SOUND_CARD(8), ASR_ENGINE(9);

    private int type;

    private ComponentType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
