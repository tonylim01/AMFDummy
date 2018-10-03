/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file UdpCallback.java
 * @author Tony Lim
 *
 */

package media.platform.amf.core.socket;

public interface UdpCallback {
    void onReceived(byte[] srcAddress, int srcPort, byte[] buf, int length);
}
