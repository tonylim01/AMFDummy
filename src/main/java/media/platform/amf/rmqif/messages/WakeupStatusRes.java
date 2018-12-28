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
    private Boolean callerWakeupStatus;
    @SerializedName("callee_wakeup_status")
    private Boolean calleeWakeupStatus;

    public boolean getCallerWakeupStatus() {
        return callerWakeupStatus;
    }

    public void setCallerWakeupStatus(boolean callerWakeupStatus) {
        this.callerWakeupStatus = callerWakeupStatus;
    }

    public boolean getCalleeWakeupStatus() {
        return calleeWakeupStatus;
    }

    public void setCalleeWakeupStatus(boolean calleeWakeupStatus) {
        this.calleeWakeupStatus = calleeWakeupStatus;
    }
}
