package media.platform.amf.core.socket;

import java.util.Arrays;

public class UdpPacket {

    private int seqNo;
    private byte[] data;

    public UdpPacket(byte[] packet, int length) {
        initPacket(packet, length);
    }

    public UdpPacket(int seqNo, byte[] packet, int length) {

        initPacket(packet, length);
        this.seqNo = seqNo;
    }

    public void initPacket(byte[] packet, int length) {
        if (length > 0) {
            data = new byte[length];
            System.arraycopy(packet, 0, this.data, 0, length);
        }
    }

    public int getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(int seqNo) {
        this.seqNo = seqNo;
    }

    public byte[] getData() {
        byte[] ret = (data != null) ? Arrays.copyOf(data, data.length) : null;
        return ret;
    }

    public void clear() {
        data = null;
    }
}
