package media.platform.amf.rtpcore.core.component.audio;

import media.platform.amf.rtpcore.core.scheduler.PriorityQueueScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import media.platform.amf.rtpcore.core.component.AbstractSink;
import media.platform.amf.rtpcore.core.spi.ComponentType;
import media.platform.amf.rtpcore.core.spi.format.AudioFormat;
import media.platform.amf.rtpcore.core.spi.format.FormatFactory;
import media.platform.amf.rtpcore.core.spi.format.Formats;
import media.platform.amf.rtpcore.core.spi.memory.Frame;

import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import java.io.IOException;

public class SoundCard extends AbstractSink {
    
	private static final long serialVersionUID = 3163342541948279068L;

	private final static AudioFormat LINEAR = FormatFactory.createAudioFormat("LINEAR", 8000, 8, 1);
    private final static Formats formats = new Formats();

    private final static Encoding GSM_ENCODING = new Encoding("GSM0610");
    
    private AudioOutput output;
    
    static{
        formats.add(LINEAR);
    }

    private boolean first;
    private SourceDataLine sourceDataLine = null;
    private javax.sound.sampled.AudioFormat audioFormat = null;

    private static final Logger logger = LoggerFactory.getLogger( SoundCard.class);
    
    public SoundCard(PriorityQueueScheduler scheduler) {
        super("soundcard");
        output=new AudioOutput(scheduler,ComponentType.SOUND_CARD.getType());
        output.join(this);
    }

    public AudioOutput getAudioOutput()
    {
    	return this.output;
    }
    
    public void activate()
    {
    	first = true;
    	output.start();
    }
    
    public void deactivate()
    {
    	output.stop();
    }
        
    @Override
    public void onMediaTransfer(Frame frame) throws IOException {
        System.out.println("Receive " + frame.getFormat() + ", len=" + frame.getLength() + ", header=" + frame.getHeader());
        if (first) {
            first = false;

            AudioFormat fmt = (AudioFormat) frame.getFormat();
            
            if (fmt == null) {
                return;
            }
            
            float sampleRate = (float) fmt.getSampleRate();
            int sampleSizeInBits = fmt.getSampleSize();
            int channels = fmt.getChannels();
            int frameSize = (fmt.getSampleSize() / 8);
            //float frameRate = 1;
            boolean bigEndian = false;
            
            Encoding encoding = getEncoding(fmt.getName().toString());

            frameSize = (channels == AudioSystem.NOT_SPECIFIED || sampleSizeInBits == AudioSystem.NOT_SPECIFIED) ? AudioSystem.NOT_SPECIFIED
                    : ((sampleSizeInBits + 7) / 8) * channels;

            audioFormat = new javax.sound.sampled.AudioFormat(encoding, sampleRate, sampleSizeInBits, channels,
                    frameSize, sampleRate, bigEndian);

            // FIXME : Need a configuration to select the specific hardware
            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);

            // TODO : Should getting the SourceDataLine go in start() In which case its configurable to know the Formats
            // beforehand.
            try {
                sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
                sourceDataLine.open(audioFormat);
                sourceDataLine.start();

            } catch (Exception e) {
                this.stop();
                logger.error( String.valueOf( e ) );
            }
        }

        // FIXME : write() will block till all bytes are written. Need async operation here.
        byte[] data = frame.getData();
        try {
            sourceDataLine.write(data, frame.getOffset(), frame.getLength());
        } catch (RuntimeException e) {
        	logger.error( String.valueOf( e ) );
        }           
    }    
    
    private Encoding getEncoding(String encodingName) {
        if (encodingName.equalsIgnoreCase("pcma")) {
            return Encoding.ALAW;
        } else if (encodingName.equalsIgnoreCase("pcmu")) {
            return Encoding.ULAW;
        } else if (encodingName.equalsIgnoreCase("gsm")) {
            return GSM_ENCODING;
        } else {
            return Encoding.PCM_SIGNED;
        }
    }

    
}
