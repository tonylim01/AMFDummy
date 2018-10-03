package media.platform.amf.rtpcore.core.concurrent;

public class Lock {
    protected boolean locked;
    public Lock() {
        locked=false;
    }
    
    public synchronized void lock() throws InterruptedException {
        while (locked) wait();
        locked=true;
    }
    
    public synchronized void unlock() {
        locked=false;
        notify();
    }
}
