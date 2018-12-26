package media.platform.amf.rtpcore.core.scheduler;

import media.platform.amf.rtpcore.core.concurrent.ConcurrentCyclicFIFO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import media.platform.amf.rtpcore.core.concurrent.*;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

public class PriorityQueueScheduler {

    private static final Logger logger = LoggerFactory.getLogger( PriorityQueueScheduler.class ) ;

	//SS7 QUEUES
	public static final Integer RECEIVER_QUEUE=0;
	public static final Integer SENDER_QUEUE=1;
	
	//MANAGEMENT QUEUE FOR CONTROL PROCESSING
	public static final Integer MANAGEMENT_QUEUE=2;
	
	//UDP MANAGER QUEUE FOR POOLING CHANNELS
	public static final Integer UDP_MANAGER_QUEUE=3;	
	
	//CORE QUEUES
	public static final Integer INPUT_QUEUE=4;
	public static final Integer MIXER_MIX_QUEUE=5;
	public static final Integer OUTPUT_QUEUE=6;
	
	//HEARTBEAT QUEUE
	public static final Integer HEARTBEAT_QUEUE=-1;
	
    //The clock for time measurement
    private Clock clock;

    //priority queue
    protected OrderedTaskQueue[] taskQueues = new OrderedTaskQueue[7];

    protected OrderedTaskQueue[] heartBeatQueue = new OrderedTaskQueue[5];
    
    //CPU bound threads
    private CoreThread coreThread;
    private CriticalThread criticalThread;
    
    //flag indicating state of the scheduler
    private boolean isActive;


    
    private ConcurrentCyclicFIFO<Task> waitingTasks=new ConcurrentCyclicFIFO<Task>();
    private ConcurrentCyclicFIFO<Task> criticalTasks=new ConcurrentCyclicFIFO<Task>();
    
    private WorkerThread[] workerThreads;
    private CriticalWorkerThread[] criticalWorkerThreads;

    /**
     * Creates new instance of scheduler.
     */
    public PriorityQueueScheduler(Clock clock) {
        this.clock = clock;

    	for(int i=0;i<taskQueues.length;i++) {
    		taskQueues[i]=new OrderedTaskQueue();
    	}
    	
    	for(int i=0;i<heartBeatQueue.length;i++) {
    		heartBeatQueue[i]=new OrderedTaskQueue();
    	}
    	
    	coreThread = new CoreThread("scheduler-core");  
    	criticalThread = new CriticalThread("scheduler-critical");
    	
        workerThreads=new WorkerThread[Runtime.getRuntime().availableProcessors()*2];
        criticalWorkerThreads=new CriticalWorkerThread[Runtime.getRuntime().availableProcessors() / 2];
        for(int i=0;i<workerThreads.length;i++) {
            workerThreads[i] = new WorkerThread("scheduler-worker-" + i);
        }
        
        for(int i=0;i<criticalWorkerThreads.length;i++) {
            criticalWorkerThreads[i] = new CriticalWorkerThread("scheduler-critical-worker-" + i);
        }
    }
    
    public PriorityQueueScheduler() {
        this(null);
    }

    public int getPoolSize()
    {
    	return workerThreads.length;
    }
    
    /**
     * Sets clock.
     *
     * @param clock the clock used for time measurement.
     */
    public void setClock(Clock clock) {
        this.clock = clock;
    }

    /**
     * Gets the clock used by this scheduler.
     *
     * @return the clock object.
     */
    public Clock getClock() {
        return clock;
    }

    /**
     * Queues task for execution according to its priority.
     *
     * @param task the task to be executed.
     */
    public void submit(Task task,Integer index) {
        task.activate(false);
        taskQueues[index].accept(task);
    }
    
    /**
     * Queues task for execution according to its priority.
     *
     * @param task the task to be executed.
     */
    public void submitHeatbeat(Task task) {
        task.activate(true);
        heartBeatQueue[coreThread.runIndex].accept(task);
    }
    
