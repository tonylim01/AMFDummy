package media.platform.amf.oam;

import com.uangel.jnauaoam.Uahastatus;
import com.uangel.jnauaoam.UalibContext;
import media.platform.amf.AppInstance;
import media.platform.amf.common.AppUtil;
import media.platform.amf.config.UserConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UaOamManager {
    private static final Logger logger = LoggerFactory.getLogger(UaOamManager.class);

    private static UalibContext ualibContext = null;
    private static UaOamManager instance = null;

    private boolean isQuit = false;
    private HAStatus haStatus;
    private Thread haStatusThread;

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

        haStatusThread = new Thread(new CheckHaStatusRunnable());
        haStatusThread.start();
    }

    public void stop() {
        isQuit = true;
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

                AppUtil.trySleep(100);
            }

            logger.warn("CheckHaStatus thread end");
        }
    }

}
