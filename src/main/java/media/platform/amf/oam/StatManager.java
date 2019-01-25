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
    public static final int SVC_OUT_AI_CANCEL   = 0x100d;
    public static final int SVC_END_DETECT  = 0x100e;
    public static final int SVC_PLAY_REQ    = 0x100f;
    public static final int SVC_PLAY_STOP_REQ    = 0x1010;
    public static final int SVC_PLAY_STOP_RES    = 0x1011;
    public static final int SVC_PLAY_RES     = 0x1012;
    public static final int SVC_PLAY_OK      = 0x1013;
    public static final int SVC_PLAY_ERROR   = 0x1014;
    public static final int SVC_CALL_DURATION = 0x1010;

    public static final String STR_INCOMING_OFFER = "INCOMING_OFFER";
    public static final String STR_OUTGOING_OFFER = "OUTGOING_OFFER";
    public static final String STR_ANSWER = "ANSWER";
    public static final String STR_INCOMING_RELEASE = "INCOMING_RELEASE";
    public static final String STR_OUTGOING_RELEASE = "OUTGOING_RELEASE";

    public static final String STR_CALLER_WAKEUP = "CALLER_WAKEUP";
    public static final String STR_CALLEE_WAKEUP = "CALLEE_WAKEUP";
    public static final String STR_AI_SVC = "AI_SVC";
    public static final String STR_INCOMING_AI_CANCEL = "INCOMING_AI_CANCEL";
    public static final String STR_OUTGOING_AI_CANCEL = "OUTGOING_AI_CANCEL";
    public static final String STR_END_DETECT = "END_DETECT";
    public static final String STR_MEDIA_PLAY = "MEDIA_PLAY";
    public static final String STR_MEDIA_STOP = "MEDIA_STOP";



    private SvcStat svcStat;

    public static int getStatType(int index) {

        switch (index) {
            case 0: return SVC_IN_CALL;
            case 1: return SVC_OUT_CALL;
            case 2: return SVC_ANSWER;
            case 3: return SVC_IN_RELEASE;
            case 4: return SVC_OUT_RELEASE;
            case 5: return SVC_CG_WAKEUP_REQ;
            case 6: return SVC_CD_WAKEUP_REQ;
            case 7: return SVC_CG_WAKEUP_OK;
            case 8: return SVC_CD_WAKEUP_OK;
            case 9: return SVC_AI_REQ;
            case 10: return SVC_AI_RES;
            case 11: return SVC_IN_AI_CANCEL;
            case 12: return SVC_OUT_AI_CANCEL;
            case 13: return SVC_END_DETECT;
            case 14: return SVC_PLAY_REQ;
            case 15: return SVC_CALL_DURATION;
            default:
                return 0;
        }
    }

    public synchronized void incCount(int statType) {
        switch (statType) {
            case SVC_IN_CALL:
                svcStat.inCall++;
                break;
            case SVC_OUT_CALL:
                svcStat.outCall++;
                break;
            case SVC_ANSWER:
                svcStat.answer++;
                break;
            case SVC_IN_RELEASE:
                svcStat.inRelease++;
                break;
            case SVC_OUT_RELEASE:
                svcStat.outRelease++;
                break;
            case SVC_CG_WAKEUP_REQ:
                svcStat.callerWakeupReq++;
                break;
            case SVC_CD_WAKEUP_REQ:
                svcStat.calleeWakeupReq++;
                break;
            case SVC_CG_WAKEUP_OK:
                svcStat.callerWakeupOk++;
                break;
            case SVC_CD_WAKEUP_OK:
                svcStat.calleeWakeupOk++;
                break;
            case SVC_AI_REQ:
                svcStat.aiReq++;
                break;
            case SVC_AI_RES:
                svcStat.aiRes++;
                break;
            case SVC_IN_AI_CANCEL:
                svcStat.inAiCancel++;
                break;
            case SVC_OUT_AI_CANCEL:
                svcStat.outAiCancel++;
                break;
            case SVC_END_DETECT:
                svcStat.endDetect++;
                break;
            case SVC_PLAY_REQ:
                svcStat.playReq++;
                break;
            case SVC_PLAY_RES:
                svcStat.playRes++;
                break;
            case SVC_PLAY_OK:
                svcStat.playOk++;
                break;
            case SVC_PLAY_ERROR:
                svcStat.playError++;
                break;
            case SVC_PLAY_STOP_REQ:
                svcStat.playStopReq++;
                break;
            case SVC_PLAY_STOP_RES:
                svcStat.playStopRes++;
                break;
        }
    }

    public synchronized void addValue(int statType, int value) {
        if (statType == SVC_CALL_DURATION) {
            svcStat.callDuration += value;
        }
    }

    public static String getKeyString(int statType) {

        switch (statType) {
            case SVC_IN_CALL:
                return "INCOMING_OFFER";
            case SVC_OUT_CALL:
                return "OUTGOING_OFFER";
            case SVC_ANSWER:
                return "ANSWER";
            case SVC_IN_RELEASE:
                return "INCOMING_RELEASE";
            case SVC_OUT_RELEASE:
                return "OUTGOING_RELEASE";
            case SVC_CG_WAKEUP_REQ:
                return "CALLER_WAKEUP";
            case SVC_CD_WAKEUP_REQ:
                return "CALLEE_WAKEUP";
            case SVC_CG_WAKEUP_OK:
                return "CALLER_WAKEUP";
            case SVC_CD_WAKEUP_OK:
                return "CALLEE_WAKEUP";
            case SVC_AI_REQ:
                return "AI_SVC";
            case SVC_AI_RES:
                return "AI_SVC";
            case SVC_IN_AI_CANCEL:
                return "AI_CANCEL";
            case SVC_OUT_AI_CANCEL:
                return "AI_CANCEL";
            case SVC_END_DETECT:
                return "END_DETECT";
            case SVC_PLAY_REQ:
                return "PLAY";
            case SVC_CALL_DURATION:
                return "CALL_DURATION";
            default:
                return null;
        }
    }

    public synchronized int[] getValues(int statType) {
        int[] values = null;

        switch (statType) {
            case SVC_IN_CALL:
            case SVC_OUT_CALL:
            case SVC_ANSWER:
            case SVC_IN_RELEASE:
            case SVC_OUT_RELEASE:
                values = new int[1];
                break;

            case SVC_CG_WAKEUP_REQ:
            case SVC_CD_WAKEUP_REQ:
            case SVC_CG_WAKEUP_OK:
            case SVC_CD_WAKEUP_OK:
                values = new int[3];
                break;

            case SVC_AI_REQ:
            case SVC_AI_RES:
            case SVC_PLAY_STOP_REQ:
                values = new int[2];
                break;

            case SVC_IN_AI_CANCEL:
            case SVC_OUT_AI_CANCEL:
            case SVC_END_DETECT:
                values = new int[1];
                break;

            case SVC_PLAY_REQ:
                values = new int[4];
                break;
            default:
                break;
        }

        if (values == null) {
            return null;
        }

        switch (statType) {
            // count
            case SVC_IN_CALL:
                values[0] = svcStat.inCall;
                break;
            case SVC_OUT_CALL:
                values[0] = svcStat.outCall;
                break;
            case SVC_ANSWER:
                values[0] = svcStat.answer;
                break;
            case SVC_IN_RELEASE:
                values[0] = svcStat.inRelease;
                break;
            case SVC_OUT_RELEASE:
                values[0] = svcStat.outRelease;
                break;

            // req, ok, fail
            case SVC_CG_WAKEUP_REQ:
            case SVC_CG_WAKEUP_OK:
                values[0] = svcStat.callerWakeupReq;
                values[1] = svcStat.calleeWakeupOk;
                values[2] = svcStat.callerWakeupReq - svcStat.calleeWakeupOk;
                break;
            case SVC_CD_WAKEUP_REQ:
            case SVC_CD_WAKEUP_OK:
                values[0] = svcStat.calleeWakeupReq;
                values[1] = svcStat.callerWakeupOk;
                values[2] = svcStat.calleeWakeupReq - svcStat.callerWakeupOk;
                break;

            // req, res
            case SVC_AI_REQ:
            case SVC_AI_RES:
                values[0] = svcStat.aiReq;
                values[1] = svcStat.aiRes;
                break;
            case SVC_PLAY_STOP_REQ:
                values[0] = svcStat.playStopReq;
                values[1] = svcStat.playStopRes;
                break;


            // count
            case SVC_IN_AI_CANCEL:
                values[0] = svcStat.inAiCancel;
                break;
            case SVC_OUT_AI_CANCEL:
                values[0] = svcStat.outAiCancel;
                break;
            case SVC_END_DETECT:
                values[0] = svcStat.endDetect;
                break;

            // req, res, ok, error
            case SVC_PLAY_REQ:
                values[0] = svcStat.playReq;
                values[1] = svcStat.playRes;
                values[2] = svcStat.playOk;
                values[2] = svcStat.playError;
                break;

            default:
                return null;
        }

        return values;
    }

    public synchronized void clearValues(int index, int[] values) {
        if (values == null) {
            return;
        }

        switch (index) {
            case 0:
                svcStat.inCall -= values[0];
                break;
            case 1:
                svcStat.outCall -= values[0];
                break;
            case 2:
                svcStat.answer -= values[0];
                break;
            case 3:
                svcStat.inRelease -= values[0];
                break;
            case 4:
                svcStat.outRelease -= values[0];
                break;
            case 5:
                svcStat.callerWakeupReq -= values[0];
                break;
            case 6:
                svcStat.calleeWakeupReq -= values[0];
                break;
            case 7:
                svcStat.callerWakeupOk -= values[0];
                break;
            case 8:
                svcStat.calleeWakeupOk -= values[0];
                break;
            case 9:
                svcStat.aiReq -= values[0];
                break;
            case 10:
                svcStat.aiRes -= values[0];
                break;
            case 11:
                svcStat.inAiCancel -= values[0];
                break;
            case 12:
                svcStat.outAiCancel -= values[0];
                break;
            case 13:
                svcStat.endDetect -= values[0];
                break;
            case 14:
                svcStat.playReq -= values[0];
                break;
            case 15:
                svcStat.callDuration -= values[0];
                break;
        }
    }

    public synchronized int[] getValues() {
        int[] values = new int[SvcStat.COLUMN_COUNT];

        values[0] = svcStat.inCall;
        values[1] = svcStat.outCall;
        values[2] = svcStat.answer;
        values[3] = svcStat.inRelease;
        values[4] = svcStat.outRelease;
        values[5] = svcStat.callerWakeupReq;
        values[6] = svcStat.calleeWakeupReq;
        values[7] = svcStat.callerWakeupOk;
        values[8] = svcStat.calleeWakeupOk;
        values[9] = svcStat.aiReq;
        values[10] = svcStat.aiRes;
        values[11] = svcStat.inAiCancel;
        values[12] = svcStat.outAiCancel;
        values[13] = svcStat.endDetect;
        values[14] = svcStat.playReq;
        values[15] = svcStat.callDuration;

        return values;
    }

    public synchronized void clearValues(int[] values) {
        if (values == null) {
            return;
        }

        svcStat.inCall -= values[0];
        svcStat.outCall -= values[1];
        svcStat.answer -= values[2];
        svcStat.inRelease -= values[3];
        svcStat.outRelease -= values[4];
        svcStat.callerWakeupReq -= values[5];
        svcStat.calleeWakeupReq -= values[6];
        svcStat.callerWakeupOk -= values[7];
        svcStat.calleeWakeupOk -= values[8];
        svcStat.aiReq -= values[9];
        svcStat.aiRes -= values[10];
        svcStat.inAiCancel -= values[11];
        svcStat.outAiCancel -= values[12];
        svcStat.endDetect -= values[13];
        svcStat.playReq -= values[14];
        svcStat.callDuration -= values[15];

    }

    public class SvcStat {
        public static final int COLUMN_COUNT = 16;

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
        public int playRes;
        public int playOk;
        public int playError;
        public int playStopReq;
        public int playStopRes;
        public int callDuration;

    }
}
