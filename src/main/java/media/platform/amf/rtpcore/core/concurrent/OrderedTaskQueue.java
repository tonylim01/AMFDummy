
package media.platform.amf.rtpcore.core.concurrent;

public class OrderedTaskQueue {
	//inner holder for tasks
    private ConcurrentCyclicFIFO<Task>[] taskList=new ConcurrentCyclicFIFO[2];
    
    private Integer activeIndex=0;
    
    public OrderedTaskQueue() {
        //intitalize task list
    	taskList[0] = new ConcurrentCyclicFIFO<Task>();
    	taskList[1] = new ConcurrentCyclicFIFO<Task>();    
    }    

    /**
     * Queues specified task using tasks dead line time.
     * 
     * @param task the task to be queued.
     * @return TaskExecutor for the scheduled task.
     */
    public void accept(Task task) {
    	if((activeIndex+1)%2==0)
    	{
    		if(!task.isInQueue0())
    		{
    			taskList[0].offer(task);
    			task.storedInQueue0();
    		}
    	}
    	else
    	{
    		if(!task.isInQueue1())
    		{
    			taskList[1].offer(task);
    			task.storedInQueue1();
    		}
    	}    	    	    
    }
    
    /**
     * Retrieves the task with earliest dead line and removes it from queue.
     * 
     * @return task which has earliest dead line
     */
    public Task poll() {
    	Task result=null;
    	if(activeIndex==0)
    	{
    		result=taskList[0].poll();
    		if(result!=null)
    			result.removeFromQueue0();    		
    	}
    	else
    	{
    		result=taskList[1].poll();
    		if(result!=null)
    			result.removeFromQueue1();
    	}
    	
    	return result;     		    
    } 

    public void changePool()
    {
    	activeIndex=(activeIndex+1)%2;      	
    }
    
    /**
     * Clean the queue.
     */
    public void clear() {
    	taskList[0].clear();
    	taskList[1].clear();    	    	    
    }
    
    /**
     * Gets the size of this queue.
     * 
     * @return the size of the queue.
     */
    public int size() {
    	return taskList[activeIndex].size();    	
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Queue[");
        
        int len = Math.min(30, taskList[activeIndex].size());
        for (int i = 0; i < len -1; i++) {
        	//sb.append(taskList[activeIndex].get(i).getPriority());
            sb.append(",");
        }
        
        //if(!taskList[activeIndex].isEmpty())
        //	sb.append(taskList[activeIndex].get(len - 1).getPriority());
        sb.append("]");
        return sb.toString();
    }
}
