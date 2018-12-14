package media.platform.amf.rtpcore.core.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import media.platform.amf.rtpcore.core.spi.MediaSink;
import media.platform.amf.rtpcore.core.spi.memory.Frame;

import java.io.IOException;

public abstract class AbstractSink extends BaseComponent implements MediaSink {

	private static final long serialVersionUID = -2119158462149998609L;
    private static final Logger logger = LoggerFactory.getLogger( AbstractSink.class);

	//shows if component is started or not.
    private volatile boolean started = false;
    
    //transmission statisctics
    //private volatile long rxPackets;
    private long rxPackets;
    private volatile long rxBytes;


    
    /**
     * Creates new instance of sink with specified name.
     * 
     * @param name the name of the sink to be created.
     */
    public AbstractSink(String name) {
        super(name);               
    }        

    @Override
    public boolean isStarted() {
        return this.started;
    }

    public abstract void onMediaTransfer(Frame frame) throws IOException;

    protected void start() {
    	if (started) {
			return;
		}

		//change state flag
		started = true;
		
		this.rxBytes = 0;
		this.rxPackets = 0;

		//send notification to component's listener
		started();		    	
    }    
    
    protected void stop() {
    	started = false;
		stopped();    	
    }

    @Override
    public abstract void activate();
    
    @Override
    public abstract void deactivate();
    
    protected void failed(Exception e) {
    }

    @Override
    public long getPacketsReceived() {
        return rxPackets;
    }

    @Override
    public long getBytesReceived() {
        return rxBytes;
    }

    @Override
    public void reset() {
        this.rxPackets = 0;
        this.rxBytes = 0;        
    }

    /**
     * Sends notification that media processing has been started.
     */
    protected void started() {
    }

    /**
     * Sends notification that detection is terminated.
     */
    protected void stopped() {
    }    

    public String report() {
    	return "";
    }
    
    @Override
    public void perform(Frame frame) {
    	if(!started) {
    		return;
    	}
    	
    	if(frame==null) {
    		return;
    	}
    	
    	rxPackets++;
    	rxBytes += frame.getLength();

    	//frame is not null, let's handle it
    	try {
    		onMediaTransfer(frame);
    	} catch (IOException e) {  
    		logger.error( String.valueOf( e ) );
    		started = false;
        	failed(e);
    	}
    }    
}
