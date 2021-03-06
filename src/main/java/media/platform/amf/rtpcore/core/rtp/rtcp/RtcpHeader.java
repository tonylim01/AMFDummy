
package media.platform.amf.rtpcore.core.rtp.rtcp;

public abstract class RtcpHeader {

	/*
	 * RTPC Packet Types
	 */
	public static final int RTCP_SR = 200;
	public static final int RTCP_RR = 201;
	public static final int RTCP_SDES = 202;
	public static final int RTCP_BYE = 203;
	public static final int RTCP_APP = 204;

	/**
	 * protocol version
	 */
	protected int version;

	/**
	 * padding flag
	 */
	protected boolean padding;

	/**
	 * varies by packet types
	 */
	protected int count;

	/**
	 * RTCP packet types
	 */
	protected int packetType;

	/**
	 * Packet length in words, w/o this word
	 */
	protected int length;

	protected RtcpHeader() {
		this(false, 0);
	}

	public RtcpHeader(boolean padding, int pt) {
		this.padding = padding;
		this.packetType = pt;
		this.count = 0;
		this.length = 0;
		this.version = 2;
	}

	protected int decode(byte[] rawData, int offSet) {
		int b = rawData[offSet++] & 0xff;

		this.version = (b & 0xC0) >> 6;
		this.padding = (b & 0x20) == 0x020;

		this.count = b & 0x1F;

		this.packetType = rawData[offSet++] & 0x000000FF;

		this.length |= rawData[offSet++] & 0xFF;
		this.length <<= 8;
		this.length |= rawData[offSet++] & 0xFF;

		/**
		 * The length of this RTCP packet in 32-bit words minus one, including
		 * the header and any padding. (The offset of one makes zero a valid
		 * length and avoids a possible infinite loop in scanning a compound
		 * RTCP packet, while counting 32-bit words avoids a validity check for
		 * a multiple of 4.)
		 */
		this.length = (this.length * 4) + 4;

		return offSet;
	}

	protected int encode(byte[] rawData, int offSet) {
		rawData[offSet] = (byte) (this.version << 6);
		if (this.padding) {
			rawData[offSet] = (byte) (rawData[offSet] | 0x20);
		}

		rawData[offSet] = (byte) (rawData[offSet] | (this.count & 0x1F));

		offSet++;

		rawData[offSet++] = (byte) (this.packetType & 0x000000FF);

		// Setting length is onus of concrete class. But we increment the offSet
		offSet += 2;

		return offSet;
	}

	public int getVersion() {
		return version;
	}

	public boolean isPadding() {
		return padding;
	}

	public int getCount() {
		return count;
	}

	public int getPacketType() {
		return packetType;
	}

	public int getLength() {
		return length;
	}

}
