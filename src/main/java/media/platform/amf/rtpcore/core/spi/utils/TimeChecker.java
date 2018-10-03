package media.platform.amf.rtpcore.core.spi.utils;

public class TimeChecker {
    public static boolean check(long[] set, double tolerance) {
        long min = 0;
        long max = 0;
        long avg = 0;

        for (int i = 0; i < set.length; i++) {
            if (set[i] < min) min = set[i];
            if (set[i] > max) max = set[i];
            avg += set[i];
        }

        avg = avg / set.length;
        double ratio = ((double)(max - min))/max;
        System.out.println(String.format("max=%s, min=%s", max, min));
        return ratio <= tolerance;
    }
}
