package media.platform.amf.rtpcore.core.scheduler;

public class TaskChain implements TaskListener {
    //the chain of tasks
    private Task[] task;
    
    //write index
    private int wi;
    
    //executor index
    private int i;
    
    //event listener
    private TaskChainListener listener;
    private final Object LOCK = new Object();
    
    private final Scheduler scheduler;

    /**
     * Creates new chain.
     * 
     * @param length the length of the chain.
     */
    public TaskChain(int length, Scheduler scheduler) {
        this.scheduler = scheduler;
        this.task = new Task[length];
    }
    
    /**
     * Modifies listener associated with this task chain.
     * 
     * @param listener the listener instance.
     */
    public void setListener(TaskChainListener listener) {
        this.listener = listener;
    }
    
    /**
     * Adds task to the chain.
     * 
     * @param task
     * @param offset 
     */
    public void add(Task task) {
    	synchronized(LOCK) {
    		//terminated task will be selected immediately before start
    		task.setListener(this);
        
    		this.task[wi] = task;        
        
    		wi++;
    	}
    }
    
    /**
     * Starts the chain
     */
    public void start() {
        //reset index
        i = 0;
        //submit first task
        scheduler.submit(task[0]);
    }
    
    /**
     * Submits next task for the execution
     */
    private void continueExecution() {
        //increment task index
        i++;
        
        //submit next if the end of the chain not reached yet
        if (i < task.length && task[i] != null) {
        	scheduler.submit(task[i]);            
        } else if (listener != null) {
            listener.onTermination();
        }
    }

    @Override
    public void handlerError(Exception e) {
        if (listener != null) {
            listener.onException(e);
        }
    }
    
    /**
     * Gets access to the subtasks.
     * 
     * @return subtasks array.
     */
    protected Task[] getTasks() {
        return task;
    }
    
    /**
     * Clean all tasks
     */
    public void clean() {
        wi = 0;
    }

    @Override
    public void onTerminate() {
    	continueExecution();
    }
}
