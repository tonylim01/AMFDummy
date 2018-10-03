/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RmqReceiver.java
 * @author Tony Lim
 *
 */

package media.platform.amf.core.rabbitmq.transport;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;

public class RmqReceiver extends RmqTransport {

    private RmqCallback callback = null;

    public RmqReceiver(String host, String userName, String password, String queueName) {
        super(host, userName, password, queueName);
    }

    public void setCallback(RmqCallback callback) {
        this.callback = callback;
    }

    private Consumer consumer = new DefaultConsumer(getChannel()) {
        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
            String msg = new String(body, "UTF-8");
            if (callback != null) {
                callback.onReceived(msg);
            }
        }
    };

    public boolean start() {

        if (getChannel().isOpen() == false) {
            return false;
        }

        boolean result = false;

        try {
            getChannel().basicConsume(getQueueName(), true, consumer);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

}
