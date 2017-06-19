package com.boredream.musicplayer.player;

import com.boredream.musicplayer.bean.PlayList;
import com.boredream.musicplayer.bean.Song;

public interface IPlayCallback<T extends Song> {

    void onSwitchSong(PlayList<T> playList);

    void onComplete(PlayList<T> playList);

    void onPlayStatusChanged(boolean isPlaying);

    void onPlayModeChanged(PlayMode playMode);
}
