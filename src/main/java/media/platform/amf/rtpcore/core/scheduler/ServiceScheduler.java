package media.platform.amf.rtpcore.core.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ServiceScheduler implements Scheduler {

    private static final Logger logger = LoggerFactory.getLogger( ServiceScheduler.class);

    public static final int POOL_SIZE = Runtime.getRuntime().availableProcessors();

    private volatile boolean started;
    private final Clock wallClock;
    private ScheduledExecutorService executor;
    private final ThreadFactory threadFactory = new ThreadFactory() {

        private AtomicInteger index = new AtomicInteger(0);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "service-scheduler-" + index.incrementAndGet());
        }
    };

    public ServiceScheduler(final Clock wallClock) {
        this.started = false;
        this.wallClock = new WallClock();
    }

    public ServiceScheduler() {
        this(new WallClock());
    }

    @Override
    public Clock getWallClock() {
        return this.wallClock;
    }

    @Override
    public Future<?> submit(Runnable task) throws RejectedExecutionException {
        if (!this.started) {
            throw new RejectedExecutionException("Scheduler is not running.");
        }
        return this.executor.submit(task);
    }
    
    @Override
    public ScheduledFuture<?> schedule(Runnable task, long delay, TimeUnit unit) throws RejectedExecutionException {
        if (!this.started) {
            throw new RejectedExecutionException("Scheduler is not running.");
        }
        return this.executor.schedule(task, delay, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long initialDelay, long period, TimeUnit unit)
            throws IllegalArgumentException, RejectedExecutionException {
        if (!this.started) {
            throw new RejectedExecutionException("Scheduler is not running.");
        }
        return this.executor.scheduleWithFixedDelay(task, initialDelay, period, unit);
    }

    @Override
    public void start() {
        if (!this.started) {
            this.started = true;
            this.executor = Executors.newScheduledThreadPool(POOL_SIZE, threadFactory);
            ((ScheduledThreadPoolExecutor) this.executor).setRemoveOnCancelPolicy(true);
            ((ScheduledThreadPoolExecutor) this.executor).prestartAllCoreThreads();
            logger.info("Started scheduler!");
        }
    }

    @Override
    public void stop() {
        if (this.started) {
            this.started = false;
            this.executor.shutdownNow();
            logger.info("Stopped scheduler!");
        }
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return this.executor.awaitTermination(timeout, unit);
    }

}
