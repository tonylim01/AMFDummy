package media.platform.amf.rtpcore.core.rtp.jitter;

import media.platform.amf.rtpcore.core.sdp.format.RTPFormat;
import media.platform.amf.rtpcore.core.spi.memory.Frame;
import media.platform.amf.rtpcore.core.spi.memory.Memory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import media.platform.amf.rtpcore.core.rtp.rtp.BufferListener;
import media.platform.amf.rtpcore.core.rtp.rtp.RtpClock;
import media.platform.amf.rtpcore.core.rtp.rtp.RtpPacket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//public class FixedJitterBuffer implements JitterBuffer, Serializable {
public class FixedJitterBuffer implements JitterBuffer {

    private static final long serialVersionUID = -389930569631795779L;
    private static final Logger logger = LoggerFactory.getLogger( FixedJitterBuffer.class);

    // The underlying buffer size
    private static final int QUEUE_SIZE = 10;
    // the underlying buffer
    private ArrayList<Frame> queue = new ArrayList<Frame>( QUEUE_SIZE);

    // RTP clock
    private RtpClock rtpClock;
    // first received sequence number
    private long isn = -1;

    // allowed jitter
    private long jitterBufferSize;

    // packet arrival dead line measured on RTP clock.
    // initial value equals to infinity
    private long arrivalDeadLine = 0;

    // packet arrival dead line measured on RTP clock.
    // initial value equals to infinity
    private long droppedInRaw = 0;

    // The number of dropped packets
    private int dropCount;

    // known duration of media wich contains in this buffer.
    private volatile long duration;

    // buffer's monitor
    private BufferListener listener;

    private AtomicBoolean ready;

    /**
     * used to calculate network jitter. currentTransit measures the relative time it takes for an RTP packet to arrive from the
     * remote server to MMS
     */
    private long currentTransit = 0;

    /**
     * continuously updated value of network jitter
     */
    private long currentJitter = 0;

    // currently used format
    private RTPFormat format;

    private Boolean useBuffer = true;



    private final Lock lock = new ReentrantLock();

    /**
     * Creates new instance of jitter.
     * 
     * @param clock the rtp clock.
     */
    public FixedJitterBuffer(RtpClock clock, int jitterBufferSize) {
        this.rtpClock = clock;
        this.jitterBufferSize = jitterBufferSize;
        this.ready = new AtomicBoolean(false);
    }

    private void initJitter(RtpPacket firstPacket) {
        long arrival = rtpClock.getLocalRtpTime();
        long firstPacketTimestamp = firstPacket.getTimestamp();
        currentTransit = arrival - firstPacketTimestamp;
    }

    /**
     * Calculates the current network jitter, which is an estimate of the statistical variance of the RTP data packet
     * interarrival time: http://tools.ietf.org/html/rfc3550#appendix-A.8
     */
    private void estimateJitter(RtpPacket newPacket) {
        long arrival = rtpClock.getLocalRtpTime();
        long newPacketTimestamp = newPacket.getTimestamp();
        long transit = arrival - newPacketTimestamp;
        long d = transit - currentTransit;
        if (d < 0) {
            d = -d;
        }
        // logger.info(String.format("recalculating jitter: arrival=%d, newPacketTimestamp=%d, transit=%d, transit delta=%d",
        // arrival, newPacketTimestamp, transit, d ));
        currentTransit = transit;
        currentJitter += d - ((currentJitter + 8) >> 4);
    }

    /**
     * 
     * @return the current value of the network RTP jitter. The value is in normalized form as specified in RFC 3550
     *         http://tools.ietf.org/html/rfc3550#appendix-A.8
     */
    public long getEstimatedJitter() {
        long jitterEstimate = currentJitter >> 4;
        // logger.info(String.format("Jitter estimated at %d. Current transit time is %d.", jitterEstimate, currentTransit));
        return jitterEstimate;
    }

    /**
     * Gets the interarrival jitter.
     *
     * @return the current jitter value.
     */
    public double getJitter() {
        return 0;
    }

