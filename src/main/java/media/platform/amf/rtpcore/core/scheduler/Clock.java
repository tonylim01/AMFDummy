package media.platform.amf.rtpcore.core.scheduler;

import java.util.concurrent.TimeUnit;

public interface Clock {
    
    /**
     * Gets the elapsed time.
     * 
     * @return current time expressed in nanoseconds.
     */
    long getTime();
    
    /**
     * Gets the current time.
     * 
     * @return An absolute time stamp in milliseconds
     */
    long getCurrentTime();

    /**
     * Gets the current time.
     *
     * @param timeUnit the time measurement units.
     * @return the value in specified units.
     */
    long getTime(TimeUnit timeUnit);

    /**
     * Gets the time measurement units.
     *
     * @return the time measurement units.
     */
    TimeUnit getTimeUnit();
}
