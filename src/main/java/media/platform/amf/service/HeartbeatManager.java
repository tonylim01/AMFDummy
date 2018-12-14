package media.platform.amf.service;

import media.platform.amf.AppInstance;
import media.platform.amf.config.AmfConfig;
import media.platform.amf.rmqif.handler.RmqProcHeartbeatReq;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class HeartbeatManager {

    private volatile static HeartbeatManager heartbeatManager = null;

    public static HeartbeatManager getInstance() {
        if (heartbeatManager == null) {
            heartbeatManager = new HeartbeatManager();
        }

        return heartbeatManager;
    }

    private String thisSessionId;
    private ScheduledExecutorService scheduleService;
    static ScheduledFuture<?> scheduleFuture;

    public HeartbeatManager() {
        thisSessionId = UUID.randomUUID().toString();
        scheduleService = Executors.newScheduledThreadPool(1);
    }

    /**
     * Starts the heartbeat scheduler
     */
    public void start() {
        scheduleFuture = scheduleService.scheduleAtFixedRate(new SendHeartbeatRunnable(), 1, 5, TimeUnit.SECONDS);
    }

    /**
     * Stops the heartbeat scheduler
     */
    public void stop() {
        scheduleFuture.cancel(true);
        scheduleService.shutdown();
    }

    /**
     * Runnable proc for the ScheduledExecutorService
     */
    class SendHeartbeatRunnable implements Runnable {
        @Override
        public void run() {
            AmfConfig config = AppInstance.getInstance().getConfig();

            RmqProcHeartbeatReq req = new RmqProcHeartbeatReq( thisSessionId, UUID.randomUUID().toString());
            req.send(config.getMcudName());
        }
    }

}
