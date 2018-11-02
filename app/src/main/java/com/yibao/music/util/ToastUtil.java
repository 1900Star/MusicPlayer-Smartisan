package com.yibao.music.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Author：Sid
 * Des：${TODO}
 * Time:2017/8/13 04:43
 */
public class ToastUtil {
    public static void initPlayState(Context context) {
        Toast.makeText(context, "请点击播放按钮 -_-", Toast.LENGTH_SHORT).show();
    }

    public static void showNotFoundFavoriteFile(Context context) {
        Toast.makeText(context, "没有发现歌曲收藏文件 -_-", Toast.LENGTH_SHORT).show();
    }

    public static void showFavoriteListBackupsDown(Context context) {
        Toast.makeText(context, "收藏列表备份完成 -_-", Toast.LENGTH_SHORT).show();
    }

    public static void showNoMusic(Context context) {
        Toast.makeText(context, "当前没有歌曲播放 -_-", Toast.LENGTH_SHORT).show();
    }

    public static void showNoReadExternalStoragePermissions(Context context) {
        Toast.makeText(context, "没SD卡读取权限! -_-", Toast.LENGTH_SHORT).show();
    }

    public static void showLoadMusicComplete(Context context) {
        Toast.makeText(context, "本地音乐加载完成 -_-", Toast.LENGTH_SHORT).show();

    }

    public static void showScreenOn(Context context) {
        Toast.makeText(context, "您开启了屏幕常亮", Toast.LENGTH_SHORT).show();

    }

    public static void showScreenOf(Context context) {
        Toast.makeText(context, "您关闭了屏幕常亮", Toast.LENGTH_SHORT).show();

    }


}
