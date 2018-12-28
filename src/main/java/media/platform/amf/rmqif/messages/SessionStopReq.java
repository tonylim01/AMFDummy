/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file ServiceStartReq.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.messages;

import com.google.gson.annotations.SerializedName;

public class SessionStopReq {
    @SerializedName("call_start_time")
    private String callStartTime;
    @SerializedName("call_stop_time")
    private String callStopTime;
    @SerializedName("call_stop_by")
    private Boolean callStopBy;

    public String getCallStartTime() {
        return callStartTime;
    }

    public void setCallStartTime(String callStartTime) {
        this.callStartTime = callStartTime;
    }

    public String getCallStopTime() {
        return callStopTime;
    }

    public void setCallStopTime(String callStopTime) {
        this.callStopTime = callStopTime;
    }

    public Boolean getCallStopBy() {
        return callStopBy;
    }

    public void setCallStopBy(Boolean callStopBy) {
        this.callStopBy = callStopBy;
    }
}
