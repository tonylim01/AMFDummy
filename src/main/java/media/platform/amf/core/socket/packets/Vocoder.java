package media.platform.amf.core.socket.packets;

public class Vocoder {
    public static final int VOCODER_ALAW = 1;
    public static final int VOCODER_ULAW = 2;

    public static final int VOCODER_AMR_WB = 3;
    public static final int VOCODER_AMR_NB = 4;
    public static final int VOCODER_EVS = 5;

    public static final int MODE_NA = -1;

    public static int getPayloadSize(int vocoder) {
        return (vocoder == VOCODER_AMR_WB || vocoder == VOCODER_EVS) ? 320 : 160;
    }
}
