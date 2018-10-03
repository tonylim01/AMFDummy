package media.platform.amf.rtpcore.format;

import media.platform.amf.rtpcore.core.spi.format.Format;

public class RTPFormat implements Cloneable {
    //payload id
    private int id;
    //format descriptor
    private Format format;

    //RTP clock rate measured in Hertz.
    private int clockRate;

    /**
     * Creates new format descriptor.
     *
     * @param id the payload number
     * @param format format descriptor
     */
    public RTPFormat(int id, Format format) {
        this.id = id;
        this.format = format;
    }

    /**
     * Creates new descriptor.
     * 
     * @param id payload number
     * @param format formats descriptor
     * @param clockRate RTP clock rate
     */
    public RTPFormat(int id, Format format, int clockRate) {
        this.id = id;
        this.format = format;
        this.clockRate = clockRate;
    }

    /**
     * Gets the payload number
     *
     * @return payload number
     */
    public int getID() {
        return id;
    }

    /**
     * Modifies payload number.
     *
     * @param id the new payload number.
     */
    protected void setID(int id) {
        this.id = id;
    }

    /**
     * Gets the rtp clock rate.
     * 
     * @return the rtp clock rate in Hertz
     */
    public int getClockRate() {
        return clockRate;
    }

    /**
     * Modify rtp clock rate.
     *
     * @param clockRate the new value in Hertz.
     */
    public void setClockRate(int clockRate) {
        this.clockRate = clockRate;
    }

    /**
     * Gets format.
     *
     * @return format descriptor.
     */
    public Format getFormat() {
        return format;
    }

    /**
     * Modifies format.
     *
     * @param format the new format descriptor.
     */
    public void setFormat(Format format) {
        this.format = format;
    }

    @Override
    public RTPFormat clone() {
        Format f = (Format) format.clone();
        return new RTPFormat(id, f, clockRate);
    }
    
    @Override
    public String toString() {
        return id + " " + format;
    }
}
