package media.platform.amf.rtpcore.core.component.audio;

import media.platform.amf.rtpcore.core.concurrent.ConcurrentMap;
import media.platform.amf.rtpcore.core.scheduler.PriorityQueueScheduler;
import media.platform.amf.rtpcore.core.scheduler.Task;
import media.platform.amf.rtpcore.core.spi.format.AudioFormat;
import media.platform.amf.rtpcore.core.spi.format.FormatFactory;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class AudioSplitter {

	// scheduler for mixer job scheduling
	private final PriorityQueueScheduler scheduler;

	// the format of the output stream.
	private static final AudioFormat FORMAT = FormatFactory.createAudioFormat("LINEAR", 8000, 16, 1);
	private static final long PERIOD = 20000000L;
	private static final int PACKET_SIZE = (int) (PERIOD / 1000000) * FORMAT.getSampleRate() / 1000 * FORMAT.getSampleSize() / 8;

	// The pools of components
	private final ConcurrentMap<AudioComponent> insideComponents;
	private final ConcurrentMap<AudioComponent> outsideComponents;

	private final InsideMixTask insideMixer;
	private final OutsideMixTask outsideMixer;
	private final AtomicBoolean started;
	private final AtomicLong mixCount;

	// gain value
	private double gain = 1.0;

	public AudioSplitter(PriorityQueueScheduler scheduler) {
		this.scheduler = scheduler;
		this.insideMixer = new InsideMixTask();
		this.outsideMixer = new OutsideMixTask();
		this.insideComponents = new ConcurrentMap<AudioComponent>();
		this.outsideComponents = new ConcurrentMap<AudioComponent>();
		this.started = new AtomicBoolean(false);
		this.mixCount = new AtomicLong(0);
	}

	public void addInsideComponent(AudioComponent component) {
		insideComponents.put(component.getComponentId(), component);
	}

	public void addOutsideComponent(AudioComponent component) {
		outsideComponents.put(component.getComponentId(), component);
	}

	protected int getPacketSize() {
		return PACKET_SIZE;
	}

	/**
	 * Releases inside component
	 * 
	 * @param component
	 */
	public void releaseInsideComponent(AudioComponent component) {
		insideComponents.remove(component.getComponentId());
	}

	/**
	 * Releases outside component
	 * 
	 * @param component
	 */
	public void releaseOutsideComponent(AudioComponent component) {
		outsideComponents.remove(component.getComponentId());
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
	    if(!this.started.get()) {
	        mixCount.set(0);
	        started.set(true);
	        scheduler.submit(insideMixer, PriorityQueueScheduler.MIXER_MIX_QUEUE);
	        scheduler.submit(outsideMixer, PriorityQueueScheduler.MIXER_MIX_QUEUE);
	    }
	}

	public void stop() {
	    if(this.started.get()) {
	        started.set(false);
	        insideMixer.cancel();
	        outsideMixer.cancel();
	    }
	}

	private class InsideMixTask extends Task {

	    private final int[] total = new int[PACKET_SIZE / 2];

		public InsideMixTask() {
			super();
		}

		@Override
		public int getQueueNumber() {
			return PriorityQueueScheduler.MIXER_MIX_QUEUE;
		}

		@Override
		public long perform() {
			// summarize all
			boolean first = true;

			final Iterator<AudioComponent> insideRIterator = insideComponents.valuesIterator();
			while (insideRIterator.hasNext()) {
				AudioComponent component = insideRIterator.next();
				component.perform();
				int[] current = component.getData();
				if (current != null) {
					if (first) {
						System.arraycopy(current, 0, total, 0, total.length);
						first = false;
					} else {
						for (int i = 0; i < total.length; i++) {
							total[i] += current[i];
						}
					}
				}
			}

			if (first) {
				scheduler.submit(this, PriorityQueueScheduler.MIXER_MIX_QUEUE);
				mixCount.incrementAndGet();
				return 0;
			}

			int minValue = 0;
			int maxValue = 0;
			for (int i = 0; i < total.length; i++) {
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

			double currGain = gain;
			if (maxValue > Short.MAX_VALUE) {
				currGain = (currGain * (double) Short.MAX_VALUE) / (double) maxValue;
			}

			for (int i = 0; i < total.length; i++) {
				total[i] = (short) Math.round((double) total[i] * currGain);
			}

			// get data for each component
			final Iterator<AudioComponent> outsideSIterator = outsideComponents.valuesIterator();
			while (outsideSIterator.hasNext()) {
				AudioComponent component = outsideSIterator.next();
				component.offer(total);
			}

			scheduler.submit(this, PriorityQueueScheduler.MIXER_MIX_QUEUE);
			mixCount.incrementAndGet();
			return 0;
		}
	}

	private class OutsideMixTask extends Task {
	    
		private final int[] total = new int[PACKET_SIZE / 2];

		public OutsideMixTask() {
			super();
		}

		@Override
		public int getQueueNumber() {
			return PriorityQueueScheduler.MIXER_MIX_QUEUE;
		}

		@Override
		public long perform() {
			// summarize all
			boolean first = true;

			final Iterator<AudioComponent> outsideRIterator = outsideComponents.valuesIterator();
			while (outsideRIterator.hasNext()) {
				AudioComponent component = outsideRIterator.next();
				component.perform();
				int[] current = component.getData();
				if (current != null) {
					if (first) {
						System.arraycopy(current, 0, total, 0, total.length);
						first = false;
					} else {
						for (int i = 0; i < total.length; i++) {
							total[i] += current[i];
						}
					}
				}
			}

			if (first) {
				scheduler.submit(this, PriorityQueueScheduler.MIXER_MIX_QUEUE);
				mixCount.incrementAndGet();
				return 0;
			}

			int minValue = 0;
			int maxValue = 0;
			for (int i = 0; i < total.length; i++) {
				if (total[i] > maxValue) {
					maxValue = total[i];
				} else if (total[i] < minValue) {
					minValue = total[i];
				}
			}

			minValue = 0 - minValue;
			if (minValue > maxValue) {
				maxValue = minValue;
			}

			double currGain = gain;
			if (maxValue > Short.MAX_VALUE) {
				currGain = (currGain * Short.MAX_VALUE) / maxValue;
			}

			for (int i = 0; i < total.length; i++) {
				total[i] = (short) Math.round((double) total[i] * currGain);
			}

			// get data for each component
			final Iterator<AudioComponent> insideSIterator = insideComponents.valuesIterator();
			while (insideSIterator.hasNext()) {
				AudioComponent component = insideSIterator.next();
				component.offer(total);
			}

			scheduler.submit(this, PriorityQueueScheduler.MIXER_MIX_QUEUE);
			mixCount.incrementAndGet();
			return 0;
		}
	}
}
