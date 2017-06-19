package com.boredream.musicplayer.event;

import com.boredream.musicplayer.bean.PlayList;
import com.boredream.musicplayer.bean.Song;
import com.boredream.musicplayer.player.PlayMode;

public class PlayCallbackEvent<T extends Song> {

    public static final int EVENT_COMPLETE = 0x0031;
    public static final int EVENT_SONG_CHANGED = 0x0032;
    public static final int EVENT_PLAY_STATUS_CHANGED = 0x0033;
    public static final int EVENT_PLAY_MODE_CHANGED = 0x0034;

    public PlayCallbackEvent(int eventType) {
        this.eventType = eventType;
    }

    public int eventType;

    private boolean isPlaying;

    private PlayMode playMode;

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public PlayMode getPlayMode() {
        return playMode;
    }

    public void setPlayMode(PlayMode playMode) {
        this.playMode = playMode;
    }

    private PlayList<T> playList;

    public PlayList<T> getPlayList() {
        return playList;
    }

    public void setPlayList(PlayList<T> playList) {
        this.playList = playList;
    }

    @Override
    public String toString() {
        switch (eventType) {
            case EVENT_COMPLETE:
                return "PlayCallbackEvent:播放完 ... " + playList.getPlayingIndex();
            case EVENT_SONG_CHANGED:
                return "PlayCallbackEvent:切换歌曲 ... " + playList.getPlayingIndex();
            case EVENT_PLAY_STATUS_CHANGED:
                return "PlayCallbackEvent:播放状态变化 ... " + isPlaying;
            case EVENT_PLAY_MODE_CHANGED:
                return "PlayCallbackEvent:播放模式变化 ... " + getPlayMode();
        }
        return "eventType " + eventType;
    }
}
