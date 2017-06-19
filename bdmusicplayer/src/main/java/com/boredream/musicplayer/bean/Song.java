package com.boredream.musicplayer.bean;

import java.io.Serializable;

public interface Song extends Serializable {

    String getDisplayName();

    String getArtist();

    String getAlbum();

    int getDuration();

    String getSongUrl();

}
