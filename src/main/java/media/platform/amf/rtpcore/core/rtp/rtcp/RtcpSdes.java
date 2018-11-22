package media.platform.amf.rtpcore.core.rtp.rtcp;

import java.util.ArrayList;
import java.util.List;

public class RtcpSdes extends RtcpHeader {

	/**
	 * SDES
	 */
	private final List<RtcpSdesChunk> sdesChunks;

	public RtcpSdes() {
		this.sdesChunks = new ArrayList<RtcpSdesChunk>(RtcpPacket.MAX_SOURCES);
	}

	public RtcpSdes(boolean padding) {
		super( padding, RTCP_SDES );
		this.sdesChunks = new ArrayList<RtcpSdesChunk>(RtcpPacket.MAX_SOURCES);
	}

	public int decode(byte[] rawData, int offSet) {
		int tmp = offSet;
		offSet = super.decode(rawData, offSet);

		while ((offSet - tmp) < this.length) {
			RtcpSdesChunk rtcpSdesChunk = new RtcpSdesChunk();
			offSet = rtcpSdesChunk.decode(rawData, offSet);
			this.sdesChunks.add(rtcpSdesChunk);
		}
		return offSet;
	}

	public int encode(byte[] rawData, int offSet) {
		int startPosition = offSet;

		offSet = super.encode(rawData, offSet);
		for (RtcpSdesChunk rtcpSdesChunk : sdesChunks) {
			if (rtcpSdesChunk != null) {
				offSet = rtcpSdesChunk.encode(rawData, offSet);
			} else {
				break;
			}
		}

		/* Reduce 4 octest of header and length is in terms 32bits word */
		this.length = (offSet - startPosition - 4) / 4;

		rawData[startPosition + 2] = ((byte) ((this.length & 0xFF00) >> 8));
		rawData[startPosition + 3] = ((byte) (this.length & 0x00FF));

		return offSet;
	}

	public void addRtcpSdesChunk(RtcpSdesChunk rtcpSdesChunk) {
		if(this.count >= RtcpPacket.MAX_SOURCES) {
			throw new ArrayIndexOutOfBoundsException("Reached maximum number of chunks: "+ RtcpPacket.MAX_SOURCES);
		}
		this.sdesChunks.add(rtcpSdesChunk);
		this.count++;
	}

	public RtcpSdesChunk[] getSdesChunks() {
		RtcpSdesChunk[] chunks = new RtcpSdesChunk[this.sdesChunks.size()];
		return this.sdesChunks.toArray(chunks);
	}
	
	public String getCname() {
		for (RtcpSdesChunk chunk : this.sdesChunks) {
			String cname = chunk.getCname();
			if(cname != null && !cname.isEmpty()) {
				return cname;
			}
		}
		return "";
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("SDES:\n");
		builder.append("version= ").append(this.version).append(", ");
		builder.append("padding= ").append(this.padding).append(", ");
		builder.append("source count= ").append(this.count).append(", ");
		builder.append("packet types= ").append(this.packetType).append(", ");
		builder.append("length= ").append(this.length).append(", ");
		for (RtcpSdesChunk chunk : this.sdesChunks) {
			builder.append("\n").append(chunk.toString());
		}
		return builder.toString();
	}

}
