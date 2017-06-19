package com.boredream.musicplayerdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.boredream.musicplayer.player.PlaybackService;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_play_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<MusicBean> musics = new ArrayList<>();
                musics.addAll(MockUtils.mockList(MusicBean.class));
                MusicPlayerActivity.startActivity(MainActivity.this, musics);
            }
        });

        initMusic();
    }

    private Intent service;

    private void initMusic() {
        service = new Intent(this, PlaybackService.class);
        startService(service);
    }

    @Override
    protected void onDestroy() {
        if (service != null) {
            stopService(service);
        }
        super.onDestroy();
    }
}
