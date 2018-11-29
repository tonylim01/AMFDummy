/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file DtmfDetectReq.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.messages;

import com.google.gson.annotations.SerializedName;
import media.platform.amf.session.StateHandler.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DtmfDetectReq {

    private static final String[] digits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "*", "#", "A", "B", "C", "D" };

    @SerializedName("DTMF")
    private String dtmf;


    public String getDtmf() {
        return dtmf;
    }

    public void setDtmf(String dtmf) {
        this.dtmf = dtmf;
    }

    public void setDtmf(int digit) {
        if (digit < digits.length) {
            dtmf = digits[digit];
        }
    }
}
