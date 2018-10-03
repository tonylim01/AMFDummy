/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file UdpClient.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rtpcore.Process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UdpClient {

    /**
     * The internet address which is the target of this client
     */
    private static final Logger logger = LoggerFactory.getLogger( NettyUDPServer.class );
    private final InetAddress address;

    /**
     * The datagram socket used by this client
     */
    private final DatagramSocket clientSocket;

    /**
     * The destination port used by this client
     */
    private final int port;

    /**
     * Creates a new UDP client
     *
     * @param address The target address to use
     * @param port The target port to use
     */
    public UdpClient(final InetAddress address, final int port)
            throws SocketException {
        //Set the address
        this.address = address;
        //Set the port
        this.port = port;
        //Set up a new datagram socket
        clientSocket = new DatagramSocket();
        //And allow addresses to be reused
        clientSocket.setReuseAddress(true);
    }

    /**
     * Sends a packet
     *
     * @param payload The payload of bytes to send
     * @return The datagram packet sent
     */
    public void send(final byte[] payload) throws IOException {
        final DatagramPacket dp =
                new DatagramPacket(payload, payload.length, address, port);

        //logger.debug( "UDP Send IP : " + address.getHostAddress() + "Port : " + port);
        clientSocket.send(dp);
    }

    /**
     * Receives data from a datagram packet
     *
     * @param dp The datagram packet to use
     * @param timeout The timeout to use
     */
    public void receive(final DatagramPacket dp, final int timeout) throws IOException {
        clientSocket.setSoTimeout(timeout);
        clientSocket.receive(dp);
    }

    /**
     * Close this client and the socket it uses
     */
    public void close() {
        clientSocket.close();
    }

}
