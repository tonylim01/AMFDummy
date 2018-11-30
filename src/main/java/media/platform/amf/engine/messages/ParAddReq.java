package media.platform.amf.engine.messages;

public class ParAddReq {

    private int id;
    private int[] srcIds;
    private Integer[] whisperTo;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int[] getSrcIds() {
        return srcIds;
    }

    public void setSrcIds(int[] srcIds) {
        this.srcIds = srcIds;
    }

    public Integer[] getWhisperTo() {
        return whisperTo;
    }

    public void setWhisperTo(Integer[] whisperTo) {
        this.whisperTo = whisperTo;
    }
}
