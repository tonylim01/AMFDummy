package media.platform.amf.rtpcore.core.spi;

import media.platform.amf.rtpcore.core.spi.memory.Frame;

public interface MediaSink extends Component {

    /**
     * Gets true if component is able to receive media.
     * 
     * @return true if component is able to receive media.
     */
    public boolean isStarted();
    
    /**
     * Shows the number of packets received by this medis sink since last startScheduler.
     * 
     * @return the number of packets.
     */
    public long getPacketsReceived();
    
    /**
     * Shows the number of bytes received by this sink since last startScheduler;
     * 
     * @return the number of bytes.
     */    
    public long getBytesReceived();
    
    /**
     * Allows to transfer frame from media source to media sink
     * 
     * @return the number of bytes.
     */
	public void perform(Frame frame);
}
