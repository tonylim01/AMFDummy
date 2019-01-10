/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file ServiceStartRes.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.messages;

import com.google.gson.annotations.SerializedName;

public class WakeupStatusRes {
    @SerializedName("caller_wakeup_status")
    private int callerWakeupStatus;
    @SerializedName("callee_wakeup_status")
    private int calleeWakeupStatus;

    public int getCallerWakeupStatus() {
        return callerWakeupStatus;
    }

    public void setCallerWakeupStatus(int callerWakeupStatus) {
        this.callerWakeupStatus = callerWakeupStatus;
    }

    public int getCalleeWakeupStatus() {
        return calleeWakeupStatus;
    }

    public void setCalleeWakeupStatus(int calleeWakeupStatus) {
        this.calleeWakeupStatus = calleeWakeupStatus;
    }
}
