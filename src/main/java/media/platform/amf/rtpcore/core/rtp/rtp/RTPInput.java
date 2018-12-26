package media.platform.amf.rtpcore.core.rtp.rtp;

import media.platform.amf.rtpcore.core.component.AbstractSource;
import media.platform.amf.rtpcore.core.component.audio.AudioInput;
import media.platform.amf.rtpcore.core.rtp.jitter.JitterBuffer;
import media.platform.amf.rtpcore.core.scheduler.PriorityQueueScheduler;
import media.platform.amf.rtpcore.core.spi.format.AudioFormat;
import media.platform.amf.rtpcore.core.spi.format.FormatFactory;
import media.platform.amf.rtpcore.core.spi.memory.Frame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Receiver implementation.
 *
 * The Media source of RTP data.
 */
public class RTPInput extends AbstractSource implements BufferListener {
	
	private static final long serialVersionUID = -737259897530641186L;

    private static final Logger logger = LoggerFactory.getLogger( RTPInput.class);

	private AudioFormat format = FormatFactory.createAudioFormat( "LINEAR", 8000, 16, 1);
	private long period = 20000000L;
    private int packetSize = (int)(period / 1000000) * format.getSampleRate()/1000 * format.getSampleSize() / 8;
    
    //jitter buffer
    private JitterBuffer rxBuffer;
    
	//digital signaling processor
//    private Processor dsp;
           
    protected Integer preEvolveCount=0;
    protected Integer evolveCount=0;

    private AudioInput input;
	/**
     * Creates new receiver.
     */
    public RTPInput(PriorityQueueScheduler scheduler, JitterBuffer jitterBuffer) {
        super("rtpinput", scheduler,PriorityQueueScheduler.INPUT_QUEUE);
        this.rxBuffer=jitterBuffer;        
        input=new AudioInput(1,packetSize);
        this.connect(input);        
    }

    public AudioInput getAudioInput()
    {
    	return this.input;
    }
    
    @Override
    public void reset() {
        super.reset();        
    }
    
    /**
     * Assigns the digital signaling processor of this component.
     * The DSP allows to get more output formats.
     *
     * @param dsp the dsp instance
     */
//    public void setDsp(Processor dsp) {
//        //assign processor
//        this.dsp = dsp;
//    }
    
    /**
     * Gets the digital signaling processor associated with this media source
     *
     * @return DSP instance.
//     */
//    public Processor getDsp() {
//        return this.dsp;
//    }
    public int getPacketsLost() {
        return 0;
    }    

    @Override
    public Frame evolve(long timestamp) {
    	Frame currFrame=rxBuffer.read(timestamp);
    	
    	if(currFrame!=null)
        {
//    		//do the transcoding job
//        	if (dsp != null) {
//        		try
//        		{
//        			currFrame = dsp.process(currFrame,currFrame.getFormat(),format);
//        		}
//        		catch(Exception e)
//        		{
//        			//transcoding error , print error and try to move to next frame
//        			logger.error(e);
//        		}
//        	}
        	
        }
    	
    	return currFrame; 
    }    
    
    /**
     * RX buffer's call back method.
     * 
     * This method is called when rxBuffer is full and it is time to startScheduler
     * transmission to the consumer.
     */
    public void onFill() {
    	this.wakeup();
    }

    @Override
    public void perform(Frame frame) {

    }
}