package media.platform.amf.rtpcore.core.spi.memory;

import media.platform.amf.rtpcore.core.spi.format.Format;

import java.util.concurrent.atomic.AtomicBoolean;

public class Frame {
    private Partition partition;
    private byte[] data;

    private volatile int offset;
    private volatile int length;

    private volatile long timestamp;
    private volatile long duration = Long.MAX_VALUE;
    private volatile long sn;

    private volatile boolean eom;
    private volatile Format format;
    private volatile String header;
    
    protected AtomicBoolean inPartition=new AtomicBoolean(false);
    
    protected Frame(Partition partition, byte[] data) {
        this.partition = partition;
        this.data = data;
    }

    protected void reset() {
        this.timestamp = 0;
        this.duration = 0;
        this.sn = 0;
        this.eom = false;
    }
    
    public String getHeader() {
        return header;
    }
    
    public void setHeader(String header) {
        this.header = header;
    }
    
    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
    
    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public byte[] getData() {
        return data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getDuration() {
        return duration;
    }
    
    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSequenceNumber(){
        return sn;
    }

    public void setSequenceNumber(long sn) {
        this.sn = sn;
    }

    public boolean isEOM() {
        return this.eom;
    }

    public void setEOM(boolean value) {
        this.eom = value;
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }    

    public void recycle() {
        partition.recycle(this);
    }

//    @Override
    public Frame clone() {
        Frame frame = Memory.allocate(data.length);
        System.arraycopy(data, offset, frame.data, offset, length);
        frame.offset = offset;
        frame.length = length;
        frame.duration = duration;
        frame.sn = sn;
        frame.eom = eom;
        frame.format = format;
        frame.timestamp = timestamp;
        frame.header = header;
        return frame;
    }
}
