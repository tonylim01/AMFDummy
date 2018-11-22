package media.platform.amf.rtpcore.core.rtp.rtcp;

public class RtcpBye extends RtcpHeader {

	private long[] ssrcs = new long[31];

	public RtcpBye() {

	}

	public RtcpBye(boolean padding) {
		super(padding, RtcpHeader.RTCP_BYE);
	}

	public int decode(byte[] rawData, int offSet) {

		int tmp = offSet;
		offSet = super.decode(rawData, offSet);

		for (int i = 0; i < this.count; i++) {
			this.ssrcs[i] |= rawData[offSet++] & 0xFF;
			this.ssrcs[i] <<= 8;
			this.ssrcs[i] |= rawData[offSet++] & 0xFF;
			this.ssrcs[i] <<= 8;
			this.ssrcs[i] |= rawData[offSet++] & 0xFF;
			this.ssrcs[i] <<= 8;
			this.ssrcs[i] |= rawData[offSet++] & 0xFF;
		}

		// Do we acre for optional part?

		return offSet;
	}

	public int encode(byte[] rawData, int offSet) {

		int startPosition = offSet;

		offSet = super.encode(rawData, offSet);

		for (int i = 0; i < this.count; i++) {
			long ssrc = ssrcs[i];

			rawData[offSet++] = ((byte) ((ssrc & 0xFF000000) >> 24));
			rawData[offSet++] = ((byte) ((ssrc & 0x00FF0000) >> 16));
			rawData[offSet++] = ((byte) ((ssrc & 0x0000FF00) >> 8));
			rawData[offSet++] = ((byte) ((ssrc & 0x000000FF)));
		}
		
		/* Reduce 4 octest of header and length is in terms 32bits word */
		this.length = (offSet - startPosition - 4) / 4;

		rawData[startPosition + 2] = ((byte) ((this.length & 0xFF00) >> 8));
		rawData[startPosition + 3] = ((byte) (this.length & 0x00FF));
		
		return offSet;
	}

	public void addSsrc(long ssrc) {
		this.ssrcs[this.count++] = ssrc;
	}

	public long[] getSsrcs() {
		return ssrcs;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("BYE:\n");
		builder.append("version= ").append(this.version).append(", ");
		builder.append("padding= ").append(this.padding).append(", ");
		builder.append("source count=").append(this.count).append(", ");
		builder.append("packet types=").append(this.packetType).append(", ");
		builder.append("length=").append(this.length).append(", ");
		for (int i = 0; i < this.ssrcs.length; i++) {
			builder.append("ssrc= ").append(this.ssrcs[i]);
			if(i < this.ssrcs.length - 1) {
				builder.append(", ");
			}
		}
		builder.append("\n");
		return builder.toString();
	}

}
