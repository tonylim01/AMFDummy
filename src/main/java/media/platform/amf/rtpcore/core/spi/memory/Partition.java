package media.platform.amf.rtpcore.core.spi.memory;

import media.platform.amf.rtpcore.core.concurrent.ConcurrentCyclicFIFO;

public class Partition {

    protected int size;
    private ConcurrentCyclicFIFO<Frame> heap = new ConcurrentCyclicFIFO<Frame>();

    protected Partition(int size) {
        this.size = size;
    }
    
    protected Frame allocate() {
    	//if (true) return new Frame(this, new byte[size]);
    	Frame result=heap.poll();    	
    	
        if (result==null) {
            return new Frame(this, new byte[size]);
        }
        
        result.inPartition.set(false);
        return result;
    }

    protected void recycle(Frame frame) {
    	if(frame.inPartition.getAndSet(true)) {
    		//dont add duplicate,otherwise may be reused in different places
    		return;
    	}
        frame.setHeader(null);
        frame.setDuration(Long.MAX_VALUE);
        frame.setEOM(false);        
        heap.offer(frame);
        //queue.offer(frame, frame.getDelay(TimeUnit.NANOSECONDS), TimeUnit.NANOSECONDS);
    }

}
