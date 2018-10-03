package media.platform.amf.rtpcore.core.scheduler;

import java.util.concurrent.TimeUnit;

public class WallClock implements Clock {

    /**
     * The default time unit: nanoseconds.
     */
    private TimeUnit timeUnit = TimeUnit.NANOSECONDS;

    @Override
    public long getTime() {
        return System.nanoTime();
    }

    @Override
    public long getCurrentTime() {
        return System.currentTimeMillis();
    }

    @Override
    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    @Override
    public long getTime(TimeUnit timeUnit) {
        return timeUnit.convert(System.nanoTime(), this.timeUnit);
    }

}
