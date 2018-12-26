package media.platform.amf.engine.messages;

import media.platform.amf.engine.messages.common.FileInfos;
import media.platform.amf.engine.messages.common.VolumeInfo;
import media.platform.amf.engine.types.EngineRequestHeader;
import media.platform.amf.engine.messages.common.SendVocoderInfo;

import java.util.Arrays;

public class FilePlayReq {

    private int id;
    private Integer type;
    private int[] dstIds;
    private FileInfos file;
    private SendVocoderInfo audio;
    private SendVocoderInfo video;
    private VolumeInfo volume;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int[] getDstIds() {
        return (dstIds != null) ? Arrays.copyOf(dstIds, dstIds.length) : null;
    }

    public void setDstIds(int[] dstIds) {
        this.dstIds = (dstIds != null) ? Arrays.copyOf(dstIds, dstIds.length) : null;
    }

    public FileInfos getFile() {
        return file;
    }

    public void setFile(FileInfos file) {
        this.file = file;
    }

    public SendVocoderInfo getAudio() {
        return audio;
    }

    public void setAudio(SendVocoderInfo audio) {
        this.audio = audio;
    }

    public SendVocoderInfo getVideo() {
        return video;
    }

    public void setVideo(SendVocoderInfo video) {
        this.video = video;
    }

    public VolumeInfo getVolume() {
        return volume;
    }

    public void setVolume(VolumeInfo volume) {
        this.volume = volume;
    }
}
