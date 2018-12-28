package media.platform.amf.session;

public class MediaFileInfo {

    private int dir;
    private int mentOrMusic;
    private int mediaType;
    private int defVolume;
    private int minVolume;
    private String mediaUrl;

    public int getDir() {
        return dir;
    }

    public void setDir(int dir) {
        this.dir = dir;
    }

    public int getMentOrMusic() {
        return mentOrMusic;
    }

    public void setMentOrMusic(int mentOrMusic) {
        this.mentOrMusic = mentOrMusic;
    }

    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    public int getDefVolume() {
        return defVolume;
    }

    public void setDefVolume(int defVolume) {
        this.defVolume = defVolume;
    }

    public int getMinVolume() {
        return minVolume;
    }

    public void setMinVolume(int minVolume) {
        this.minVolume = minVolume;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }
}
