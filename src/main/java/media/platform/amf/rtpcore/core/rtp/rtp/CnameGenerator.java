package media.platform.amf.rtpcore.core.rtp.rtp;

import javax.xml.bind.DatatypeConverter;
import java.nio.ByteBuffer;
import java.util.UUID;

public class CnameGenerator {

	/**
	 * For every new RTP session, a new RTCP CNAME is created by generating a
	 * cryptographically pseudorandom value as described in [RFC4086]. This
	 * value MUST be at least 96 bits.
	 * <p>
	 * After performing that procedure, minimally the least significant 96 bits
	 * SHOULD be converted to ASCII using Base64 encoding [RFC4648]. The RTCP
	 * CNAME cannot change over the life of an RTP session [RFC3550]. The
	 * "user@" part of the RTCP CNAME is omitted when generating per-session
	 * RTCP CNAMEs.
	 * </p>
	 * 
	 * @return
	 */
	public static String generateCname() {
		// generate unique identifier
		UUID uuid = UUID.randomUUID();
		// get the 64 least significant bits 
		long leastSignificantBits = uuid.getLeastSignificantBits();
		// get the 64 most significant bits 
		long mostSignificantBits = uuid.getMostSignificantBits();
		
		// convert the 128 bits to byte array
		// note: 128 / 8 = 16 bytes
		ByteBuffer buffer = ByteBuffer.allocate(16);
		buffer.putLong(leastSignificantBits).putLong(mostSignificantBits);
		buffer.flip();

		// get the 96 least significant bits
		// note: 96 / 8 = 12
		byte[] data = new byte[12];
		buffer.get(data, 0, 12);
		
		// convert the least 96 bits to ASCII Base64
		return DatatypeConverter.printBase64Binary(data);
	}

}