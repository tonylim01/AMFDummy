/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file NettyRTPServer.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rtpcore.Process;


import io.netty.channel.*;
import media.platform.amf.rtpcore.core.rtp.jitter.FixedJitterBuffer;
import media.platform.amf.rtpcore.core.rtp.jitter.JitterBuffer;
import media.platform.amf.rtpcore.core.rtp.netty.RtpInboundHandler;
import media.platform.amf.rtpcore.core.rtp.netty.RtpInboundHandlerGlobalContext;
import media.platform.amf.rtpcore.core.rtp.rtp.RTPInput;
import media.platform.amf.rtpcore.core.rtp.rtp.RtpClock;
import media.platform.amf.rtpcore.core.rtp.rtp.statistics.RtpStatistics;
import media.platform.amf.rtpcore.core.scheduler.Clock;
import media.platform.amf.rtpcore.core.scheduler.WallClock;
import media.platform.amf.rtpcore.core.sdp.format.AVProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import static org.mockito.Mockito.mock;

public class NettyRTPServer {

    private static final Logger logger = LoggerFactory.getLogger( NettyRTPServer.class );
    private int port;
    Bootstrap b;
    NioEventLoopGroup group;
    private static NettyRTPServer nettyRTPServer = null;

    public NettyRTPServer() {
    }

    public NettyRTPServer run() throws Exception {

        group = new NioEventLoopGroup();

        try {
            b = new Bootstrap();
            b.group(group).channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new ChannelInitializer<NioDatagramChannel>() {

                        @Override
                        public void initChannel(final NioDatagramChannel ch) throws Exception {

                            //final Clock clock = mock( Clock.class);
                            final Clock clock = new WallClock();
                            final RtpStatistics statistics = mock( RtpStatistics.class);
                            //final JitterBuffer jitterBuffer = mock( JitterBuffer.class);

                            RtpClock rtpClock = new RtpClock(clock);
                            final JitterBuffer jitterBuffer = new FixedJitterBuffer(rtpClock, 500);
                            final RTPInput rtpInput = mock( RTPInput.class);
                            final ChannelPipeline pipeline = ch.pipeline();

                            final RtpInboundHandlerGlobalContext context = new RtpInboundHandlerGlobalContext( clock, statistics, jitterBuffer, rtpInput);
                            context.setReceivable( true );
                            context.setLoopable( false );
                            context.setFormats( AVProfile.audio );

                            //pipeline.addLast("logger", new LoggingHandler( LogLevel.DEBUG));
                            pipeline.addLast(new RtpInboundHandler( context));
                        }
                    });

        } finally {
            logger.info("In Server Finally");
        }
        return null;
    }

	// RTP 수신 부
    public Channel addBindPort(String ip, int port)
    {
        Integer pPort = port;
        InetAddress address  = null;
        Channel ch = null;

        logger.debug("IP [{}] Port [{}]",ip,port);

        try {
            address = InetAddress.getByName( ip);
            logger.debug("waiting for message port : " + String.format(pPort.toString())+ " IP : " +String.format( address.toString()));
            ch = b.bind( address, port).sync().channel();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return ch;
    }

	// RTP 전송 부
    public UdpClient addConnectPort(String ip, int port)
    {
        logger.debug("addConnectPort IP [{}] Port [{}]",ip,port);

        UdpClient udpClient = null;
        try {
            InetAddress addr = InetAddress.getByName(ip);
            udpClient = new UdpClient( addr,port );
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        logger.debug("addConnectPort IP [{}] Port [{}] Success",ip,port);

        return udpClient;
    }

    public void removeBindPort(Channel ch)
    {
        ch.close();
    }

}
