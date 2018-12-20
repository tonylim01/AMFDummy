/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file SdpConfig.java
 * @author Tony Lim
 *
 */

package media.platform.amf.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SdpConfig {
    private String localHost;
    private String localIpAddress;
    private List<String> attributes = null;
    private Map<String, List<String>> codecAttributes = null;

    public String getLocalHost() {
        return localHost;
    }

    public void setLocalHost(String localHost) {
        this.localHost = localHost;
    }

    public String getLocalIpAddress() {
        return localIpAddress;
    }

    public void setLocalIpAddress(String localIpAddress) {
        this.localIpAddress = localIpAddress;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public void addAttribute(String attribute) {
        if (attributes == null) {
            attributes = new ArrayList<>();
        }
        attributes.add(attribute);
    }

    public List<String> getCodecAttribute(String codec) {
        if (codec == null) {
            return null;
        }

        return ((codecAttributes != null && codecAttributes.containsKey(codec)) ? codecAttributes.get(codec) : null);
    }

    public void addCodecAttribute(String codec, String attribute) {
        if (codecAttributes == null) {
            codecAttributes = new HashMap<>();
        }

        List<String> attrs;
        if (codecAttributes.containsKey(codec) == false) {
            attrs = new ArrayList<>();
            codecAttributes.put(codec, attrs);
        }
        else {
            attrs = codecAttributes.get(codec);
        }

        attrs.add(attribute);
    }
}
