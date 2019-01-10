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
    private Integer callerWakeupStatus;
    @SerializedName("callee_wakeup_status")
    private Integer calleeWakeupStatus;
    private MediaPlayReq success;
    private MediaPlayReq fail;

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
