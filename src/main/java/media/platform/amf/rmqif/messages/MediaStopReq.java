/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file MediaStopReq.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.messages;

import com.google.gson.annotations.SerializedName;

public class MediaStopReq {
    public static final int MEDIA_MENT = 1;
    public static final int MEDIA_MUSIC = 2;

    @SerializedName("channel_id")
    private int mentOrMusic;

    public int getMentOrMusic() {
        return mentOrMusic;
    }

    public void setMentOrMusic(int mentOrMusic) {
        this.mentOrMusic = mentOrMusic;
    }

}
