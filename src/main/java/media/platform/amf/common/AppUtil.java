package media.platform.amf.common;

import media.platform.amf.session.SessionState;

public class AppUtil {
    public static void trySleep(int msec) {
        try {
            Thread.sleep(msec);
        } catch (Exception e) { }
    }
}
