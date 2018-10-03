/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file HeartbeatReq.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.messages;

import com.google.gson.annotations.SerializedName;

public class HeartbeatReq {
    @SerializedName("amfId")
    private int amfId;

    @SerializedName("session_total")
    private int sessionTotal;
    @SerializedName("session_idle")
    private int sessionIdle;

    @SerializedName("conference_channel_total")
    private int conferenceChannelTotal;
    @SerializedName("conference_channel_idle")
    private int conferenceChannelIdle;

    public int getSessionTotal() {
        return sessionTotal;
    }

    public void setAmfId(int amfId) {
        this.amfId = amfId;
    }

    public void setSessionTotal(int sessionTotal) {
        this.sessionTotal = sessionTotal;
    }

    public int getSessionIdle() {
        return sessionIdle;
    }

    public void setSessionIdle(int sessionIdle) {
        this.sessionIdle = sessionIdle;
    }

    public int getConferenceChannelTotal() {
        return conferenceChannelTotal;
    }

    public void setConferenceChannelTotal(int conferenceChannelTotal) {
        this.conferenceChannelTotal = conferenceChannelTotal;
    }

    public int getConferenceChannelIdle() {
        return conferenceChannelIdle;
    }

    public void setConferenceChannelIdle(int conferenceChannelIdle) {
        this.conferenceChannelIdle = conferenceChannelIdle;
    }
}
