/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file AmfConfig.java
 * @author Tony Lim
 *
 */


package media.platform.amf.config;

import media.platform.amf.common.NetUtil;
import media.platform.amf.common.StringUtil;
import media.platform.amf.core.config.ConfigChangedListener;
import media.platform.amf.core.config.DefaultConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmfConfig extends DefaultConfig {

    private static final Logger logger = LoggerFactory.getLogger(AmfConfig.class);

    private static final int DEFAULT_TIMER_T2 = 500;
    private static final int DEFAULT_TIMER_T4 = 2000;
    private static final int DEFAULT_TIMER_MEDIA_INACTIVITY = 10000;

    private int amfId;

    private boolean heartbeat;
    private boolean isTest;
    private boolean relayMode;

    private int rmqBufferCount;

    private String mediaConfPath;

    public AmfConfig(int instanceId, String configPath) {

        super(configPath);

        boolean result = load();
        logger.info("Load config ... [{}]", StringUtil.getOkFail(result));

        setConfigChangedListener(new ConfigChangedListener() {
            @Override
            public void configChanged(boolean changed) {
                logger.warn("Config changed");
                loadConfig(instanceId);
            }
        });

        if (result == true) {
            loadConfig(instanceId);
        }
    }

    @Override
    public String getStrValue(String session, String key, String defaultValue) {
        String value = super.getStrValue(session, key, defaultValue);

        logger.info("\tConfig key [{}] value [{}]", key, value);
        return value;
    }

    private void loadConfig(int instanceId) {

        String instanceSection = String.format("INSTANCE-%d", instanceId);

        loadCommonConfig();
        loadRmqConfig(instanceSection);
        loadTimerConfig();
    }

    private void loadCommonConfig() {
        try {
            mediaConfPath = getStrValue("COMMON", "MEDIA_CONF_PATH", null);
            amfId = getIntValue("COMMON", "AMF_ID", 0);
            heartbeat = getBooleanValue("COMMON", "HEARTBEAT", false);
            isTest = getBooleanValue("COMMON", "TEST", true);
            relayMode = getBooleanValue("COMMON", "RELAY_MODE", true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadRmqConfig(String instanceSection) {
        try {
            rmqBufferCount = getIntValue("RMQ", "RMQ_BUFFER_COUNT", 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTimerConfig() {
        try {
            timerSipT2 = getIntValue("TIMER", "TIMER_SIP_T2", DEFAULT_TIMER_T2);
            timerSipT4 = getIntValue("TIMER", "TIMER_SIP_T4", DEFAULT_TIMER_T4);
            timerMediaNoActivity = getIntValue("TIMER", "TIMER_MEDIA_NOACTIVITY", DEFAULT_TIMER_MEDIA_INACTIVITY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getAmfId() {
        return amfId;
    }

    public boolean getHeartbeat() {
        return heartbeat;
    }

    public boolean isTest() {
        return isTest;
    }

    public int getRmqBufferCount() {
        return rmqBufferCount;
    }

    public boolean isRelayMode() {
        return relayMode;
    }

    public String getMediaConfPath() {
        return mediaConfPath;
    }

    private int timerSipT4;
    private int timerSipT2;
    private int timerMediaNoActivity;

    public int getTimerSipT4() {
        return timerSipT4;
    }

    public int getTimerSipT2() {
        return timerSipT2;
    }

    public int getTimerMediaNoActivity() {
        return timerMediaNoActivity;
    }
}
