/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file SdpAttribute.java
 * @author Tony Lim
 *
 */


package media.platform.amf.core.sdp;

public class SdpAttribute {

    public static final int PAYLOADID_NONE = -1;

    public static final String NAME_RTPMAP = "rtpmap";
    public static final String NAME_FMTP = "fmtp";
    public static final String DESC_TELEPHONY_EVENT = "telephone-event";
    public static final String NAME_SENDRECV = "sendrecv";

    private String name;
    private int payloadId;
    private String description;

    public SdpAttribute() {
        this.payloadId = PAYLOADID_NONE;
    }

    public SdpAttribute(int payloadId, String description) {
        this.payloadId = payloadId;
        this.description = description;

        if (payloadId != PAYLOADID_NONE) {
            name = NAME_RTPMAP;
        }
    }

    public SdpAttribute(String name, String description) {
        this.name = name;
        this.payloadId = PAYLOADID_NONE;
        this.description = description;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPayloadId() {
        return payloadId;
    }

    public void setPayloadId(int payloadId) {
        this.payloadId = payloadId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
