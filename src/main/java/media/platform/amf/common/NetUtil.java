/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file NetUtil.java
 * @author Tony Lim
 *
 */

package media.platform.amf.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;

public class NetUtil {

    private static final Logger logger = LoggerFactory.getLogger(NetUtil.class);

    public static boolean ping(String ip, int timeout) {

        boolean result = false;
        try {
            InetAddress inet = InetAddress.getByName(ip);
            logger.debug("Sending ping to {}", inet);
            result = inet.isReachable(timeout);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static String getLocalIP(String networkName) {
        String ipAddress = null;
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

            for (NetworkInterface networkInterface : Collections.list(networkInterfaces)) {
                if (networkInterface.getDisplayName().equals(networkName)) {
                    logger.debug("Network name [{}]", networkInterface.getDisplayName());

                    Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                    for (InetAddress inetAddress: Collections.list(inetAddresses)) {
                        String hostAddress = inetAddress.getHostAddress();
                        if (inetAddress instanceof Inet4Address) {
                            logger.debug("Network address [{}]", hostAddress);
                                ipAddress = hostAddress;
                                break;
                        }
                    }

                    if (ipAddress != null) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ipAddress;
    }

    public static byte[] getLittleEndian4Bytes(int value) {
        byte[] buf = new byte[4];

        buf[3] = (byte)((value >>> 24) & 0xff);
        buf[2] = (byte)((value >>> 16) & 0xff);
        buf[1] = (byte)((value >>> 8) & 0xff);
        buf[0] = (byte)((value >>> 0) & 0xff);

        return buf;
    }

    public static int getBigEndian4BytesValue(byte[] bytes) {
        if (bytes.length < 4) {
            return -1;
        }

        return ((bytes[3] & 0xff) << 24) + ((bytes[2] & 0xff) << 16) + ((bytes[1] & 0xff) << 8) + (bytes[0] & 0xff);
    }

}
