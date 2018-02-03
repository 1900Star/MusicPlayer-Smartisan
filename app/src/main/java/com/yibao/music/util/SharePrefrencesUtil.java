package com.yibao.music.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @项目名： BigGirl
 * @包名： com.yibao.biggirl.util
 * @文件名: SharePrefrencesUtil
 * @author: Stran
 * @创建时间: 2018/1/6 23:28
 * @描述： TODO
 */

public class SharePrefrencesUtil {

    /**
     * 用于存储和获取音乐的播放模式
     *
     * @param context
     * @param value
     */
    public static void setMusicMode(Context context, int value) {
        SharedPreferences sp = context.getSharedPreferences(Constants.MUSIC_MODE, Constants.MODE_KEY);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(Constants.PLAY_MODE_KEY, value);
        editor.commit();
    }

    public static int getMusicMode(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Constants.MUSIC_MODE, Constants.MODE_KEY);
        return sp.getInt(Constants.PLAY_MODE_KEY, Constants.MODE_KEY);
    }

    /**
     * 用于存储和获取音乐播放的位置
     *
     * @param context
     * @param value
     */
    public static void setMusicPosition(Context context, int value) {
        SharedPreferences sp = context.getSharedPreferences(Constants.MUSIC_POSITION, Constants.MODE_KEY);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(Constants.MUSIC_ITEM_POSITION, value);
        editor.commit();
    }

    public static int getMusicPosition(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Constants.MUSIC_POSITION, Constants.MODE_KEY);
        return sp.getInt(Constants.MUSIC_ITEM_POSITION, Constants.MODE_KEY);
    }

    /**
     * 用于存储关闭音乐时，音乐的播放状态 。 1：表示暂停时关闭 ， 2：表示播放时关闭
     *
     * @param context
     * @param value
     */
    public static void setMusicPlayState(Context context, int value) {
        SharedPreferences sp = context.getSharedPreferences(Constants.MUSIC_PLAY_STATE, Constants.MODE_KEY);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(Constants.MUSIC_PLAY_STATE_KEY, value);
        editor.commit();
    }

    public static int getMusicPlayState(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Constants.MUSIC_PLAY_STATE, Constants.MODE_KEY);
        return sp.getInt(Constants.MUSIC_PLAY_STATE_KEY, Constants.MODE_KEY);
    }

    /**
     * 用于存储和获取用户是否有播放记录
     *
     * @param context
     */
    public static void setMusicConfig(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Constants.MUSIC_CONFIG, Constants.MODE_KEY);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(Constants.MUSIC_REMENBER_FLAG, true);
        editor.commit();
    }

    public static boolean getMusicConfig(Context context, boolean b) {
        SharedPreferences sp = context.getSharedPreferences(Constants.MUSIC_CONFIG, Constants.MODE_KEY);

        return sp.getBoolean(Constants.MUSIC_REMENBER_FLAG, b);
    }

}
