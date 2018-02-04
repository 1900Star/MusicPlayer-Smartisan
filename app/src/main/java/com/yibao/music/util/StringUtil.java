package com.yibao.music.util;

import android.content.ContentUris;
import android.net.Uri;

import com.yibao.music.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Author：Sid
 * Des：${歌曲的时间格式处理}
 * Time:2017/8/12 18:39
 */
public class StringUtil {
    private static final int HOUR = 60 * 60 * 1000;
    private static final int MIN = 60 * 1000;
    private static final int SEC = 1000;
    private static String TAG = "StringUtil";

    //解析时间
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

    public static String getSongName(String songName) {
        String underLine = String.valueOf(R.string.under_line);
        if (songName.contains(underLine)) {

            songName = songName.substring(songName.indexOf(underLine) + 1, songName.length());
        }
        return songName;
    }

    public static Uri getAlbulm(long albulmId) {

        return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"),
                albulmId);

    }

    //yyyy-MM-dd HH:mm:ss
    public static String getCurrentTime() {
        long time = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
        Date date = new Date(time);
        return format.format(date);

    }

    // 输入汉字返回拼音的通用方法函数。
    public static String getPinYin(String hanzi) {
        ArrayList<HanziToPinyin.Token> tokens = HanziToPinyin.getInstance()
                .get(hanzi);
        StringBuilder sb = new StringBuilder();
        if (tokens != null && tokens.size() > 0) {
            for (HanziToPinyin.Token token : tokens) {
                if (HanziToPinyin.Token.PINYIN == token.type) {
                    sb.append(token.target);
                } else {
                    sb.append(token.source);
                }
            }
        }

        return sb.toString()
                .toUpperCase()
                .substring(0, 1);
    }



    public static String getBottomSheetTitile(int size) {

        return "收藏列表 ( " + size + " )";
    }

}
