/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file ServiceStartReq.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.messages;

import com.google.gson.annotations.SerializedName;
import media.platform.amf.session.MediaFileInfo;

public class MediaPlayReq {
    private int dir;
    @SerializedName("channel_id")
    private int mentOrMusic;
    @SerializedName("media_type")
    private int mediaType;
    @SerializedName("def_vol")
    private int defVolume;
    @SerializedName("min_vol")
    private int minVolume;
    @SerializedName("media_uri")
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

    public MediaFileInfo getMediaFileInfo() {
        MediaFileInfo mediaFileInfo = new MediaFileInfo();

        mediaFileInfo.setDir(dir);
        mediaFileInfo.setMentOrMusic(mentOrMusic);
        mediaFileInfo.setMediaType(mediaType);
        mediaFileInfo.setDefVolume(defVolume);
        mediaFileInfo.setMinVolume(minVolume);
        mediaFileInfo.setMediaUrl(mediaUrl);

        return mediaFileInfo;
    }
}
