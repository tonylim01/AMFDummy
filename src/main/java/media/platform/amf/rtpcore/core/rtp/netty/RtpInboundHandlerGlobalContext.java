
package media.platform.amf.rtpcore.core.rtp.netty;

import media.platform.amf.rtpcore.core.rtp.jitter.JitterBuffer;
import media.platform.amf.rtpcore.core.scheduler.Clock;
import media.platform.amf.rtpcore.core.sdp.format.RTPFormats;
import media.platform.amf.rtpcore.core.rtp.rtp.RTPInput;
import media.platform.amf.rtpcore.core.rtp.rtp.statistics.RtpStatistics;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class RtpInboundHandlerGlobalContext {

    // RTP Components
    private final Clock clock;
    private final RtpStatistics statistics;
    private final JitterBuffer jitterBuffer;
    private final RTPInput rtpInput;

    // Handler Context
    private final AtomicReference<RTPFormats> formats;
    private final AtomicBoolean loopable;
    private final AtomicBoolean receivable;

    public RtpInboundHandlerGlobalContext(Clock clock, RtpStatistics statistics, JitterBuffer jitterBuffer, RTPInput rtpInput) {
        // RTP Components
        this.clock = clock;
        this.statistics = statistics;
        this.rtpInput = rtpInput;
        this.jitterBuffer = jitterBuffer;
        this.jitterBuffer.setListener(this.rtpInput);

        // Handler Context
        this.formats = new AtomicReference<RTPFormats>(new RTPFormats());
        this.loopable = new AtomicBoolean(false);
        this.receivable = new AtomicBoolean(false);
    }

    Clock getClock() {
        return clock;
    }

    RtpStatistics getStatistics() {
        return statistics;
    }

    JitterBuffer getJitterBuffer() {
        return jitterBuffer;
    }

    RTPInput getRtpInput() {
        return rtpInput;
    }


    boolean isLoopable() {
        return this.loopable.get();
    }

    public void setLoopable(boolean loopable) {
        this.loopable.set(loopable);
    }

    boolean isReceivable() {
        return this.receivable.get();
    }

    public void setReceivable(boolean receivable) {
        this.receivable.set(receivable);
    }

    RTPFormats getFormats() {
        return this.formats.get();
    }

    public void setFormats(RTPFormats rtpFormats) {
        this.formats.set(rtpFormats);
    }

}
