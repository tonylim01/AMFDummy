package media.platform.amf.rtpcore.core.rtp.rtcp;

public class RtcpSdesItem {

	public static final short RTCP_SDES_END = 0;
	public static final short RTCP_SDES_CNAME = 1;
	public static final short RTCP_SDES_NAME = 2;
	public static final short RTCP_SDES_EMAIL = 3;
	public static final short RTCP_SDES_PHONE = 4;
	public static final short RTCP_SDES_LOC = 5;
	public static final short RTCP_SDES_TOOL = 6;
	public static final short RTCP_SDES_NOTE = 7;
	public static final short RTCP_SDES_PRIV = 8;

	/*
	 * SDES item
	 */

	/* type of item (rtcp_sdes_type_t) */
	private int type = 0;

	/* length of item (in octets) */
	private int length = 0;

	/* text, not null-terminated */
	private String text = null;

	public RtcpSdesItem(short type, String text) {
		this.type = type;
		this.text = text;
	}

	public RtcpSdesItem() {

	}

	public int decode(byte[] rawData, int offSet) {
		this.type = rawData[offSet++] & 0xFF;

		if (type == RtcpSdesItem.RTCP_SDES_END) {
			while (offSet < rawData.length) {
				if (rawData[offSet] != 0x00) {
					break;
				}
				offSet++;
			}
			return offSet;
		}

		this.length = (short) rawData[offSet++] & 0xFF;

		byte[] chunkData = new byte[length];
		System.arraycopy(rawData, offSet, chunkData, 0, length);
		this.text = new String(chunkData);

		offSet += length;

		return offSet;

	}

	public int encode(byte[] rawData, int offSet) {
		byte[] textData = this.text.getBytes();
		this.length = (short) textData.length;
		rawData[offSet++] = ((byte) ((this.type & 0x000000FF)));
		rawData[offSet++] = ((byte) ((this.length & 0x000000FF)));
		System.arraycopy(textData, 0, rawData, offSet, this.length);
		return (offSet + this.length);
	}

	public int getType() {
		return type;
	}

	public int getLength() {
		return length;
	}

	public String getText() {
		return text;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("SDES ITEM: \n");
		builder.append("type= ").append(resolveType(this.type)).append(", ");
		builder.append("value= ").append(this.text).append(", ");
		builder.append("length= ").append(this.length).append("\n");
		return builder.toString();
	}

	private String resolveType(int type) {
		switch (type) {
		case RTCP_SDES_END:
			return "END";
		case RTCP_SDES_CNAME:
			return "CNAME";
		case RTCP_SDES_NAME:
			return "NAME";
		case RTCP_SDES_EMAIL:
			return "EMAIL";
		case RTCP_SDES_PHONE:
			return "PHONE";
		case RTCP_SDES_LOC:
			return "LOC";
		case RTCP_SDES_TOOL:
			return "TOOL";
		case RTCP_SDES_NOTE:
			return "NOTE";
		case RTCP_SDES_PRIV:
			return "PRIV";
		default:
			return "UNKNOWN";
		}
	}
}
