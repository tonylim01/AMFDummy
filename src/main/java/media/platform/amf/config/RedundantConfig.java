package media.platform.amf.config;

import media.platform.amf.common.StringUtil;
import media.platform.amf.core.config.DefaultConfig;
import media.platform.amf.core.socket.packets.Vocoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class RedundantConfig {

    private static final Logger logger = LoggerFactory.getLogger(RedundantConfig.class);

    public static final int DEFAULT_PORT = 4090;

    public static final int MODE_ACTIVE = 1;
    public static final int MODE_STANDBY = 2;

    private boolean isRun;
    private int localPort;
    private String remoteIp;
    private int remotePort;
    private int defaultMode;

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

    public boolean isActive() {
        return (isRun & (defaultMode == MODE_ACTIVE));
    }

    public void setRun(boolean run) {
        isRun = run;
    }

    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    public void setDefaultMode(int defaultMode) {
        this.defaultMode = defaultMode;
    }
}
