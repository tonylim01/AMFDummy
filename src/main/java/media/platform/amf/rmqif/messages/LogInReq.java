/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file LogInReq.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.messages;

import com.google.gson.annotations.SerializedName;

public class LogInReq {
    @SerializedName("amf_id")
    private int amfid;

    public int getAmfid() {
        return amfid;
    }

    public void setAmfid(int amfid) {
        this.amfid = amfid;
    }

}
