
package media.platform.amf.rtpcore.core.network.deprecated;

import java.util.concurrent.atomic.AtomicInteger;

public class RtpPortManager implements PortManager {

    public static final int MIN_PORT = 1024;
    // Todo : Managed RTP Port
    public static final int MAX_PORT = 10240;

    private final int minimum;
    private final int maximum;
    private final int step;
    private final AtomicInteger current;

    /**
     * Creates a new Port Manager.
     * 
     * @param minimum The lowest available port.
     * @param maximum The highest available port.
     */
    public RtpPortManager(int minimum, int maximum) {
        this.minimum = (minimum % 2 == 0) ? minimum : minimum + 1;
        this.maximum = (maximum % 2 == 0) ? maximum : maximum - 1;
        this.step = (this.maximum - this.minimum) / 2;
        this.current = new AtomicInteger(0);
    }

    /**
     * Create a new Port Manager with port range between {@link RtpPortManager#MIN_PORT} and {@link RtpPortManager#MAX_PORT}
     */
    public RtpPortManager() {
        this(MIN_PORT, MAX_PORT);
    }

    @Override
    public int getLowest() {
        return this.minimum;
    }

    @Override
    public int getHighest() {
        return this.maximum;
    }

    @Override
    public int next() {
        return this.maximum - (this.current.getAndAdd(1) % step) * 2;
    }

    public int peek() {
        return this.maximum - ((this.current.get() + 1) % step) * 2;
    }

    public int current() {
        return this.maximum - (this.current.get() % step) * 2;
    }

}
