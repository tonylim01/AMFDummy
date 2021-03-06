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
    private int priority;

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

    public void addFirstAttribute(int payloadId, String description) {
        SdpAttribute sdpAttribute = new SdpAttribute(payloadId, description);

        if (attributes == null) {
            attributes = new ArrayList<>();
            attributes.add(sdpAttribute);
        }
        else {
            attributes.add(0, sdpAttribute);
        }
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

    public SdpAttribute getAttributeByIndex(int index) {
        if (attributes == null) {
            return null;
        }

        if (index >= attributes.size()) {
            return null;
        }

        return attributes.get(index);
    }

    public SdpAttribute getAttribute(int payloadId) {
        if (attributes == null || payloadId == -1) {
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

    public SdpAttribute getAttributeByCodec(int codecId) {
        if (attributes == null) {
            return null;
        }

        SdpAttribute result = null;

        for (SdpAttribute attr: attributes) {
            String desc = attr.getDescription();
            if (desc != null && desc.contains("/")) {
                String codec = desc.substring(0, desc.indexOf('/')).trim();
                //String sampleRate = desc.substring(desc.indexOf('/') + 1).trim();

                if (codec != null && SdpCodec.getCodecId(codec) == codecId) {
                    result = attr;
                    break;
                }
            }
        }

        return result;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
