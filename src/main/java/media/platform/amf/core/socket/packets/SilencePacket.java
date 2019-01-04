package media.platform.amf.core.socket.packets;

public class SilencePacket {

    private static final int SID_AMRWB = 9;
    private static final int SID_AMRNB = 8;

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

    public static boolean checkSID(int vocoder, byte[] payload) {
        if (payload == null || payload.length < 4) {
            return false;
        }

        boolean result = false;

        if (vocoder == Vocoder.VOCODER_AMR_WB) {
            int frameType = ((payload[0] & 0xff) >> 3) & 0x0f;
            result = (frameType == SID_AMRWB);
        } else if (vocoder == Vocoder.VOCODER_AMR_NB) {
            int frameType = ((payload[0] & 0xff) >> 3) & 0x0f;
            result = (frameType == SID_AMRNB);
        } else if (vocoder == Vocoder.VOCODER_EVS) {
            result = (payload.length == 6);
        }

        return result;
    }
}
