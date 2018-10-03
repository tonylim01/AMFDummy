package media.platform.amf.rtpcore.core.network.deprecated;

public interface PortManager {

    /**
     * Gets the low boundary of available range.
     * 
     * @return Minimum port number
     */
    int getLowest();

    /**
     * Gets the upper boundary of available range.
     * 
     * @return Maximum port number
     */
    int getHighest();

    /**
     * Gets the current port.
     * 
     * @return The current port.
     */
    public int current();

    /**
     * Peeks into the next available port. Does not move the internal pointer.
     * 
     * @return The next available port.
     */
    public int peek();

    /**
     * Moves to the next available port.
     * 
     * @return The next available port.
     */
    public int next();

}
