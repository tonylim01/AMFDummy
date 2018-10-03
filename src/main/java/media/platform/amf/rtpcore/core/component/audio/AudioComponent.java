

package media.platform.amf.rtpcore.core.component.audio;

import media.platform.amf.rtpcore.core.concurrent.ConcurrentMap;
import media.platform.amf.rtpcore.core.spi.format.AudioFormat;
import media.platform.amf.rtpcore.core.spi.format.FormatFactory;
import media.platform.amf.rtpcore.core.spi.memory.Frame;
import media.platform.amf.rtpcore.core.spi.memory.Memory;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;


public class AudioComponent {

	// Format of the output stream.
    private final static AudioFormat FORMAT = FormatFactory.createAudioFormat("LINEAR", 8000, 16, 1);
    private final static long PERIOD = 20000000L;
    private final static int PACKET_SIZE = (int) (PERIOD / 1000000) * FORMAT.getSampleRate() / 1000 * FORMAT.getSampleSize() / 8;

    // Component State
    private final int componentId;
	private final ConcurrentMap<AudioInput> inputs;
	private final ConcurrentMap<AudioOutput> outputs;
	
	protected final AtomicBoolean shouldRead;
	protected final AtomicBoolean shouldWrite;

	// Mixing State
	private final int[] data;
	final AtomicBoolean first;

	/**
	 * Creates new instance with default name.
	 */
	public AudioComponent(int componentId) {
	    // Component State
		this.componentId = componentId;
		this.inputs = new ConcurrentMap<AudioInput>();
		this.outputs = new ConcurrentMap<AudioOutput>();
		this.shouldRead = new AtomicBoolean(false);
		this.shouldWrite = new AtomicBoolean(false);

		// Mixing State
		this.data = new int[PACKET_SIZE / 2];
		this.first = new AtomicBoolean(false);
	}

	public int getComponentId() {
		return componentId;
	}

	public void updateMode(boolean shouldRead, boolean shouldWrite) {
		this.shouldRead.set(shouldRead);
		this.shouldWrite.set(shouldWrite);
	}

	public void addInput(AudioInput input) {
		inputs.put(input.getInputId(), input);
	}

	public void addOutput(AudioOutput output) {
		outputs.put(output.getOutputId(), output);
	}

	public void remove(AudioInput input) {
		inputs.remove(input.getInputId());
	}

	public void remove(AudioOutput output) {
		outputs.remove(output.getOutputId());
	}

    public void perform() {
        this.first.set(true);

        final Iterator<AudioInput> activeInputs = this.inputs.valuesIterator();
        while (activeInputs.hasNext()) {
            final AudioInput input = activeInputs.next();
            final Frame inputFrame = input.poll();

            if (inputFrame != null) {
                try {
                    final byte[] dataArray = inputFrame.getData();

                    int inputIndex = 0;
                    for (int inputCount = 0; inputCount < dataArray.length; inputCount += 2) {
                        this.data[inputIndex++] = (short) (((dataArray[inputCount + 1]) << 8) | (dataArray[inputCount] & 0xff));
                    }

                    if (first.get()) {
                        this.first.set(false);
                    }
                } finally {
                    inputFrame.recycle();
                }
            }
        }
    }

	public int[] getData() {
		if (!this.shouldRead.get()) {
			return null;
		}

		if (first.get()) {
			return null;
		}

		return data;
	}

	public void offer(int[] data) {
		if (!this.shouldWrite.get()) {
			return;
		}

		final Frame outputFrame = Memory.allocate(PACKET_SIZE);
		final byte[] dataArray = outputFrame.getData();

		int outputIndex = 0;
		for (int outputCount = 0; outputCount < data.length;) {
			dataArray[outputIndex++] = (byte) (data[outputCount]);
			dataArray[outputIndex++] = (byte) (data[outputCount++] >> 8);
		}

		outputFrame.setOffset(0);
		outputFrame.setLength(PACKET_SIZE);
		outputFrame.setDuration(PERIOD);
		outputFrame.setFormat(FORMAT);

		final Iterator<AudioOutput> activeOutputs = outputs.valuesIterator();
		while (activeOutputs.hasNext()) {
			AudioOutput output = activeOutputs.next();
			if (!activeOutputs.hasNext()) {
				output.offer(outputFrame);
			} else {
				output.offer(outputFrame.clone());
			}
			output.wakeup();
		}
	}
}
