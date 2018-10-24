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
import media.platform.amf.AppInstance;
import media.platform.amf.core.socket.packets.Vocoder;
import media.platform.amf.room.RoomInfo;
import media.platform.amf.room.RoomManager;
import media.platform.amf.service.AudioFileReader;
import media.platform.amf.session.SessionManager;
import media.platform.amf.rtpcore.core.rtp.rtp.RTPInput;
import media.platform.amf.rtpcore.core.sdp.format.RTPFormats;
import media.platform.amf.rtpcore.core.spi.ConnectionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import media.platform.amf.session.SessionInfo;
import media.platform.amf.rtpcore.core.rtp.rtp.RtpPacket;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetAddress;
import java.util.Random;

public class RtpInboundHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private static final Logger logger = LoggerFactory.getLogger( RtpInboundHandler.class);

    private final RtpInboundHandlerGlobalContext context;
    private final RtpInboundHandlerFsm fsm;

    public RtpInboundHandler(RtpInboundHandlerGlobalContext context) {
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
        RtpPacket rtpPacket = new RtpPacket(rcvPktLength, true);
        rtpPacket.getBuffer().put(rcvPktBuf, 0, rcvPktLength);

        //logger.debug("<- ({}:{}) {}", srcAddr.toString(), msg.sender().getPort(), rtpPacket.toString());

        String adddress = ctx.channel().localAddress().toString();
        int port = adddress.lastIndexOf(":");
        String temp = adddress.substring(port + 1, adddress.length());

        SessionManager sessionManager = SessionManager.getInstance();
        SessionInfo sessionInfo = sessionManager.getFinePort(Integer.parseInt(temp));

        int version = rtpPacket.getVersion();
        if (version == 0) {
            if (logger.isDebugEnabled()) {
                logger.debug("RTP Channel " + this.context.getStatistics().getSsrc() + " dropped RTP v0 packet.");
            }
            return;
        }

        // Check if channel can receive traffic
        boolean canReceive = (context.isReceivable() || context.isLoopable());

//        logger.debug("RTP canReceive " + canReceive + " packet.");

        if (!canReceive) {
            if (logger.isDebugEnabled()) {
                logger.debug("RTP Channel " + this.context.getStatistics().getSsrc() + " dropped packet because channel mode does not allow to receive traffic.");
            }
            return;
        }

//       logger.debug("getRtpInput : " + context.getStatistics());

        // Check if packet is not empty
        boolean hasData = (rtpPacket.getLength() > 0);
        if (!hasData) {
            if (logger.isDebugEnabled()) {
                logger.debug("RTP Channel " + this.context.getStatistics().getSsrc() + " dropped packet because payload was empty.");
            }
            return;
        }

        /*
        // Process incoming packet
        RtpInboundHandlerPacketReceivedContext txContext = new RtpInboundHandlerPacketReceivedContext(rtpPacket);
        this.fsm.fire(RtpInboundHandlerEvent.PACKET_RECEIVED, txContext);
        */

        boolean toOtherSession = false;

        if (sessionInfo.getConferenceId() != null) {

            RoomInfo roomInfo = RoomManager.getInstance().getRoomInfo(sessionInfo.getConferenceId());
            if (roomInfo != null) {
                String otherSessionId = roomInfo.getOtherSession(sessionInfo.getSessionId());
                if (otherSessionId != null) {
                    SessionInfo otherSession = SessionManager.findSession(otherSessionId);
                    if (otherSession != null) {

                        byte[] payload = new byte[rtpPacket.getPayloadLength()];
                        rtpPacket.readRegionToBuff(rcvPktLength - rtpPacket.getPayloadLength(), rtpPacket.getPayloadLength(), payload);

                        otherSession.getJitterSender().put(rtpPacket.getSeqNumber(), payload);
                        toOtherSession = true;

                        payload = null;
                    }
                }
            }
        }

        if (!toOtherSession) {
            if (sessionInfo.getRtpPacket() == null) {

                RtpPacket sentPacket = new RtpPacket(rcvPktLength, true);
                sessionInfo.setRtpPacket(sentPacket);

                logger.info("Jitter vocoder {}", sessionInfo.getJitterSender().getVocoder());

                String audioFilename = AppInstance.getInstance().getPromptConfig().getWaitingPrompt(sessionInfo.getJitterSender().getVocoder());
                if (audioFilename == null) {
                    audioFilename = "test.alaw";
                }

                AudioFileReader fileReader = new AudioFileReader(audioFilename);
                fileReader.load();

                if (sessionInfo.getJitterSender().getVocoder() == Vocoder.VOCODER_AMR_WB) {
                    byte[] header = new byte[9];    // #!AMR-WB\a
                    fileReader.get(header, header.length);
                    header = null;
                }
                else if (sessionInfo.getJitterSender().getVocoder() == Vocoder.VOCODER_AMR_NB) {
                    byte[] header = new byte[6];    // #!AMR\a
                    fileReader.get(header, header.length);
                    header = null;
                }

                sessionInfo.setFileReader(fileReader);
            }
            else {

                byte[] payload = null;

                if (sessionInfo.getJitterSender().getVocoder() == Vocoder.VOCODER_AMR_WB) {
                    payload = sessionInfo.getFileReader().getAMRWBPayload();
                }
                else if (sessionInfo.getJitterSender().getVocoder() == Vocoder.VOCODER_AMR_NB) {
                    payload = sessionInfo.getFileReader().getAMRNBPayload();
                }
                else {
                    payload = new byte[rtpPacket.getPayloadLength()];
                    sessionInfo.getFileReader().get(payload, rtpPacket.getPayloadLength());
                }


                if (payload != null) {
                    sessionInfo.getJitterSender().put(-1, payload);
                    payload = null;
                }
            }

        }


        // Relay RTP Packet
        //sessionInfo.udpClient.send( rcvPktBuf );
        //sessionInfo.udpClient.send(sessionInfo.getRtpPacket().getRawData());

        //logger.debug( "-> UDP ({}:{}) size={}", sessionInfo.getSdpDeviceInfo().getRemoteIp(), sessionInfo.getSdpDeviceInfo().getRemotePort(), rcvPktBuf.length);

        rtpPacket.getBuffer().clear();
        rtpPacket = null;
    }
}
