package com.yibao.music.util;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yibao.biggirl.R;
import com.yibao.music.view.ZoomImageView;

/**
 * 作者：Stran on 2017/3/23 03:23
 * 描述：${保存妹子}
 * 邮箱：strangermy@outlook.com
 *
 * @author Stran
 */
public class ImageUitl {

    public static ZoomImageView creatZoomView(Context context) {
        ZoomImageView view = new ZoomImageView(context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(1080, 1920);
        view.setScaleType(ImageView.ScaleType.MATRIX);
        view.reSetState();
        view.setLayoutParams(params);
        return view;
    }

    //加载图片
    public static void loadPic(Context context, String url, ImageView view) {
        Glide.with(context)
                .load(url)
                .asBitmap()
                .error(R.mipmap.xuan)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(view);

    }


}







