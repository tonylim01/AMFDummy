/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file NegoDoneReq.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.messages;

public class NegoDoneReq {
    private String sdp;
    private String deviceSdp;

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
}
