package media.platform.amf.config;

import media.platform.amf.common.StringUtil;
import media.platform.amf.core.config.DefaultConfig;
import media.platform.amf.core.socket.packets.Vocoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class RedundantConfig extends DefaultConfig {

    private static final Logger logger = LoggerFactory.getLogger(RedundantConfig.class);

    private static final int DEFAULT_PORT = 4090;

    private static final int MODE_ACTIVE = 1;
    private static final int MODE_STANDBY = 2;

    private boolean isRun;
    private int localPort;
    private String remoteIp;
    private int remotePort;
    private int defaultMode;

    public RedundantConfig(String configPath) {

        super(configPath);

        boolean result = load();
        logger.info("Load config [{}] ... [{}]", configPath, StringUtil.getOkFail(result));

        if (result == true) {
            loadConfig();
        }
    }

    private void loadConfig() {
        try {
            isRun = getBooleanValue("REDUNDANT", "RUN", false);
            localPort = getIntValue("REDUNDANT", "LOCAL_PORT", DEFAULT_PORT);
            remoteIp = getStrValue("REDUNDANT", "REMOTE_IP", null);
            remotePort = getIntValue("REDUNDANT", "REMOTE_PORT", DEFAULT_PORT);
            String modeStr = getStrValue("REDUNDANT", "DEFAULT_MODE", null);

            if (modeStr != null && modeStr.toUpperCase().equals("ACTIVE")) {
                defaultMode = MODE_ACTIVE;
            }
            else {
                defaultMode = MODE_STANDBY;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getLocalPort() {
        return localPort;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public int getDefaultMode() {
        return defaultMode;
    }

    public boolean isRun() {
        return isRun;
    }
}
