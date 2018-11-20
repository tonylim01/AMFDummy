package media.platform.amf.redundant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class RedundantServer {
    private static final Logger logger = LoggerFactory.getLogger(RedundantServer.class);

    private static final int MAX_BUFFER_SIZE = 4096;
    private static final int DEFAULT_QUEUE_SIZE = 32;

    private DatagramSocket socket;
    private Thread serverThread = null;
    private Thread consumerThread = null;
    private boolean isQuit = false;
    private int serverPort;
    private BlockingQueue<byte[]> queue;

    public RedundantServer(int port) {
        try {
            socket = new DatagramSocket(port);
        } catch (Exception e) {
            e.printStackTrace();
        }

        queue = new ArrayBlockingQueue<>(DEFAULT_QUEUE_SIZE);

        this.serverPort = port;
    }

    public boolean start() {
        if (serverThread != null) {
            return false;
        }

        consumerThread = new Thread(new RedundantConsumer(queue));
        consumerThread.start();

        serverThread = new Thread(new RedundantServer.RedundantServerRunnable(queue));
        serverThread.start();

        return true;
    }

    public void stop() {
        if (serverThread != null) {
            serverThread.interrupt();
            serverThread = null;
        }

        if (consumerThread != null) {
            consumerThread.interrupt();
            consumerThread = null;
        }

        socket.close();
    }

    class RedundantServerRunnable implements Runnable {

        private byte[] buf = new byte[MAX_BUFFER_SIZE];
        BlockingQueue<byte[]> recvQueue;

        public RedundantServerRunnable(BlockingQueue<byte[]> queue) {
            this.recvQueue = queue;
        }

        @Override
        public void run() {

            logger.info("RedundantServer ({}) start", serverPort);
            while (!isQuit) {
                try {
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);

                    byte[] data = new byte[buf.length];
                    System.arraycopy(buf, 0, data,0, buf.length);

                    recvQueue.put(data);

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
