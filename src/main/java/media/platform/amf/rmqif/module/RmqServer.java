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
import media.platform.amf.core.rabbitmq.transport.RmqCallback;
import media.platform.amf.core.rabbitmq.transport.RmqReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class RmqServer {
    private static final Logger logger = LoggerFactory.getLogger(RmqServer.class);

    private static final int QUEUE_SIZE = 8;

    private RmqReceiver receiver = null;
    private BlockingQueue<String> queue;
    private Thread rmqConsumerThread;

    public RmqServer() {
        queue = new ArrayBlockingQueue<>(QUEUE_SIZE);
        rmqConsumerThread = new Thread(new RmqConsumer(queue));
    }

    public void start() {
        AmfConfig config = AppInstance.getInstance().getConfig();

        logger.info("{} start", getClass().getSimpleName());

        rmqConsumerThread.start();

        receiver = new RmqReceiver(config.getRmqHost(), config.getRmqUser(), config.getRmqPass(), config.getLocalName());
//        receiver = new RmqReceiver(config.getRmqHost(), config.getRmqUser(), config.getRmqPass(), "amf_amfd");
        receiver.setCallback(new MessageCallback());

        boolean result = receiver.connectServer();
        logger.info( "{} connect ... [{}]", getClass().getSimpleName(), StringUtil.getOkFail( result));

        if (result == false) {
            return;
        }

        result = receiver.start();
        logger.info("{} [{}] start ... [{}]", getClass().getSimpleName(), config.getLocalName(), StringUtil.getOkFail(result));
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
