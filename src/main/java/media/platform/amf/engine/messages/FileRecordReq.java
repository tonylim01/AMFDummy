package media.platform.amf.engine.messages;

import media.platform.amf.engine.messages.common.FileInfo;
import media.platform.amf.engine.messages.common.RecvVocoderInfo;
import media.platform.amf.engine.types.EngineRequestHeader;

public class FileRecordReq {

    private int id;
    private FileInfo file;
    private RecvVocoderInfo audio;
    private RecvVocoderInfo video;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public FileInfo getFile() {
        return file;
    }

    public void setFile(FileInfo file) {
        this.file = file;
    }

    public RecvVocoderInfo getAudio() {
        return audio;
    }

    public void setAudio(RecvVocoderInfo audio) {
        this.audio = audio;
    }

    public RecvVocoderInfo getVideo() {
        return video;
    }

    public void setVideo(RecvVocoderInfo video) {
        this.video = video;
    }
}
