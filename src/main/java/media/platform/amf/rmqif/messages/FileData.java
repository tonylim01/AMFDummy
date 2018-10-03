/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file FileData.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.messages;

import com.google.gson.annotations.SerializedName;

public class FileData {
    public static final int CHANNEL_BGM = 1;
    public static final int CHANNEL_MENT = 2;

    public static final String PLAY_TYPE_CALLER_ONLY = "Caller_Only";
    public static final String PLAY_TYPE_BOTH = "both";

    public static final String MEDIA_TYPE_FILE = "file";
    public static final String MEDIA_TYPE_STREAM = "HLS";

    private Integer channel;
    @SerializedName("media_type")
    private String mediaType;
    @SerializedName("play_file")
    private String playFile;
    @SerializedName("Def_volume")
    private int defVolume;
    @SerializedName("Mix_volume")
    private int mixVolume;
    @SerializedName("Play_Type")
    private String playType;    // "Caller_Only" or "both"

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getPlayFile() {
        return playFile;
    }

    public void setPlayFile(String playFile) {
        this.playFile = playFile;
    }
    public int getDefVolume() {
        return defVolume;
    }

    public int getMixVolume() {
        return mixVolume;
    }

    public String getPlayType() {
        return playType;
    }

}
