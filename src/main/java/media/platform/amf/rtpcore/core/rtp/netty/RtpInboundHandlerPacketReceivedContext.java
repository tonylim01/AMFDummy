package media.platform.amf.rtpcore.core.rtp.netty;

import media.platform.amf.rtpcore.core.rtp.rtp.RtpPacket;

public class RtpInboundHandlerPacketReceivedContext implements RtpInboundHandlerTransactionContext {

    private final RtpPacket packet;

    public RtpInboundHandlerPacketReceivedContext(RtpPacket packet) {
        super();
        this.packet = packet;
    }

    public RtpPacket getPacket() {
        return packet;
    }

}
