/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file StringUtil.java
 * @author Tony Lim
 *
 */


package media.platform.amf.common;

public class StringUtil {
    private static final String STR_OK = "OK";
    private static final String STR_FAIL = "FAIL";

    public static String getOkFail(boolean result) {
        return (result ? STR_OK : STR_FAIL);
    }

    public static boolean isNumeric(String str) {
        return (str != null && str.matches("-?\\d+")) ? true : false;
    }
}
