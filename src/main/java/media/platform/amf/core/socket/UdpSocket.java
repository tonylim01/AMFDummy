/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file UdpSocket.java
 * @author Tony Lim
 *
 */

package media.platform.amf.core.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpSocket {

    private static final Logger logger = LoggerFactory.getLogger(UdpServer.class);

    private static final int MAX_BUFFER_SIZE = 4096;

    private DatagramSocket socket;
    private Thread thread = null;
    private byte[] buf = new byte[MAX_BUFFER_SIZE];
    private boolean isQuit = false;
    private InetAddress address;
    private int localPort;
    private int remotePort;
    private int tag = 0;

    private UdpSocket remoteSocket;

    public UdpSocket(String ipAddress, int remotePort, int localPort) {
        try {
            socket = new DatagramSocket(localPort);
            address = InetAddress.getByName(ipAddress);
            logger.debug("UdpSocket ip {} port {}", socket.getLocalSocketAddress().toString(), localPort);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.remotePort = remotePort;
        this.localPort = localPort;
    }

    private UdpCallback callback = null;

    public void setUdpCallback(UdpCallback callback) {
        this.callback = callback;
    }

    public boolean start() {
        if (thread != null) {
            return false;
        }

        thread = new Thread(new UdpServerRunnable());
        thread.start();

        return true;
    }

    public void stop() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }

        if (socket != null) {
            socket.close();
        }
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public int getTag() {
        return tag;
    }

    public UdpSocket getRemoteSocket() {
        return remoteSocket;
    }

    public void setRemoteSocket(UdpSocket remoteSocket) {
        this.remoteSocket = remoteSocket;
    }

    public boolean send(byte[] buf, int size) {
        if (socket == null) {
            return false;
        }

        if (buf == null || (buf != null && buf.length == 0)) {
            return false;
        }

        if (size > buf.length) {
            return false;
        }

        boolean result = false;
        DatagramPacket packet = new DatagramPacket(buf, size, address, remotePort);
        try {
            socket.send(packet);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    class UdpServerRunnable implements Runnable {
        @Override
        public void run() {

            logger.info("UdpSocket server ({}) start", localPort);
            while (!isQuit) {
                try {
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    if (callback != null) {
                        callback.onReceived(packet.getAddress().getAddress(), packet.getPort(), buf, packet.getLength());
                    }
                } catch (Exception e) {
                    logger.warn("Exception [{}] [{}]", e.getClass(), e.getMessage());
                    e.printStackTrace();
                    if (e.getClass() != IOException.class) {
                        isQuit = true;
                    }
                }
            }
            logger.info("UdpServer server ({}) end", localPort);
        }
    }

}
