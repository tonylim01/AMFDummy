package media.platform.amf.rtpcore.core.rtp.jitter;

import media.platform.amf.rtpcore.core.sdp.format.RTPFormat;
import media.platform.amf.rtpcore.core.spi.memory.Frame;
import media.platform.amf.rtpcore.core.rtp.rtp.BufferListener;
import media.platform.amf.rtpcore.core.rtp.rtp.RtpPacket;

public interface JitterBuffer {

    /**
     * Offers a packet to the jitter buffer.
     * 
     * @param packet The RTP packet
     * @param format The format of the RTP packet
     */
    void write(RtpPacket packet, RTPFormat format);

    /**
     * Consumes a frame from the jitter buffer.
     * 
     * @param timestamp
     * @return The next ordered frame in the jitter buffer.
     */
    Frame read(long timestamp);

    /**
     * Sets a listener to be warned of events raised by the jitter buffer.
     * 
     * @param listener the listener
     */
    void setListener(BufferListener listener);

    /**
     * Sets whether the buffer is active or not.
     * 
     * @param inUse
     */
    void setInUse(boolean inUse);

    /**
     * Restarts the jitter buffer.
     */
    void restart();

}
