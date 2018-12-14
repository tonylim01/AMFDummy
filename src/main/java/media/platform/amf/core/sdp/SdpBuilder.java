/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file SdpBuilder.java
 * @author Tony Lim
 *
 */

package media.platform.amf.core.sdp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class  SdpBuilder {

    private static final Logger logger = LoggerFactory.getLogger(SdpBuilder.class);

    private static final String CRLF = "\r\n";

    String host = null;
    String sessionName = null;
    String localIpAddress = null;
    int localPort = 0;
    List<SdpAttribute> audioAttrs = null;

    public SdpBuilder() {
        audioAttrs = new ArrayList<>();
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setLocalIpAddress(String localIpAddress) {
        this.localIpAddress = localIpAddress;
    }

    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public void addRtpAttribute(int payload, String description) {
        audioAttrs.add(new SdpAttribute(payload, description));
    }

    public void addGeneralAttribute(String description) {
        audioAttrs.add(new SdpAttribute(SdpAttribute.PAYLOADID_NONE, description));
    }
    public String build() {
        StringBuilder sb = new StringBuilder(1024);
        sb.append("v=0\r\n");
        // Origin
        if (host != null) {
            sb.append("o=- ");
            sb.append(System.currentTimeMillis());
            sb.append(" 0 IN IP4 ");
            sb.append(host);
            sb.append(CRLF);
        }
        // Session
        if (sessionName != null) {
            sb.append("s=");
            sb.append(sessionName);
            sb.append(CRLF);
        }
        sb.append("t=0 0\r\n");
        // Media
        sb.append("m=audio ");
        sb.append(localPort);
        sb.append(" RTP/AVP");

        for (SdpAttribute attr: audioAttrs) {
            if (attr.getPayloadId() != SdpAttribute.PAYLOADID_NONE) {
                sb.append(" ");
                sb.append(attr.getPayloadId());
            }
        }
        sb.append(CRLF);

        // Connection
        sb.append("c=IN IP4 ");
        sb.append(localIpAddress);
        sb.append(CRLF);

        // Attributes
        for (SdpAttribute attr: audioAttrs) {
            if (attr.getDescription() != null) {
                sb.append("a=");
                if (attr.getPayloadId() != SdpAttribute.PAYLOADID_NONE) {
                    sb.append("rtpmap:");
                    sb.append(attr.getPayloadId());
                    sb.append(" ");
                }
                sb.append(attr.getDescription());
                sb.append(CRLF);
            }
        }

        return sb.toString();
    }
}
