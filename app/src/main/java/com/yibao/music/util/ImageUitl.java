package com.yibao.music.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;
import com.yibao.music.MusicApplication;
import com.yibao.music.R;
import com.yibao.music.network.RetrofitHelper;
import com.yibao.music.view.ZoomImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 作者：Stran on 2017/3/23 03:23
 * 描述：${图片加载 }
 * 邮箱：strangermy@outlook.com
 *
 * @author Stran
 */
@SuppressLint("CheckResult")
public class ImageUitl {
    private static final String TAG = "====" + ImageUitl.class.getSimpleName() + "    ";

    public static ZoomImageView creatZoomView(Context context) {
        ZoomImageView view = new ZoomImageView(context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(1080, 1920);
        view.setScaleType(ImageView.ScaleType.MATRIX);
        view.reSetState();
        view.setLayoutParams(params);
        return view;
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

    public static void glideSaveImg(Context context, String url, String songName, String artist) {
        Observable.create((ObservableOnSubscribe<File>) e -> {
            //通过gilde下载得到file文件,这里需要注意android.permission.INTERNET权限
            e.onNext(Glide.with(context)
                    .load(url)
                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get());
            e.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(file -> {
                    //获取到下载得到的图片，进行本地保存
                    File songAlbumFile = new File(Constants.MUSIC_SONG_ALBUM_ROOT);
                    if (!songAlbumFile.exists()) {
                        songAlbumFile.mkdirs();
                    }
                    String fileName = songName + "-" + artist + ".jpg";
                    File destFile = new File(songAlbumFile, fileName);
                    //把gilde下载得到图片复制到定义好的目录中去
                    copy(file, destFile);

                });
    }

    /**
     * 复制文件
     *
     * @param source 输入文件
     * @param target 输出文件
     */
    private static void copy(File source, File target) {
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileInputStream = new FileInputStream(source);
            fileOutputStream = new FileOutputStream(target);
            byte[] buffer = new byte[1024];
            while (fileInputStream.read(buffer) > 0) {
                fileOutputStream.write(buffer);
            }
            LogUtil.d(TAG, "歌曲图片保存完成");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fileInputStream.close();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 裁剪原始的图片
     */
    public static Intent cropRawPhotoIntent(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX , aspectY :宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 480);
        intent.putExtra("outputY", 480);
        intent.putExtra("return-data", true);
        File headerFile = new File(Constants.HEADER_PATH);
        if (!headerFile.exists()) {
            headerFile.mkdirs();
        }
        File file = new File(headerFile, Constants.CROP_IMAGE_FILE_NAME);
        Uri uriPath = Uri.parse("file://" + file.getAbsolutePath());
        //将裁剪好的图输出到所建文件中
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriPath);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        //注意：此处应设置return-data为false，如果设置为true，是直接返回bitmap格式的数据，耗费内存。设置为false，然后，设置裁剪完之后保存的路径，即：intent.putExtra(MediaStore.EXTRA_OUTPUT, uriPath);
//        intent.putExtra("return-data", true);
        intent.putExtra("return-data", false);
        return intent;
    }

    public static File getTempFile() {
        return new File(
                Environment.getExternalStorageDirectory(),
                Constants.IMAGE_FILE_NAME);
    }
}







