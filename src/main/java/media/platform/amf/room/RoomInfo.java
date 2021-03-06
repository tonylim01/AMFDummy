package media.platform.amf.room;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Vector;

public class RoomInfo {
    private static final Logger logger = LoggerFactory.getLogger(RoomInfo.class);

    private String roomId;

    private Vector<String> sessions;

    private int groupId;
    private int mixerId;
    private boolean mixerAvailable;

    private boolean isVolumeMin;
    private boolean isBgm;
    private boolean isMent;
    private boolean isVoice;

    private transient Object syncObj;

    public RoomInfo() {
        this.sessions = new Vector<>();
        groupId = -1;
        mixerId = -1;

        isSyncWait = false;
        syncObj = new Object();
    }

    public boolean addSession(String sessionId) {
        if (sessions.contains(sessionId)) {
            return false;
        }

        sessions.add(sessionId);
        return true;
    }

    public void removeSession(String sessionId) {
        if (sessions.contains(sessionId)) {
            sessions.remove(sessionId);
        }
    }

    public boolean hasSession(String sessionId) {
        return sessions.contains(sessionId);
    }

    public int getSessionSize() {
        return sessions.size();
    }

    /**
     * Gets the other session for the sessionId
     * @param sessionId
     * @return
     */
    public String getOtherSession(String sessionId) {
        String otherSession = null;
        if (sessions.contains(sessionId) && sessions.size() > 1) {
            for (String session: sessions) {
//                System.out.println("Session list " + session + " input " + sessionId);
                if (session != null && !session.equals(sessionId)) {
                    otherSession = session;
                    break;
                }
            }
        }
        return otherSession;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getMixerId() {
        return mixerId;
    }

    public void setMixerId(int mixerId) {
        this.mixerId = mixerId;
    }

    public boolean isVolumeMin() {
        return isVolumeMin;
    }

    public void setVolumeMin(boolean volumeMin) {
        isVolumeMin = volumeMin;
    }

    public boolean isBgm() {
        return isBgm;
    }

    public void setBgm(boolean bgm) {
        isBgm = bgm;
    }

    public boolean isMent() {
        return isMent;
    }

    public void setMent(boolean ment) {
        isMent = ment;
    }

    public boolean isVoice() {
        return isVoice;
    }

    public void setVoice(boolean voice) {
        if (isVoice != voice) {
            isVoice = voice;
        }
    }

    private transient boolean isSyncWait;

    public boolean isMixerAvailable() {
        return mixerAvailable;
    }

    public void setMixerAvailable(boolean mixerAvailable) {
        logger.debug("({}) Set mixer [{}]", roomId, mixerAvailable);
        this.mixerAvailable = mixerAvailable;

        if (isSyncWait) {
            synchronized (syncObj) {
                syncObj.notify();
            }
        }

        isSyncWait = false;
    }

    public boolean waitReady(int millisec) {
        boolean result = false;
        isSyncWait = true;

        synchronized (syncObj) {
            try {
                if (millisec > 0) {
                    syncObj.wait(millisec);
                }
                else {
                    syncObj.wait();
                }

                result = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    /**
     * wakeupStatus : [caller 2bit][callee 2bit]
     *   0 - N/A
     *   1 - Needs wakeup
     *   2 - Gets wakeup response from engine
     *
     *   0 1 . . - Caller needs wakeup
     *   1 0 . . - Caller is ready to wakeup
     *   . . 0 1 - Callee needs wakeup
     *   . . 1 0 - Callee is ready to wakeup
     */
    private int wakeupStatus;

    public static final int WAKEUP_STATUS_NONE      = 0;
    public static final int WAKEUP_STATUS_PREPARE   = 1;
    public static final int WAKEUP_STATUS_READY     = 2;

    public int setWakeupStatus(boolean isCaller, int status) {

        logger.debug("setWakeupStatus. isCaller [{}] status [{}] status [{}]", isCaller, status, wakeupStatus);

        if (isCaller) {
            wakeupStatus = (wakeupStatus & 0x3) | (((status & 0x3) << 2) & 0xc);
        }
        else {
            wakeupStatus = (wakeupStatus & 0xc) | (status & 0x3);
        }

        logger.debug("setWakeupStatus. new status [{}]", wakeupStatus);

        return wakeupStatus;
    }

    public int getWakeupStatus() {
        return wakeupStatus;
    }

    private String lastTransactionId;
    private String awfQueueName;

    public String getLastTransactionId() {
        return lastTransactionId;
    }

    public void setLastTransactionId(String lastTransactionId) {
        this.lastTransactionId = lastTransactionId;
    }

    public String getAwfQueueName() {
        return awfQueueName;
    }

    public void setAwfQueueName(String awfQueueName) {
        this.awfQueueName = awfQueueName;
    }
}
