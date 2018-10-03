package media.platform.amf.rtpcore.core.network.deprecated.channel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.Selector;

public interface NetworkChannel extends AutoCloseable {

    /**
     * Binds the channel to an address.
     * 
     * @param address the address the channel will be bounds to
     * @throws IOException In case the channel is closed or could not be bound.
     */
    void bind(InetSocketAddress address) throws IOException;

    /**
     * Opens the channel.
     * 
     * @throws IOException If the channel is already open or if it could not be opened.
     */
    void open() throws IOException;

    /**
     * Disconnects and closes the channel.<br>
     * Invoking this method will have no effect if the channel is already closed.
     */
    @Override
    void close();

    /**
     * Registers this channel into a selector for multiplexing purposes.
     * 
     * @param selector The selector where the channel will be registered.
     * @param The channel valid operation keys.
     * @throws ClosedChannelException If the channel is closed
     */
    void register(Selector selector, int opts) throws ClosedChannelException;

    /**
     * Connects the channel to a remote peer.<br>
     * The channel will only be able to accept traffic from that peer.
     * 
     * @param address The address of the remote peer to connect to
     * @throws IOException
     */
    void connect(InetSocketAddress address) throws IOException;

    /**
     * Disconnect the channel
     * 
     * @throws IOException
     */
    void disconnect() throws IOException;

    /**
     * Gets the address the channel is bound to.
     * 
     * @return Returns the local address of the channel. Returns null if the channel is not currently bound.
     */
    InetSocketAddress getLocalAddress();

    /**
     * Gets the address of the remote peer this channel is connected to.
     * 
     * @return Returns the address of the remote peer. Returns null if the channel is not connected.
     */
    InetSocketAddress getRemoteAddress();

    /**
     * Receives incoming data.
     * 
     * @throws IOException
     */
    void receive() throws IOException;

    /**
     * Sends data to the remote peer. <b>Only usable if channel is connected.</b>
     * 
     * @param data The data to be sent.
     * @throws IOException
     */
    void send(byte[] data) throws IOException;

    /**
     * Send data through the channel.
     * 
     * @param data The data to be sent.
     * @param remotePeer The address of the remote peer.
     * @throws IOException
     */
    void send(byte[] data, InetSocketAddress remotePeer) throws IOException;

    /**
     * Send data through the channel.
     * 
     * @param data The data to be sent.
     * @param remotePeer The address of the remote peer.
     * @throws IOException
     */
    void send(ByteBuffer data, InetSocketAddress remotePeer) throws IOException;

    /**
     * Gets whether channel is connected or not.
     * 
     * @return <code>true</code> if the channel is connected. Otherwise, returns <code>false</code>.
     */
    boolean isConnected();

    /**
     * Gets whether channel is open or not.
     * 
     * @return <code>true</code> if the channel is open. Otherwise, returns <code>false</code>.
     */
    boolean isOpen();

    /**
     * Gets whether channel is registered in a Selector.
     * 
     * @return <code>true</code> if the channel is registered. Otherwise, returns <code>false</code>.
     */
    boolean isRegistered();

}
