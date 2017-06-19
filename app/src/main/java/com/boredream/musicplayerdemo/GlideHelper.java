package com.boredream.musicplayerdemo;


import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class GlideHelper {

    public static void showOvalImage(Context context, String url, ImageView iv) {
        Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(new GlideCircleTransform(context))
                .placeholder(R.drawable.default_oval_image)
                .error(R.drawable.default_oval_image)
                .crossFade()
                .into(iv);
    }

    public static void showAvatar(Context context, String avatar, ImageView iv) {
        showOvalImage(context, avatar, iv);
    }

    public static void showImage(Context context, String imageUrl, ImageView iv) {
        Glide.with(context)
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.default_image)
                .error(R.drawable.default_image)
                .into(iv);
    }

}
