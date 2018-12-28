/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file ServiceStartReq.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.messages;

import com.google.gson.annotations.SerializedName;

public class WakeupStatusReq {
    @SerializedName("caller_wakeup_status")
    private Boolean callerWakeupStatus;
    @SerializedName("callee_wakeup_status")
    private Boolean calleeWakeupStatus;
    private MediaPlayReq success;
    private MediaPlayReq fail;

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

    public MediaPlayReq getSuccess() {
        return success;
    }

    public void setSuccess(MediaPlayReq success) {
        this.success = success;
    }

    public MediaPlayReq getFail() {
        return fail;
    }

    public void setFail(MediaPlayReq fail) {
        this.fail = fail;
    }
}
