package media.platform.amf.core.socket;

import media.platform.amf.core.socket.packets.SilencePacket;
import media.platform.amf.core.socket.packets.Vocoder;
import media.platform.amf.rtpcore.Process.UdpClient;
import media.platform.amf.rtpcore.core.rtp.rtp.RtpPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
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

    private int vocoder;
    private int vocoderMode;
    private int payloadType;

    //private Queue<UdpPacket> buffer;
    private List<UdpPacket> buffer;
    private UdpPacket lastPacket;

    public JitterSender(int vocoder, int vocoderMode, int payloadType, int duration, int jitterCount, int payloadSize) {

        this.vocoder = vocoder;
        this.vocoderMode = vocoderMode;
        this.payloadType = payloadType;

        this.duration = (duration > 0) ? duration : DEFAULT_DURATION;
        this.payloadSize = (payloadSize > 0) ? payloadSize : DEFAULT_PAYLOAD_SIZE;
        this.jitterCount = (jitterCount > 0) ? jitterCount : DEFAULT_JITTER_COUNT;

        seq = 0;
        ssrc = new Random().nextLong();
        timestamp = System.currentTimeMillis();

        //buffer = new ConcurrentLinkedQueue<>();
        buffer = new ArrayList<>();
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

    public void put(int seqNo, byte[] buf) {
        if (buf == null) {
            return;
        }

        if (seqNo >= 0) {
            boolean buffered = false;

            UdpPacket udpPacket = new UdpPacket(seqNo, buf, buf.length);
            if (lastPacket != null) {
                int seqDiff = seqNo - lastPacket.getSeqNo();
                if (seqDiff < 0) {

                    // how to insert
                    synchronized (buffer) {
                        if (buffer.size() > 0) {
                            int i;
                            for (i = buffer.size() - 1; i <= 0; --i) {
                                if (buffer.get(i).getSeqNo() < seqNo) {
                                    break;
                                }
                            }

                            buffer.add(i + 1, udpPacket);
                            buffered = true;
                        }
                    }
                }
            }

            if (!buffered) {
                synchronized (buffer) {
                    buffer.add(udpPacket);
                }
            }
        }
        else {
            UdpPacket udpPacket = new UdpPacket(0, buf, buf.length);

            synchronized (buffer) {
                buffer.add(udpPacket);
            }
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
            RtpPacket rtpPacket = null;

            while (!isQuit) {

                try {
                    UdpPacket udpPacket = null;
                    synchronized (buffer) {
                        if (buffer.size() > 0) {
                            udpPacket = buffer.get(0);
                        }
                    }

//                    logger.debug("packet found. client ({}) data ({})", udpPacket != null, udpPacket.getData() != null);

                    if (udpPacket != null && udpPacket.getData() != null) {

                        if (rtpPacket == null || (rtpPacket != null && rtpPacket.getPayloadLength() != udpPacket.getData().length)) {
                            if (rtpPacket != null) {
                                rtpPacket.getBuffer().clear();
                                rtpPacket = null;
                            }
                            rtpPacket = new RtpPacket(udpPacket.getData().length + RtpPacket.FIXED_HEADER_SIZE, true);
                        }

                        rtpPacket.wrap(false, payloadType, seq, timestamp, ssrc, udpPacket.getData(), 0, udpPacket.getData().length);

                        udpPacket.clear();
                        udpPacket = null;

                        synchronized (buffer) {
                            if (buffer.size() > 0) {
                                buffer.remove(0);
                            }
                        }
                    }
                    else {
                        byte[] silenceBuf = SilencePacket.get(vocoder, vocoderMode);
                        if (silenceBuf != null) {
                            if (rtpPacket == null || (rtpPacket != null && rtpPacket.getPayloadLength() != silenceBuf.length)) {
                                if (rtpPacket != null) {
                                    rtpPacket.getBuffer().clear();
                                    rtpPacket = null;
                                }
                                rtpPacket = new RtpPacket(silenceBuf.length + RtpPacket.FIXED_HEADER_SIZE, true);
                            }

                            rtpPacket.wrap(false, payloadType, seq, timestamp, ssrc, silenceBuf, 0, silenceBuf.length);
                        }
                        else {
                            // TODO
                            logger.error("No rtp packet");
                            rtpPacket.getBuffer().clear();
                            rtpPacket = null;
                        }
                    }


                    if (udpClient != null && rtpPacket != null) {
                        udpClient.send(rtpPacket.getRawData());
                    }

                    seq++;
                    switch (vocoder) {
                        case Vocoder.VOCODER_AMR_WB:
                            timestamp += 320;
                            break;
                        default:
                            timestamp += 160;
                            break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                tick++;
                long endTime = System.currentTimeMillis();
                long delay = (tick * duration) - (endTime - startTime);

                // For debugging. What makes the delay?
                if ((buffer.size() - beforeRemaining > 5) || (delay <= 0) || (delay >= 50)) {
                    logger.debug("-> tick {} remaining {} delay {}", tick, buffer.size(), delay);
                    --delay;
                }

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

                if (buffer.size() > 10) {
                    int n = buffer.size() - 10;
                    logger.warn("Trash [{}] packets", n);
                    for (int i = 0; i < n; i++) {
                        synchronized (buffer) {
                            UdpPacket trash = buffer.get(0);
                            trash.clear();
                            buffer.remove(0);
                        }
                    }
                }

                beforeRemaining = buffer.size();
            }

            logger.info("LoopProc end");
        }

    }

    public int getVocoder() {
        return vocoder;
    }

    public int getVocoderMode() {
        return vocoderMode;
    }

    public int getPayloadType() {
        return payloadType;
    }
}