/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file SdpInfo.java
 * @author Tony Lim
 *
 */

package media.platform.amf.core.sdp;

import java.util.ArrayList;
import java.util.List;

public class SdpInfo {

    private String remoteIp;
    public int remotePort;

    private int payloadId;
    private String codecStr;
    private int sampleRate;

    private int payload2833;

    List<SdpAttribute> attributes = null;

    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    public int getPayloadId() {
        return payloadId;
    }

    public void setPayloadId(int payloadId) {
        this.payloadId = payloadId;
    }

    public String getCodecStr() {
        return codecStr;
    }

    public void setCodecStr(String codecStr) {
        this.codecStr = codecStr;
    }

    public int getPayload2833() {
        return payload2833;
    }

    public void setPayload2833(int payload2833) {
        this.payload2833 = payload2833;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public List<SdpAttribute> getAttributes() {
        return attributes;
    }

    public void addAttribute(int payloadId, String description) {
        if (attributes == null) {
            attributes = new ArrayList<>();
        }
        attributes.add(new SdpAttribute(payloadId, description));
    }

    public void addAttribute(String name, String description) {
        if (attributes == null) {
            attributes = new ArrayList<>();
        }
        attributes.add(new SdpAttribute(name, description));
    }

    public boolean updateAttribute(int payloadId, String description) {
        SdpAttribute attr = getAttribute(payloadId);
        if (attr == null) {
            return false;
        }

        attr.setDescription(description);
        return true;
    }

    public boolean findAttribute(int payloadId) {
        return (getAttribute(payloadId) != null);
    }

    public SdpAttribute getAttribute(int payloadId) {
        if (attributes == null) {
            return null;
        }

        SdpAttribute result = null;

        for (SdpAttribute attr: attributes) {
            if (attr.getPayloadId() == payloadId) {
                result = attr;
                break;
            }
        }

        return result;
    }

}
