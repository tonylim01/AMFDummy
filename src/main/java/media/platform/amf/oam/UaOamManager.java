package media.platform.amf.oam;

import com.uangel.jnauaoam.Uahastatus;
import com.uangel.jnauaoam.UalibContext;
import com.uangel.jnauaoam.stat.Uastat;
import media.platform.amf.AppInstance;
import media.platform.amf.common.AppUtil;
import media.platform.amf.config.UserConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UaOamManager {
    private static final Logger logger = LoggerFactory.getLogger(UaOamManager.class);

    private static final int STAT_DURATION_MSEC = 1000;
    private static final int HB_DURATION_MSEC = 1000;

    private static UaOamManager instance = null;

    private static UalibContext ualibContext = null;

    private boolean isQuit = false;
    private HAStatus haStatus;
    private Thread haStatusThread;
    private Thread statThread;

    private Uastat callStat;
    private Uastat svcStat;
    private Uastat reqStat;
    private Uastat cntStat;
    private Uastat playStat;

    public static UaOamManager getInstance() {
        if (instance == null) {
            instance = new UaOamManager();
        }

        return instance;
    }

    public UaOamManager() {
        UserConfig config = AppInstance.getInstance().getUserConfig();

        ualibContext = new UalibContext(config.getProcessName());
    }

    public void start() {
        logger.info("UaOamManager start", haStatus);

        ualibContext.initHA();
        haStatus = getHaStatus();

        logger.info("Current HA status [{}]", haStatus);

        initStat();

        haStatusThread = new Thread(new CheckHaStatusRunnable());
        haStatusThread.start();

        statThread = new Thread(new StatRunnable());
        statThread.start();
    }

    public void stop() {
        isQuit = true;
    }

    private void initStat() {
        callStat = new Uastat("AMFSTAT", "CALL_STAT");
        svcStat = new Uastat("AMFSTAT", "SERVICE_STAT");
        reqStat = new Uastat("AMFSTAT", "REQUEST_STAT");
        cntStat = new Uastat("AMFSTAT", "COUNT_STAT");
        playStat = new Uastat("AMFSTAT", "PLAY_STAT");
    }

    public HAStatus getHaStatus() {

        // UNKNOWN(-1), STANDBY(0), ACTIVE(1), STANDALONE(2);
        Uahastatus ha = ualibContext.getHAStatus();

        HAStatus status;

        switch (ha.getCode()) {
            case 0:
                status = HAStatus.STANDBY;
                break;
            case 1:
                status = HAStatus.ACTIVE;
                break;
            case 2:
                status = HAStatus.STANDALONE;
                break;
            default:
                status = HAStatus.UNKNOWN;
                break;
        }

        return status;
    }



    public class CheckHaStatusRunnable implements Runnable {

        private long startTimestamp = 0;
        private int tick = 0;

        public CheckHaStatusRunnable() {
            this.startTimestamp = System.currentTimeMillis();
        }

        @Override
        public void run() {
            while (!isQuit) {
                HAStatus newStatus = getHaStatus();

                if (newStatus != haStatus) {
                    logger.warn("HA status changed: [{}] -> [{}]", haStatus, newStatus);
                    haStatus = newStatus;

                    //
                    // TODO
                    //
                }

                ualibContext.stamp();

                tick++;

                if (tick >= 10) {
                    startTimestamp += (tick - 1) * HB_DURATION_MSEC;
                    tick = 1;

                }

                long timestamp = System.currentTimeMillis();

                AppUtil.trySleep(tick * HB_DURATION_MSEC - (int) (timestamp - startTimestamp));
            }

            logger.warn("CheckHaStatus thread end");
        }
    }

    public class StatRunnable implements Runnable {
        private long lastTimestamp;
        private int tick;
        private UserConfig config;
        private StatManager statManager;

        public StatRunnable() {
            this.lastTimestamp = System.currentTimeMillis();
            this.tick = 0;
            this.config = AppInstance.getInstance().getUserConfig();
            this.statManager = StatManager.getInstance();
        }

        @Override
        public void run() {
            while (!isQuit) {

                if (config == null || config.getInstanceName() == null) {
                    logger.error("Instance name not found. [{}]", (config == null) ? "null config" : "null instance");
                }
                else if (statManager == null) {
                    logger.error("Null statManager");
                }
                else {
                    saveCallStat();
                    saveSvcStat();
                    saveReqStat();
                    saveCntStat();
                    savePlayStat();
                }

                tick++;

                if (tick >= 10) {
                    lastTimestamp += (tick - 1) * STAT_DURATION_MSEC;
                    tick = 1;
                }

                long timestamp = System.currentTimeMillis();


                AppUtil.trySleep(tick * STAT_DURATION_MSEC - (int) (timestamp - lastTimestamp));
            }

            logger.warn("Stat thread end");
        }

        private void saveCallStat() {

            int result = callStat.open();
            if (result >= 0) {

                // Init call stat
                int[] initCallValues = {0};
                callStat.stat(StatManager.STR_INCOMING_OFFER, initCallValues);
                callStat.stat(StatManager.STR_OUTGOING_OFFER, initCallValues);
                callStat.stat(StatManager.STR_ANSWER, initCallValues);
                callStat.stat(StatManager.STR_INCOMING_RELEASE, initCallValues);
                callStat.stat(StatManager.STR_OUTGOING_RELEASE, initCallValues);
            }

            // Call stat
            callStat.stat(StatManager.STR_INCOMING_OFFER, statManager.getValues(StatManager.SVC_IN_CALL));
            callStat.stat(StatManager.STR_OUTGOING_OFFER, statManager.getValues(StatManager.SVC_OUT_CALL));
            callStat.stat(StatManager.STR_ANSWER, statManager.getValues(StatManager.SVC_ANSWER));
            callStat.stat(StatManager.STR_INCOMING_RELEASE, statManager.getValues(StatManager.SVC_IN_RELEASE));
            callStat.stat(StatManager.STR_OUTGOING_RELEASE, statManager.getValues(StatManager.SVC_OUT_RELEASE));

            callStat.close();
        }

        private void saveSvcStat() {
            int result = svcStat.open();
            if (result >= 0) {

                // Init req/ok/fail stat
                int[] initSvcValues = {0, 0, 0};
                svcStat.stat(StatManager.STR_CALLER_WAKEUP, initSvcValues);
                svcStat.stat(StatManager.STR_CALLEE_WAKEUP, initSvcValues);
            }

            // Req/ok/fail stat
            svcStat.stat(StatManager.STR_CALLER_WAKEUP, statManager.getValues(StatManager.SVC_CG_WAKEUP_REQ));
            svcStat.stat(StatManager.STR_CALLEE_WAKEUP, statManager.getValues(StatManager.SVC_CD_WAKEUP_REQ));

            svcStat.close();
        }

        private void saveReqStat() {
            int result = reqStat.open();
            if (result >= 0) {
                // Init req/res stat
                int[] initFairValues = {0, 0};
                reqStat.stat(StatManager.STR_AI_SVC, initFairValues);
                reqStat.stat(StatManager.STR_MEDIA_STOP, initFairValues);
            }

            // Req/res stat
            reqStat.stat(StatManager.STR_AI_SVC, statManager.getValues(StatManager.SVC_AI_REQ));
            reqStat.stat(StatManager.STR_MEDIA_STOP, statManager.getValues(StatManager.SVC_PLAY_STOP_REQ));

            reqStat.close();
        }

        private void saveCntStat() {
            int result = cntStat.open();
            if (result >= 0) {
                // Init count stat
                int[] initCntValues = {0};
                cntStat.stat(StatManager.STR_INCOMING_AI_CANCEL, initCntValues);
                cntStat.stat(StatManager.STR_OUTGOING_AI_CANCEL, initCntValues);
                cntStat.stat(StatManager.STR_END_DETECT, initCntValues);
            }

            // Count stat
            cntStat.stat(StatManager.STR_INCOMING_AI_CANCEL, statManager.getValues(StatManager.SVC_IN_AI_CANCEL));
            cntStat.stat(StatManager.STR_OUTGOING_AI_CANCEL, statManager.getValues(StatManager.SVC_OUT_AI_CANCEL));
            cntStat.stat(StatManager.STR_END_DETECT, statManager.getValues(StatManager.SVC_END_DETECT));

            cntStat.close();
        }

        private void savePlayStat() {
            int result = playStat.open();
            if (result >= 0) {

                // Init play stat
                int[] initPlayValues = {0, 0, 0, 0};
                playStat.stat(StatManager.STR_MEDIA_PLAY, initPlayValues);
            }

            // Play stat
            playStat.stat(StatManager.STR_MEDIA_PLAY, statManager.getValues(StatManager.SVC_PLAY_REQ));

            playStat.close();
        }
    }
}
