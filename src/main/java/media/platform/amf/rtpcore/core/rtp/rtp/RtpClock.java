package media.platform.amf.rtpcore.core.rtp.rtp;

import media.platform.amf.rtpcore.core.scheduler.Clock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class RtpClock implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger( RtpClock.class);
	
    //absolute time clock
    private Clock wallClock;

    //the clock rate measured in Hertz.
    private int clockRate;
    private int scale;

    //the difference between media time measured by local and remote clock
    protected long drift;

    //the flag indicating the state of relation between local and remote clocks
    //the flag value is true if relation established
    private boolean isSynchronized;

    public RtpClock(Clock wallClock) {
        this.wallClock = wallClock;
    }
    
    public Clock getWallClock() {
		return wallClock;
	}

    /**
     * Modifies clock rate.
     *
     * @param clockRate the new value of clock rate in Hertz.
     */
    public void setClockRate(int clockRate) {
        this.clockRate = clockRate;
        this.scale = clockRate/1000;
    }

    /**
     * Gets the clock rate.
     *
     * @return the value in Hertz
     */
    public int getClockRate() {
        return clockRate;
    }

    /**
     * Synchronizes this clock with remote clock
     *
     * @param remote the time on remote clock.
     */
    public void synchronize(long remote) {
        this.drift = remote - getLocalRtpTime();
        this.isSynchronized = true;
    }

    /**
     * The state of the relation between remote and local clock.
     *
     * @return true if time is same on both clocks.
     */
    public boolean isSynchronized() {
        return this.isSynchronized;
    }
    
    /**
     * Resets clocks.
     */
    public void reset() {
        this.drift = 0;
        this.clockRate = 0;
        this.isSynchronized = false;
    }

    /**
     * Time in RTP timestamps.
     * @return
     */
    public long getLocalRtpTime() {
        return scale * wallClock.getTime(TimeUnit.MILLISECONDS) + drift;
    }
    
    /**
     * Returns the time in milliseconds
     * 
     * @param timestamp the rtp timestamp
     * @return the time in milliseconds
     */
    public long convertToAbsoluteTime(long timestamp) {
        return timestamp * 1000 / clockRate;
    }
    
    /**
     * Calculates RTP timestamp
     * 
     * @param time the time in milliseconds
     * @return rtp timestamp.
     */
    public long convertToRtpTime(long time) {
    	return time * clockRate / 1000;
    }

}
