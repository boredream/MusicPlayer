package com.boredream.musicplayer.player;


import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.SystemClock;
import android.util.Log;

import com.boredream.musicplayer.bean.PlayList;
import com.boredream.musicplayer.bean.Song;

import java.io.IOException;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/5/16
 * Time: 5:57 PM
 * Desc: Player
 */
public class MusicPlayer<T extends Song> implements
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener {

    private static final String TAG = "Player";

    private static volatile MusicPlayer sInstance;

    private MediaPlayer mPlayer;

    private PlayList<T> mPlayList;

    private IPlayCallback<T> callback;

    public void registerCallback(IPlayCallback<T> callback) {
        this.callback = callback;
    }

    // Player status
    private boolean isPaused;
    private boolean isStopped;

    private MusicPlayer() {
        mPlayer = new MediaPlayer();
        mPlayList = new PlayList<>();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnErrorListener(this);
    }

    public static MusicPlayer getInstance() {
        if (sInstance == null) {
            synchronized (MusicPlayer.class) {
                if (sInstance == null) {
                    sInstance = new MusicPlayer();
                }
            }
        }
        return sInstance;
    }

    public void setPlayList(PlayList<T> list) {
        if (list == null) {
            list = new PlayList<>();
        }
        mPlayList = list;
    }

    public boolean play(PlayList<T> list) {
        if (list == null) return false;

        isPaused = false;
        setPlayList(list);
        return play();
    }

    public boolean play() {
        if (isPaused) {
            mPlayer.start();
            if(callback != null) {
                callback.onPlayStatusChanged(true);
            }
            isStopped = false;
            isPaused = false;
            return true;
        }
        if (mPlayList.prepare() || isStopped) {
            if(isStopped) {
                mPlayList.setPlayingIndex(0);
            }
            T song = mPlayList.getCurrentSong();
            try {
                mPlayer.reset();
                mPlayer.setDataSource(song.getSongUrl());
                mPlayer.prepareAsync();

                if(callback != null) {
                    callback.onSwitchSong(mPlayList);
                    callback.onPlayStatusChanged(true);
                }
            } catch (IOException e) {
                Log.e(TAG, "play: ", e);

                // FIXME: 2017/2/21 部分手机会报错1,-107等。但是不影响播放音乐
//                if(callback != null) {
//                    callback.onSwitchSong(mPlayList);
//                    callback.onPlayStatusChanged(false);
//                }
                return false;
            }
            isStopped = false;
            return true;
        }
        return false;
    }


    public boolean replayList() {
        if (mPlayList == null) return false;

        isPaused = false;
        mPlayList.setPlayingIndex(0);
        return play();
    }

    public boolean playLast() {
        isPaused = false;
        boolean hasLast = mPlayList.hasLast();
        if (hasLast) {
            mPlayList.last();
            play();
            return true;
        }
        return false;
    }

    public boolean playNext() {
        isPaused = false;
        boolean hasNext = mPlayList.hasNext(true);
        if (hasNext) {
            mPlayList.next();
            play();
            return true;
        }
        return false;
    }

    public boolean pause() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            isPaused = true;
            if(callback != null) {
                callback.onPlayStatusChanged(false);
            }
            return true;
        }
        return false;
    }

    public void stop() {
        isPaused = false;
        isStopped = true;
        mPlayer.stop();
    }

    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    public void setPlayMode(PlayMode playMode) {
        mPlayList.setPlayMode(playMode);

        if(callback != null) {
            callback.onPlayModeChanged(mPlayList.getPlayMode());
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (!isPaused && !isStopped) mPlayer.start();
    }

    private long lastTime = -1;
    @Override
    public void onCompletion(MediaPlayer mp) {
        if(SystemClock.elapsedRealtime() - lastTime < 500) {
            // 异常结束 // FIXME: 2017/2/22 是不是有更好的方法
            return;
        }

        lastTime = SystemClock.elapsedRealtime();

        if (mPlayList.getPlayMode() == PlayMode.LIST && mPlayList.getPlayingIndex() == mPlayList.getNumOfSongs() - 1) {
            // 列表播完最后一首
            if(callback != null) {
                callback.onComplete(mPlayList);
            }
        } else if (mPlayList.getPlayMode() == PlayMode.SINGLE) {
            // 单曲循环播放完后继续播本首
            mPlayList.getCurrentSong();
            play();
        } else if (mPlayList.getPlayMode() == PlayMode.ONCE) {
            // 只一次播放完
            if(callback != null) {
                callback.onPlayStatusChanged(false);
            }
        } else {
            // 播放下一首
            playNext();
        }
    }

    public void releasePlayer() {
        mPlayList = null;
        mPlayer.reset();
        mPlayer.release();
        mPlayer = null;
        sInstance = null;
    }
}
