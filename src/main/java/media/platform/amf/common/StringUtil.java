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

    public static int strcasecmp(String a, String b) {
        if (a == null || b == null) {
            return 0;
        }

        int alen = a.length();
        int blen = b.length();

        if (alen < blen) {
            return 1;
        }
        else if (alen > blen) {
            return -1;
        }

        for (int i = 0; i < alen; i++) {
            if (a.charAt(i) < b.charAt(i)) {
                return 1;
            }
            else if (a.charAt(i) > b.charAt(i)) {
                return -1;
            }
        }

        return 0;
    }

    public static boolean compareString(String a, String b) {
        if (a == null && b == null) {
            return true;
        }

        if (a == null || b == null) {
            return  false;
        }

        return (a.equals(b));
    }
}
