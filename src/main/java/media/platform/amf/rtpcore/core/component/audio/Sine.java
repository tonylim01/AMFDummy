
package media.platform.amf.rtpcore.core.component.audio;

import media.platform.amf.rtpcore.core.scheduler.PriorityQueueScheduler;
import media.platform.amf.rtpcore.core.component.AbstractSource;
import media.platform.amf.rtpcore.core.spi.ComponentType;
import media.platform.amf.rtpcore.core.spi.format.AudioFormat;
import media.platform.amf.rtpcore.core.spi.format.FormatFactory;
import media.platform.amf.rtpcore.core.spi.format.Formats;
import media.platform.amf.rtpcore.core.spi.memory.Frame;
import media.platform.amf.rtpcore.core.spi.memory.Memory;


public class Sine extends AbstractSource {

	private static final long serialVersionUID = -886146896423710570L;

	//the format of the output stream.
    private final static AudioFormat LINEAR_AUDIO = FormatFactory.createAudioFormat("LINEAR", 8000, 16, 1);
    private final static Formats formats = new Formats();

    private volatile long period = 20000000L;
    private int packetSize = (int)(period / 1000000) * LINEAR_AUDIO.getSampleRate()/1000 * LINEAR_AUDIO.getSampleSize() / 8;

    private int f;
    private short A = Short.MAX_VALUE;
    private double dt;
    private double time;

    private AudioInput input;
    
    static {
        formats.add(LINEAR_AUDIO);
    }
    
    public Sine(PriorityQueueScheduler scheduler) {
        super("sine.generator", scheduler, PriorityQueueScheduler.INPUT_QUEUE);
        //number of seconds covered by one sample
        dt = 1. / LINEAR_AUDIO.getSampleRate();
        
        this.input=new AudioInput(ComponentType.SINE.getType(),packetSize);
        this.connect(this.input); 
    }

    public AudioInput getAudioInput()
    {
    	return this.input;
    }
    
    public void setAmplitude(short A) {
        this.A = A;
    }

    public short getAmplitude() {
        return A;
    }

    public void setFrequency(int f) {
        this.f = f;
    }

    public int getFrequency() {
        return f;
    }

    private short getValue(double t) {
        return (short) (A * Math.sin(2 * Math.PI * f * t));
    }

    @Override
    public Frame evolve(long timestamp) {
        Frame frame = Memory.allocate(packetSize);
        int k = 0;

        int frameSize = packetSize / 2;

        byte[] data = frame.getData();
        for (int i = 0; i < frameSize; i++) {
            short v = getValue(time + dt * i);
            data[k++] = (byte) v;
            data[k++] = (byte) (v >> 8);
        }

        frame.setOffset(0);
        frame.setLength(packetSize);
        frame.setDuration(period);
        frame.setFormat(LINEAR_AUDIO);
        
        time += ((double) period) / 1000000000.0;
        return frame;
    }

    @Override
    public void perform(Frame frame) {

    }
}