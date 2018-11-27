package media.platform.amf.core.socket.packets;

public class SilencePacket {

    public static byte[] get(int vocoder, int mode) {
        byte[] buf = null;

        if (vocoder == Vocoder.VOCODER_ALAW || vocoder == Vocoder.VOCODER_ULAW) {
            buf = G711SilencePacket.get(vocoder);
        }
        else if (vocoder == Vocoder.VOCODER_AMR_WB || vocoder == Vocoder.VOCODER_AMR_NB) {
            buf = AMRSilencePacket.get(vocoder, mode);
        }
        else if (vocoder == Vocoder.VOCODER_EVS) {
            buf = EVSSilencePacket.get(vocoder, mode);
        }

        return buf;
    }
}
