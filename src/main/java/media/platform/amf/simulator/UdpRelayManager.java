package media.platform.amf.simulator;

import media.platform.amf.AppInstance;
import media.platform.amf.config.AmfConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class UdpRelayManager {

    private static final Logger logger = LoggerFactory.getLogger(UdpRelayManager.class);

    private static final int DEFAULT_LOCAL_UDP_PORT_MIN = 20000;
    private static final int DEFAULT_LOCAL_UDP_PORT_MAX = 65535;

    private static UdpRelayManager udpRelayManager = null;

    public static UdpRelayManager getInstance() {
        if (udpRelayManager == null) {
            udpRelayManager = new UdpRelayManager();
        }
        return udpRelayManager;
    }

    private int localUdpPortMin;
    private int localUdpPortMax;
    private int currentUdpPort;
    private Map<String, UdpRelay> udpRelayMap;

    public UdpRelayManager() {
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

        synchronized (udpRelayManager) {
            result = currentUdpPort;
            currentUdpPort += 2;

            if (currentUdpPort > localUdpPortMax) {
                currentUdpPort = localUdpPortMin;
            }
        }

        return result;
    }

    public boolean openServer(String sessionId, int localPort) {
        UdpRelay udpRelay = getUdpRelay(sessionId);
        udpRelay.setLocalPort(localPort);

        return true;
    }

    public boolean openClient(String sessionId, String remoteIpAddress, int remotePort) {
        logger.debug("Open UDP client. remote [{}:{}]", remoteIpAddress, remotePort);

        UdpRelay udpRelay = getUdpRelay(sessionId);
        udpRelay.openUdpClient(remoteIpAddress, remotePort);

        return true;
    }

    public void close(String sessionId) {
        UdpRelay udpRelay = getUdpRelay(sessionId);
        udpRelay.closeUdpSocket();
    }

    private UdpRelay getUdpRelay(String sessionId) {
        UdpRelay udpRelay;
        if (udpRelayMap.containsKey(sessionId)) {
            udpRelay = udpRelayMap.get(sessionId);
        }
        else {
            udpRelay = new UdpRelay();
            udpRelayMap.put(sessionId, udpRelay);
        }

        return udpRelay;
    }
}
