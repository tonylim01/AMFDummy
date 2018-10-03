package media.platform.amf.rtpcore.core.component.audio;

public class Resampler {
    private int f;
    private int F;

    /**
     * Creates new resampler.
     *
     * @param f the sampling rate of the original signal in Hertz
     * @param F the sampling rate of the new signal in Hertz.
     */
    public Resampler(int f, int F) {
        this.f = f;
        this.F = F;
    }

    /**
     * Performs resampling of the given signal.
     *
     * @param buffer the buffer containing the signal.
     * @param len the length of the signal in bytes.
     * @return resampled signal
     */
    public double[] perform(double[] buffer, int len) {
        int size = (int)((double)F/f * len);
        double signal[] = new double[size];

        double dx = 1./(double)f;
        double dX = 1./(double)F;

        signal[0] = buffer[0];

        double k = 0;
        for (int i = 1; i < size - 1; i++) {
            double X = i * dX;

            int p = (int)(X/dx);
            int q = p + 1;

            k = (buffer[q] - buffer[p])/dx;
            double x = p * dx;

            signal[i] = buffer[p] + (X - x) * k;
        }

        signal[size - 1] = buffer[len - 1] + ((size - 1) *dX - (len -1)*dx) *k;
        return signal;
    }
}
