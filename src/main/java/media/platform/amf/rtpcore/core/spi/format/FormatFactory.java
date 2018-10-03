package media.platform.amf.rtpcore.core.spi.format;

public class FormatFactory {

    /**
     * Creates new audio format descriptor.
     *
     * @param name the encoding name.
     */
    public static AudioFormat createAudioFormat(EncodingName name) {
        //check name and create specific

        //default format
        return new AudioFormat(name);
    }

    /**
     * Creates new format descriptor
     *
     * @param name the encoding
     * @param sampleRate sample rate value in Hertz
     * @param sampleSize sample size in bits
     * @param channels number of channels
     */
    public static AudioFormat createAudioFormat(EncodingName name, int sampleRate, int sampleSize, int channels) {
        AudioFormat fmt = createAudioFormat(name);
        fmt.setSampleRate(sampleRate);
        fmt.setSampleSize(sampleSize);
        fmt.setChannels(channels);
        return fmt;
    }

    /**
     * Creates new format descriptor
     *
     * @param name the encoding
     * @param sampleRate sample rate value in Hertz
     * @param sampleSize sample size in bits
     * @param channels number of channels
     */
    public static AudioFormat createAudioFormat(String name, int sampleRate, int sampleSize, int channels) {
        AudioFormat fmt = createAudioFormat(new EncodingName(name));
        fmt.setSampleRate(sampleRate);
        fmt.setSampleSize(sampleSize);
        fmt.setChannels(channels);
        return fmt;
    }

    /**
     * Creates new format descriptor
     *
     * @param name the encoding
     * @param sampleRate sample rate value in Hertz
     */
    public static AudioFormat createAudioFormat(String name, int sampleRate) {
        AudioFormat fmt = createAudioFormat(new EncodingName(name));
        fmt.setSampleRate(sampleRate);
        return fmt;
    }


	/**
	 * Creates a new format descriptor for application line
	 * 
	 * @param name
	 *            format encoding name
	 * @return the format descriptor
	 */
    public static ApplicationFormat createApplicationFormat(EncodingName name) {
    	return new ApplicationFormat(name);
    }
    
	/**
	 * Creates a new format descriptor for application line
	 * 
	 * @param name
	 *            format encoding name
	 * @return the format descriptor
	 */
    public static ApplicationFormat createApplicationFormat(String name) {
    	return new ApplicationFormat(name);
    }

}
