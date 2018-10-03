
package media.platform.amf.rtpcore.core.network.deprecated;

import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;

public interface ProtocolHandler {
    /**
     * Protocol specific interpretator of the UDP data.
     * 
     * @param channel the channel to receive datagrams
     */
    public void receive(DatagramChannel channel);

    /**
     * Constructs and sends protocol specific message over UDP.
     *
     * @param channel the channel used for sending.
     */
    public void send(DatagramChannel channel);

    public boolean isReadable();
    public boolean isWriteable();
    
    public void setKey(SelectionKey key);
    
    /**
     * Allows udp manager to notify that channel that exists in list
     * was closed for some reason 
     *
     * @param channel the channel used for sending.
     */
    public void onClosed();    
}
