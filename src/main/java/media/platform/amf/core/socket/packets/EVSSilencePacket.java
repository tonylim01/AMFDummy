package media.platform.amf.core.socket.packets;

public class EVSSilencePacket {

    /**
     * AMR-WB silence packets
     */
    private static final byte[] EVS_SID = {
            (byte)0x46, (byte)0x46, (byte)0xec, (byte)0xa6, (byte)0x01, (byte)0x80
    };


    public static byte[] get(int vocoder, int mode) {
        byte[] buf = null;

        if (vocoder == Vocoder.VOCODER_EVS) {
            buf = EVS_SID;
        }

        return buf;
    }

}