    /**
     * Gets the maximum interarrival jitter.
     *
     * @return the jitter value.
     */
    public double getMaxJitter() {
        return 0;
    }

    /**
     * Get the number of dropped packets.
     * 
     * @return the number of dropped packets.
     */
    public int getDropped() {
        return dropCount;
    }

    public boolean bufferInUse() {
        return this.useBuffer;
    }

    @Override
    public void setInUse(boolean useBuffer) {
        this.useBuffer = useBuffer;
    }

    /**
     * Assigns listener for this buffer.
     * 
     * @param listener the listener object.
     */
    @Override
    public void setListener(BufferListener listener) {
        this.listener = listener;
    }

    private void safeWrite(RtpPacket packet, RTPFormat format) {
        if (this.format == null || this.format.getID() != format.getID()) {
            this.format = format;
            if (logger.isDebugEnabled()) {
                logger.debug("Format changed! [" + this.format.toString() + "]");
            }
        }

        // if this is first packet then synchronize clock
        if (isn == -1) {
            rtpClock.synchronize(packet.getTimestamp());
            isn = packet.getSeqNumber();
            initJitter(packet);
        } else {
            estimateJitter(packet);
        }

        // update clock rate
        rtpClock.setClockRate(this.format.getClockRate());

        // drop outstanding packets
        // packet is outstanding if its timestamp of arrived packet is less
        // then consumer media time
        if (packet.getTimestamp() < this.arrivalDeadLine) {
            if (logger.isTraceEnabled()) {
                logger.trace("drop packet: dead line=" + arrivalDeadLine + ", packet time=" + packet.getTimestamp() + ", seq="
                        + packet.getSeqNumber() + ", payload length=" + packet.getPayloadLength() + ", format="
                        + this.format.toString());
            }
            dropCount++;

            // checking if not dropping too much
            droppedInRaw++;
            if (droppedInRaw == QUEUE_SIZE / 2 || queue.size() == 0) {
                arrivalDeadLine = 0;
            } else {
                return;
            }
        }

        Frame f = Memory.allocate( packet.getPayloadLength());
        // put packet into buffer irrespective of its sequence number
        f.setHeader(null);
        f.setSequenceNumber(packet.getSeqNumber());
        // here time is in milliseconds
        f.setTimestamp(rtpClock.convertToAbsoluteTime(packet.getTimestamp()));
        f.setOffset(0);
        f.setLength(packet.getPayloadLength());
        packet.getPayload(f.getData(), 0);

        // set format
        f.setFormat(this.format.getFormat());

        // make checks only if have packet
        if (f != null) {

            droppedInRaw = 0;

            // find correct position to insert a packet
            // use timestamp since its always positive
            int currIndex = queue.size() - 1;
            while (currIndex >= 0 && queue.get(currIndex).getTimestamp() > f.getTimestamp()) {
                currIndex--;
            }

            // check for duplicate packet
            if (currIndex >= 0 && queue.get(currIndex).getSequenceNumber() == f.getSequenceNumber()) {
                return;
            }

            queue.add(currIndex + 1, f);

            // recalculate duration of each frame in queue and overall duration
            // since we could insert the frame in the middle of the queue
            duration = 0;
            if (queue.size() > 1) {
                duration = queue.get(queue.size() - 1).getTimestamp() - queue.get(0).getTimestamp();
            }

            for (int i = 0; i < queue.size() - 1; i++) {
                // duration measured by wall clock
                long d = queue.get(i + 1).getTimestamp() - queue.get(i).getTimestamp();
                // in case of RFC2833 event timestamp remains same
                queue.get(i).setDuration(d > 0 ? d : 0);
            }

            // if overall duration is negative we have some mess here,try to
            // reset
            if (duration < 0 && queue.size() > 1) {
                logger.warn("Something messy happened. Reseting jitter buffer!");
                reset();
                return;
            }

            // overflow?
            // only now remove packet if overflow , possibly the same packet we just received
            if (queue.size() > QUEUE_SIZE) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Jitter Buffer overflow! [duration=" + duration + "ms, frames=" + queue.size() + "]");
                }
                dropCount++;
                queue.remove(0).recycle();
            }

