/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file InboundGetAnswerRes.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.messages;

public class InboundGetAnswerRes {
    private String sdp;

    public String getSdp() {
        return sdp;
    }

    public void setSdp(String sdp) {
        this.sdp = sdp;
    }
}
