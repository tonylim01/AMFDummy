/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RmqCallback.java
 * @author Tony Lim
 *
 */

package media.platform.amf.core.rabbitmq.transport;

public interface RmqCallback {
    void onReceived(String msg);
}
