
package media.platform.amf.rtpcore.core.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Task implements Runnable {
	private static AtomicInteger id=new AtomicInteger(0);
	
    private volatile boolean isActive = true;
    private volatile boolean isHeartbeat = true;
    //error handler instance
    protected TaskListener listener;
    
    private final Object LOCK = new Object();    
        
    private AtomicBoolean inQueue0=new AtomicBoolean(false);
    private AtomicBoolean inQueue1=new AtomicBoolean(false);

    private static final Logger logger = LoggerFactory.getLogger( Task.class);
    
    protected int taskId;
    
    public Task() {
    	taskId=id.incrementAndGet();
    }

    public void storedInQueue0()
    {
    	inQueue0.set(true);
    }
    
    public void storedInQueue1()
    {
    	inQueue1.set(true);
    }
    
    public void removeFromQueue0()
    {
    	inQueue0.set(false);
    }
    
    public void removeFromQueue1()
    {
    	inQueue1.set(false);
    }
   
    public Boolean isInQueue0()
    {
    	return inQueue0.get();
    }
    
    public Boolean isInQueue1()
    {
    	return inQueue1.get();
    }
    
    /**
     * Modifies task listener.
     * 
     * @param listener the handler instance.
     */
    public void setListener(TaskListener listener) {
        this.listener = listener;
    }
    
    /**
     * Current queue of this task.
     * 
     * @return the value of queue
     */
    public abstract int getQueueNumber();    
    
    
    /**
     * Executes task.
     * 
     * @return dead line of next execution
     */
    public abstract long perform();

    /**
     * Cancels task execution
     */
    public void cancel() {
    	synchronized(LOCK) {
    		this.isActive = false;    		
    	}
    }

    //call should not be synchronized since can run only once in queue cycle
    public void run() {
    		if (this.isActive)  {
    			try {
    				perform();                
                
    				//notify listener                
    				if (this.listener != null) {
    					this.listener.onTerminate();
    				}
    				
    			} catch (Exception e) {
    			    logger.error("Could not execute task " + this.taskId + ": "+ e.getMessage(), e);
    				if (this.listener != null) {
    					listener.handlerError(e);
    				} 
    			}
    		}      		    		    	
    }

    protected void activate(Boolean isHeartbeat) {
    	synchronized(LOCK) {
    		this.isActive = true;
    		this.isHeartbeat=isHeartbeat;
    	}
    }    
}
