package media.platform.amf;

import media.platform.amf.config.AmfConfig;
import media.platform.amf.config.PromptConfig;
import media.platform.amf.config.RedundantConfig;
import media.platform.amf.rtpcore.Process.NettyRTPServer;
import media.platform.amf.rtpcore.Process.NettyUDPServer;

public class AppInstance {

    private static AppInstance instance = null;
    private static final String AMF_CONFIG_FILE = "amf.conf";

    public static AppInstance getInstance() {
        if (instance == null) {
            instance = new AppInstance();
        }

        return instance;
    }

    private int instanceId = 0;
    private String configFile = null;
    private AmfConfig amfConfig = null;
    private PromptConfig promptConfig = null;
    //private RedundantConfig redundantConfig = null;

    public int getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(int instanceId) {
        this.instanceId = instanceId;
    }

    public AmfConfig getConfig() {
        return amfConfig;
    }

    private NettyRTPServer nettyRTPServer;

    public NettyRTPServer getNettyRTPServer() {
        return nettyRTPServer;
    }

    public void setNettyRTPServer(NettyRTPServer nettyRTPServer) {
        this.nettyRTPServer = nettyRTPServer;
    }

    private NettyUDPServer nettyUDPServer;

    public NettyUDPServer getNettyUDPServer() {
        return nettyUDPServer;
    }

    public void setNettyUDPServer(NettyUDPServer nettyUDPServer) {
        this.nettyUDPServer = nettyUDPServer;
    }

    public void setConfig(AmfConfig config) {
        this.amfConfig = config;
    }

    public String getConfigFile() {
        return (configFile != null) ? configFile : AMF_CONFIG_FILE;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    public void loadPromptConfig() {
        if (amfConfig != null && amfConfig.getPromptConfPath() != null) {
            promptConfig = new PromptConfig(amfConfig.getPromptConfPath());
        }
    }

    public PromptConfig getPromptConfig() {
        if (promptConfig == null) {
            loadPromptConfig();
        }
        return promptConfig;
    }
}
