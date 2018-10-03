
package media.platform.amf.rtpcore.core.network.deprecated;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import media.platform.amf.rtpcore.core.network.deprecated.channel.Channel;
import media.platform.amf.rtpcore.core.network.deprecated.channel.NetworkChannel;
import media.platform.amf.rtpcore.core.scheduler.Scheduler;
import media.platform.amf.rtpcore.core.scheduler.ServiceScheduler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class UdpManager {

    private static final Logger logger = LoggerFactory.getLogger( UdpManager.class);

    // Core elements
    private final Scheduler scheduler;
    private final PortManager portManager;
    private final PortManager localPortManager;

    // UDP Manager properties
    private static final int PORT_ANY = -1;
    private static final String INET_UNKNOWN = "unknown";
    private static final String LOCALHOST = "192.168.5.70";

    private String inet;
    private String bindAddress;
    private String localBindAddress;
    private String externalAddress;

    private byte[] localNetwork;
    private String localNetworkString;
    private IPAddressType currNetworkType;
    private byte[] localSubnet;
    private String localSubnetString;
    private IPAddressType currSubnetType;

    private Boolean useSbc;
    private int rtpTimeout; // in seconds!
    private volatile boolean active;

    private final Object LOCK;
    private final List<Selector> selectors;
    private List<PollTask> pollTasks;
    private List<Future<?>> pollTaskFutures;
    private AtomicInteger currSelectorIndex;

    public UdpManager(Scheduler scheduler, PortManager portManager, PortManager localPortManager) {
        // Core elements
        this.portManager = portManager;
        this.localPortManager = localPortManager;

        // UDP Manager properties
        this.inet = "en7";
        this.bindAddress = LOCALHOST;
        this.localBindAddress = LOCALHOST;
        this.externalAddress = "";

        this.useSbc = false;
        this.rtpTimeout = 0;
        this.active = false;

        this.LOCK = new Object();

        // UDP manager tasks
        this.scheduler = scheduler;
        this.selectors = new ArrayList<Selector>(ServiceScheduler.POOL_SIZE);
        this.pollTasks = new ArrayList<PollTask>(ServiceScheduler.POOL_SIZE);
        this.pollTaskFutures = new ArrayList<Future<?>>(ServiceScheduler.POOL_SIZE);
        this.currSelectorIndex = new AtomicInteger(0);
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    /**
     * Modify bind address.
     * 
     * @param address the IP address as character string.
     */
    public void setBindAddress(String address) {
        this.bindAddress = address;
    }

    /**
     * Gets the bind address.
     * 
     * @return the IP address as character string.
     */
    public String getBindAddress() {
        return bindAddress;
    }

    /**
     * Modify bind address.
     * 
     * @param address the IP address as character string.
     */
    public void setLocalBindAddress(String address) {
        this.localBindAddress = address;
    }

    /**
     * Gets the bind address.
     * 
     * @return the IP address as character string.
     */
    public String getLocalBindAddress() {
        return localBindAddress;
    }

    public String getExternalAddress() {
        return externalAddress;
    }

    public void setExternalAddress(String externalAddress) {
        this.externalAddress = externalAddress;
    }

    /**
     * Modify rtp timeout.
     * 
     * @param rtpTimeout the time in seconds.
     */
    public void setRtpTimeout(int rtpTimeout) {
        this.rtpTimeout = rtpTimeout;
    }

    /**
     * Gets the rtp timeout.
     * 
     * @return the rtptimeout as integer.
     */
    public int getRtpTimeout() {
        return this.rtpTimeout;
    }


    public void setLocalNetwork(String localNetwork) {
        IPAddressType currNetworkType = IPAddressCompare.getAddressType(localNetwork);
        this.currNetworkType = currNetworkType;
        this.localNetworkString = localNetwork;

        if (currNetworkType == IPAddressType.IPV4) {
            this.localNetwork = IPAddressCompare.addressToByteArrayV4(localNetwork);
        } else if (currNetworkType == IPAddressType.IPV6) {
            this.localNetwork = IPAddressCompare.addressToByteArrayV6(localNetwork);
        }
    }
    
    public String getLocalNetwork() {
        return localNetworkString;
    }


    public void setLocalSubnet(String localSubnet) {
        IPAddressType currSubnetType = IPAddressCompare.getAddressType(localSubnet);
        this.currSubnetType = currSubnetType;
        this.localSubnetString = localSubnet;

        if (currSubnetType == IPAddressType.IPV4) {
            this.localSubnet = IPAddressCompare.addressToByteArrayV4(localSubnet);
        } else if (currSubnetType == IPAddressType.IPV6) {
            this.localSubnet = IPAddressCompare.addressToByteArrayV6(localSubnet);
        }
    }
    
    public String getLocalSubnet() {
        return localSubnetString;
    }

    /**
     * Set the useSbc property
     * 
     * @param useSbc whether to use sbc or not
     */
    public void setUseSbc(Boolean useSbc) {
        this.useSbc = useSbc;
    }

    public PortManager getPortManager() {
        return portManager;
    }

    /**
     * Gets the low boundary of available range.
     * 
     * @return low min port number
     */
    public int getLowestPort() {
        return portManager.getLowest();
    }

    /**
     * Gets the upper boundary of available range.
     * 
     * @retun min port number
     */
    public int getHighestPort() {
        return portManager.getLowest();
    }

    public void addSelector(Selector selector) {
        synchronized (LOCK) {
            if (!this.selectors.contains(selector)) {
                this.selectors.add(selector);
                PollTask pollTask = new PollTask(selector);
                this.pollTasks.add(pollTask);
                ScheduledFuture<?> future = this.scheduler.scheduleWithFixedDelay(pollTask, 0L, 2L, TimeUnit.MILLISECONDS);
                this.pollTaskFutures.add(future);
            }
        }
    }

    public boolean connectImmediately(InetSocketAddress address) {
        if (!useSbc) {
            return true;
        }

        boolean connectImmediately = false;
        byte[] addressValue = address.getAddress().getAddress();

        if (currSubnetType == IPAddressType.IPV4 && currNetworkType == IPAddressType.IPV4) {
            if (IPAddressCompare.isInRangeV4(localNetwork, localSubnet, addressValue)) {
                connectImmediately = true;
            }
        } else if (currSubnetType == IPAddressType.IPV6 && currNetworkType == IPAddressType.IPV6) {
            if (IPAddressCompare.isInRangeV6(localNetwork, localSubnet, addressValue)) {
                connectImmediately = true;
            }
        }
        return connectImmediately;
    }

    @Deprecated
    public DatagramChannel open(ProtocolHandler handler) throws IOException {
        DatagramChannel channel = DatagramChannel.open();
        channel.configureBlocking(false);
        int index = currSelectorIndex.getAndIncrement();
        SelectionKey key = channel.register(selectors.get(index % selectors.size()), SelectionKey.OP_READ);
        key.attach(handler);
        handler.setKey(key);
        return channel;
    }

    public SelectionKey open(Channel channel) throws IOException {
        DatagramChannel dataChannel = DatagramChannel.open();
        dataChannel.configureBlocking(false);
        int index = currSelectorIndex.getAndIncrement();
        SelectionKey key = dataChannel.register(selectors.get(index % selectors.size()), SelectionKey.OP_READ);
        key.attach(channel);
        return key;
    }
    
    public void register(NetworkChannel channel) throws IOException {
        int index = this.currSelectorIndex.getAndIncrement();
        channel.register(this.selectors.get(index % this.selectors.size()), SelectionKey.OP_READ);
    }

    @Deprecated
    public SelectionKey open(DatagramChannel dataChannel, Channel channel) throws IOException {
        // Get a selector
        int index = currSelectorIndex.getAndIncrement();
        Selector selector = selectors.get(index % selectors.size());
        // Register the channel under the chosen selector
        SelectionKey key = dataChannel.register(selector, SelectionKey.OP_READ);
        // Attach the multiplexer to the key
        key.attach(channel);
        return key;
    }

    @Deprecated
    public void open(DatagramChannel channel, ProtocolHandler handler) throws IOException {
        // Get a selector
        int index = currSelectorIndex.getAndIncrement();
        Selector selector = selectors.get(index % selectors.size());
        // Register the channel under the chosen selector
        SelectionKey key = channel.register(selector, SelectionKey.OP_READ);
        // Attach the protocol handler to the key
        key.attach(handler);
        handler.setKey(key);
    }

    public void bind(DatagramChannel channel, int port, boolean local) throws IOException {
        if (local) {
            bindLocal(channel, port);
        } else {
            bind(channel, port);
        }
    }

    /**
     * Binds socket to global bind address and specified port.
     * 
     * @param channel the channel
     * @param port the port to bind to
     * @throws IOException
     */
    public void bind(DatagramChannel channel, int port) throws IOException {
        // select port if wildcarded
        if (port == PORT_ANY) {
            port = portManager.next();
        }

        // try bind
        IOException ex = null;
        for (int q = 0; q < 100; q++) {
            try {
                logger.info("bindAddress : " + bindAddress + " port : " + port);
                channel.bind(new InetSocketAddress(bindAddress, port));
                ex = null;
                break;
            } catch (IOException e) {
                ex = e;
                logger.info("Failed trying to bind " + bindAddress + ":" + port + " " + ex.getMessage());
                port = portManager.next();
            }
        }

        if (ex != null) {
            throw ex;
        }
    }

    /**
     * Binds socket to global bind address and specified port.
     * 
     * @param channel the channel
     * @param port the port to bind to
     * @throws IOException
     */
    public void bindLocal(DatagramChannel channel, int port) throws IOException {
        // select port if wildcarded
        if (port == PORT_ANY) {
            port = localPortManager.next();
        }

        // try bind
        IOException ex = null;
        for (int q = 0; q < 100; q++) {
            try {
                logger.info("trying to bind " + localBindAddress + ":" + port);
                channel.bind(new InetSocketAddress(localBindAddress, port));
                ex = null;
                break;
            } catch (IOException e) {
                ex = e;
                logger.info("Failed trying to bind " + localBindAddress + ":" + port);
                port = localPortManager.next();
            }
        }

        if (ex != null) {
            throw ex;
        }
    }

    private void generateTasks() throws IOException {
        logger.debug( "POOL_SIZE : "  + ServiceScheduler.POOL_SIZE);
        for (int i = 0; i < ServiceScheduler.POOL_SIZE; i++) {
            this.selectors.add(SelectorProvider.provider().openSelector());
            PollTask pollTask = new PollTask(this.selectors.get(i));
            this.pollTasks.add(pollTask);
            ScheduledFuture<?> future = this.scheduler.scheduleWithFixedDelay(pollTask, 0L, 2L, TimeUnit.MILLISECONDS);
            this.pollTaskFutures.add(future);
        }
    }

    private void stopTasks() {
        for (Future<?> future : this.pollTaskFutures) {
            future.cancel(false);
        }
        this.pollTaskFutures.clear();
    }

    private void closeSelectors() {
        for (int i = 0; i < this.selectors.size(); i++) {
            Selector selector = this.selectors.get(i);
            if (selector != null && selector.isOpen()) {
                try {
                    selector.close();
                } catch (Exception e) {
                    logger.error("Could not close selector " + i, e);
                }
            }
        }
    }

    private void cleanResources() {
        this.pollTasks.clear();
        this.selectors.clear();
    }

    /**
     * Starts polling the network.
     */
    public void start() {
        synchronized (LOCK) {
            if (!this.active) {
                this.active = true;
                logger.info("Starting UDP Manager");
                try {
                    generateTasks();
                    logger.info("Initialized UDP interface[" + inet + "]: bind address=" + bindAddress);
                } catch (IOException e) {
                    logger.error("An error occurred while initializing the polling tasks", e);
                    stop();
                }
            }
        }
    }

    /**
     * Stops polling the network.
     */
    public void stop() {
        synchronized (LOCK) {
            if (this.active) {
                this.active = false;
                logger.info("Stopping UDP Manager");
                stopTasks();
                closeSelectors();
                cleanResources();
                logger.info("UDP Manager has stopped");
            }
        }
    }

    /**
     * Runnable task for polling UDP channels
     */
    private class PollTask implements Runnable {

        private final Selector localSelector;

        public PollTask(Selector selector) {
            this.localSelector = selector;
        }

        @Override
        public void run() {

            if (active) {
                try {
                    // Select channels enabled for reading operation (without blocking!)
                    int selected = localSelector.selectNow();
                    if (selected == 0) {
                        return;
                    }
                } catch (IOException e) {
                    logger.error("Could not select channels from Selector!");
                }

                // Iterate over selected channels
                Iterator<SelectionKey> it = localSelector.selectedKeys().iterator();
                while (it.hasNext() && active) {
                    SelectionKey key = it.next();
                    it.remove();
                    // Get references to channel and associated RTP socket
                    DatagramChannel udpChannel = (DatagramChannel) key.channel();
                    Object attachment = key.attachment();

                    if (attachment == null) {
                        continue;
                    }

                    try {
                        if (attachment instanceof ProtocolHandler) {
                            ProtocolHandler handler = (ProtocolHandler) key.attachment();
                            if (!udpChannel.isOpen()) {
                                handler.onClosed();
                                continue;
                            }

                            if (key.isValid()) {
                                logger.debug( "UDP Data Receive !!!!!" );
                                    handler.receive( udpChannel );
                            }

                        } else if (attachment instanceof Channel) {
                            logger.debug( "channel !!!!!" );
                            Channel channel = (Channel) attachment;

                            // Perform an operation only if channel is open and key is valid
                            if (udpChannel.isOpen()) {
                                if (key.isValid()) {
                                    channel.receive();

                                    if (channel.hasPendingData()) {
                                        channel.send();
                                    }
                                }
                            } else {
                                // Close data channel if datagram channel is closed
                                channel.close();
                            }
                        } else if (attachment instanceof NetworkChannel) {
                            logger.debug( "NetworkChannel !!!!!" );
                            NetworkChannel channel = (NetworkChannel) attachment;

                            // Perform an operation only if channel is open and key is valid
                            if (udpChannel.isOpen()) {
                                if (key.isValid()) {
                                    channel.receive();
                                }
                            } else {
                                // Close data channel if datagram channel is closed
                                channel.close();
                            }
                        }
                    } catch (Exception e) {
                        logger.error("An unexpected problem occurred while reading from channel.", e);
                    }
                }
                localSelector.selectedKeys().clear();
            }
        }

        private ByteBuffer buffer;

        private void udpread(SelectionKey key) {
            DatagramChannel channel = (DatagramChannel) key.channel();

            // Get buffer ready to read new data
//            this.buffer.clear();

            // Read data from channel
            int dataLength = 0;
            try {
                logger.debug( "channel.receive !!!!!");
                SocketAddress remotePeer = channel.receive( this.buffer);
                logger.debug( "channel.receive end !!!!");
                if (!channel.isConnected() && remotePeer != null) {
                    channel.connect(remotePeer);
                }
                dataLength = (remotePeer == null) ? -1 : this.buffer.position();
            } catch (IOException e) {
                dataLength = -1;
            }

            // Stop if socket was shutdown or error occurred
            if (dataLength == -1) {
                try {
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                key.cancel();
                return;
            }
        }
    }

    private static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder();
        for(final byte b: a)
            sb.append(String.format("%02x ", b&0xff));
        return sb.toString();
    }

    private ByteBuffer buffer;

//    private void udpread(SelectionKey key) throws IOException {
//        DatagramChannel channel = (DatagramChannel) key.channel();
//
//        // Get buffer ready to read new data
//        this.buffer.clear();
//
//        // Read data from channel
//        int dataLength = 0;
//        try {
//            SocketAddress remotePeer = channel.receive( this.buffer);
//            if (!channel.isConnected() && remotePeer != null) {
//                channel.connect(remotePeer);
//            }
//            dataLength = (remotePeer == null) ? -1 : this.buffer.position();
//        } catch (IOException e) {
//            dataLength = -1;
//        }
//
//        // Stop if socket was shutdown or error occurred
//        if (dataLength == -1) {
//            channel.close();
//            key.cancel();
//            return;
//        }
//
//        // Delegate work to a handler
////        if (protocolHandler != null) {
////            // Handler processes the requests and provides an answer
////            byte[] data = this.buffer.array();
////            byte[] response = protocolHandler.process(key, data, dataLength);
////            // Keep reading if handler provided no answer
////            if (response != null) {
////                // schedule STUN request to be sent to browser
////                this.scheduler.schedule(channel, response, response.length);
////            }
////        }
//    }

}