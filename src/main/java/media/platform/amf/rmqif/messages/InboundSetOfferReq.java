/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file InboundSetOfferReq.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.messages;

import com.google.gson.annotations.SerializedName;

public class InboundSetOfferReq {

    @SerializedName("from_no")
    private String fromNo;
    @SerializedName("to_no")
    private String toNo;
    @SerializedName("conference_id")
    private String conferenceId;
    @SerializedName("sdp")
    private String sdp;
    // 0 - inbound, 1 - outbound, 2 - inbound_only(not used)
    @SerializedName("in_out_flag")
    private int outbound;

    public String getFromNo() {
        return fromNo;
    }

    public void setFromNo(String fromNo) {
        this.fromNo = fromNo;
    }

    public String getToNo() {
        return toNo;
    }

    public void setToNo(String toNo) {
        this.toNo = toNo;
    }

    public String getConferenceId() {
        return conferenceId;
    }

    public void setConferenceId(String conferenceId) {
        this.conferenceId = conferenceId;
    }

    public String getSdp() {
        return sdp;
    }

    public void setSdp(String sdp) {
        this.sdp = sdp;
    }

    public int getOutbound() {
        return outbound;
    }

    public void setOutbound(int outbound) {
        this.outbound = outbound;
    }
}
