package media.platform.amf.simulator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import media.platform.amf.core.socket.UdpCallback;
import media.platform.amf.core.socket.UdpSocket;

public class BiUdpRelay {

    private static final Logger logger = LoggerFactory.getLogger(BiUdpRelay.class);

    /**
     * Working like below:
     *   srcLocalPort -> dstUdpSocket
     *   dstLocalPort -> srcUdpSocket
     */
    private UdpSocket srcUdpSocket = null;
    private UdpSocket dstUdpSocket = null;

    private int srcLocalPort;
    private int dstLocalPort;

    private AiifRelay aiifRelay = null;
    private String dstQueueName = null;

    private String sessionId;

    public BiUdpRelay(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setSrcLocalPort(int localPort) {
        srcLocalPort = localPort;
    }

    public void setDstLocalPort(int localPort) {
        dstLocalPort = localPort;
    }

    /**
     * Transports packets received on localPort to remoteIp:remotePort
     * @param remoteIpAddress
     * @param remotePort
     */
    public void openSrcUdpClient(String remoteIpAddress, int remotePort) {
        logger.debug("Open src UDP client. remote [{}:{}] lport [{}]", remoteIpAddress, remotePort, srcLocalPort);
        srcUdpSocket = new UdpSocket(remoteIpAddress, remotePort, srcLocalPort);
        srcUdpSocket.setUdpCallback(new RelayUdpCallback(srcUdpSocket));
        srcUdpSocket.start();

        updateRemoteSocket();
    }

    public void openDstUdpClient(String remoteIpAddress, int remotePort) {
        logger.debug("Open dst UDP client. remote [{}:{}] lport [{}]", remoteIpAddress, remotePort, dstLocalPort);
        dstUdpSocket = new UdpSocket(remoteIpAddress, remotePort, dstLocalPort);
        dstUdpSocket.setUdpCallback(new RelayUdpCallback(dstUdpSocket));
        dstUdpSocket.start();

        updateRemoteSocket();
    }

    private void updateRemoteSocket() {
        if (srcUdpSocket != null && dstUdpSocket != null) {
            dstUdpSocket.setRemoteSocket(srcUdpSocket);
        }
        if (srcUdpSocket != null && dstUdpSocket != null) {
            srcUdpSocket.setRemoteSocket(dstUdpSocket);
        }

    }

    public void setDupUdpQueue(String inputCodec, String dstQueueName) {
        logger.debug("Open UDP relay queue [{}]", dstQueueName);
        this.dstQueueName = dstQueueName;
        if (srcUdpSocket != null) {
            aiifRelay = new AiifRelay();

            aiifRelay.setSessionId(sessionId);
            aiifRelay.setInputCodec(inputCodec);
            if (dstQueueName != null) {
                aiifRelay.setRelayQueue(dstQueueName);

                String filename = String.format("/tmp/%s.pcm", dstQueueName);
                aiifRelay.saveToFile(filename);
            }
            else {
                String pipeName = String.format("cd_%d", srcLocalPort);
                aiifRelay.createPipe(pipeName);
            }
            aiifRelay.start();

            srcUdpSocket.setTag(aiifRelay.hashCode());
        }
    }

    public void closeUdpSocket() {
        logger.debug("[{} closeUdpSocket", sessionId);

        if (srcUdpSocket != null) {
            srcUdpSocket.stop();
        }
        if (dstUdpSocket != null) {
            dstUdpSocket.stop();
        }

        try {
            Thread.sleep(200);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (aiifRelay != null) {
            aiifRelay.stop();
        }
    }

    class RelayUdpCallback implements UdpCallback {

        private UdpSocket udpSocket;

        public RelayUdpCallback(UdpSocket udpSocket) {
            this.udpSocket = udpSocket;
        }

        @Override
        public void onReceived(byte[] srcAddress, int srcPort, byte[] buf, int length) {
            if (udpSocket.getRemoteSocket() != null) {
                udpSocket.getRemoteSocket().send(buf, length);
                if (udpSocket.getTag() != 0) {
                    aiifRelay.send(buf, length);

                }
            }
        }
    }
}
