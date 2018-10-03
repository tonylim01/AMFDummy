package media.platform.amf.rtpcore.core.spi.memory;

import media.platform.amf.rtpcore.core.concurrent.ConcurrentMap;

public class Memory
{
    private static ConcurrentMap<Partition> partitions = new ConcurrentMap<Partition>();
    
    public static Frame allocate(int size) 
    {
    	Partition currPartition=partitions.get(size);
    	if(currPartition==null)
    	{
    		currPartition=new Partition(size);
    		Partition oldPartition=partitions.putIfAbsent(size,currPartition);
    		if(oldPartition!=null)
    			currPartition=oldPartition;		
    	}
    	
    	return currPartition.allocate();
    }
}
