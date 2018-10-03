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

    private static final Map<String, String> dtmfMap;
    static {
        Map<String, String> map = new HashMap<>();
        map.put("DTMF0", "0");
        map.put("DTMF1", "1");
        map.put("DTMF2", "2");
        map.put("DTMF3", "3");
        map.put("DTMF4", "4");
        map.put("DTMF5", "5");
        map.put("DTMF6", "6");
        map.put("DTMF7", "7");
        map.put("DTMF8", "8");
        map.put("DTMF9", "9");
        map.put("DTMF10", "*");
        map.put("DTMF11", "#");
        map.put("DTMF12", "A");
        map.put("DTMF13", "B");
        map.put("DTMF14", "C");
        map.put("DTMF15", "D");

        dtmfMap = Collections.unmodifiableMap(map);
    }

    @SerializedName("MDN")
    private String mdn;
    @SerializedName("DTMF")
    private String dtmf;

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public void setDtmf(String dtmf) {
        this.dtmf = dtmf;
    }

    public static String getDtmfByName(String dtmfName) {
        String dtmf = null;
        if (dtmfName != null && dtmfMap.containsKey(dtmfName)) {
            dtmf = dtmfMap.get(dtmfName);
        }
        return dtmf;
    }
}
