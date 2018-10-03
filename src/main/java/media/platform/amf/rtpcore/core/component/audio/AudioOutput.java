package media.platform.amf.rtpcore.core.component.audio;

import media.platform.amf.rtpcore.core.concurrent.ConcurrentCyclicFIFO;
import media.platform.amf.rtpcore.core.scheduler.PriorityQueueScheduler;
import media.platform.amf.rtpcore.core.component.AbstractSink;
import media.platform.amf.rtpcore.core.component.AbstractSource;
import media.platform.amf.rtpcore.core.spi.memory.Frame;

public class AudioOutput extends AbstractSource {

	private static final long serialVersionUID = -5988244809612104056L;

	private int outputId;
	private ConcurrentCyclicFIFO<Frame> buffer = new ConcurrentCyclicFIFO<Frame>();

	/**
	 * Creates new instance with default name.
	 */
	public AudioOutput(PriorityQueueScheduler scheduler, int outputId) {
		super("compound.output", scheduler, PriorityQueueScheduler.OUTPUT_QUEUE);
		this.outputId = outputId;
	}

	public int getOutputId() {
		return outputId;
	}

	public void join(AbstractSink sink) {
		connect(sink);
	}

	public void unjoin() {
		disconnect();
	}

	@Override
	public Frame evolve(long timestamp) {
		return buffer.poll();
	}

	@Override
	public void stop() {
		while (buffer.size() > 0) {
			Frame frame = buffer.poll();
			if(frame != null) {
			    frame.recycle();
			}
		}
		super.stop();
	}

	public void resetBuffer() {
		while (buffer.size() > 0) {
			buffer.poll().recycle();
		}
	}

	public void offer(Frame frame) {
		if (buffer.size() > 1) {
			buffer.poll().recycle();
		}
		buffer.offer(frame);
	}

	@Override
	public void perform(Frame frame) {

	}
}
