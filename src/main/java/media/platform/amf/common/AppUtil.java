package media.platform.amf.common;

public class AppUtil {
    public static void trySleep(int msec) {
        try {
            Thread.sleep(msec);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
