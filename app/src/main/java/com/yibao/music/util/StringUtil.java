package com.yibao.music.util;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.net.Uri;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Author：Sid
 * Des：${歌曲的时间格式处理}
 * Time:2017/8/12 18:39
 *
 * @author Stran
 */
public class StringUtil {
    private static final int HOUR = 60 * 60 * 1000;
    private static final int MIN = 60 * 1000;
    private static final int SEC = 1000;
    private static String TAG = "StringUtil";


    /**
     * 解析歌词时间
     *
     * @param duration
     * @return
     */
    @SuppressLint("DefaultLocale")
    public static String parseDuration(int duration) {
        int hour = duration / HOUR;
        int min = duration % HOUR / MIN;
        int sec = duration % MIN / SEC;
        if (hour == 0) {

            return String.format("%02d:%02d", min, sec);
        } else {
            return String.format("%02d:%02d:%02d", hour, min, sec);
        }
    }

    public static Long stringToLong(String str) {
        return Long.valueOf(str.replaceAll("[^\\d]+", ""));
    }

    /**
     * 返回专辑图片地址
     *
     * @param albulmId a
     * @return f
     */
    public static Uri getAlbulm(Long albulmId) {

        return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"),
                albulmId);

    }


    /**
     * 返回一个标准的时间格式   yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String getCurrentTime() {
        long time = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date(time);
        return format.format(date);

    }public static String getCurrentTime(Long l) {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
        Date date = new Date(l);
        return format.format(date);

    }

    public static String getSongName(String songName) {
        LogUtil.d("===========SongName====  " + songName);
        String underLine = "_";
//        return songName.contains(underLine) ? songName.substring(songName.indexOf(underLine) + 1, songName.length()) : songName;
        return songName;
    }

    public static String getBottomSheetTitle(int size) {

        return "收藏列表 ( " + size + " )";
    }

}
