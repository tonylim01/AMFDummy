/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file ServiceStartReq.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.messages;

import com.google.gson.annotations.SerializedName;

public class ServiceStartReq {
    @SerializedName("MDN")
    private String fromNo;
    @SerializedName("AIIF ID")
    private int aiifId;

    public String getFromNo() {
        return fromNo;
    }

    public int getAiifId() {
        return aiifId;
    }
}
