package com.yibao.music.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.yibao.music.R;
import com.yibao.music.view.ZoomImageView;

/**
 * 作者：Stran on 2017/3/23 03:23
 * 描述：${图片加载 }
 * 邮箱：strangermy@outlook.com
 *
 * @author Stran
 */
@SuppressLint("CheckResult")
public class ImageUitl {

    public static ZoomImageView creatZoomView(Context context) {
        ZoomImageView view = new ZoomImageView(context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(1080, 1920);
        view.setScaleType(ImageView.ScaleType.MATRIX);
        view.reSetState();
        view.setLayoutParams(params);
        return view;
    }

    public static ZoomImageView creatZoomViewTop(Context context) {

        return new ZoomImageView(context);
    }

    // 加载图片
    public static void loadPic(Activity activity, String url, ImageView view, RequestListener listener) {
        if (!activity.isDestroyed()) {
            RequestOptions options = new RequestOptions();
            options.diskCacheStrategy(DiskCacheStrategy.NONE);
            Glide.with(activity).load(url).listener(listener).apply(options).into(view);
        } else {
            LogUtil.d("Picture loading failed,context is null");
        }

    }

    public static void loadPlaceholder(Activity activity, String url, ImageView view) {
        if (!activity.isDestroyed()) {
            RequestOptions options = new RequestOptions();
            options.placeholder(R.drawable.nina);
            options.error(R.drawable.nina);
            options.diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(activity).load(url).apply(options).into(view);
        } else {
            LogUtil.d("Picture loading failed,context is null");
        }
    }

    public static void loadPlaceholder(Context context, String url, ImageView view) {
        if (context != null) {
            RequestOptions options = new RequestOptions();
            options.placeholder(R.drawable.nina);
            options.error(R.drawable.nina);
            options.diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(context).load(url).apply(options).into(view);
        } else {
            LogUtil.d("Picture loading failed,context is null");
        }
    }

    public static void customLoadPic(Activity activity, String url, int placeId, ImageView view) {
        if (!activity.isDestroyed()) {
            RequestOptions options = new RequestOptions();
            options.placeholder(placeId);
            options.error(placeId);
            options.diskCacheStrategy(DiskCacheStrategy.NONE);
            Glide.with(activity).load(url)
                    .apply(options)
                    .into(view);
        } else {
            LogUtil.d("Picture loading failed,context is null");
        }


    }

    public static void customLoadPic(Context context, String url, int placeId, ImageView view) {
        if (context != null) {
            RequestOptions options = new RequestOptions();
            options.placeholder(placeId);
            options.error(placeId);
            options.diskCacheStrategy(DiskCacheStrategy.NONE);
            Glide.with(context).load(url)
                    .apply(options)
                    .into(view);
        } else {
            LogUtil.d("Picture loading failed,context is null");
        }


    }


}







