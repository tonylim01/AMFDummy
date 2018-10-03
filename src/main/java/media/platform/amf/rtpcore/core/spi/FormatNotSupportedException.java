package media.platform.amf.rtpcore.core.spi;

public class FormatNotSupportedException extends Exception {

	private static final long serialVersionUID = -5699441095705760619L;

	/**
	 * Creates a new instance of <code>FormatNotSupportedException</code>
	 * without detail message.
	 */
	public FormatNotSupportedException() {
	}

	/**
	 * Constructs an instance of <code>FormatNotSupportedException</code> with
	 * the specified detail message.
	 * 
	 * @param msg
	 *            the detail message.
	 */
	public FormatNotSupportedException(String msg) {
		super(msg);
	}
}
