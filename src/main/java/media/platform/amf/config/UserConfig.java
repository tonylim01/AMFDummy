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

import java.util.ArrayList;
import java.util.List;

public class UserConfig extends DefaultConfig {

    private static final Logger logger = LoggerFactory.getLogger(UserConfig.class);

    private List<String> mediaPriorities;
    private SdpConfig sdpConfig;

    public UserConfig(int instanceId, String configPath) {

        super(configPath);

        boolean result = load();
        logger.info("Load config ... [{}]", StringUtil.getOkFail(result));

        mediaPriorities = new ArrayList<>();
        sdpConfig = new SdpConfig();

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

    private void loadMediaConfig() {
        try {
            String mediaPriority = getStrValue("MEDIA", "MEDIA_PRIORITY", null);
            if (mediaPriority != null) {
                setMediaPriority(mediaPriority);
            }

            String localHost = getStrValue("MEDIA", "SDP_LOCAL_HOST", null);
            String localIp = getStrValue("MEDIA", "SDP_LOCAL_IP", null);

            sdpConfig.clearCodecAttribute();
            sdpConfig.setLocalHost(localHost);

            if (localIp == null || localIp.startsWith("xxx")) {
                sdpConfig.setLocalIpAddress(localIpAddress);
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

        mediaPriorities.clear();

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

//    private int rmqBufferCount;
    private String rmqHost;
    private String rmqLocal;
    private String rmqMcud;
    private String rmqAcswf;
    private String rmqUser, rmqPass;
    private String rmqAiifs[];  // Not used
    private String rmqAiifFmt;  // Not used

    private int sessionMaxSize;
    private int sessionTimeout;

//    private String heartbeat;
//    private boolean isTest;
//    private boolean relayMode;
    private String logPath;
    private int logTime;

    private RedundantConfig redundantConfig;

    private int localUdpPortMin;
    private int localUdpPortMax;
    private String localNetInterface;
    private String localIpAddress;

    private String localBasePath;
    private long audioEnergyLevel;      // Not used
    private long silenceEnergyLevel;    // Not used
    private long silenceDetectDuration; // Not used
    private long energyDetectDuration;  // Not used

    private String engineIp;
    private int engineLocalPort;
    private int engineRemotePort;

    private String promptConfPath;
//    private int instanceId;

    private String awfQueue, awfRmqHost, awfRmqUser, awfRmqPass;

    private String processName;


    @Override
    public String getStrValue(String session, String key, String defaultValue) {
        String value = super.getStrValue(session, key, defaultValue);

        logger.info("\tConfig key [{}] value [{}]", key, value);
        return value;
    }

    private void loadConfig(int instanceId) {

        String instanceSection = String.format("INSTANCE-%d", instanceId);

        loadCommonConfig();
        loadSessionConfig();
        loadRmqConfig(instanceSection);
        loadAwfConfig(instanceSection);
        loadRedundantConfig();
        loadInstanceConfig(instanceSection);
        loadMediaConfig();
        loadAudioConfig();
        loadOamConfig();
    }

    private void loadCommonConfig() {
        try {
            promptConfPath = getStrValue("COMMON", "PROMPT_CONF_PATH", null);
            logPath = getStrValue("COMMON", "LOG_PATH", null);
            logTime = getIntValue("COMMON", "LOG_TIME", 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadSessionConfig() {
        try {
            sessionMaxSize = getIntValue("SESSION", "SESSION_MAX_SIZE", 0);
            sessionTimeout = getIntValue("SESSION", "SESSION_TIMEOUT_SEC", 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadOamConfig() {
        try {
            processName = getStrValue("OAM", "PROMPT_CONF_PATH", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void loadRmqConfig(String instanceSection) {
        try {
//            rmqBufferCount = getIntValue("RMQ", "RMQ_BUFFER_COUNT", 0);
            rmqHost = getStrValue("RMQ", "RMQ_HOST", "localhost");
            rmqMcud = getStrValue("RMQ", "RMQ_MCUD", null);
            rmqAcswf = getStrValue("RMQ", "RMQ_ACSWF", null);
            rmqUser = getStrValue("RMQ", "RMQ_USER", null);
            rmqPass = getStrValue("RMQ", "RMQ_PASS", null);

            rmqLocal = getStrValue(instanceSection, "RMQ_LOCAL", "localhost");

//            String rmqAiif = getStrValue("RMQ", "RMQ_AIIF", null);
//            if (rmqAiif != null && rmqAiif.contains(",")) {
//                String[] aiifs = rmqAiif.split(",");
//                if (aiifs != null) {
//                    rmqAiifs = new String[aiifs.length];
//                    for (int i = 0; i < aiifs.length; i++) {
//                        rmqAiifs[i] = aiifs[i].trim();
//                    }
//                }
//            }
//
//            rmqAiifFmt = getStrValue("RMQ", "RMQ_AIIF_FMT", null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAwfConfig(String instanceSection) {
        try {
            awfQueue = getStrValue("AWF", "RMQ_AWF", null);
            awfRmqHost = getStrValue("AWF", "RMQ_HOST", "localhost");
            awfRmqUser = getStrValue("AWF", "RMQ_USER", null);
            awfRmqPass = getStrValue("AWF", "RMQ_PASS", null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void loadInstanceConfig(String instanceSection) {

        try {
            localUdpPortMin = getIntValue(instanceSection, "LOCAL_UDP_PORT_MIN", 0);
            localUdpPortMax = getIntValue(instanceSection, "LOCAL_UDP_PORT_MAX", 0);

            engineIp = getStrValue(instanceSection, "ENGINE_IP", null);
            engineLocalPort = getIntValue(instanceSection, "ENGINE_LOCAL_PORT", 0);
            engineRemotePort = getIntValue(instanceSection, "ENGINE_REMOTE_PORT", 0);

            localNetInterface = getStrValue("COMMON", "LOCAL_NET_INTERFACE", null);

            if (localNetInterface != null) {
                localIpAddress = NetUtil.getLocalIP(localNetInterface);
            }
            else {
                logger.error("Local IP not found for [{}]", localNetInterface);
            }

            localBasePath = getStrValue("COMMON", "LOCAL_BASE_PATH", null);

//            audioEnergyLevel = (long)getIntValue("MEDIA", "AUDIO_ENERGY_LEVEL", 0);
//            silenceEnergyLevel = (long)getIntValue("MEDIA", "SILENCE_ENERGY_LEVEL", 0);
//            silenceDetectDuration = (long)getIntValue("MEDIA", "SILENCE_DETECT_DURATION", 0);
//            energyDetectDuration = (long)getIntValue("MEDIA", "ENERGY_DETECT_DURATION", 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadRedundantConfig() {
        if (redundantConfig == null) {
            redundantConfig = new RedundantConfig();
        }

        try {
            boolean isRun = getBooleanValue("REDUNDANT", "RUN", false);
            int localPort = getIntValue("REDUNDANT", "LOCAL_PORT", RedundantConfig.DEFAULT_PORT);
            String remoteIp = getStrValue("REDUNDANT", "REMOTE_IP", null);
            int remotePort = getIntValue("REDUNDANT", "REMOTE_PORT", RedundantConfig.DEFAULT_PORT);
            String modeStr = getStrValue("REDUNDANT", "DEFAULT_MODE", null);

            int defaultMode;
            if (modeStr != null && modeStr.toUpperCase().equals("ACTIVE")) {
                defaultMode = RedundantConfig.MODE_ACTIVE;
            }
            else {
                defaultMode = RedundantConfig.MODE_STANDBY;
            }

            redundantConfig.setRun(isRun);
            redundantConfig.setLocalPort(localPort);
            redundantConfig.setRemoteIp(remoteIp);
            redundantConfig.setRemotePort(remotePort);
            redundantConfig.setDefaultMode(defaultMode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String getRmqHost() {
        return rmqHost;
    }

    public String getLocalName() {
        return rmqLocal;
    }

    public String getMcudName() {
        return rmqMcud;
    }

    public String getRmqAcswf() {
        return rmqAcswf;
    }

    public String getRmqUser() {
        return rmqUser;
    }

    public String getRmqPass() {
        return rmqPass;
    }

//    public SdpConfig getSdpConfig() {
//        return sdpConfig;
//    }

    public int getSessionMaxSize() {
        return sessionMaxSize;
    }

    public int getSessionTimeout() {
        return sessionTimeout;
    }

    public int getLocalUdpPortMin() {
        return localUdpPortMin;
    }

    public int getLocalUdpPortMax() {
        return localUdpPortMax;
    }

//    private void setMediaPriority(String priorityStr) {
//        if (priorityStr == null) {
//            return;
//        }
//
//        String[] priorities = priorityStr.split("\\s");
//        if (priorities != null) {
//            for (String priority: priorities) {
//                try {
//                    mediaPriorities.add(priority.trim());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

//    public List<String> getMediaPriorities() {
//        return mediaPriorities;
//    }

//    public String getMediaPriority(int index) {
//        if (index < 0 || index >= mediaPriorities.size()) {
//            return null;
//        }
//
//        return mediaPriorities.get(index);
//    }

    public String getLocalNetInterface() {
        return localNetInterface;
    }

    public String getLocalIpAddress() {
        return localIpAddress;
    }

    public String getLocalBasePath() {
        return localBasePath;
    }

    public String getRmqAiif(int index) {
        if (rmqAiifs == null || index < 0 || index >= rmqAiifs.length) {
            return null;
        }

        return rmqAiifs[index];
    }

    public String getRmqAiifFmt() {
        return this.rmqAiifFmt;
    }

    public long getAudioEnergyLevel() {
        return audioEnergyLevel;
    }

    public long getSilenceEnergyLevel() {
        return silenceEnergyLevel;
    }

    public long getSilenceDetectDuration() {
        return silenceDetectDuration;
    }

    public long getEnergyDetectDuration() {
        return energyDetectDuration;
    }

    public String getPromptConfPath() {
        return promptConfPath;
    }

    public String getLogPath() {
        return logPath;
    }

    public int getLogTime() {
        return logTime;
    }

//    public int getRmqBufferCount() {
//        return rmqBufferCount;
//    }

    public RedundantConfig getRedundantConfig() {
        return redundantConfig;
    }

    public String getEngineIp() {
        return engineIp;
    }

    public int getEngineLocalPort() {
        return engineLocalPort;
    }

    public int getEngineRemotePort() {
        return engineRemotePort;
    }

    private int audioSilenceDuration;
    private int audioDetectionTimeout;

    public int getAudioSilenceDuration() {
        return audioSilenceDuration;
    }



    public int getAudioDetectionTimeout() {
        return audioDetectionTimeout;
    }

    private void loadAudioConfig() {
        try {
            audioSilenceDuration = getIntValue("AUDIO", "SILENCE_DURATION", 0);
            audioDetectionTimeout = getIntValue("AUDIO", "DETECTION_TIMEOUT", 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getAwfRmqHost() {
        return awfRmqHost;
    }

    public String getAwfRmqUser() {
        return awfRmqUser;
    }

    public String getAwfRmqPass() {
        return awfRmqPass;
    }

    public String getAwfQueue() {
        return awfQueue;
    }

    public String getProcessName() {
        return processName;
    }
}
