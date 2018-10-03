package media.platform.amf.rtpcore.core.rtp.rtp;

import media.platform.amf.rtpcore.core.rtp.rtp.statistics.RtpStatistics;
import media.platform.amf.rtpcore.core.scheduler.PriorityQueueScheduler;
import media.platform.amf.rtpcore.core.sdp.format.AVProfile;
import media.platform.amf.rtpcore.core.sdp.format.RTPFormat;
import media.platform.amf.rtpcore.core.sdp.format.RTPFormats;
import media.platform.amf.rtpcore.core.spi.memory.Frame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.net.PortUnreachableException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
public class RtpTransmitter {

	private static final Logger logger = LoggerFactory.getLogger( RtpTransmitter.class);
	
	// Channel properties
	private DatagramChannel channel;
	private final RtpClock rtpClock;
	private final RtpStatistics statistics;
	private boolean dtmfSupported;
	private final RTPOutput rtpOutput;

	// Packet representations with internal buffers
	private final RtpPacket rtpPacket = new RtpPacket(RtpPacket.RTP_PACKET_MAX_SIZE, true);
	private final RtpPacket oobPacket = new RtpPacket(RtpPacket.RTP_PACKET_MAX_SIZE, true);
	
	// WebRTC
	private boolean secure;

	// Details of a transmitted packet
	private RTPFormats formats;
	private RTPFormat currentFormat;
	private long timestamp;
	private long dtmfTimestamp;
	private long dtmfDuration;
	private int sequenceNumber;

	public RtpTransmitter(final PriorityQueueScheduler scheduler, final RtpClock clock, final RtpStatistics statistics) {
		this.rtpClock = clock;
		this.statistics = statistics;
		this.dtmfSupported = false;
		this.rtpOutput = new RTPOutput(scheduler, this);
		this.sequenceNumber = 0;
		this.dtmfTimestamp = -1;
		this.dtmfDuration = -1;
		this.timestamp = -1;
		this.formats = null;
		this.secure = false;
	}
	
	public void setFormatMap(final RTPFormats rtpFormats) {
		this.dtmfSupported = rtpFormats.contains( AVProfile.telephoneEventsID);
		this.formats = rtpFormats;
	}
	
	public RTPOutput getRtpOutput() {
		return rtpOutput;
	}

	public void activate() {
		this.rtpOutput.activate();
	}
	
	public void deactivate() {
		this.rtpOutput.deactivate();
		this.dtmfSupported = false;
	}
	
	public void setChannel(final DatagramChannel channel) {
		this.channel = channel;
	}
	
	private boolean isConnected() {
		return this.channel != null && this.channel.isConnected();
	}
	
	private void disconnect() throws IOException {
		if(this.channel != null) {
			this.channel.disconnect();
		}
	}
	
	public void reset() {
		deactivate();
		clear();
	}
	
	public void clear() {
		this.timestamp = -1;
		this.dtmfTimestamp = -1;
		this.dtmfDuration = -1;
		// Reset format in case connection is reused.
		// Otherwise it would point to incorrect codec.
		this.currentFormat = null;
	}
	
	private void send(RtpPacket packet) throws IOException {
		// Do not send data while DTLS handshake is ongoing. WebRTC calls only.

		// Secure RTP packet. WebRTC calls only. 
		// SRTP handler returns null if an error occurs
		ByteBuffer buffer = packet.getBuffer();

		if(packet != null) {
			channel.send(buffer, channel.socket().getRemoteSocketAddress());
			// send RTP packet to the network and update statistics for RTCP
			statistics.onRtpSent(packet);
			
		}
	}
	
	public void sendDtmf(Frame frame) {
		if (!this.dtmfSupported) {
			frame.recycle();
			return;
		}
		
		// ignore frames with duplicate timestamp
		if (frame.getTimestamp() / 1000000L == dtmfTimestamp) {
			frame.recycle();
			return;
		}

        // // convert to milliseconds first
        // dtmfTimestamp = frame.getTimestamp() / 1000000L;
        // // convert to rtp time units
        // dtmfTimestamp = rtpClock.convertToRtpTime(dtmfTimestamp);
        // oobPacket.wrap(false, AVProfile.telephoneEventsID, this.sequenceNumber++, dtmfTimestamp, this.statistics.getSsrc(),
        // frame.getData(), frame.getOffset(), frame.getLength());

		// hrosa - Hack to workaround MEDIA-61: https://telestax.atlassian.net/browse/MEDIA-61
		long duration = (frame.getData()[2]<<8) | (frame.getData()[3] & 0xFF); 
		boolean toneChanged = false;
		
		if(this.dtmfDuration == -1 || this.dtmfDuration > duration) {
		    this.dtmfTimestamp = this.timestamp;
		    toneChanged = true;
		}
		this.dtmfDuration = duration;
		
		oobPacket.wrap(toneChanged, AVProfile.telephoneEventsID, this.sequenceNumber++, this.dtmfTimestamp, this.statistics.getSsrc(), frame.getData(), frame.getOffset(), frame.getLength());
		// end of hack - hrosa
		
		frame.recycle();
		
		try {
			if(isConnected()) {
				send(oobPacket);
			}
		} catch (PortUnreachableException e) {
			try {
				// icmp unreachable received
				// disconnect and wait for new packet
				disconnect();
			} catch (IOException ex) {
				logger.error(ex.getMessage(), ex);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void send(Frame frame) {
		// discard frame if format is unknown
		if (frame.getFormat() == null) {
			frame.recycle();
			return;
		}

		// determine current RTP format if it is unknown
		if (currentFormat == null || !currentFormat.getFormat().matches(frame.getFormat())) {
			currentFormat = formats.getRTPFormat(frame.getFormat());
			// discard packet if format is still unknown
			if (currentFormat == null) {
				frame.recycle();
				return;
			}
			// update clock rate
			rtpClock.setClockRate(currentFormat.getClockRate());
		}

		// ignore frames with duplicate timestamp
		if (frame.getTimestamp() / 1000000L == timestamp) {
			frame.recycle();
			return;
		}

		// convert to milliseconds first
		timestamp = frame.getTimestamp() / 1000000L;
		// convert to rtp time units
		timestamp = rtpClock.convertToRtpTime(timestamp);
		rtpPacket.wrap(false, currentFormat.getID(), this.sequenceNumber++, timestamp, this.statistics.getSsrc(), frame.getData(), frame.getOffset(), frame.getLength());

		frame.recycle();
		try {
			if (isConnected()) {
				send(rtpPacket);
			}
		} catch (PortUnreachableException e) {
			// icmp unreachable received
			// disconnect and wait for new packet
			try {
				disconnect();
			} catch (IOException ex) {
				logger.error(ex.getMessage(), ex);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

}
