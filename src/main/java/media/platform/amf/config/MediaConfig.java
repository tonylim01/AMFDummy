/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file AmfConfig.java
 * @author Tony Lim
 *
 */


package media.platform.amf.config;

import media.platform.amf.AppInstance;
import media.platform.amf.common.NetUtil;
import media.platform.amf.common.StringUtil;
import media.platform.amf.core.config.ConfigChangedListener;
import media.platform.amf.core.config.DefaultConfig;
import media.platform.amf.engine.messages.ParAddReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MediaConfig extends DefaultConfig {

    private static final Logger logger = LoggerFactory.getLogger(MediaConfig.class);

    private List<String> mediaPriorities;
    private SdpConfig sdpConfig;

    public MediaConfig(String configPath) {

        super(configPath);

        boolean result = load();
        logger.info("Load config ... [{}]", StringUtil.getOkFail(result));

        mediaPriorities = new ArrayList<>();
        sdpConfig = new SdpConfig();

        setConfigChangedListener(new ConfigChangedListener() {
            @Override
            public void configChanged(boolean changed) {
                logger.warn("Config changed");
                loadConfig();
            }
        });

        if (result == true) {
            loadConfig();
        }
    }

    @Override
    public String getStrValue(String session, String key, String defaultValue) {
        String value = super.getStrValue(session, key, defaultValue);

        logger.info("\tConfig key [{}] value [{}]", key, value);
        return value;
    }

    private void loadConfig() {
        try {
            String mediaPriority = getStrValue("MEDIA", "MEDIA_PRIORITY", null);
            if (mediaPriority != null) {
                setMediaPriority(mediaPriority);
            }

            String localHost = getStrValue("MEDIA", "SDP_LOCAL_HOST", null);
            String localIp = getStrValue("MEDIA", "SDP_LOCAL_IP", null);

            sdpConfig.setLocalHost(localHost);

            if (localIp == null || localIp.startsWith("xxx")) {
                sdpConfig.setLocalIpAddress(AppInstance.getInstance().getConfig().getLocalIpAddress());
            }
            else {
                sdpConfig.setLocalIpAddress(localIp);
            }

            for (String codec: mediaPriorities) {

                if (codec == null) {
                    continue;
                }

                String codecSession = String.format("SDP-ATTR-%s", codec);

                for (int i = 0; ; i++) {
                    String key = String.format("SDP_LOCAL_ATTR_%d", i);
                    String attr = getStrValue(codecSession, key, null);
                    logger.debug("SDP [{}] attr config: key [{}] attr [{}]", codec, key, attr);
                    if (attr == null) {
                        break;
                    }
                    sdpConfig.addCodecAttribute(codec, attr);
                }
            }

            for (int i = 0; ; i++) {
                String key = String.format("SDP_LOCAL_ATTR_%d", i);
                String attr = getStrValue("SDP-ATTR", key, null);
                logger.debug("SDP config: key [{}] attr [{}]", key, attr);
                if (attr == null) {
                    break;
                }
                sdpConfig.addAttribute(attr);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SdpConfig getSdpConfig() {
        return sdpConfig;
    }

    private void setMediaPriority(String priorityStr) {
        if (priorityStr == null) {
            return;
        }

        String[] priorities = priorityStr.split("\\s");
        if (priorities != null) {
            for (String priority: priorities) {
                try {
                    mediaPriorities.add(priority.trim());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<String> getMediaPriorities() {
        return mediaPriorities;
    }

    public String getMediaPriority(int index) {
        if (index < 0 || index >= mediaPriorities.size()) {
            return null;
        }

        return mediaPriorities.get(index);
    }
}
