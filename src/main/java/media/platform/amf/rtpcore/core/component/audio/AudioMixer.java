package media.platform.amf.rtpcore.core.component.audio;

import media.platform.amf.rtpcore.core.concurrent.ConcurrentMap;
import media.platform.amf.rtpcore.core.scheduler.PriorityQueueScheduler;
import media.platform.amf.rtpcore.core.scheduler.Task;
import media.platform.amf.rtpcore.core.spi.format.AudioFormat;
import media.platform.amf.rtpcore.core.spi.format.FormatFactory;

import java.util.Iterator;

public class AudioMixer {
	// scheduler for mixer job scheduling
	private PriorityQueueScheduler scheduler;

	// the format of the output stream.
	private AudioFormat format = FormatFactory.createAudioFormat("LINEAR", 8000, 16, 1);

	// The pool of components
	private ConcurrentMap<AudioComponent> components = new ConcurrentMap<AudioComponent>();

	private long period = 20000000L;
	private int packetSize = (int) (period / 1000000) * format.getSampleRate() / 1000 * format.getSampleSize() / 8;

	private MixTask mixer;
	private volatile boolean started = false;

	public long mixCount = 0;

	// gain value
	private double gain = 1.0;

	public AudioMixer(PriorityQueueScheduler scheduler) {
		this.scheduler = scheduler;
		this.mixer = new MixTask();
	}

	public void addComponent(AudioComponent component) {
		components.put(component.getComponentId(), component);
	}

	protected int getPacketSize() {
		return this.packetSize;
	}

	/**
	 * Releases unused input stream
	 * 
	 * @param input
	 *            the input stream previously created
	 */
	public void release(AudioComponent component) {
		components.remove(component.getComponentId());
	}

	/**
	 * Modify gain of the output stream.
	 * 
	 * @param gain
	 *            the new value of the gain in dBm.
	 */
	public void setGain(double gain) {
		this.gain = gain > 0 ? gain * 1.26 : gain == 0 ? 1 : 1 / (gain * 1.26);
	}

	public void start() {
		mixCount = 0;
		started = true;
		scheduler.submit(mixer, PriorityQueueScheduler.MIXER_MIX_QUEUE);
	}

	public void stop() {
		started = false;
		mixer.cancel();
	}

	private class MixTask extends Task {
		int sourcesCount = 0;
		private int i;
		private int minValue = 0;
		private int maxValue = 0;
		private double currGain = 0;
		private int[] total = new int[packetSize / 2];
		private int[] current;

		public MixTask() {
			super();
		}

		@Override
		public int getQueueNumber() {
			return PriorityQueueScheduler.MIXER_MIX_QUEUE;
		}

		@Override
		public long perform() {
			// summarize all
			sourcesCount = 0;

			Iterator<AudioComponent> activeComponents = components.valuesIterator();
			while (activeComponents.hasNext()) {
				AudioComponent component = activeComponents.next();
				component.perform();
				current = component.getData();
				if (current != null) {
					if (sourcesCount == 0) {
						System.arraycopy(current, 0, total, 0, total.length);
					} else {
						for (i = 0; i < total.length; i++) {
							total[i] += current[i];
						}
					}
					sourcesCount++;
				}
			}

			if (sourcesCount == 0) {
				scheduler.submit(this, PriorityQueueScheduler.MIXER_MIX_QUEUE);
				mixCount++;
				System.out.println( "sourcesCount : " + sourcesCount +" mixCount :" + mixCount);
				return 0;
			}

			minValue = 0;
			maxValue = 0;
			for (i = 0; i < total.length; i++) {
				if (total[i] > maxValue) {
					maxValue = total[i];
				} else if (total[i] < minValue) {
					minValue = total[i];
				}
			}

			if (minValue > 0) {
				minValue = 0 - minValue;
			}

			if (minValue > maxValue) {
				maxValue = minValue;
			}

			System.out.println( "gain :"+ gain +"minValue :"+minValue + "maxValue :" + maxValue);

			currGain = gain;
			if (maxValue > Short.MAX_VALUE) {
				currGain = (currGain * (double) Short.MAX_VALUE) / (double) maxValue;
			}

			for (i = 0; i < total.length; i++) {
				total[i] = (short) ((double) total[i] * currGain);
			}

			// get data for each component
			activeComponents = components.valuesIterator();
			while (activeComponents.hasNext()) {
				AudioComponent component = activeComponents.next();
				current = component.getData();
				if (current != null && sourcesCount > 1) {
					for (i = 0; i < total.length; i++) {
						current[i] = total[i] - (short) ((double) current[i] * currGain);
					}
					component.offer(current);
				} else if (current == null) {
					component.offer(total);
				}
			}

			scheduler.submit(this, PriorityQueueScheduler.MIXER_MIX_QUEUE);
			mixCount++;
			return 0;
		}
	}
}
