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
        int[] ret = null;
        if (srcIds != null) {
            ret = new int[srcIds.length];
            System.arraycopy(srcIds, 0, ret, 0, srcIds.length);
        }
        return ret;
    }

    public void setSrcIds(int[] srcIds) {
        if (srcIds != null) {
            this.srcIds = new int[srcIds.length];
            System.arraycopy(srcIds, 0, this.srcIds, 0, srcIds.length);
        }
    }

    public Integer[] getWhisperTo() {
        Integer[] ret = null;
        if (whisperTo != null) {
            ret = new Integer[whisperTo.length];
            System.arraycopy(whisperTo, 0, ret, 0, whisperTo.length);
        }
        return ret;
    }

    public void setWhisperTo(Integer[] whisperTo) {
        if (whisperTo != null) {
            this.whisperTo = new Integer[whisperTo.length];
            System.arraycopy(whisperTo, 0, this.whisperTo, 0, whisperTo.length);
        }
    }
}
