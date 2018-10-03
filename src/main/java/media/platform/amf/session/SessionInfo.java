package media.platform.amf.session;

import io.netty.channel.Channel;
import io.netty.channel.socket.DatagramChannel;
import media.platform.amf.core.sdp.SdpInfo;
import media.platform.amf.core.socket.JitterSender;
import media.platform.amf.rmqif.messages.FileData;
import media.platform.amf.rtpcore.Process.UdpClient;
import media.platform.amf.rtpcore.core.rtp.rtp.RtpPacket;
import media.platform.amf.service.AudioFileReader;

import java.util.ArrayList;
import java.util.List;

public class SessionInfo {

    public Channel channel;
    public UdpClient udpClient;
    private String sessionId;
    private long createdTime;

    private SessionState serviceState;
    private long lastSentTime;
    private long t2Time;
    private long t4Time;

    private String conferenceId;
    private SdpInfo sdpInfo;
    private SdpInfo sdpDeviceInfo;

    private String localIpAddress;
    private int srcLocalPort;
    private int dstLocalPort;

    private String dstIpAddress;

    private boolean isCaller;
    private String fromNo;
    private String toNo;
    private String aiifName;

    private boolean isBgmPlaying;
    private boolean isMentPlaying;
    private String bgmFilename;
    private String mentFilename;

    private FileData fileData;
    private String fromQueue;

    private int volumeMin;
    private int volumeMax;

    private RtpPacket rtpPacket;
    private AudioFileReader fileReader;
    private JitterSender jitterSender;

    private List<String> playIds;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public SessionState getServiceState() {
        return serviceState;
    }

    public void setServiceState(SessionState serviceState) {
        synchronized (this) {
            this.serviceState = serviceState;
            this.lastSentTime = 0;
            this.t2Time = 0;
            this.t4Time = 0;
        }
    }

    public long getLastSentTime() {
        synchronized (this) {
            return lastSentTime;
        }
    }

    public void setLastSentTime(long lastSentTime) {
        synchronized (this) {
            this.lastSentTime = lastSentTime;
        }
    }

    public void setLastSentTime() {
        setLastSentTime(System.currentTimeMillis());
    }

    public long getT2Time() {
        synchronized (this) {
            return t2Time;
        }
    }

    public void setT2Time(long t2Time) {
        synchronized (this) {
            this.t2Time = t2Time;
        }
    }

    public long getT4Time() {
        synchronized (this) {
            return t4Time;
        }
    }

    public void setT4Time(long t4Time) {
        this.t4Time = t4Time;
    }

    public void updateT2Time(long t2interval) {
        synchronized (this) {
            this.t2Time = System.currentTimeMillis() + t2interval;
        }
    }

    public void updateT4Time(long t4interval) {
        synchronized (this) {
            this.t4Time = System.currentTimeMillis() + t4interval;
        }
    }

    public String getConferenceId() {
        return conferenceId;
    }

    public void setConferenceId(String conferenceId) {
        this.conferenceId = conferenceId;
    }

    public SdpInfo getSdpInfo() {
        return sdpInfo;
    }

    public void setSdpInfo(SdpInfo sdpInfo) {
        this.sdpInfo = sdpInfo;
    }

    public SdpInfo getSdpDeviceInfo() {
        return sdpDeviceInfo;
    }

    public void setSdpDeviceInfo(SdpInfo sdpDeviceInfo) {
        this.sdpDeviceInfo = sdpDeviceInfo;
    }

    public String getLocalIpAddress() {
        return localIpAddress;
    }

    public void setLocalIpAddress(String localIpAddress) {
        this.localIpAddress = localIpAddress;
    }

    public String getDstIpAddress() {
        return dstIpAddress;
    }

    public void setDstIpAddress(String dstIpAddress) {
        this.dstIpAddress = dstIpAddress;
    }

    public String getFromNo() {
        return fromNo;
    }

    public void setFromNo(String fromNo) {
        this.fromNo = fromNo;
    }

    public String getToNo() {
        return toNo;
    }

    public void setToNo(String toNo) {
        this.toNo = toNo;
    }

    public boolean isCaller() {
        return isCaller;
    }

    public void setCaller(boolean caller) {
        isCaller = caller;
    }

    public int getSrcLocalPort() {
        return srcLocalPort;
    }

    public void setSrcLocalPort(int srcLocalPort) {
        this.srcLocalPort = srcLocalPort;
    }

    public int getDstLocalPort() {
        return dstLocalPort;
    }

    public void setDstLocalPort(int dstLocalPort) {
        this.dstLocalPort = dstLocalPort;
    }

    public String getAiifName() {
        return aiifName;
    }

    public void setAiifName(String aiifName) {
        this.aiifName = aiifName;
    }

    public boolean isBgmPlaying() {
        return isBgmPlaying;
    }

    public void setBgmPlaying(boolean bgmPlaying) {
        isBgmPlaying = bgmPlaying;
    }

    public String getBgmFilename() {
        return bgmFilename;
    }

    public void setBgmFilename(String bgmFilename) {
        this.bgmFilename = bgmFilename;
    }

    public String getMentFilename() {
        return mentFilename;
    }

    public void setMentFilename(String mentFilename) {
        this.mentFilename = mentFilename;
    }

    public boolean isMentPlaying() {
        return isMentPlaying;
    }

    public void setMentPlaying(boolean mentPlaying) {
        isMentPlaying = mentPlaying;
    }

    public FileData getFileData() {
        return fileData;
    }

    public void setFileData(FileData fileData) {
        this.fileData = fileData;
    }

    public String getFromQueue() {
        return fromQueue;
    }

    public void setFromQueue(String fromQueue) {
        this.fromQueue = fromQueue;
    }

    public int getVolumeMin() {
        return volumeMin;
    }

    public void setVolumeMin(int volumeMin) {
        this.volumeMin = volumeMin;
    }

    public int getVolumeMax() {
        return volumeMax;
    }

    public void setVolumeMax(int volumeMax) {
        this.volumeMax = volumeMax;
    }

    public void putPlayId(String playId) {
        if (playIds == null) {
            playIds = new ArrayList<>();
        }

        playIds.add(playId);
    }

    public List<String> getPlayIds() {
        return playIds;
    }

    public void removePlayId(String playId) {
        if (playIds == null || playId == null) {
            return;
        }

        if (playIds.contains(playId)) {
            playIds.remove(playId);
        }
    }

    public RtpPacket getRtpPacket() {
        return rtpPacket;
    }

    public void setRtpPacket(RtpPacket rtpPacket) {
        this.rtpPacket = rtpPacket;
    }

    public AudioFileReader getFileReader() {
        return fileReader;
    }

    public void setFileReader(AudioFileReader fileReader) {
        this.fileReader = fileReader;
    }

    public JitterSender getJitterSender() {
        return jitterSender;
    }

    public void setJitterSender(JitterSender jitterSender) {
        this.jitterSender = jitterSender;
    }
}
