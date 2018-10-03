package media.platform.amf.rtpcore.core.network.deprecated.channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

public class MultiplexedNetworkChannel extends AbstractNetworkChannel {

    private static final Logger logger = LoggerFactory.getLogger( MultiplexedNetworkChannel.class);

    protected final PacketHandlerPipeline handlers;

    public MultiplexedNetworkChannel(NetworkGuard guard, PacketHandler... handlers) {
        super(guard);
        this.handlers = new PacketHandlerPipeline();
        for (PacketHandler handler : handlers) {
            this.handlers.addHandler(handler);
        }
    }

    @Override
    protected void onIncomingPacket(byte[] data, InetSocketAddress remotePeer) {
        // Get appropriate handler to process incoming packet
        final PacketHandler handler = this.handlers.getHandler(data);

        if (handler == null) {
            if (log().isDebugEnabled()) {
                log().debug("No protocol handler was found to process an incoming packet. Packet will be dropped.");
            }
        } else {
            try {
                // Let the handler process the incoming packet. A response MAY be provided as result.
                final byte[] response = handler.handle(data, this.getLocalAddress(), remotePeer);
                if (response != null && response.length > 0) {
                    send(response, remotePeer);
                }
            } catch (PacketHandlerException e) {
                log().warn(handler.getClass().getSimpleName() + " could not handle incoming packet: " + e.getMessage());
            } catch (IOException e) {
                log().warn(handler.getClass().getSimpleName() + " could not send response to remote peer " + remotePeer.getAddress().toString());
            }
        }
    }
    
    @Override
    protected Logger log() {
        return logger;
    }

}
