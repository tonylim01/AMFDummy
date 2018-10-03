package media.platform.amf.core.socket;

public class UdpPacket {

    private byte[] data;

    public UdpPacket(byte[] packet, int length) {
        if (length > 0) {
            data = new byte[length];
            System.arraycopy(packet, 0, this.data, 0, length);
        }
    }

    public byte[] getData() {
        return data;
    }
}
