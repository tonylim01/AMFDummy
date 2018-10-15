/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file SdpParser.java
 * @author Tony Lim
 *
 */

package media.platform.amf.core.sdp;

import gov.nist.javax.sdp.SessionDescriptionImpl;
import gov.nist.javax.sdp.parser.SDPAnnounceParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import media.platform.amf.AppInstance;

import javax.sdp.*;
import java.util.List;
import java.util.Vector;

public class SdpParser {

    private static final Logger logger = LoggerFactory.getLogger(SdpParser.class);

    private static final String TELEPHONE_EVENT = "telephone-event";

    public static SdpInfo selectAttribute(SdpInfo sdpInfo) {
        if (sdpInfo == null || sdpInfo.getAttributes() == null) {
            return null;
        }

        List<Integer> mediaPriorities = AppInstance.getInstance().getConfig().getMediaPriorities();

        if (mediaPriorities != null && mediaPriorities.size() > 0) {
            for (Integer priority : mediaPriorities) {
                SdpAttribute attr = sdpInfo.getAttribute(priority);

                if (attr != null) {
                    String desc = attr.getDescription();
                    if (desc != null && desc.contains("/")) {
                        String codec = desc.substring(0, desc.indexOf('/')).trim();
                        String sampleRate = desc.substring(desc.indexOf('/' + 1)).trim();

                    }
                    sdpInfo.setPayloadId(priority);
                    break;
                }
            }
        }

        return sdpInfo;
    }
    /**
     * Simple static parser to call the parse() of SdpParser
     * @param sdp
     * @return SdpInfo
     */
    public static SdpInfo parseSdp(String sdp) {
        if (sdp == null) {
            return null;
        }

        SdpInfo sdpInfo = null;
        SdpParser sdpParser = new SdpParser();
        try {
            sdpInfo = sdpParser.parse(sdp);

            if (sdpInfo.getAttributes() != null) {
                List<Integer> mediaPriorities = AppInstance.getInstance().getConfig().getMediaPriorities();

                if (mediaPriorities != null && mediaPriorities.size() > 0) {
                    for (Integer priority : mediaPriorities) {
                        SdpAttribute attr = sdpInfo.getAttribute(priority);

                        if (attr != null) {
                            String desc = attr.getDescription();
                            if (desc != null && desc.contains("/")) {
                                String codec = desc.substring(0, desc.indexOf('/')).trim();
                                String sampleRate = desc.substring(desc.indexOf('/') + 1).trim();

                                logger.debug("priority [{}] codec [{}] samplerate [{}]", priority, codec, sampleRate);
                                sdpInfo.setCodecStr(codec);
                                if (sampleRate != null) {
                                    sdpInfo.setSampleRate(Integer.valueOf(sampleRate));
                                }
                            }

                            sdpInfo.setPayloadId(priority);
                            break;
                        }
                    }
                }
                else {
                    logger.warn("No media priority defined");
                }

                // Gets telephone-event
                for (SdpAttribute attr: sdpInfo.getAttributes()) {
                    if (attr != null && attr.getDescription() != null &&
                            attr.getDescription().startsWith(TELEPHONE_EVENT)) {
                        if (attr.getPayloadId() > 0) {
                            sdpInfo.setPayload2833(attr.getPayloadId());
                            logger.debug("2833 payload [{}]", sdpInfo.getPayload2833());
                            break;
                        }
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sdpInfo;
    }

    /**
     * Parses a SDP body and returns SdpInfo
     * @param msg
     * @return
     * @throws Exception
     */
    public SdpInfo parse(String msg) throws Exception {
        SDPAnnounceParser parser = new SDPAnnounceParser(msg);
        SessionDescriptionImpl sdp = parser.parse();

        Vector mdVector = sdp.getMediaDescriptions(false);
        if (mdVector == null) {
            return null;
        }

        MediaDescription md = (MediaDescription)mdVector.get(0);

        Connection connection = sdp.getConnection();
        if (connection == null) {
            connection = md.getConnection();
        }

        if (connection == null) {
            //
            // TODO
            //

            return null;
        }

        Media media = md.getMedia();
        if (media == null) {
            return null;
        }

        SdpInfo sdpInfo = new SdpInfo();
        sdpInfo.setRemoteIp(connection.getAddress());
        sdpInfo.setRemotePort(media.getMediaPort());

        Vector<Integer> mediaFormats = new Vector<>();

        for (Object obj: media.getMediaFormats(false)) {

            Integer payloadId = Integer.valueOf((String)obj);

            mediaFormats.add(payloadId);
            sdpInfo.addAttribute(payloadId, null);
        }

        for (Object obj: md.getAttributes(false)) {

            Attribute attr = (Attribute)obj;
            if (attr.getName() == null) {
                continue;
            }

            if (!attr.hasValue()) {
                sdpInfo.addAttribute(attr.getName(), null);
                continue;
            }

            String value = attr.getValue();

            if (attr.getName().equals("rtpmap")) {
                int space = value.indexOf(' ');
                if (space <= 0) {
                    continue;
                }

                Integer payloadId = Integer.valueOf(value.substring(0, space));
                String description = value.substring(space + 1);

                if (mediaFormats.contains(payloadId)) {
                    mediaFormats.remove(payloadId);
                }

                sdpInfo.updateAttribute(payloadId, description);
            }
            else {
                sdpInfo.addAttribute(attr.getName(), value);
            }
        }

        // sdp connection addr 1.255.239.173 type IP4 net IN
        logger.debug("sdp connection addr {} type {} net {}", sdpInfo.getRemoteIp(), connection.getAddressType(), connection.getNetworkType());

        // sdp media port 35664 type audio
        logger.debug("sdp media port {} type {}", sdpInfo.getRemotePort(), media.getMediaType());

        // sdp media [0, 8, 4, 18, 101]
        logger.debug("sdp media {}", media.getMediaFormats(false).toString());

        for (SdpAttribute attr: sdpInfo.getAttributes()) {
            // sdp attr name [rtpmap] payload [0] value [PCMU/8000]
            logger.debug("sdp attr name [{}] payload [{}] value [{}]", attr.getName(), attr.getPayloadId(), attr.getDescription());
        }

        return sdpInfo;
    }
}
