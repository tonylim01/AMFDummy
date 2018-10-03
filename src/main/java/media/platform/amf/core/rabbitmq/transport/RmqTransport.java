/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RmqTransport.java
 * @author Tony Lim
 *
 */

package media.platform.amf.core.rabbitmq.transport;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RmqTransport {

    private final String host;
    private final String queueName;
    private final String userName;
    private final String password;

    private Connection connection;
    private Channel channel;

    public RmqTransport(String host, String userName, String password) {
        this.host = host;
        this.queueName = null;
        this.userName = userName;
        this.password = password;
    }

    public RmqTransport(String host, String userName, String password, String queueName) {
        this.host = host;
        this.queueName = queueName;
        this.userName = userName;
        this.password = password;
    }

    protected Channel getChannel() {
        return channel;
    }

    protected String getQueueName() {
        return queueName;
    }

    public boolean connectServer() {

        if (makeConnection() == false) {
            return false;
        }

        if (makeChannel(true) == false) {
            closeConnection();
            return false;
        }

        return true;
    }

    public boolean connectClient() {

        if (makeConnection() == false) {
            return false;
        }

        if (makeChannel(false) == false) {
            closeConnection();
            return false;
        }

        return true;
    }

    public void close() {
        closeChannel();
        closeConnection();
    }

    private boolean makeConnection() {

        boolean result = false;

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setUsername(userName);
        factory.setPassword(password);
        factory.setRequestedHeartbeat(1);

        try {
            connection = factory.newConnection();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private void closeConnection() {
        try {
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean makeChannel(boolean declareQueue) {

        boolean result = false;

        try {
            channel = connection.createChannel();
            if (declareQueue) {
                channel.queueDeclare(queueName, false, false, false, null);
            }
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private void closeChannel() {
        try {
            channel.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