    /**
     * Queues chain of the tasks for execution.
     * 
     * @param taskChain the chain of the tasks
     */
    public void submit(TaskChain taskChain) {    	
        taskChain.start();
    }    
    
    /**
     * Starts scheduler.
     */
    public void startScheduler() {
    	if(this.isActive)
    		return;
    	
        if (clock == null) {
            throw new IllegalStateException("Clock is not set");
        }

        this.isActive = true;
        
        logger.info("Starting : workerThreads.length : " + workerThreads.length + "criticalWorkerThreads.length :" + criticalWorkerThreads.length);
        
        coreThread.activate();
        criticalThread.activate();
        for(int i=0;i<workerThreads.length;i++)
        	workerThreads[i].activate();
        
        for(int i=0;i<criticalWorkerThreads.length;i++)
        	criticalWorkerThreads[i].activate();        
        
        logger.info("Started ");
    }

    /**
     * Stops scheduler.
     */
    public void stop() {
        if (!this.isActive) {
            return;
        }

        coreThread.shutdown();
        criticalThread.shutdown();
        for(int i=0;i<workerThreads.length;i++)
        	workerThreads[i].shutdown();
        
        for(int i=0;i<criticalWorkerThreads.length;i++)
        	criticalWorkerThreads[i].shutdown();
        
        try
        {
        	Thread.sleep(40);
        }
        catch(InterruptedException e)
		{                				
		}
        
        for(int i=0;i<taskQueues.length;i++)
        	taskQueues[i].clear();
        
        for(int i=0;i<heartBeatQueue.length;i++)
        	heartBeatQueue[i].clear();
    }

    /**
     * Shows the miss rate.
     * 
     * @return the miss rate value;
     */
    public double getMissRate() {
        return 0;
    }

    public long getWorstExecutionTime() {
        return 0;
    }

    /**
     * Executor thread.
     */
    private class CoreThread extends Thread {        
        private volatile boolean active;
        private int currQueue=UDP_MANAGER_QUEUE;        
        private AtomicInteger activeTasksCount=new AtomicInteger();
        private long cycleStart=0;
        private int runIndex=0;
        private Object LOCK=new Object();
        
        public CoreThread(String name) {
            super(name);            
        }
        
        public void activate() {        	        	
        	this.active = true;
        	start();
        }
        
        public void notifyCompletion() {
        	if(activeTasksCount.decrementAndGet()==0)
        		LockSupport.unpark(coreThread); 	        	
        }
        
        @Override
        public void run() {        	
        	long cycleDuration;
        	
        	cycleStart = clock.getTime();
        	while(active)
        	{
        		long taskStart=cycleStart;
        		currQueue=MANAGEMENT_QUEUE;
        		while(currQueue<=OUTPUT_QUEUE)
    			{    		
        			executeQueue(taskQueues[currQueue]);
					while(activeTasksCount.get()!=0)
						LockSupport.park();
					
					currQueue++;															
    			}				        		
        		
        		executeQueue(taskQueues[MANAGEMENT_QUEUE]);
        		while(activeTasksCount.get()!=0)
					LockSupport.park();					
        		
        		runIndex=(runIndex+1)%5;        		
    			executeQueue(heartBeatQueue[runIndex]);
        		while(activeTasksCount.get()!=0)
					LockSupport.park();
        		
        		executeQueue(taskQueues[MANAGEMENT_QUEUE]);
        		while(activeTasksCount.get()!=0)
					LockSupport.park();	
        		
        		//sleep till next cycle
        		cycleDuration=clock.getTime() - cycleStart;
        		if(cycleDuration<20000000L)
        			try  {                                               
        				sleep(20L-cycleDuration/1000000L,(int)((20000000L-cycleDuration)%1000000L));
        			}
                	catch(InterruptedException e)  {                                               
                		//lets continue
                	}
        		
        		//new cycle starts , updating cycle startScheduler time by 4ms
                cycleStart = cycleStart + 20000000L;                                              
        	}
        }
        
