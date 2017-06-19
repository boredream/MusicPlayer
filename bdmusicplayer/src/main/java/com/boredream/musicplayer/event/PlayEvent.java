package com.boredream.musicplayer.event;

import com.boredream.musicplayer.bean.PlayList;
import com.boredream.musicplayer.bean.Song;
import com.boredream.musicplayer.player.PlayMode;

public class PlayEvent<T extends Song> {

    public static final int EVENT_PLAY_LIST = 0x0001;
    public static final int EVENT_PLAY_TOGGLE = 0x0002;
    public static final int EVENT_PLAY_PLAY = 0x0003;
    public static final int EVENT_PLAY_PAUSE = 0x0004;
    public static final int EVENT_PLAY_NEXT = 0x0005;
    public static final int EVENT_PLAY_LAST = 0x0006;
    public static final int EVENT_PLAY_STOP = 0x0007;
    public static final int EVENT_PLAY_RESTART = 0x0008;

    public static final int EVENT_SET_PLAY_MODE = 0x0011;

    public PlayEvent(int eventType) {
        this.eventType = eventType;
    }

    public int eventType;

    private PlayList<T> playList;

    private PlayMode playMode;

    public PlayList<T> getPlayList() {
        return playList;
    }

    public void setPlayList(PlayList<T> playList) {
        this.playList = playList;
    }

    public void setPlayMode(PlayMode playMode) {
        this.playMode = playMode;
    }

    public PlayMode getPlayMode() {
        return playMode;
    }

    @Override
    public String toString() {
        switch (eventType) {
            case EVENT_PLAY_TOGGLE:
                return "PlayEvent:播放切换";
            case EVENT_PLAY_PLAY:
                return "PlayEvent:播放";
            case EVENT_PLAY_PAUSE:
                return "PlayEvent:暂停";
            case EVENT_PLAY_NEXT:
                return "PlayEvent:下一首";
            case EVENT_PLAY_LAST:
                return "PlayEvent:上一首";
            case EVENT_PLAY_STOP:
                return "PlayEvent:停止";
            case EVENT_PLAY_RESTART:
                return "PlayEvent:重新开始";
            case EVENT_PLAY_LIST:
                return "PlayEvent:播放列表";
            case EVENT_SET_PLAY_MODE:
                return "PlayEvent:设置播放模式 ... " + playMode;
        }
        return "eventType " + eventType;
    }
}
