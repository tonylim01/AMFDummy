package media.platform.amf.oam;

public class StatManager {

    private static StatManager instance = null;

    public static StatManager getInstance() {
        if (instance == null) {
            instance = new StatManager();
        }
        return instance;
    }

    public StatManager() {
        svcStat = new SvcStat();
    }

    public static final int SVC_IN_CALL     = 0x1001;
    public static final int SVC_OUT_CALL    = 0x1002;
    public static final int SVC_ANSWER      = 0x1003;
    public static final int SVC_IN_RELEASE  = 0x1004;
    public static final int SVC_OUT_RELEASE = 0x1005;
    public static final int SVC_CG_WAKEUP_REQ   = 0x1006;
    public static final int SVC_CD_WAKEUP_REQ   = 0x1007;
    public static final int SVC_CG_WAKEUP_OK    = 0x1008;
    public static final int SVC_CD_WAKEUP_OK    = 0x1009;
    public static final int SVC_AI_REQ      = 0x100a;
    public static final int SVC_AI_RES      = 0x100b;
    public static final int SVC_IN_AI_CANCEL    = 0x100c;
    public static final int SVC_OUT_AI_CANCEL   = 0x100c;
    public static final int SVC_END_DETECT  = 0x100d;
    public static final int SVC_PLAY_REQ    = 0x100e;
    public static final int SVC_CALL_DURATION = 0x100f;

    private SvcStat svcStat;

    public synchronized void incCount(int statType) {
        if (statType == SVC_IN_CALL) {
            svcStat.inCall++;
        }
        else if (statType == SVC_OUT_CALL) {
            svcStat.outCall++;
        }
        else if (statType == SVC_ANSWER) {
            svcStat.answer++;
        }
        else if (statType == SVC_IN_RELEASE) {
            svcStat.inRelease++;
        }
        else if (statType == SVC_OUT_RELEASE) {
            svcStat.outRelease++;
        }
        else if (statType == SVC_CG_WAKEUP_REQ) {
            svcStat.callerWakeupReq++;
        }
        else if (statType == SVC_CD_WAKEUP_REQ) {
            svcStat.calleeWakeupReq++;
        }
        else if (statType == SVC_CG_WAKEUP_OK) {
            svcStat.callerWakeupOk++;
        }
        else if (statType == SVC_CD_WAKEUP_OK) {
            svcStat.calleeWakeupOk++;
        }
        else if (statType == SVC_AI_REQ) {
            svcStat.aiReq++;
        }
        else if (statType == SVC_AI_RES) {
            svcStat.aiRes++;
        }
        else if (statType == SVC_IN_AI_CANCEL) {
            svcStat.inAiCancel++;
        }
        else if (statType == SVC_OUT_AI_CANCEL) {
            svcStat.outAiCancel++;
        }
        else if (statType == SVC_END_DETECT) {
            svcStat.endDetect++;
        }
        else if (statType == SVC_PLAY_REQ) {
            svcStat.playReq++;
        }
    }

    public synchronized void addValue(int statType, int value) {
        if (statType == SVC_CALL_DURATION) {
            svcStat.callDuration += value;
        }
    }

    public class SvcStat {
        public int inCall;
        public int outCall;
        public int answer;
        public int inRelease;
        public int outRelease;
        public int callerWakeupReq;
        public int calleeWakeupReq;
        public int callerWakeupOk;
        public int calleeWakeupOk;
        public int aiReq;
        public int aiRes;
        public int inAiCancel;
        public int outAiCancel;
        public int endDetect;
        public int playReq;
        public int callDuration;
    }
}
