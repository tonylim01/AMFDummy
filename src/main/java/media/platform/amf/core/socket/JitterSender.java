package media.platform.amf.core.socket;

import media.platform.amf.rtpcore.Process.UdpClient;
import media.platform.amf.rtpcore.core.rtp.rtp.RtpPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class JitterSender {

    private static final Logger logger = LoggerFactory.getLogger(JitterSender.class);

    private static final int DEFAULT_DURATION   = 20;   // millisec
    private static final int DEFAULT_JITTER_COUNT = 3;
    private static final int DEFAULT_BUFFER_COUNT = 64;
    private static final int DEFAULT_PAYLOAD_SIZE = 160;

    private int duration;   // millisec
    private int jitterCount;
    private int payloadSize;
    private boolean isQuit;
    private UdpClient udpClient;

    private int seq;
    private long ssrc;
    private long timestamp;
    private Thread thread;

    private Queue<UdpPacket> buffer;

    public JitterSender(int duration, int jitterCount, int payloadSize) {

        this.duration = (duration > 0) ? duration : DEFAULT_DURATION;
        this.payloadSize = (payloadSize > 0) ? payloadSize : DEFAULT_PAYLOAD_SIZE;
        this.jitterCount = (jitterCount > 0) ? jitterCount : DEFAULT_JITTER_COUNT;

        seq = 0;
        ssrc = new Random().nextLong();
        timestamp = System.currentTimeMillis();

        buffer = new ConcurrentLinkedQueue<>();
    }

    public void setUdpClient(UdpClient udpClient) {
        this.udpClient = udpClient;
    }

    public void start() {

        isQuit = false;
        thread = new Thread(new LoopProc());
        thread.start();
    }

    public void stop() {

        isQuit = true;
    }

    public void put(byte[] buf) {
        if (buf == null) {
            return;
        }

        UdpPacket udpPacket = new UdpPacket(buf, buf.length);

        try {
            buffer.offer(udpPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class LoopProc implements Runnable {
        @Override
        public void run() {

            logger.info("LoopProc start");
            int tick = 0;
            int beforeRemaining = 0;

            do {
                try {
                    Thread.sleep(20);
                } catch (Exception e) {
                    e.printStackTrace();
                }

//                logger.debug("buffer size {}", buffer.size());
            } while (!isQuit && buffer.size() < jitterCount);

            long startTime = System.currentTimeMillis();

            while (!isQuit) {

                try {
                    UdpPacket udpPacket = buffer.poll();

//                    logger.debug("packet found. client ({}) data ({})", udpPacket != null, udpPacket.getData() != null);

                    RtpPacket rtpPacket = null;
                    if (udpPacket != null && udpPacket.getData() != null) {

                        rtpPacket = new RtpPacket(udpPacket.getData().length + RtpPacket.FIXED_HEADER_SIZE, true);
                        rtpPacket.wrap(false, 8, seq, timestamp, ssrc, udpPacket.getData(), 0, udpPacket.getData().length);

                    }
                    else {
                        byte[] silenceBuf = SilencePacket.get(SilencePacket.VOCODER_ALAW);
                        if (silenceBuf != null) {
                            rtpPacket = new RtpPacket(silenceBuf.length + RtpPacket.FIXED_HEADER_SIZE, true);
                            rtpPacket.wrap(false, 8, seq, timestamp, ssrc, silenceBuf, 0, silenceBuf.length);
                        }
                        else {
                            // TODO
                        }
                    }


                    if (udpClient != null) {
                        udpClient.send(rtpPacket.getRawData());
                    }

                    seq++;
                    timestamp += payloadSize;

                } catch (Exception e) {
                    e.printStackTrace();
                }

                tick++;
                long endTime = System.currentTimeMillis();
                long delay = (tick * duration) - (endTime - startTime);

                if ((buffer.size() - beforeRemaining > 5) || (delay <= 0) || (delay >= 50)) {
                    logger.debug("-> tick {} remaining {} delay {}", tick, buffer.size(), delay);
                }

                beforeRemaining = buffer.size();

                if (delay > 0) {
                    try {
                        Thread.sleep(delay);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    tick++;
                }
            }

            logger.info("LoopProc end");
        }

    }
}
