
package media.platform.amf.rtpcore.core.component;


import media.platform.amf.rtpcore.core.scheduler.PriorityQueueScheduler;
import media.platform.amf.rtpcore.core.scheduler.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import media.platform.amf.rtpcore.core.spi.MediaSource;
import media.platform.amf.rtpcore.core.spi.memory.Frame;

public abstract class AbstractSource extends BaseComponent implements MediaSource {

	private static final long serialVersionUID = 3157479112733053482L;
    private static final Logger logger = LoggerFactory.getLogger( AbstractSource.class);

	//transmission statisctics
    private volatile long txPackets;
    private volatile long txBytes;
    
    //shows if component is started or not.
    private volatile boolean started;

    //stream synchronization flag
    private volatile boolean isSynchronized;

    //local media time
    private volatile long timestamp = 0;
    
    //initial media time
    private long initialOffset;
    
    //frame sequence number
    private long sn = 1;

    //scheduler instance
    private PriorityQueueScheduler scheduler;

    //media generator
    private final Worker worker;

  //duration of media stream in nanoseconds
    protected long duration = -1;

    //intial delay for media processing
    private long initialDelay = 0;
    
    //media transmission pipe
    protected AbstractSink mediaSink;


    /**
     * Creates new instance of source with specified name.
     * 
     * @param name
     *            the name of the source to be created.
     */
    public AbstractSource(String name, PriorityQueueScheduler scheduler, int queueNumber) {
        super(name);
        this.scheduler = scheduler;
        this.worker = new Worker(queueNumber);        
    }    

    /**
     * (Non Java-doc.)
     * 
     * @see AbstractSource#setInitialDelay(long)
     */
    public void setInitialDelay(long initialDelay) {
        this.initialDelay = initialDelay;
    }


    public long getMediaTime() {
        return timestamp;
    }
    

    public void setDuration(long duration) {
        this.duration = duration;
    }
    

    public long getDuration() {
        return this.duration;
    }
    

    public void setMediaTime(long timestamp) {
        this.initialOffset = timestamp;
    }       


    public void start() {
    	synchronized(worker) {
    		//check scheduler
    		try {
    			//prevent duplicate starting
    			if (started) {
    				return;
    			}

    			if (scheduler == null) {
    				throw new IllegalArgumentException("Scheduler is not assigned");
    			}

    			this.txBytes = 0;
    			this.txPackets = 0;
            
    			//reset media time and sequence number
    			timestamp = this.initialOffset;
    			this.initialOffset = 0;
            
    			sn = 0;

    			//switch indicator that source has been started
    			started = true;

    			//just started component always synchronized as well
    			this.isSynchronized = true;
    			
    			if(mediaSink!=null)
    				mediaSink.start();
    			
    			//scheduler worker    
    			worker.reinit();
    			scheduler.submit(worker,worker.getQueueNumber());
    			
    			//started!
    			started();
    		} catch (Exception e) {
    			started = false;
    			failed(e);
    			logger.error( String.valueOf( e ) );
    		}
    	}
    }

    /**
     * Restores synchronization
     */
    public void wakeup() {    	
        synchronized(worker) {
            if (!started) {            	
                return;
            }
            
            if (!this.isSynchronized) {
                this.isSynchronized = true;
                scheduler.submit(worker,worker.getQueueNumber());
            }
        }
    }
    

    public void stop() {
        if (started) {
            stopped();
        }
        started = false;
        if (worker != null) {
            worker.cancel();
        }
        
        if(mediaSink!=null) {
        	mediaSink.stop();
        }
        	
        timestamp = 0;
    }

    public void activate()
    {
    	start();
    }
    
    public void deactivate()
    {
    	stop();
    }
    

    protected void connect(AbstractSink sink) {
        this.mediaSink = sink;
        if(started)
        	this.mediaSink.start();
    }


    protected void disconnect() {
    	if(this.mediaSink!=null)
    	{
    		this.mediaSink.stop();
    		this.mediaSink=null;
    	}
    }


    public boolean isConnected() {
        return mediaSink != null;
    }


    public boolean isStarted() {
        return this.started;
    }

    public abstract Frame evolve(long timestamp);

    /**
     * Sends notification that media processing has been started.
     */
    protected void started() {
    }

    /**
     * Sends failure notification.
     * 
     * @param e the exception caused failure.
     */
    protected void failed(Exception e) {
    }

    /**
     * Sends notification that signal is completed.
     * 
     */
    protected void completed() {
        this.started = false;
    }

    /**
     * Called when source is stopped by request
     * 
     */
    protected void stopped() {
    }

    public long getPacketsTransmitted() {
    	return txPackets;
    }

    /**
     * (Non Java-doc).
     * 
     * @see MediaSource#getBytesTransmitted()
     */
    public long getBytesTransmitted() {
        return txBytes;
    }

    @Override
    public void reset() {
        this.txPackets = 0;
        this.txBytes = 0;        
    }
    
    public String report() {
        return "";
    }    

    /**
     * Media generator task
     */
    private class Worker extends Task {
    	/**
         * Creates new instance of task.
         *
         * @param scheduler the scheduler instance.
         */
    	private int queueNumber;    	
    	private long initialTime;
    	int readCount=0,length;
    	long overallDelay=0;
    	Frame frame;
    	long frameDuration;
    	Boolean isEOM;
    	
    	public Worker(int queueNumber) {
            super();
            this.queueNumber=queueNumber;
            initialTime=scheduler.getClock().getTime();            
        }
        
        public void reinit()
        {
        	initialTime=scheduler.getClock().getTime();        	
        }
        
        public int getQueueNumber()
        {
        	return queueNumber;
        }

        public long perform() {
        	if(initialDelay+initialTime>scheduler.getClock().getTime())
        	{
        		//not a time yet
        		scheduler.submit(this,queueNumber);
                return 0;
        	}
        	
        	readCount=0;
        	overallDelay=0;
        	while(overallDelay<20000000L)
        	{
        		readCount++;
        		frame = evolve(timestamp);
        		if (frame == null) {
        			if(readCount==1)
        			{     
        				//stop if frame was not generated
        				isSynchronized = false;
        				return 0;
        			}
        			else
        			{
        				//frame was generated so continue
        				scheduler.submit(this,queueNumber);
        	            return 0;
        			}
            	}

            	//mark frame with media time and sequence number
            	frame.setTimestamp(timestamp);
            	frame.setSequenceNumber(sn);

            	//update media time and sequence number for the next frame
            	timestamp += frame.getDuration();
            	overallDelay += frame.getDuration();
            	sn= (sn==Long.MAX_VALUE) ? 0: sn+1;

            	//set end_of_media flag if stream has reached the end
            	if (duration > 0 && timestamp >= duration) {
            		frame.setEOM(true);
            	}

            	frameDuration = frame.getDuration();            	            	            	          
            	isEOM=frame.isEOM();
            	length=frame.getLength();
            	
            	//delivering data to the other party.
            	if (mediaSink != null) {
            		mediaSink.perform(frame);
            	}
            	
            	//update transmission statistics
            	txPackets++;
            	txBytes += length;
            
            	//send notifications about media termination
            	//and do not resubmit this task again if stream has bee ended
            	if (isEOM) { 
            		started=false;
        			completed();
            		return -1;
            	}

            	//check synchronization
            	if (frameDuration <= 0) {
            		//loss of synchronization
                	isSynchronized = false;
                	return 0;
            	}            
        	}
        	
        	scheduler.submit(this,queueNumber);
            return 0;
        }

        @Override
        public String toString() {
            return getName();
        }

    }
}
