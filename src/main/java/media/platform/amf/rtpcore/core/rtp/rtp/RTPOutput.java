package media.platform.amf.rtpcore.core.rtp.rtp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import media.platform.amf.rtpcore.core.component.AbstractSink;
import media.platform.amf.rtpcore.core.component.audio.AudioOutput;
import media.platform.amf.rtpcore.core.rtp.RTPDataChannel;
import media.platform.amf.rtpcore.core.scheduler.PriorityQueueScheduler;
import media.platform.amf.rtpcore.core.spi.FormatNotSupportedException;
import media.platform.amf.rtpcore.core.spi.format.AudioFormat;
import media.platform.amf.rtpcore.core.spi.format.FormatFactory;
import media.platform.amf.rtpcore.core.spi.format.Formats;
import media.platform.amf.rtpcore.core.spi.memory.Frame;

import java.io.IOException;

public class RTPOutput extends AbstractSink {

	private static final long serialVersionUID = 3227885808614338323L;

	private static final Logger logger = LoggerFactory.getLogger( RTPOutput.class);

	private AudioFormat format = FormatFactory.createAudioFormat("LINEAR", 8000, 16, 1);

	@Deprecated
	private RTPDataChannel channel;

	private RtpTransmitter transmitter;

	// active formats
	private Formats formats;

	// signaling processor
//	private Processor dsp;

	private AudioOutput output;

	/**
	 * Creates new transmitter
	 */
	@Deprecated
	public RTPOutput(PriorityQueueScheduler scheduler, RTPDataChannel channel) {
		super("Output");
		this.channel = channel;
		output = new AudioOutput(scheduler, 1);
		output.join(this);
	}

	protected RTPOutput(PriorityQueueScheduler scheduler, RtpTransmitter transmitter) {
		super("Output");
		this.transmitter = transmitter;
		output = new AudioOutput(scheduler, 1);
		output.join(this);
	}

	public AudioOutput getAudioOutput() {
		return this.output;
	}

	@Override
	public void activate() {
		output.start();
	}

	@Override
	public void deactivate() {
		output.stop();
	}

	/**
	 * Assigns the digital signaling processor of this component. The DSP allows
	 * to get more output formats.
	 * 
	 * @param dsp
	 *            the dsp instance
	 */

	/**
	 * Gets the digital signaling processor associated with this media source
	 * 
	 * @return DSP instance.
	 */

	public void setFormats(Formats formats) throws FormatNotSupportedException {
		this.formats = formats;
	}

	@Override
	public void onMediaTransfer(Frame frame) throws IOException {
		// do transcoding

		if (this.transmitter != null) {
			this.transmitter.send(frame);
		}

		// XXX deprecated code
		if (this.channel != null) {
			channel.send(frame);
		}

	}
}
