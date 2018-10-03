package media.platform.amf.rtpcore.core.rtp;

import media.platform.amf.rtpcore.core.network.deprecated.PortManager;
import media.platform.amf.rtpcore.core.network.deprecated.UdpManager;
import media.platform.amf.rtpcore.core.scheduler.Clock;
import media.platform.amf.rtpcore.core.scheduler.PriorityQueueScheduler;
import media.platform.amf.rtpcore.core.scheduler.WallClock;
import media.platform.amf.rtpcore.core.sdp.format.AVProfile;
import media.platform.amf.rtpcore.core.sdp.format.RTPFormats;

import java.util.concurrent.atomic.AtomicInteger;

public class ChannelsManager {
    //transport for RTP and RTCP
	private UdpManager udpManager;

    private Clock clock = new WallClock();

    private boolean isControlEnabled=false;

    private PriorityQueueScheduler scheduler;
    
    private int jitterBufferSize=50;
    
    //channel id generator
    private AtomicInteger channelIndex = new AtomicInteger(100);
    
    private RTPFormats codecs;

    public ChannelsManager(UdpManager udpManager, RTPFormats codecs) {
        this.udpManager = udpManager;
        this.codecs = codecs;
    }

    public ChannelsManager(UdpManager udpManager) {
        this( udpManager, AVProfile.audio);
    }

    /**
     * Gets list of supported codecs
     * 
     * @return The collection of supported codecs.
     */
    public RTPFormats getCodecs() {
        return codecs;
    }

    public String getBindAddress() {
        return udpManager.getBindAddress();
    }

    public String getLocalBindAddress() {
        return udpManager.getLocalBindAddress();
    }
    
    public String getExternalAddress() {
    	return udpManager.getExternalAddress();
    }
    
    public PortManager getPortManager() {
    	return udpManager.getPortManager();
    }

    public void setScheduler(PriorityQueueScheduler scheduler) {
        this.scheduler = scheduler;
    }

    public PriorityQueueScheduler getScheduler() {
        return this.scheduler;
    }
    
    public Clock getClock() {
        return clock;
    }

    public Boolean getIsControlEnabled() {
        return isControlEnabled;
    }
    
    public int getJitterBufferSize() {
    	return this.jitterBufferSize;
    }
    
    public void setJitterBufferSize(int jitterBufferSize) {
    	this.jitterBufferSize=jitterBufferSize;
    }        
    
    public UdpManager getUdpManager() {
    	return this.udpManager;
    }    
    
    @Deprecated
    public RTPDataChannel getChannel() {
        return new RTPDataChannel( this, channelIndex.incrementAndGet());
    }
    
//    public RtpChannel getRtpChannel(RtpStatistics statistics, RtpClock clock, RtpClock oobClock) {
//    	return new RtpChannel(channelIndex.incrementAndGet(), jitterBufferSize, statistics, clock, oobClock, scheduler, udpManager, dtlsServerProvider);
//    }
//
//    public RtcpChannel getRtcpChannel(RtpStatistics statistics) {
//        return new RtcpChannel(channelIndex.incrementAndGet(), statistics, udpManager, dtlsServerProvider);
//    }
//
//    public LocalDataChannel getLocalChannel() {
//        return new LocalDataChannel(this, channelIndex.incrementAndGet());
//    }
//
//    public AudioChannel getAudioChannel() {
//    	return new AudioChannel(this.scheduler.getClock(), this);
//    }
    
}
