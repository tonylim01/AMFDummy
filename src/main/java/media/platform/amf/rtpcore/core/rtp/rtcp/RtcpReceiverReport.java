package media.platform.amf.rtpcore.core.rtp.rtcp;

public class RtcpReceiverReport extends RtcpReport {
	
	private static final boolean IS_SENDER = false;

	public RtcpReceiverReport() {
		super();
	}

	public RtcpReceiverReport(boolean padding, long ssrc) {
		super( padding, ssrc, RTCP_RR );
	}

	@Override
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

		int tmpCount = 0;
		while ((offSet - tmp) < this.length) {
			RtcpReportBlock rtcpReceptionReportItem = new RtcpReportBlock();
			offSet = rtcpReceptionReportItem.decode(rawData, offSet);
			this.reportBlocks.add(rtcpReceptionReportItem);
			tmpCount++;
		}

		return offSet;
	}

	@Override
	public int encode(byte[] rawData, int offSet) {
		int startPosition = offSet;

		offSet = super.encode(rawData, offSet);

		rawData[offSet++] = ((byte) ((this.ssrc & 0xFF000000) >> 24));
		rawData[offSet++] = ((byte) ((this.ssrc & 0x00FF0000) >> 16));
		rawData[offSet++] = ((byte) ((this.ssrc & 0x0000FF00) >> 8));
		rawData[offSet++] = ((byte) ((this.ssrc & 0x000000FF)));

		for (RtcpReportBlock report : this.reportBlocks) {
			if (report != null) {
				offSet = report.encode(rawData, offSet);
			} else {
				break;
			}
		}

		/* Reduce 4 octets of header and length is in terms 32bits word */
		this.length = (offSet - startPosition - 4) / 4;

		rawData[startPosition + 2] = ((byte) ((this.length & 0xFF00) >> 8));
		rawData[startPosition + 3] = ((byte) (this.length & 0x00FF));

		return offSet;
	}

	@Override
	public boolean isSender() {
		return IS_SENDER;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("RECEIVER REPORT: \n");
		builder.append("version=").append(this.version).append(", ");
		builder.append("padding=").append(this.padding).append(", ");
		builder.append("packet types=").append(this.packetType).append(", ");
		builder.append("length=").append(this.length).append(", ");
		builder.append("ssrc=").append(this.ssrc).append("\n");
		for (RtcpReportBlock rr : this.reportBlocks) {
			builder.append("\n").append(rr.toString());
		}
		return builder.toString();
	}
	
}
