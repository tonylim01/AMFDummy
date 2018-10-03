package media.platform.amf.rtpcore.core.rtp.rtcp;


import media.platform.amf.rtpcore.core.network.deprecated.UdpManager;
import media.platform.amf.rtpcore.core.network.deprecated.channel.MultiplexedChannel;
import media.platform.amf.rtpcore.core.rtp.statistics.RtpStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import media.platform.amf.rtpcore.core.rtp.rtp.RtpListener;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.DatagramChannel;

public class RtcpChannel extends MultiplexedChannel {

	private static final Logger logger = LoggerFactory.getLogger( RtcpChannel.class);

	// Core elements
	private final UdpManager udpManager;

	// Channel attribute
	private int channelId;
	private boolean bound;

	// Protocol handler pipeline
	private static final int STUN_PRIORITY = 3; // a packet each 400ms
	private static final int RTCP_PRIORITY = 2; // a packet each 5s
	private static final int DTLS_PRIORITY = 1; // only for a handshake
	
	private RtcpHandler rtcpHandler;

	// WebRTC
	private boolean ice;
	private boolean secure;
	
	// Listeners
	private RtpListener rtpListener;

	public RtcpChannel(int channelId, RtpStatistics statistics, UdpManager udpManager) {
		// Initialize MultiplexedChannel elements
		super();

		// Core elements
		this.udpManager = udpManager;

		// Channel attributes
		this.channelId = channelId;
		this.bound = false;

		// Protocol Handler pipeline
		this.rtcpHandler = new RtcpHandler(udpManager.getScheduler(), statistics);

		// WebRTC
		this.secure = false;
	}

	public void setRemotePeer(SocketAddress remotePeer) {
		if (this.dataChannel != null) {
			if (this.dataChannel.isConnected()) {
				try {
					this.dataChannel.disconnect();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}

			boolean connectNow = this.udpManager.connectImmediately((InetSocketAddress) remotePeer);
			if (connectNow) {
				try {
					this.dataChannel.connect(remotePeer);
				} catch (IOException e) {
					logger.error("Can not connect to remote address. Check that you are not using local address (127.0.0.X)", e);
				}
			}
		}
	}
	
	public void setRtpListener(RtpListener rtpListener) {
		this.rtpListener = rtpListener;
	}
	
	public boolean isAvailable() {
		// The channel is available is is connected
		boolean available = this.dataChannel != null && this.dataChannel.isConnected();
		// In case of WebRTC calls the DTLS handshake must be completed
		return available;
	}

	public boolean isBound() {
		return bound;
	}

	private void onBinding() {
		// Set protocol handler priorities
		this.rtcpHandler.setPipelinePriority(RTCP_PRIORITY);

		// Protocol Handler pipeline
		this.rtcpHandler.setChannel(this.dataChannel);
		this.handlers.addHandler(this.rtcpHandler);


		this.rtcpHandler.joinRtpSession();
	}

	/**
	 * Binds the channel to an address and port
	 * 
	 * @param isLocal
	 *            whether the connection is local or not
	 * @param port
	 *            The RTCP port. Usually the RTP channel gets the even port and
	 *            RTCP channel get the next port.
	 * @throws IOException
	 *             When the channel cannot be openend or bound
	 */
	public void bind(boolean isLocal, int port) throws IOException {
		try {
			// Open this channel with UDP Manager on first available address
			this.selectionKey = udpManager.open(this);
			this.dataChannel = (DatagramChannel) this.selectionKey.channel();
		} catch (IOException e) {
			throw new SocketException(e.getMessage());
		}

		// activate media elements
		onBinding();

		// bind data channel
		this.udpManager.bind(this.dataChannel, port, isLocal);
		this.bound = true;
	}

	@Deprecated
	public void bind(DatagramChannel channel) throws SocketException {
		// External channel must be bound already
		if (!channel.socket().isBound()) {
			throw new SocketException("Datagram channel is not bound!");
		}

		try {
			// Register the channel on UDP Manager
			this.selectionKey = udpManager.open(channel, this);
			this.dataChannel = channel;
		} catch (IOException e) {
			throw new SocketException(e.getMessage());
		}

		// activate media elements
		onBinding();
		this.bound = true;
	}
	
	/**
	 * Checks whether the channel is secure or not.
	 * 
	 * @return Whether the channel handles regular RTCP traffic or SRTCP (secure).
	 */
	public boolean isSecure() {
		return secure;
	}
	



	@Override
	public void close() {
		/*
		 * Instruct the RTCP handler to leave the RTP session.
		 * 
		 * This will result in scheduling an RTCP BYE to be sent. Since the BYE
		 * is not sent right away, the datagram channel can only be closed once
		 * the BYE has been sent. So, the handler is responsible for closing the
		 * channel.
		 */
		this.rtcpHandler.leaveRtpSession();
		this.bound = false;
		super.close();
		reset();
	}
	
	public void reset() {
		this.rtcpHandler.reset();
		
	}



}
