package media.platform.amf.rtpcore.core.component.audio;

import media.platform.amf.rtpcore.core.scheduler.PriorityQueueScheduler;
import media.platform.amf.rtpcore.core.component.AbstractSink;
import media.platform.amf.rtpcore.core.spi.ComponentType;
import media.platform.amf.rtpcore.core.spi.format.AudioFormat;
import media.platform.amf.rtpcore.core.spi.format.FormatFactory;
import media.platform.amf.rtpcore.core.spi.format.Formats;
import media.platform.amf.rtpcore.core.spi.memory.Frame;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class SpectraAnalyzer extends AbstractSink {

	private static final long serialVersionUID = 1646539542777368667L;

	private final static int tolerance = 5;
    private final static AudioFormat LINEAR_AUDIO = FormatFactory.createAudioFormat("LINEAR", 8000, 16, 1);
    private final static Formats formats = new Formats();

    //more then 10 seconds
    private double[] buffer = new double[81920];
    private volatile int len;

    private double pow[];

    private FFT fft = new FFT();
    private Resampler resampler = new Resampler(8000, 8192);
    static {
        formats.add(LINEAR_AUDIO);
    }

    private AudioOutput output;
    
    public SpectraAnalyzer(String name, PriorityQueueScheduler scheduler) {
        super(name);
        output=new AudioOutput(scheduler,ComponentType.SPECTRA_ANALYZER.getType());
        output.join(this);
    }

    public AudioOutput getAudioOutput()
    {
    	return this.output;
    }
    
    public void activate()
    {
    	this.len = 0;
        System.out.println("startScheduler, len=" + len);
        output.start();
    }
    
    public void deactivate()
    {
    	output.stop();
    }        
    
    private double[] mod(Complex[] x) {
        double[] res = new double[x.length];
        for (int i = 0; i < res.length; i++) {
            res[i] = Math.sqrt(x[i].re() * x[i].re() + x[i].im() * x[i].im());
        }
        return res;
    }

    public void onMediaTransfer(Frame frame) throws IOException {
        byte[] data = frame.getData();
        int j = 0;
        for (int i = 0; i < (frame.getLength() / 2) && len < buffer.length; i++) {
            buffer[len++] = (data[j++] & 0xff) | (data[j++] << 8);
//            System.out.println(buffer[len-1]);
        }
    }

    private double[] derivative(double[] spectra) {
        double[] res = new double[spectra.length / 2 - 1];
        for (int i = 0; i < res.length; i++) {
            res[i] = spectra[i + 1] - spectra[i];
        }
        return res;
    }

    private int[] findPeaks(double[] data) {
        ArrayList<Integer> peaks = new ArrayList<Integer>();
        for (int i = 0; i < data.length; i++) {
            if (data[i] > 10000000) peaks.add(i);
        }
        int[] res = new int[peaks.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = peaks.get(i);
        }
        return res;
    }

    private void append(ArrayList<Integer> list, int v) {
        boolean found = false;
        for (int i : list) {
            if (Math.abs(i - v) <= tolerance) {
                found = true;
                break;
            }
        }
        if (!found) list.add(v);
    }

    public int[] getSpectra() {
        ArrayList<Integer> frequency = new ArrayList<Integer>();
        System.out.println("len=" + len);
        int count = len / 8000;
        for (int i = 0; i < count; i++) {
            double[] data = new double[8000];
            System.arraycopy(buffer, 8000 * i, data, 0, 8000);

            double s[] = resampler.perform(data, 8000);

            Complex[] signal = new Complex[8192];
            for (int j = 0; j < 8192; j++) {
                signal[j] = new Complex(s[j], 0);
            }

            Complex[] sp = fft.fft(signal);
            pow = mod(sp);

            double[] dif = this.derivative(pow);
            int[] freqs = this.findPeaks(dif);

            for (int k = 0; k < freqs.length; k++) {
                append(frequency, freqs[k]);
            }
        }

        int[] res = new int[frequency.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = frequency.get(i);
        }
        
        return res;
    }

    public void print(String fileName) throws FileNotFoundException, IOException {
        FileOutputStream fout = new FileOutputStream(fileName, false);
        for (int i = 0; i < len; i++) {
            fout.write((i + "  " + buffer[i] + "\n").getBytes(Charset.defaultCharset()));
        }
        fout.flush();
        fout.close();
    }

    public Formats getNativeFormats() {
        return formats;
    }

}

