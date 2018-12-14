/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file NegoDoneReq.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.messages;

import com.google.gson.annotations.SerializedName;

public class NegoDoneReq {
    @SerializedName("in_out_flag")
    private int inOutFlag;
    private String sdp;
    private String deviceSdp;
    @SerializedName("peer_sdp")
    private String peerSdp;
    @SerializedName("peed_deviceSdp")
    private String peerDeviceSdp;

    public String getSdp() {
        return sdp;
    }

    public void setSdp(String sdp) {
        this.sdp = sdp;
    }

    public String getDeviceSdp() {
        return deviceSdp;
    }

    public void setDeviceSdp(String deviceSdp) {
        this.deviceSdp = deviceSdp;
    }

    public int getInOutFlag() {
        return inOutFlag;
    }

    public void setInOutFlag(int inOutFlag) {
        this.inOutFlag = inOutFlag;
    }

    public String getPeerSdp() {
        return peerSdp;
    }

    public void setPeerSdp(String peerSdp) {
        this.peerSdp = peerSdp;
    }

    public String getPeerDeviceSdp() {
        return peerDeviceSdp;
    }

    public void setPeerDeviceSdp(String peerDeviceSdp) {
        this.peerDeviceSdp = peerDeviceSdp;
    }
}
