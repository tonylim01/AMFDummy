/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file UdpServer.java
 * @author Tony Lim
 *
 */

package media.platform.amf.core.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UdpServer {

    private static final Logger logger = LoggerFactory.getLogger(UdpServer.class);

    private static final int MAX_BUFFER_SIZE = 4096;

    private DatagramSocket socket;
    private Thread thread = null;
    private byte[] buf = new byte[MAX_BUFFER_SIZE];
    private boolean isQuit = false;
    private int serverPort;

    public UdpServer(int port) {
        try {
            socket = new DatagramSocket(port);
            logger.debug("UdpServer ip {} port {}", socket.getLocalAddress().toString(), port);
            logger.debug("UdpServer ip {} port {}", socket.getLocalSocketAddress().toString(), port);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.serverPort = port;
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

        socket.close();
    }

    class UdpServerRunnable implements Runnable {
        @Override
        public void run() {

            logger.info("UdpServer ({}) startScheduler", serverPort);
            while (!isQuit) {
                try {
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    if (callback != null) {
                        callback.onReceived(packet.getAddress().getAddress(), packet.getPort(), buf, buf.length);
                    }
                } catch (Exception e) {
                    logger.warn("Exception [{}] [{}]", e.getClass(), e.getMessage());
                    if (e.getClass() != IOException.class) {
                        isQuit = true;
                    }
                }
            }
            logger.info("UdpServer ({}) end", serverPort);
        }
    }
}
