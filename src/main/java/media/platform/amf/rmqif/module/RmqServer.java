/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RmqServer.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.module;

import media.platform.amf.common.StringUtil;
import media.platform.amf.config.AmfConfig;
import media.platform.amf.AppInstance;
import media.platform.amf.config.UserConfig;
import media.platform.amf.core.rabbitmq.transport.RmqCallback;
import media.platform.amf.core.rabbitmq.transport.RmqReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class RmqServer {
    private static final Logger logger = LoggerFactory.getLogger(RmqServer.class);

    private static final int QUEUE_SIZE = 16;

    private RmqReceiver receiver = null;
    private BlockingQueue<String> queue;
    private Thread rmqConsumerThread;

    private String rmqHost, rmqUser, rmqPass, rmqLocal;

    public RmqServer(String host, String user, String pass, String localName) {
        this.rmqHost = host;
        this.rmqUser = user;
        this.rmqPass = pass;
        this.rmqLocal = localName;
    }

    public void start() {
        UserConfig config = AppInstance.getInstance().getUserConfig();

        logger.info("{} startScheduler", getClass().getSimpleName());

        int queueSize = AppInstance.getInstance().getConfig().getRmqBufferCount();
        if (queueSize == 0) {
            queueSize = QUEUE_SIZE;
        }

        queue = new ArrayBlockingQueue<>(queueSize);

        rmqConsumerThread = new Thread(new RmqConsumer(queue));
        rmqConsumerThread.start();

        logger.info("Rmq host [{}] user [{}] pass [{}] local [{}]", rmqHost, rmqUser, rmqPass, rmqLocal);

        receiver = new RmqReceiver(rmqHost, rmqUser, rmqPass, rmqLocal);
        receiver.setCallback(new MessageCallback());

        boolean result = receiver.connectServer();
        logger.info("[{}] connect ... [{}]", config.getLocalName(), StringUtil.getOkFail( result));

        if (result == false) {
            return;
        }

        result = receiver.start();
        logger.info("[{}] startScheduler ... [{}]", config.getLocalName(), StringUtil.getOkFail(result));
    }

    public void stop() {
        receiver.close();
        rmqConsumerThread.interrupt();
        rmqConsumerThread = null;
    }

    private class MessageCallback implements RmqCallback {
        @Override
        public void onReceived(String msg) {
            logger.info("onReceived : {}", msg);

            if (msg != null) {
                try {
                    queue.put(msg);
                } catch (Exception e) {
                    e.getClass();
                }
            }
        }
    }
}
