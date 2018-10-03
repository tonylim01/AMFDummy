/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RmqSender.java
 * @author Tony Lim
 *
 */

package media.platform.amf.core.rabbitmq.transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RmqSender extends RmqTransport {

    private static final Logger logger = LoggerFactory.getLogger(RmqSender.class);

    public RmqSender(String host, String userName, String password, String queueName) {
        super(host, userName, password, queueName);
    }

    public boolean send(byte[] msg, int size) {

        if (getChannel().isOpen() == false) {
            logger.error("RMQ channel is NOT opened");
            return false;
        }

        if ((size <= 0) || (msg == null) || (msg.length < size)) {
            logger.error("Send error: wrong param. size [{}] msg [{}]", size,
                    (msg != null) ? msg.length : 0);
            return false;
        }

        boolean result = false;

        try {
            byte[] data = new byte[size];
            System.arraycopy(msg, 0, data, 0, size);
            getChannel().basicPublish("", getQueueName(), null, data);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


    public boolean send(String msg) {

        return send(msg.getBytes(), msg.length());
    }

    public boolean isOpened() {
        return getChannel().isOpen();
    }
}
