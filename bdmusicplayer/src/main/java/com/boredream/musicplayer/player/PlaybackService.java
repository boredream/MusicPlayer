package com.boredream.musicplayer.player;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.boredream.musicplayer.bean.PlayList;
import com.boredream.musicplayer.bean.Song;
import com.boredream.musicplayer.event.PlayCallbackEvent;
import com.boredream.musicplayer.event.PlayEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class PlaybackService<T extends Song> extends Service {

    private MusicPlayer<T> mPlayer;

    @SuppressWarnings(value = {"unchecked"})
    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer = MusicPlayer.getInstance();
        mPlayer.registerCallback(callback);

        EventBus.getDefault().register(this);
        Log.i("DDD", "onCreate: PlaybackService");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlayEvent(PlayEvent musicEvent) {
        switch (musicEvent.eventType) {
            case PlayEvent.EVENT_PLAY_LIST:
                mPlayer.play(musicEvent.getPlayList());
                break;
            case PlayEvent.EVENT_PLAY_TOGGLE:
                if (mPlayer.isPlaying()) {
                    mPlayer.pause();
                } else {
                    mPlayer.play();
                }
                break;
            case PlayEvent.EVENT_PLAY_NEXT:
                mPlayer.playNext();
                break;
            case PlayEvent.EVENT_PLAY_LAST:
                mPlayer.playLast();
                break;
            case PlayEvent.EVENT_PLAY_STOP:
                mPlayer.stop();
                break;
            case PlayEvent.EVENT_PLAY_RESTART:
                mPlayer.replayList();
                break;
            case PlayEvent.EVENT_SET_PLAY_MODE:
                mPlayer.setPlayMode(musicEvent.getPlayMode());
                break;
        }
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        callback = null;
        mPlayer.releasePlayer();
        EventBus.getDefault().unregister(this);
        super.onDestroy();

        Log.i("DDD", "onDestroy: PlaybackService");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private IPlayCallback<T> callback = new IPlayCallback<T>() {

        @Override
        public void onSwitchSong(PlayList<T> playList) {
            PlayCallbackEvent<T> event = new PlayCallbackEvent<>(PlayCallbackEvent.EVENT_SONG_CHANGED);
            event.setPlayList(playList);
            EventBus.getDefault().post(event);
            Log.i("DDD", "onSwitchSong: " + event);
        }

        @Override
        public void onComplete(PlayList<T> playList) {
            if (playList == null) {
                return;
            }

            PlayCallbackEvent<T> event = new PlayCallbackEvent<>(PlayCallbackEvent.EVENT_COMPLETE);
            event.setPlayList(playList);
            EventBus.getDefault().post(event);
            Log.i("DDD", "onComplete: " + event);
        }

        @Override
        public void onPlayStatusChanged(boolean isPlaying) {
            PlayCallbackEvent<T> event = new PlayCallbackEvent<>(PlayCallbackEvent.EVENT_PLAY_STATUS_CHANGED);
            event.setPlaying(isPlaying);
            EventBus.getDefault().post(event);
            Log.i("DDD", "onPlayStatusChanged: " + event);
        }

        @Override
        public void onPlayModeChanged(PlayMode playMode) {
            PlayCallbackEvent<T> event = new PlayCallbackEvent<>(PlayCallbackEvent.EVENT_PLAY_MODE_CHANGED);
            event.setPlayMode(playMode);
            EventBus.getDefault().post(event);
            Log.i("DDD", "onPlayModeChanged: " + event);
        }
    };
}
