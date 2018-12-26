
package media.platform.amf.rtpcore.core.rtp.rtcp;

import java.nio.charset.Charset;

public class RtcpAppDefined extends RtcpHeader {

	private byte[] data;
	private String name;
	private long ssrc;

	public RtcpAppDefined() {
		// TODO Auto-generated constructor stub
	}

	public RtcpAppDefined(boolean padding, int subType, long ssrc, String name, byte[] data) {
		super( padding, RTCP_APP );
		this.count = subType;
		this.ssrc = ssrc;

		// Should we check if name is 4 octets?
		this.name = name;
		this.data = data;
	}

	public int decode(byte[] rawData, int offSet) {
		int tmp = offSet;

		offSet = super.decode(rawData, offSet);

		this.ssrc |= rawData[offSet++] & 0xFF;
		this.ssrc <<= 8;
		this.ssrc |= rawData[offSet++] & 0xFF;
		this.ssrc <<= 8;
		this.ssrc |= rawData[offSet++] & 0xFF;
		this.ssrc <<= 8;
		this.ssrc |= rawData[offSet++] & 0xFF;

		byte[] nameBytes = new byte[4];
		System.arraycopy(rawData, offSet, nameBytes, 0, 4);
		offSet+=4;

		this.name = new String(nameBytes, Charset.defaultCharset());

		this.data = new byte[(this.length - 12)];
		System.arraycopy(rawData, offSet, this.data, 0, this.data.length);
		offSet += this.data.length;

		return offSet;
	}

	public int encode(byte[] rawData, int offSet) {

		int startPosition = offSet;

		offSet = super.encode(rawData, offSet);

		rawData[offSet++] = ((byte) ((this.ssrc & 0xFF000000) >> 24));
		rawData[offSet++] = ((byte) ((this.ssrc & 0x00FF0000) >> 16));
		rawData[offSet++] = ((byte) ((this.ssrc & 0x0000FF00) >> 8));
		rawData[offSet++] = ((byte) ((this.ssrc & 0x000000FF)));

		byte[] nameBytes = this.name.getBytes(Charset.defaultCharset());

		for (int i = 0; i < 4; i++) {
			rawData[offSet++] = nameBytes[i];
		}

		System.arraycopy(data, 0, rawData, offSet, data.length);

		offSet += data.length;
		
		/* Reduce 4 octets of header and length is in terms 32bits word */
		this.length = (offSet - startPosition - 4) / 4;

		rawData[startPosition + 2] = ((byte) ((this.length & 0xFF00) >> 8));
		rawData[startPosition + 3] = ((byte) (this.length & 0x00FF));

		return offSet;
	}

	public byte[] getData() {
		return data;
	}

	public String getName() {
		return name;
	}

	public long getSsrc() {
		return ssrc;
	}
	
	public int getSubType(){
		return this.count;
	}

}
