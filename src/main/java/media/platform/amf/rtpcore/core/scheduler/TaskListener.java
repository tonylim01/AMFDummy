package media.platform.amf.rtpcore.core.scheduler;

public interface TaskListener {
    /**
     * Called when task execution terminates normally
     */
    public void onTerminate();
    
    /**
     * Called when error has occurred.
     * 
     * @param e the error class
     */
    public void handlerError(Exception e);
}
