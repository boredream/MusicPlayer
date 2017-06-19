package com.boredream.musicplayerdemo;

import com.boredream.musicplayer.bean.Song;

public class MusicBean implements Song {

    public String title;
    public String coverImgUrl;
    public String musicUrl;

    @Override
    public String getDisplayName() {
        return title;
    }

    @Override
    public String getArtist() {
        return "";
    }

    @Override
    public String getAlbum() {
        return coverImgUrl;
    }

    @Override
    public int getDuration() {
        // TODO: 2017/6/19 need?
        return 0;
    }

    @Override
    public String getSongUrl() {
        return musicUrl;
    }
}
