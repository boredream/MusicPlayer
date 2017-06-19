package com.boredream.musicplayerdemo;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.boredream.musicplayer.bean.PlayList;
import com.boredream.musicplayer.event.PlayEvent;

import org.greenrobot.eventbus.EventBus;

public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.ViewHolder> {

    private Context context;
    private PlayList<MusicBean> playList;

    public MusicListAdapter(Context context, PlayList<MusicBean> playList) {
        this.context = context;
        this.playList = playList;
    }

    @Override
    public int getItemCount() {
        return playList.getItemCount();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_name;
        public ImageView iv_is_playing;

        public ViewHolder(final View itemView) {
            super(itemView);

            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            iv_is_playing = (ImageView) itemView.findViewById(R.id.iv_is_playing);
        }
    }

    @Override
    public MusicListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_music_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MusicListAdapter.ViewHolder holder, final int position) {
        MusicBean MusicBean = playList.getSongs().get(position);

        holder.tv_name.setText((position + 1) + ".  " + MusicBean.getDisplayName());
        holder.tv_name.setTextColor(playList.getPlayingIndex() == position ? 0xFF50E3C2 : Color.WHITE);

        holder.iv_is_playing.setVisibility(playList.getPlayingIndex() == position ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playList.getPlayingIndex() != position) {
                    playList.setPlayingIndex(position);

                    PlayEvent<MusicBean> event = new PlayEvent<>(PlayEvent.EVENT_PLAY_LIST);
                    event.setPlayList(playList);
                    EventBus.getDefault().post(event);
                }
            }
        });
    }
}
