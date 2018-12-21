/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file InboundSetOfferRes.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.messages;

import com.google.gson.annotations.SerializedName;

public class InboundSetOfferRes {
    private String sdp;
    @SerializedName("in_out_flag")
    private int outbound;

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
