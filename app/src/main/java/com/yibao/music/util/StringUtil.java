package com.yibao.music.util;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.yibao.music.aidl.MusicBean;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
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
        if (str != null) {
            return Long.valueOf(str.replaceAll("[^\\d]+", ""));

        }
        return 1L;
    }

    /**
     * 返回专辑图片地址
     *
     * @param picType 1 歌手图片 、 2 专辑图片
     * @param albumId a
     * @return f
     */
    public static String getAlbum(int picType, Long albumId, String artist) {
        String path = picType == 1 ? Constants.MUSIC_ARITIST_IMG_ROOT + artist + ".jpg" : Constants.MUSIC_ALBUM_ROOT + artist + ".jpg";
        File file = new File(path);

        return file.exists() ? path : ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"),
                albumId).toString();

    }

    /**
     * 返回下载保存到本地的专辑图片路径
     *
     * @param songName a
     * @return f
     */
    public static String getDownAlbum(String songName, String artist) {

        return Constants.MUSIC_SONG_ALBUM_ROOT + songName + ".jpg";

    }


    /**
     * @param context c
     * @param albumId id
     * @return 根据专辑 id 获得专辑图片保存路径,通知栏使用。
     */
    public static synchronized String getAlbumArtPath(Context context, String albumId) {

        if (!StringUtil.isReal(albumId)) {
            return null;
        }

        String[] projection = {MediaStore.Audio.Albums.ALBUM_ART};
        String imagePath = null;
        Uri uri = Uri.parse("content://media" + MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI.getPath() + "/" + albumId);

        Cursor cur = context.getContentResolver().query(uri, projection, null, null, null);
        if (cur == null) {
            return null;
        }

        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            imagePath = cur.getString(0);
        }
        cur.close();
        return imagePath;
    }

    /**
     * 返回一个标准的时间格式   yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String getCurrentTime() {
        long time = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        Date date = new Date(time);
        return format.format(date);

    }

    public static String getFormatDate(Long l) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        Date date = new Date(l);
        return format.format(date);

    }

    public static String getTitle(MusicBean musicBean) {
        String musicTitle = musicBean.getTitle();
        return musicTitle.contains("[mqms2]")
                ? TitleArtistUtil.getBean(musicTitle).getSongName()
                : musicTitle;
    }

    public static String getArtist(MusicBean musicBean) {
        String musicTitle = musicBean.getTitle();
        String musicArtist = musicBean.getArtist();
        return musicTitle.contains("[mqms2]")
                ? TitleArtistUtil.getBean(musicTitle).getSongArtist()
                : "<unknown>".equals(musicArtist) || "群星".equals(musicArtist)
                ? "Smartisan" : musicArtist;
    }

    public static String getSongName(String songName) {
        return songName.contains("[mqms2]") ? songName.substring(songName.lastIndexOf("-") + 2, songName.lastIndexOf("[mqms2]") - 1) : songName;
    }

    public static String getArtist(String singerName) {
        String unknownName = "<unknown>";
        return unknownName.equals(singerName) || "群星".equals(singerName) ? "Smartisan" : singerName;

    }

    public static String getBottomSheetTitle(int size) {

        return "收藏列表 ( " + size + " )";
    }

    public static boolean isReal(String string) {
        return string != null && string.length() > 0 && !"null".equals(string);
    }

    public static String getTime() {
        return String.valueOf(System.currentTimeMillis());
    }

    public static String idToString(Context context, int resourcesId) {
        return context.getResources().getString(resourcesId);
    }

    public static long getSetCountdown(String countdown) {
        int mContdownTime = 0;
        switch (countdown) {
            case "正在倒计时":
                mContdownTime = -1;
                break;
            case "无":
                LogUtil.d(TAG, "     时间为无");
                break;
            case "15 分":
                mContdownTime = 15 * 60 * 1000;
                break;
            case "30 分":
                mContdownTime = 30 * 60 * 1000;
                break;
            case "1 小时":
                mContdownTime = 60 * 60 * 1000;
                break;
            case "1 小时 30 分":
                mContdownTime = 90 * 60 * 1000;
                break;
            case "2 小时":
                mContdownTime = 120 * 60 * 1000;
                break;
            default:
                break;
        }
        return mContdownTime;
    }
}