            // check if this buffer already full
            boolean readyTest = (!useBuffer || (duration >= jitterBufferSize && queue.size() > 1));
            if (ready.compareAndSet(false, readyTest)) {
                if (ready.get() && listener != null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Jitter Buffer is ready! [duration=" + duration + "ms, frames=" + queue.size() + "]");
                    }
                    listener.onFill();
                }
            }
        }
    }

    /**
     * Accepts specified packet
     *
     * @param packet the packet to accept
     */
    @Override
    public void write(RtpPacket packet, RTPFormat format) {
        // checking format
        if (format == null) {
            if (logger.isTraceEnabled()) {
                logger.trace("No format specified. Packet dropped!");
            }
            return;
        }

        boolean locked = false;
        try {
            locked = this.lock.tryLock() || this.lock.tryLock(5, TimeUnit.MILLISECONDS);
            if (locked) {
                safeWrite(packet, format);
            }
        } catch (InterruptedException e) {
            if (logger.isTraceEnabled()) {
                logger.trace("Could not aquire write lock for jitter buffer. Dropped packet.");
            }
        } finally {
            if (locked) {
                this.lock.unlock();
            }
        }
    }

    /**
     * Polls packet from buffer's head.
     *
     * @param timestamp the media time measured by reader
     * @return the media frame.
     */
    public Frame read(long timestamp) {
        Frame frame = null;
        boolean locked = false;
        try {
            locked = this.lock.tryLock() || this.lock.tryLock(5, TimeUnit.MILLISECONDS);
            if (locked) {
                frame = safeRead();
            } else {
                this.ready.set(false);
            }
        } catch (InterruptedException e) {
            if (logger.isTraceEnabled()) {
                logger.trace("Could not acquire reading lock for jitter buffer.");
            }
            this.ready.set(false);
        } finally {
            if (locked) {
                lock.unlock();
            }
        }
        return frame;
    }

    private Frame safeRead() {
        if (queue.size() == 0) {
            this.ready.set(false);
            if (logger.isTraceEnabled()) {
                logger.trace("Jitter Buffer is empty. Consumer will wait until buffer is filled.");
            }
            return null;
        }

        // extract packet
        Frame frame = queue.remove(0);

        // buffer empty now? - change ready flag.
        if (queue.size() == 0) {
            this.ready.set(false);
            if (logger.isTraceEnabled()) {
                logger.trace("Read last packet from Jitter Buffer.");
            }
            // arrivalDeadLine = 0;
            // set it as 1 ms since otherwise will be dropped by pipe
            frame.setDuration(1);
        }

        arrivalDeadLine = rtpClock.convertToRtpTime(frame.getTimestamp() + frame.getDuration());

        // convert duration to nanoseconds
        frame.setDuration(frame.getDuration() * 1000000L);
        frame.setTimestamp(frame.getTimestamp() * 1000000L);

        return frame;
    }

    /**
     * Resets buffer.
     */
    public void reset() {
        boolean locked = false;
        try {
            locked = lock.tryLock() || lock.tryLock(5, TimeUnit.MILLISECONDS);
            if (locked) {
                while (queue.size() > 0) {
                    queue.remove(0).recycle();
                }
            }
        } catch (InterruptedException e) {
            if (logger.isTraceEnabled()) {
                logger.trace("Could not acquire lock to reset jitter buffer.");
            }
        } finally {
            if (locked) {
                lock.unlock();
            }
        }
    }

    public void restart() {
        reset();
        this.ready.set(false);
        arrivalDeadLine = 0;
        dropCount = 0;
        droppedInRaw = 0;
        format = null;
        isn = -1;

        if (logger.isDebugEnabled()) {
            logger.debug("Restarted jitter buffer.");
        }
    }

}
