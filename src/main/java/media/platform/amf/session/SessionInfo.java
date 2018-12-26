package media.platform.amf.session;

import io.netty.channel.Channel;
import media.platform.amf.core.sdp.SdpInfo;
import media.platform.amf.core.socket.JitterSender;
import media.platform.amf.rmqif.messages.FileData;
import media.platform.amf.rtpcore.Process.UdpClient;
import media.platform.amf.rtpcore.core.rtp.rtp.RtpPacket;
import media.platform.amf.service.AudioFileReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SessionInfo {

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

    private int enginePort;

    private String dstIpAddress;

    private boolean isCaller;
    private String fromNo;
    private String toNo;
    private String aiifName;

    private String fromQueue;
    private transient FileData fileData;

    private transient boolean isBgmPlaying;
    private transient boolean isMentPlaying;
    private transient String bgmFilename;
    private transient String mentFilename;

    private transient int volumeMin;
    private transient int volumeMax;

    public transient Channel rtpChannel;
    public transient Channel udpChannel;

    public transient UdpClient rtpClient;
    public transient UdpClient udpClient;

    private transient RtpPacket rtpPacket;
    private transient byte[] lastPacket;
    private transient int lastSeqNo;

    private transient AudioFileReader fileReader;
    private transient JitterSender udpSender;
    private transient JitterSender rtpSender;

    private transient List<String> playIds;

    private int engineToolId;
    private int mixerToolId;

    public SessionInfo() {
        payload2833 = -1;
        lastDtmf = -1;

        audioCreated = false;
        syncObj = new Object();
    }

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
        synchronized (this) {
            this.t4Time = t4Time;
        }
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

    public int getEnginePort() {
        return enginePort;
    }

    public void setEnginePort(int enginePort) {
        this.enginePort = enginePort;
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

    public JitterSender getUdpSender() {
        return udpSender;
    }

    public void setUdpSender(JitterSender udpSender) {
        this.udpSender = udpSender;
    }

    public UdpClient getRtpClient() {
        return rtpClient;
    }

    public UdpClient getUdpClient() {
        return udpClient;
    }

    public void setUdpClient(UdpClient udpClient) {
        this.udpClient = udpClient;
    }

    public JitterSender getRtpSender() {
        return rtpSender;
    }

    public void setRtpSender(JitterSender rtpSender) {
        this.rtpSender = rtpSender;
    }


    public byte[] getLastPacket() {
        byte[] packet = (lastPacket != null ) ? Arrays.copyOf(lastPacket, lastPacket.length) : null;
        return packet;
    }

    public void setLastPacket(byte[] srcPacket) {
        if (srcPacket == null) {
            return;
        }

        if (lastPacket == null ||
                (lastPacket.length != srcPacket.length)) {
            lastPacket = new byte[srcPacket.length];
        }

        System.arraycopy(srcPacket, 0, lastPacket, 0, srcPacket.length);
    }

    public int getLastSeqNo() {
        return lastSeqNo;
    }

    public void setLastSeqNo(int lastSeqNo) {
        this.lastSeqNo = lastSeqNo;
    }


    public int getEngineToolId() {
        return engineToolId;
    }

    public void setEngineToolId(int engineToolId) {
        this.engineToolId = engineToolId;
    }

    public int getMixerToolId() {
        return mixerToolId;
    }

    public void setMixerToolId(int mixerToolId) {
        this.mixerToolId = mixerToolId;
    }

    private transient int payload2833;

    private int get2833PayloadType() {
        SdpInfo tSdpInfo = null;
        if (sdpDeviceInfo != null) {
            tSdpInfo = sdpDeviceInfo;
        }
        else if (sdpInfo != null) {
            tSdpInfo = sdpInfo;
        }

        return (tSdpInfo != null) ? tSdpInfo.getPayload2833() : 0;
    }

    public int getPayload2833() {
        if (payload2833 < 0) {
            payload2833 = get2833PayloadType();
        }
        return payload2833;
    }

    public void setPayload2833(int payload2833) {
        this.payload2833 = payload2833;
    }

    private transient int lastDtmf;
    private transient boolean lastDtmfEnd;

    public int getLastDtmf() {
        return lastDtmf;
    }

    public void setLastDtmf(int lastDtmf) {
        this.lastDtmf = lastDtmf;
    }

    public boolean isLastDtmfEnd() {
        return lastDtmfEnd;
    }

    public void setLastDtmfEnd(boolean lastDtmfEnd) {
        this.lastDtmfEnd = lastDtmfEnd;
    }

    private String remoteRmqName;

    public String getRemoteRmqName() {
        return remoteRmqName;
    }

    public void setRemoteRmqName(String remoteRmqName) {
        this.remoteRmqName = remoteRmqName;
    }

    private transient Object syncObj;
    private transient boolean isSyncWait;
    private boolean audioCreated;

    public synchronized boolean isAudioCreated() {
        return audioCreated;
    }

    public synchronized void setAudioCreated(boolean audioCreated) {
        this.audioCreated = audioCreated;

        if (isSyncWait) {
            synchronized (syncObj) {
                syncObj.notify();
            }
        }

        isSyncWait = false;
    }

    public boolean waitAudioCreated(int millisec) {
        boolean result = false;

        synchronized (syncObj) {
            isSyncWait = true;

            try {
                if (millisec > 0) {
                    syncObj.wait(millisec);
                }
                else {
                    syncObj.wait();
                }

                result = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public Channel getRtpChannel() {
        return rtpChannel;
    }

    public void setRtpChannel(Channel rtpChannel) {
        this.rtpChannel = rtpChannel;
    }

    public Channel getUdpChannel() {
        return udpChannel;
    }

    public void setUdpChannel(Channel udpChannel) {
        this.udpChannel = udpChannel;
    }

    // 20181204 Add
    private int outbound;

    public int getOutbound() {
        return outbound;
    }

    public void setOutbound(int outbound) {
        this.outbound = outbound;
    }
}
