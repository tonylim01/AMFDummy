package media.platform.amf.rtpcore.core.component.audio;

import media.platform.amf.rtpcore.core.concurrent.ConcurrentCyclicFIFO;
import media.platform.amf.rtpcore.core.component.AbstractSink;
import media.platform.amf.rtpcore.core.spi.memory.Frame;
import media.platform.amf.rtpcore.core.spi.memory.Memory;

import java.io.IOException;


public class AudioInput extends AbstractSink {
	
	private static final long serialVersionUID = -6377790166652701617L;

	private int inputId;
    private int limit=3;
    private ConcurrentCyclicFIFO<Frame> buffer = new ConcurrentCyclicFIFO<Frame>();
    private Frame activeFrame=null;
    private byte[] activeData;
    private byte[] oldData;
    private int byteIndex=0;
    private int count=0;
    private int packetSize=0;
    
    /**
     * Creates new stream
     */
    public AudioInput(int inputId,int packetSize) {
        super("compound.input");
        this.inputId=inputId;
        this.packetSize=packetSize;
    }
    
    public int getInputId()
    {
    	return inputId;
    }
    
    public void activate()
    {
    	
    }
    
    public void deactivate()
    {
    	
    }
    
    @Override
    public void onMediaTransfer(Frame frame) throws IOException {
    	//generate frames with correct size here , aggregate frames if needed.
    	//allows to accept several sources with different ptime ( packet time ) 
    	oldData=frame.getData();
    	count=0;
    	while(count<oldData.length)
    	{
    		if(activeData==null)
    		{
    			activeFrame=Memory.allocate(packetSize);
    			activeFrame.setOffset(0);
    			activeFrame.setLength(packetSize);
    			activeData=activeFrame.getData(); 
    			byteIndex=0;
    		}
    		
    		if(oldData.length-count<activeData.length-byteIndex)
    		{
    			System.arraycopy(oldData, count, activeData, byteIndex, oldData.length-count);
    			byteIndex+=oldData.length-count;
    			count=oldData.length;    			
    		}
    		else
    		{
    			System.arraycopy(oldData, count, activeData, byteIndex, activeData.length-byteIndex);
    			count+=activeData.length-byteIndex;
    			
    			if (buffer.size() >= limit) 
            		buffer.poll().recycle();
                
            	buffer.offer(activeFrame);
            	
            	activeFrame=null;
    			activeData=null;    			    			
    		}
    	}
    	
    	frame.recycle();
    }

    /**
     * Indicates the state of the input buffer.
     *
     * @return true if input buffer has no frames.
     */
    public boolean isEmpty() {
        return buffer.size()==0;
    }

    /**
     * Retrieves frame from the input buffer.
     *
     * @return the media frame.
     */
    public Frame poll() {
        return buffer.poll();
    }

    /**
     * Recycles input stream
     */
    public void recycle() {
    	while(buffer.size()>0)
    		buffer.poll().recycle();
    	
    	if(activeFrame!=null)
    		activeFrame.recycle();
    	
        activeFrame=null;
		activeData=null;
		byteIndex=0;		        
    }
    
    public void resetBuffer()
    {
    	while(buffer.size()>0)
    		buffer.poll().recycle();
    }

    @Override
    public void perform(Frame frame) {

    }
}