        private void executeQueue(OrderedTaskQueue currQueue)
        {
        	Task t;        	
        	currQueue.changePool();
            t = currQueue.poll();
            
            //submit all tasks in current queue
            while(t!=null)
            {
            	activeTasksCount.incrementAndGet();
            	waitingTasks.offer(t);
            	t = currQueue.poll();
            }            
        }

        /**
         * Terminates thread.
         */
        private void shutdown() {
            this.active = false;
        }
    }    
    
    /**
     * Executor thread.
     */
    private class CriticalThread extends Thread {        
        private volatile boolean active;
        private AtomicInteger activeTasksCount=new AtomicInteger();
        private long cycleStart=0;
        private Object LOCK=new Object();
        
        public CriticalThread(String name) {
            super(name);            
        }
        
        public void activate() {        	        	
        	this.active = true;
        	cycleStart = clock.getTime();
        	start();
        }
        
        public void notifyCompletion() {
        	if(activeTasksCount.decrementAndGet()==0)
        		LockSupport.unpark(criticalThread);        	        	
        }
        
        @Override
        public void run() {        	
        	long cycleDuration;
        	
        	while(active)
        	{
        		executeQueue(taskQueues[RECEIVER_QUEUE]);
        		while(activeTasksCount.get()!=0)
					LockSupport.park();	
        		
        		executeQueue(taskQueues[SENDER_QUEUE]);
        		while(activeTasksCount.get()!=0)
					LockSupport.park();
        		
        		//sleep till next cycle
        		cycleDuration=clock.getTime() - cycleStart;
        		if(cycleDuration<4000000L)
        			try  {                                               
        				sleep(4L-cycleDuration/1000000L,(int)((4000000L-cycleDuration)%1000000L));
        			}
                	catch(InterruptedException e)  {                                               
                		//lets continue
                	}
        		
        		//new cycle starts , updating cycle startScheduler time by 4ms
                cycleStart = cycleStart + 4000000L;                                              
        	}
        }
        
        private void executeQueue(OrderedTaskQueue currQueue)
        {
        	Task t;        	
        	currQueue.changePool();
            t = currQueue.poll();
            
            //submit all tasks in current queue
            while(t!=null)
            {
            	activeTasksCount.incrementAndGet();
            	criticalTasks.offer(t);
            	t = currQueue.poll();
            }            
        }

        /**
         * Terminates thread.
         */
        private void shutdown() {
            this.active = false;
        }
    }
    
    private class WorkerThread extends Thread {
    	private volatile boolean active;
    	private Task current;
    	
    	public WorkerThread(String name) {
    	    super(name);
        }
    	
    	public void run() {
    		while(active)
    		{
    			current=null;
    			while(current==null)
    			{
    				try
    				{
    					current=waitingTasks.take();
    				}
    				catch(Exception ex)
    				{
    					logger.warn("Could not poll waiting task in timely fashion. Will keep trying.");
    				}    				
    			}
    			current.run();
    			coreThread.notifyCompletion();    			
    		}
    	}
    	
    	public void activate() {        	        	
        	this.active = true;
        	start();
        }
    	
    	/**
         * Terminates thread.
         */
        private void shutdown() {
            this.active = false;
        }
    }
    
    private class CriticalWorkerThread extends Thread {
        
    	private volatile boolean active;
        private Task current;
        
        public CriticalWorkerThread(String name) {
            super(name);
        }
        
    	public void run() {
    		while(active)
    		{
    			current=null;
    			while(current==null)
    			{
    				try
    				{
    					current=criticalTasks.take();
    				}
    				catch(Exception ex)
    				{
    					
    				}    				
    			}
    			current.run();
    			criticalThread.notifyCompletion();
    		}
    	}
    	
    	public void activate() {        	        	
        	this.active = true;
        	start();
        }
    	
    	/**
         * Terminates thread.
         */
        private void shutdown() {
            this.active = false;
        }
    }
}
