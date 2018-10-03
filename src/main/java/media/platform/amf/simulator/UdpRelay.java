package media.platform.amf.simulator;

import media.platform.amf.core.socket.UdpCallback;
import media.platform.amf.core.socket.UdpSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UdpRelay {

    private static final Logger logger = LoggerFactory.getLogger(UdpRelay.class);

    private UdpSocket udpSocket = null;
    private int localPort;

    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }

    /**
     * Transports packets received on localPort to remoteIp:remotePort
     * @param remoteIpAddress
     * @param remotePort
     */
    public void openUdpClient(String remoteIpAddress, int remotePort) {
        logger.debug("Open UDP client. remote [{}:{}] lport [{}]", remoteIpAddress, remotePort, localPort);
        udpSocket = new UdpSocket(remoteIpAddress, remotePort, localPort);
        udpSocket.setUdpCallback(new RelayUdpCallback());
        udpSocket.start();
    }

    public void closeUdpSocket() {
        if (udpSocket != null) {
            udpSocket.stop();
        }
    }

    class RelayUdpCallback implements UdpCallback {
        @Override
        public void onReceived(byte[] srcAddress, int srcPort, byte[] buf, int length) {
            logger.debug("UDP received: size [{}]", length);
            udpSocket.send(buf, length);
        }
    }
}
