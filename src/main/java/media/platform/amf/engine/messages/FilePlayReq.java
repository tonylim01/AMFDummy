package media.platform.amf.engine.messages;

import media.platform.amf.engine.messages.common.FileInfos;
import media.platform.amf.engine.types.EngineRequestHeader;
import media.platform.amf.engine.messages.common.SendVocoderInfo;

public class FilePlayReq {

    private int id;
    private FileInfos file;
    private SendVocoderInfo audio;
    private SendVocoderInfo video;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}
