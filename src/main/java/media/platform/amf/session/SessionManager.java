package media.platform.amf.session;

import media.platform.amf.config.AmfConfig;
import media.platform.amf.config.UserConfig;
import media.platform.amf.room.RoomInfo;
import media.platform.amf.room.RoomManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import media.platform.amf.AppInstance;
import media.platform.amf.rmqif.handler.RmqProcOutgoingHangupReq;
import media.platform.amf.rmqif.types.RmqMessageType;
import media.platform.amf.service.ServiceManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SessionManager {

    private static final Logger logger = LoggerFactory.getLogger(SessionManager.class);

    private static final int DEFAULT_SESSION_MAX_SIZE = 16000;
    private static final int DEFAULT_SESSION_TIMEOUT = 3600;

    // T2 : Timer 2 - Retransmission interval
    // T4 : Timer 4 - Maximum interval

//    private static final int DEFAULT_T2 = 500;
//    private static final int DEFAULT_T4 = 2000;
//    private static final int DEFAULT_MEDIA_INACTIVITY = 10000;
//
//    public static final int TIMER_PREPARE_T2 = DEFAULT_T2;
//    public static final int TIMER_PREPARE_T4 = DEFAULT_T4;
//    public static final int TIMER_HANGUP_T2 = DEFAULT_T2;
//    public static final int TIMER_HANGUP_T4 = DEFAULT_T4;

    private volatile static SessionManager sessionManager = null;

    public static SessionManager getInstance() {
        if (sessionManager == null) {
            sessionManager = new SessionManager();
        }

        return sessionManager;
    }

    private int sessionSize;
    private int sessionTimeout;
    private int lastSessionCount = -1;

    private Map<String, SessionInfo> sessionInfos;

    private ScheduledExecutorService scheduleService;
    private ScheduledFuture<?> scheduleFuture;
    private SessionMonitorRunnable sessionMonitorRunnable;

    private SessionStateManager sessionStateManager;

    public SessionManager() {

        UserConfig config = AppInstance.getInstance().getUserConfig();
        if (config == null) {
            return;
        }

        sessionSize = config.getSessionMaxSize();
        sessionTimeout = config.getSessionTimeout();

        if (sessionSize <= 0 || sessionSize > DEFAULT_SESSION_MAX_SIZE) {
            sessionSize = DEFAULT_SESSION_MAX_SIZE;
        }

        if (sessionTimeout < 0) {
            sessionTimeout = DEFAULT_SESSION_TIMEOUT;
        }

        sessionInfos = new HashMap<>();

        scheduleService = Executors.newScheduledThreadPool(1);
        sessionMonitorRunnable = new SessionMonitorRunnable();

        sessionStateManager = SessionStateManager.getInstance();

        logger.error("SessionManager started session Size :" + sessionSize);
    }

    /**
     * Starts the session scheduler which called per every 1 second
     */
    public void start() {
        scheduleFuture = scheduleService.scheduleAtFixedRate(sessionMonitorRunnable, 1, 1, TimeUnit.SECONDS);
    }

    /**
     * Stops the session scheduler
     */
    public void stop() {
        sessionStateManager.stop();

        scheduleFuture.cancel(true);
        scheduleService.shutdown();
    }

    /**
     * Creates new sessionInfo and sets the sessionId
     * @param sessionId
     * @return
     */
    public SessionInfo createSession(String sessionId) {

        if (sessionId == null) {
            logger.error("createSession() failed: Null sessionId");
            return null;
        }

        if (sessionInfos.containsKey(sessionId)) {
            logger.warn("createSession() failed: Already sessionId [{}] found", sessionId);
            return sessionInfos.get(sessionId);
        }

        if (sessionInfos.size() >= sessionSize) {
            logger.warn("(createSession() failed: Session full");
            return null;
        }

        SessionInfo sessionInfo = new SessionInfo();

        sessionInfo.setSessionId(sessionId);
        sessionInfo.setCreatedTime(System.currentTimeMillis());
        sessionInfo.setServiceState(SessionState.IDLE);
        //
        // TODO
        //

        synchronized (sessionInfos) {
            sessionInfos.put(sessionId, sessionInfo);
        }

        logger.debug("[{}] New session. Total [{}]", sessionId, sessionInfos.size());

        return sessionInfo;
    }

    /**
     * Deletes sessionInfo with the sessionId from the session queue
     * @param sessionId
     */
    public void deleteSession(String sessionId) {
        synchronized (sessionInfos) {
            sessionInfos.remove(sessionId);
            logger.debug("[{}] Delete session. Remaining count [{}]", sessionId, sessionInfos.size());
        }
    }

    /**
     * Finds and returns sessionInfo with the sessionId
     * @param sessionId
     * @return
     */
    public SessionInfo getSession(String sessionId) {
        SessionInfo sessionInfo = null;
        synchronized (sessionInfos) {
            if (sessionInfos.containsKey(sessionId)) {
                sessionInfo = sessionInfos.get(sessionId);
            }
        }

        return sessionInfo;
    }

    /**
     * Static function of the above getSession()
     * @param sessionId
     * @return
     */
    public static SessionInfo findSession(String sessionId) {
        if (sessionId == null) {
            return null;
        }

        SessionInfo sessionInfo = SessionManager.getInstance().getSession(sessionId);
        if (sessionInfo == null) {
            logger.error("[{}] No sessionInfo found", sessionId);
            return null;
        }

        return sessionInfo;
    }

    /**
     * Finds and returns other parties' SessionInfo
     * @param sessionInfo
     * @return
     */
    public static SessionInfo findOtherSession(SessionInfo sessionInfo) {
        if (sessionInfo == null) {
            return null;
        }

        SessionInfo otherSession = null;

        if (sessionInfo.getConferenceId() != null) {

            RoomInfo roomInfo = RoomManager.getInstance().getRoomInfo(sessionInfo.getConferenceId());
            if (roomInfo != null) {
                String otherSessionId = roomInfo.getOtherSession(sessionInfo.getSessionId());
                if (otherSessionId != null) {
                    otherSession = SessionManager.findSession(otherSessionId);
                }
            }
        }

        return otherSession;
    }

    /**
     * Returns total number of sessions
     * @return
     */
    public int getTotalCount() {
        return sessionSize;
    }

    /**
     * Returns current idle session count
     * @return
     */
    public int getIdleCount() {
        return sessionSize - sessionInfos.size();
    }

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("mm:dd-hhmmss");

    public void printSessionList() {
        synchronized (sessionInfos) {
            logger.error("Session count: {}", sessionInfos.size());

            for (Map.Entry<String, SessionInfo> entry: sessionInfos.entrySet()) {
                SessionInfo sessionInfo = entry.getValue();
                if (sessionInfo == null) {
                    continue;
                }

                logger.debug("Session [{}] time [{}]", sessionInfo.getSessionId(),
                        dateFormat.format(new Date(sessionInfo.getCreatedTime())));
            }
        }
    }

    /**
     * Checks the session's validity and deletes the wrong session
     */
    public void checkSessionValidity() {
        long current = System.currentTimeMillis();

        AmfConfig config = AppInstance.getInstance().getConfig();
        if (config == null) {
            return;
        }

        synchronized (sessionInfos) {
//            logger.error("Session count: {}", sessionInfos.size());
            if (sessionInfos.size() != lastSessionCount) {
                lastSessionCount = sessionInfos.size();
                if(lastSessionCount>0)
                    logger.debug("Session count: {}", lastSessionCount);
            }

            for (Map.Entry<String, SessionInfo> entry: sessionInfos.entrySet()) {
                SessionInfo sessionInfo = entry.getValue();
                if (sessionInfo == null) {
                    continue;
                }

                //
                // TODO : Long Call 처리 로직 개발 필요
                //
                if (current - sessionInfo.getCreatedTime() >= sessionTimeout) {
                    //
                    // TODO: Session timeout expired
                    //
                }

                //
                // In case of no packet
                //
                if (current - sessionInfo.getRtpReceivedTime() >= config.getTimerMediaNoActivity()) {
                    logger.warn("[{}] No packet. Force release", sessionInfo.getSessionId());

                    sendHangupReq(sessionInfo);
                    continue;
                }

                if (sessionInfo.getLastSentTime() > 0) {
                    if (sessionInfo.getServiceState() == SessionState.PREPARE) {

                    }
                    else if (sessionInfo.getServiceState() == SessionState.RELEASE) {
                        checkSessionStateRelease(sessionInfo,
                                sessionInfo.getLastSentTime(), sessionInfo.getT2Time(), sessionInfo.getT4Time());
                    }
                }
            }
        }

        long elapsed = System.currentTimeMillis() - current;
        if (elapsed > 100) {
            logger.debug("Sleep diff [{}]", elapsed);
        }
    }

    /**
     * Calls checkSessionValidity() periodically
     */
    private static class SessionMonitorRunnable implements Runnable {
        @Override
        public void run() {
            SessionManager manager = SessionManager.getInstance();

            if (manager != null) {
                manager.checkSessionValidity();
            }
        }
    }

    /**
     * Checks if the session state = PREPARE
     * @param sessionInfo
     * @param lastSentTime
     * @param t2Time
     * @param t4Time
     * @return
     */
    private boolean checkSessionStatePrepare(SessionInfo sessionInfo, long lastSentTime, long t2Time, long t4Time) {
        if (sessionInfo == null) {
            logger.error("Null sessionInfo");
            return false;
        }

        if (lastSentTime < t2Time) {
            // Nothing to do
        }
        else if (lastSentTime >= t2Time && lastSentTime < t4Time) {
        }
        else if (lastSentTime >= t4Time) {
            // Stop retransmitting
        }

        return true;
    }

    /**
     * Checks if the session state = RELEASE
     * @param sessionInfo
     * @param lastSentTime
     * @param t2Time
     * @param t4Time
     * @return
     */
    private boolean checkSessionStateRelease(SessionInfo sessionInfo, long lastSentTime, long t2Time, long t4Time) {
        if (sessionInfo == null) {
            logger.error("Null sessionInfo");
            return false;
        }

        if (lastSentTime < t2Time) {
            // Nothing to do
        }
        else if (lastSentTime >= t2Time && lastSentTime < t4Time) {
            // Retransmits HangupReq
            logger.warn("[{}] Retransmit {}", sessionInfo.getSessionId(),
                    RmqMessageType.getMessageTypeStr(RmqMessageType.RMQ_MSG_TYPE_HANGUP_REQ));

            sendHangupReq(sessionInfo);
        }
        else if (lastSentTime >= t4Time) {
            ServiceManager.getInstance().releaseResource(sessionInfo.getSessionId());
        }

        return true;
    }

    public SessionInfo getSrcLocalPort(int port) {

        synchronized (sessionInfos) {
            if (sessionInfos.size() != lastSessionCount) {
                lastSessionCount = sessionInfos.size();
                logger.debug("Session count: {}", lastSessionCount);
            }

            for (Map.Entry<String, SessionInfo> entry: sessionInfos.entrySet()) {
                SessionInfo sessionInfo = entry.getValue();
                if (sessionInfo == null) {
                    continue;
                }

                if(sessionInfo.getSrcLocalPort() == port)
                {
                    return sessionInfo;
                }
            }
        }

        return null;
    }

    public SessionInfo getDstLocalPort(int port) {

        synchronized (sessionInfos) {
            if (sessionInfos.size() != lastSessionCount) {
                lastSessionCount = sessionInfos.size();
                logger.debug("Session count: {}", lastSessionCount);
            }

            for (Map.Entry<String, SessionInfo> entry: sessionInfos.entrySet()) {
                SessionInfo sessionInfo = entry.getValue();
                if (sessionInfo == null) {
                    continue;
                }

                if(sessionInfo.getDstLocalPort() == port) {
                    return sessionInfo;
                }
            }
        }

        return null;
    }

    private void sendHangupReq(SessionInfo sessionInfo) {
        if (sessionInfo == null) {
            return;
        }

        RmqProcOutgoingHangupReq hangupReq = new RmqProcOutgoingHangupReq(sessionInfo.getSessionId(), null);
        hangupReq.sendToMcud();
    }

}
