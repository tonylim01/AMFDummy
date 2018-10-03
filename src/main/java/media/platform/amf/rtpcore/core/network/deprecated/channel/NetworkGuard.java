package media.platform.amf.rtpcore.core.network.deprecated.channel;

import java.net.InetSocketAddress;


@Deprecated
public interface NetworkGuard {

    /**
     * Decides whether a remote peer is secure or not.
     * 
     * @param channel The channel who received the packet.
     * @param source The address of the remote peer.
     * @return Returns true if source is considered secure; otherwise, returns false.
     */
    boolean isSecure(NetworkChannel channel, InetSocketAddress source);

}
