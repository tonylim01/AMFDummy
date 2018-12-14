package media.platform.amf.simulator;

import media.platform.amf.core.sdp.SdpInfo;
import media.platform.amf.AppInstance;
import media.platform.amf.config.AmfConfig;
import media.platform.amf.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import media.platform.amf.session.SessionInfo;

import java.util.HashMap;
import java.util.Map;

public class BiUdpRelayManager {

    private static final Logger logger = LoggerFactory.getLogger(BiUdpRelayManager.class);

    private static final int DEFAULT_LOCAL_UDP_PORT_MIN = 20000;
    private static final int DEFAULT_LOCAL_UDP_PORT_MAX = 65535;

    private volatile  static BiUdpRelayManager udpRelayManager = null;

    public static BiUdpRelayManager getInstance() {
        if (udpRelayManager == null) {
            udpRelayManager = new BiUdpRelayManager();
        }
        return udpRelayManager;
    }

    private int localUdpPortMin;
    private int localUdpPortMax;
    private int currentUdpPort;
    private Map<String, BiUdpRelay> udpRelayMap;

    public BiUdpRelayManager() {
        AmfConfig config = AppInstance.getInstance().getConfig();
        localUdpPortMin = config.getLocalUdpPortMin();
        localUdpPortMax = config.getLocalUdpPortMax();

        if (localUdpPortMin == 0) {
            localUdpPortMin = DEFAULT_LOCAL_UDP_PORT_MIN;
        }
        if (localUdpPortMax == 0) {
            localUdpPortMax = DEFAULT_LOCAL_UDP_PORT_MAX;
        }
        if (localUdpPortMin > localUdpPortMax) {
            localUdpPortMax = DEFAULT_LOCAL_UDP_PORT_MAX;
        }

        currentUdpPort = localUdpPortMin;
        udpRelayMap = new HashMap<>();
    }

    /**
     * Returns the next available UDP port
     * @return
     */
    public int getNextLocalPort() {
        int result;

        synchronized (this) {
            result = currentUdpPort;

            currentUdpPort += 2;

            if (currentUdpPort > localUdpPortMax) {
                currentUdpPort = localUdpPortMin;
            }
        }

        return result;
    }

    public boolean openSrcServer(String sessionId, int localPort) {
        BiUdpRelay udpRelay = getUdpRelay(sessionId);
        udpRelay.setSrcLocalPort(localPort);

        return true;
    }

    public boolean openDstServer(String sessionId, int localPort) {
        BiUdpRelay udpRelay = getUdpRelay(sessionId);
        udpRelay.setDstLocalPort(localPort);

        return true;
    }

    public boolean openSrcClient(String sessionId, String remoteIpAddress, int remotePort) {
        logger.debug("[{}] Open src UDP client. remote [{}:{}]", sessionId,
                remoteIpAddress, remotePort);

        BiUdpRelay udpRelay = getUdpRelay(sessionId);
        udpRelay.openSrcUdpClient(remoteIpAddress, remotePort);

        return true;
    }

    public boolean openDstClient(String sessionId, String remoteIpAddress, int remotePort) {
        logger.debug("[{}] Open dst UDP client. remote [{}:{}]", sessionId,
                remoteIpAddress, remotePort);

        BiUdpRelay udpRelay = getUdpRelay(sessionId);
        udpRelay.openDstUdpClient(remoteIpAddress, remotePort);

        return true;
    }

    public boolean openDstDupQueue(String sessionId, String queueName) {
        logger.debug("[{}] Open dst DUP queue. name [{}]", sessionId, queueName);


        SessionInfo sessionInfo = SessionManager.getInstance().getSession( sessionId);

        if (sessionInfo == null) {
            return false;
        }

        SdpInfo sdpInfo = sessionInfo.getSdpInfo();

        if (sdpInfo == null) {
            return false;
        }

        BiUdpRelay udpRelay = getUdpRelay(sessionId);
        udpRelay.setDupUdpQueue(sdpInfo.getCodecStr(), queueName);

        return true;
    }

    public void close(String sessionId) {
        if (udpRelayMap.containsKey(sessionId)) {
            logger.debug("[{}] close. session found", sessionId);
            BiUdpRelay udpRelay = udpRelayMap.get(sessionId);
            if (udpRelay != null) {
                udpRelay.closeUdpSocket();
            }
        }
        else {
            logger.debug("[{}] close. session not found", sessionId);
        }
    }

    private BiUdpRelay getUdpRelay(String sessionId) {
        BiUdpRelay udpRelay;
        if (udpRelayMap.containsKey(sessionId)) {
            udpRelay = udpRelayMap.get(sessionId);
        }
        else {
            udpRelay = new BiUdpRelay(sessionId);
            udpRelayMap.put(sessionId, udpRelay);
        }

        return udpRelay;
    }

}
