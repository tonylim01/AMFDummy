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
        setDescription(description);

        if (payloadId != PAYLOADID_NONE) {
            name = NAME_RTPMAP;
        }
    }

    public SdpAttribute(String name, String description) {
        this.name = name;
        this.payloadId = PAYLOADID_NONE;
        setDescription(description);
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

    private String codec;
    private int sampleRate;

    public void setDescription(String description) {
        this.description = description;

        if (description != null && description.contains("/")) {
            codec = description.substring(0, description.indexOf('/')).trim();
            String sampleRateStr = description.substring(description.indexOf('/') + 1).trim();

            if (sampleRateStr != null) {
                if (sampleRateStr.contains("/")) {
                    sampleRate = Integer.parseInt(sampleRateStr.substring(0, sampleRateStr.indexOf('/')).trim());
                }
                else {
                    sampleRate = Integer.parseInt(sampleRateStr);
                }
            }
        }
    }

    public String getCodec() {
        return codec;
    }

    public int getSampleRate() {
        return sampleRate;
    }
}
