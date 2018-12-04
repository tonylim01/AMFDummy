/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RtpInboundHandler.java
 * @author Tony Lim
 *
 */
package media.platform.amf.rtpcore.core.rtp.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import media.platform.amf.rtpcore.core.rtp.rtp.RTPInput;
import media.platform.amf.rtpcore.core.sdp.format.RTPFormats;
import media.platform.amf.rtpcore.core.spi.ConnectionMode;
import media.platform.amf.session.SessionInfo;
import media.platform.amf.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

public class UdpInboundHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private static final Logger logger = LoggerFactory.getLogger( UdpInboundHandler.class);

    private final RtpInboundHandlerGlobalContext context;
    private final RtpInboundHandlerFsm fsm;

    public UdpInboundHandler(RtpInboundHandlerGlobalContext context) {
        this.context = context;
        this.fsm = RtpInboundHandlerFsmBuilder.INSTANCE.build(context);

        this.isActive();
        if(!this.isActive()) {
            this.fsm.start();
        }
    }

    public void activate() {
        if(!this.isActive()) {
            this.fsm.start();
        }
    }

    public void deactivate() {
        if(this.isActive()) {
            this.fsm.fire(RtpInboundHandlerEvent.DEACTIVATE);
        }
    }
    
    public boolean isActive() {
        return RtpInboundHandlerState.ACTIVATED.equals(this.fsm.getCurrentState());
    }
    
    public void updateMode(ConnectionMode mode) {
        switch (mode) {
            case INACTIVE:
            case SEND_ONLY:
                this.context.setLoopable(false);
                this.context.setReceivable(false);
                break;

            case RECV_ONLY:
                this.context.setLoopable(false);
                this.context.setReceivable(true);
                break;

            case SEND_RECV:
            case CONFERENCE:
                this.context.setLoopable(false);
                this.context.setReceivable(true);
                break;

            case NETWORK_LOOPBACK:
                this.context.setLoopable(true);
                this.context.setReceivable(false);
                break;

            default:
                this.context.setLoopable(false);
                this.context.setReceivable(false);
                break;
        }
    }
    
    public void setFormatMap(RTPFormats formats) {
        this.context.setFormats(formats);
    }

    public void useJitterBuffer(boolean use) {
        this.context.getJitterBuffer().setInUse(use);
    }

    public RTPInput getRtpInput() {
        return context.getRtpInput();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {

        InetAddress srcAddr = msg.sender().getAddress();
        ByteBuf buf = msg.content();

        int rcvPktLength = buf.readableBytes();
        byte[] rcvPktBuf = new byte[rcvPktLength];
        buf.readBytes(rcvPktBuf);

        //RtpPacket rtpPacket = new RtpPacket( RtpPacket.RTP_PACKET_MAX_SIZE, true);
//        RtpPacket rtpPacket = new RtpPacket(rcvPktLength, true);
//        rtpPacket.getBuffer().put(rcvPktBuf, 0, rcvPktLength);

        //logger.debug("<- ({}:{}) {}", srcAddr.toString(), msg.sender().getPort(), rtpPacket.toString());

        String adddress = ctx.channel().localAddress().toString();
        int port = adddress.lastIndexOf(":");
        String temp = adddress.substring(port + 1, adddress.length());

        SessionManager sessionManager = SessionManager.getInstance();
        SessionInfo sessionInfo = sessionManager.getDstLocalPort(Integer.parseInt(temp));

        if (sessionInfo == null) {
            logger.warn("Session not found");
            return;
        }

//        int version = rtpPacket.getVersion();
//        if (version == 0) {
//            if (logger.isDebugEnabled()) {
//                logger.debug("RTP Channel " + this.context.getStatistics().getSsrc() + " dropped RTP v0 packet.");
//            }
//            rtpPacket.getBuffer().clear();
//            return;
//        }

        // Check if channel can receive traffic
        boolean canReceive = (context.isReceivable() || context.isLoopable());

//        logger.debug("RTP canReceive " + canReceive + " packet.");

        if (!canReceive) {
            if (logger.isDebugEnabled()) {
                logger.debug("UDP Channel " + this.context.getStatistics().getSsrc() + " dropped packet because channel mode does not allow to receive traffic.");
            }
//            rtpPacket.getBuffer().clear();
            return;
        }

        if (sessionInfo.getRtpSender() != null) {
//            logger.debug("push udp packet {}", rcvPktBuf.length);
            sessionInfo.getRtpSender().put(-1, rcvPktBuf);
        }

    }

}
