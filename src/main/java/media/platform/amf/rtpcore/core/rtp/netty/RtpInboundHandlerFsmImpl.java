
package media.platform.amf.rtpcore.core.rtp.netty;

import media.platform.amf.rtpcore.core.sdp.format.RTPFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import media.platform.amf.rtpcore.core.rtp.rtp.RtpPacket;
import media.platform.amf.rtpcore.core.rtp.rtp.statistics.RtpStatistics;

public class RtpInboundHandlerFsmImpl extends AbstractRtpInboundHandlerFsm {

    private static final Logger logger = LoggerFactory.getLogger( RtpInboundHandlerFsmImpl.class);

    private final RtpInboundHandlerGlobalContext context;

    public RtpInboundHandlerFsmImpl(RtpInboundHandlerGlobalContext context) {
        super();
        this.context = context;
    }

    @Override
    public void enterActivated(RtpInboundHandlerState from, RtpInboundHandlerState to, RtpInboundHandlerEvent event, RtpInboundHandlerTransactionContext context) {
        this.context.getRtpInput().activate();
    }

    @Override
    public void enterDeactivated(RtpInboundHandlerState from, RtpInboundHandlerState to, RtpInboundHandlerEvent event, RtpInboundHandlerTransactionContext context) {
        this.context.getRtpInput().deactivate();
        this.context.getJitterBuffer().restart();
    }

    @Override
    public void onPacketReceived(RtpInboundHandlerState from, RtpInboundHandlerState to, RtpInboundHandlerEvent event, RtpInboundHandlerTransactionContext context) {
        final RtpInboundHandlerPacketReceivedContext txContext = (RtpInboundHandlerPacketReceivedContext) context;
        final RtpPacket packet = txContext.getPacket();
        final int payloadType = packet.getPayloadType();
        final RTPFormat format = this.context.getFormats().find( payloadType);
        final RtpStatistics statistics = this.context.getStatistics();

        // RTP keep-alive
        statistics.setLastHeartbeat(this.context.getClock().getTime());
        
        if (format == null) {
            // Drop packet with unknown format
            logger.warn("RTP Channel " + statistics.getSsrc() + " dropped packet because payload types " + payloadType + " is unknown.");
        } else {
            // Consume packet

            this.context.getJitterBuffer().write(packet, format);

            logger.warn("RTP format : " + format.toString());

            // Update statistics
            statistics.onRtpReceive(packet);
        }
    }

}
