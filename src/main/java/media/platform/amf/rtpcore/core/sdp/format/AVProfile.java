package media.platform.amf.rtpcore.core.sdp.format;

import media.platform.amf.rtpcore.core.spi.format.AudioFormat;
import media.platform.amf.rtpcore.core.spi.format.FormatFactory;
import media.platform.amf.rtpcore.core.spi.utils.Text;

public class AVProfile {
	public final static Text AUDIO = new Text( "audio");
	public final static Text VIDEO = new Text("video");

	public static final int DYNAMIC_PT_MIN = 96;
	public static final int DYNAMIC_PT_MAX = 127;

	public final static int telephoneEventsID=101;
    public final static AudioFormat telephoneEvent = FormatFactory.createAudioFormat( "telephone-event", 8000);
    static {
        telephoneEvent.setOptions(new Text("0-15"));
    }
    public final static RTPFormats audio = new RTPFormats();
    public final static RTPFormats video = new RTPFormats();
    public final static RTPFormats application = new RTPFormats();
    
    private final static RTPFormat pcmu = new RTPFormat(0, FormatFactory.createAudioFormat("pcmu", 8000, 8, 1), 8000);
    private final static RTPFormat pcma = new RTPFormat(8, FormatFactory.createAudioFormat("pcma", 8000, 8, 1), 8000);
    private final static RTPFormat gsm = new RTPFormat(3, FormatFactory.createAudioFormat("gsm", 8000), 8000);
    private final static RTPFormat g729 = new RTPFormat(18, FormatFactory.createAudioFormat("g729", 8000), 8000);
    private final static RTPFormat l16 = new RTPFormat(97, FormatFactory.createAudioFormat("l16", 8000, 16, 1), 8000);    
    private final static RTPFormat dtmf = new RTPFormat(telephoneEventsID, telephoneEvent, 8000);
    private final static RTPFormat ilbc = new RTPFormat(102, FormatFactory.createAudioFormat("ilbc", 8000, 16, 1), 8000);
    private final static RTPFormat amr_nb = new RTPFormat(96, FormatFactory.createAudioFormat("amr", 8000, 8, 1), 8000);
    private final static RTPFormat amr_wb = new RTPFormat(100, FormatFactory.createAudioFormat("amr_wb", 16000, 16, 1), 16000);

    private final static RTPFormat opus = new RTPFormat(111, FormatFactory.createAudioFormat("opus", 48000, 16, 2), 48000);
    private final static RTPFormat linear = new RTPFormat(150, FormatFactory.createAudioFormat("linear", 8000, 16, 1), 8000);


    static {
        audio.add(pcma);
        audio.add(pcmu);
        audio.add(gsm);
        audio.add(g729);
        audio.add(l16);
        audio.add(ilbc);
        audio.add(opus);
        audio.add(dtmf);
        audio.add(amr_nb);
        audio.add(amr_wb);
    }


    public static RTPFormat getFormat(int p) {    	
        RTPFormat res = audio.find(p);
        return res == null ? video.find(p) : res;
    }

    public static RTPFormat getFormat(String name) {
        RTPFormat res = audio.getRTPFormat(name);
        return (res == null) ? video.getRTPFormat(name) : res;
    }
    
    public static RTPFormat getFormat(int p,Text mediaType) {
    	RTPFormat res=null;
    	if(mediaType.equals(AUDIO)) {
    		res = audio.find(p);    		
    	} else if(mediaType.equals(VIDEO)) {
    		res = video.find(p);    		
    	}
    	return res;
    }
    
    public static boolean isDtmf(RTPFormat format) {
        return (format != null) && dtmf.getFormat().getName().equals(format.getFormat().getName());
    }

    public static boolean isDefaultDtmf(RTPFormat format) {
        return (format != null) && (dtmf.getID() == format.getID());
    }
}
