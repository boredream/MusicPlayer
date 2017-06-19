package com.boredream.musicplayerdemo;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.boredream.musicplayer.bean.PlayList;
import com.boredream.musicplayer.event.PlayCallbackEvent;
import com.boredream.musicplayer.event.PlayEvent;
import com.boredream.musicplayer.player.PlayMode;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class MusicPlayerActivity extends Activity implements View.OnClickListener {

    private TextView titlebar_tv;
    private ImageView iv_pre;
    private ImageView iv_play;
    private ImageView iv_next;
    private ImageView iv_cover;
    private ImageView iv_cd_cover;
    private CheckBox cb_loop;
    private RecyclerView rv_music;

    private MusicListAdapter adapter;
    private PlayList<MusicBean> playList;
    private ObjectAnimator animator;
    private long animationTime;

    public static void startActivity(Context context, ArrayList<MusicBean> musics) {
        if (musics == null || musics.size() == 0) {
            return;
        }

        Intent intent = new Intent(context, MusicPlayerActivity.class);
        intent.putExtra("musics", musics);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_music_player);

        initExtras();
        initView();
        initData();
    }

    private void initExtras() {
        ArrayList<MusicBean> MusicBeans = (ArrayList<MusicBean>) getIntent().getSerializableExtra("musics");

        playList = new PlayList<>();
        playList.setPlayMode(PlayMode.LIST);
        playList.setNumOfSongs(MusicBeans.size());
        playList.setName("睡眠模式列表");
        playList.setPlayingIndex(0);
        playList.setSongs(MusicBeans);
    }

    private void initView() {
        findViewById(R.id.titlebar_iv_left).setOnClickListener(this);

        titlebar_tv = (TextView) findViewById(R.id.titlebar_tv);
        iv_pre = (ImageView) findViewById(R.id.iv_pre);
        iv_play = (ImageView) findViewById(R.id.iv_play);
        iv_next = (ImageView) findViewById(R.id.iv_next);
        iv_cover = (ImageView) findViewById(R.id.iv_cover);
        iv_cd_cover = (ImageView) findViewById(R.id.iv_cd_cover);
        cb_loop = (CheckBox) findViewById(R.id.cb_loop);
        rv_music = (RecyclerView) findViewById(R.id.rv_music);

        iv_pre.setOnClickListener(this);
        iv_play.setOnClickListener(this);
        iv_next.setOnClickListener(this);

        cb_loop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setLoopMode(isChecked);
            }
        });

        initAnim();

        startAnim();
    }

    private void initAnim() {
        animator = ObjectAnimator.ofFloat(iv_cd_cover, "rotation", 0f, 360f);
        animator.setDuration(20000);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setRepeatMode(ObjectAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
    }

    private void startAnim() {
        if (!animator.isRunning()) {
            animator.start();
            animator.setCurrentPlayTime(animationTime);
        }
    }

    private void cancelAnim() {
        if (animator.isRunning()) {
            animationTime = animator.getCurrentPlayTime();
            animator.cancel();
        }
    }

    private void initData() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rv_music.setLayoutManager(manager);

        adapter = new MusicListAdapter(this, playList);
        rv_music.setAdapter(adapter);

        setPlaylist(0);

        updatePlayingMusicBean();

        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlayEvent(PlayCallbackEvent playEvent) {
        // 这里只处理回调
        switch (playEvent.eventType) {
            case PlayCallbackEvent.EVENT_SONG_CHANGED:
                playList = playEvent.getPlayList();
                updatePlayingMusicBean();
                break;
            case PlayCallbackEvent.EVENT_PLAY_STATUS_CHANGED:
                if (playEvent.isPlaying()) {
                    start();
                } else {
                    pause();
                }
                break;
            case PlayCallbackEvent.EVENT_PLAY_MODE_CHANGED:
                PlayMode playMode = playEvent.getPlayMode();
                cb_loop.setChecked(playMode == PlayMode.LOOP);
                break;
        }
    }

    private void start() {
        startAnim();
        iv_play.setImageResource(R.drawable.list_pause);
    }

    private void pause() {
        cancelAnim();
        iv_play.setImageResource(R.drawable.list_play);
    }

    private void updatePlayingMusicBean() {
        if (this.playList == null) return;

        MusicBean music = playList.getCurrentSong();

        titlebar_tv.setText(music.getDisplayName());

        GlideHelper.showOvalImage(this, music.coverImgUrl, iv_cd_cover);
        GlideHelper.showImage(this, music.coverImgUrl, iv_cover);

        adapter.notifyDataSetChanged();
    }

    private void setPlaylist(int startIndex) {
        PlayEvent<MusicBean> event = new PlayEvent<>(PlayEvent.EVENT_PLAY_LIST);
        playList.setPlayingIndex(startIndex);
        event.setPlayList(playList);
        EventBus.getDefault().post(event);
    }

    private void playOrPause() {
        EventBus.getDefault().post(new PlayEvent(PlayEvent.EVENT_PLAY_TOGGLE));
    }

    private void playLast() {
        EventBus.getDefault().post(new PlayEvent(PlayEvent.EVENT_PLAY_LAST));
    }

    private void playNext() {
        EventBus.getDefault().post(new PlayEvent(PlayEvent.EVENT_PLAY_NEXT));
    }

    private void setLoopMode(boolean isLoop) {
        PlayEvent<MusicBean> event = new PlayEvent<>(PlayEvent.EVENT_SET_PLAY_MODE);
        event.setPlayMode(isLoop ? PlayMode.LOOP : PlayMode.LIST);
        EventBus.getDefault().post(event);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().post(new PlayEvent(PlayEvent.EVENT_PLAY_STOP));
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titlebar_iv_left:
                finish();
                break;
            case R.id.iv_play:
                playOrPause();
                break;
            case R.id.iv_pre:
                playLast();
                break;
            case R.id.iv_next:
                playNext();
                break;
        }
    }
}
