/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file NegoDoneRes.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.messages;

import com.google.gson.annotations.SerializedName;

public class NegoDoneRes {
    @SerializedName("in_out_flag")
    private int inOutFlag;

    public int getInOutFlag() {
        return inOutFlag;
    }

    public void setInOutFlag(int inOutFlag) {
        this.inOutFlag = inOutFlag;
    }
}
