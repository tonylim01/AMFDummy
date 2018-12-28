/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RmqClient.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.module;

import media.platform.amf.config.AmfConfig;
import media.platform.amf.AppInstance;
import media.platform.amf.config.UserConfig;
import media.platform.amf.core.rabbitmq.transport.RmqSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class RmqClient {

    private static final Logger logger = LoggerFactory.getLogger(RmqClient.class);

    private static Map<String, RmqClient> clients = null;

    public static RmqClient getInstance(String queueName) {
        if (clients == null) {
            clients = new HashMap<>();
        }

        RmqClient client = clients.get(queueName);
        if (client == null) {
            client = new RmqClient(queueName);
            clients.put(queueName, client);
        }

        return client;
    }

    public static boolean hasInstance(String queueName) {
        if (clients == null) {
            return false;
        }

        return clients.containsKey(queueName);
    }

    private RmqSender sender = null;
    private boolean isConnected = false;
    private String queueName = null;

    public RmqClient(String queueName) {

        this.queueName = queueName;
        this.isConnected = createSender(queueName);
    }

    private boolean createSender(String queueName) {
        UserConfig config = AppInstance.getInstance().getUserConfig();
        if (config == null) {
            return false;
        }
        sender = new RmqSender(config.getRmqHost(), config.getRmqUser(), config.getRmqPass(), queueName);
        return sender.connectClient();
   }

   public void closeSender() {
        if (sender != null) {
            sender.close();
            sender = null;
        }
   }

    public boolean isConnected() {
        return isConnected;
    }

    public boolean send(String msg) {
        return send(msg.getBytes(Charset.defaultCharset()), msg.length());
    }

    public boolean send(byte[] msg, int size) {
        if (sender == null) {
            if (createSender(queueName) == false) {
                return false;
            }
            if (sender == null) {
                return false;
            }
        }

        if (!sender.isOpened()) {
            if (!sender.connectClient()) {
                return false;
            }
        }

        return sender.send(msg, size);
    }
}
