package com.yibao.music.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @项目名： BigGirl
 * @包名： com.yibao.biggirl.util
 * @文件名: SpUtil
 * @author: Stran
 * @创建时间: 2018/1/6 23:28
 * @描述： TODO
 */

public class SpUtil {

    /**
     * 用于存储和获取音乐的播放模式
     *
     * @param context c
     * @param value   0 全部  1 单曲   2 随机
     */
    public static void setMusicMode(Context context, int value) {
        SharedPreferences sp = context.getSharedPreferences(Constants.MUSIC_MODE, Constants.MODE_KEY);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(Constants.PLAY_MODE_KEY, value);
        editor.apply();
    }

    public static int getMusicMode(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Constants.MUSIC_MODE, Constants.MODE_KEY);
        return sp.getInt(Constants.PLAY_MODE_KEY, Constants.MODE_KEY);
    }


    /**
     * 用于存储程序退出或关闭音乐界面时，音乐播放的位置。
     *
     * @param context c
     * @param value   播放位置
     */
    public static void setMusicPosition(Context context, int value) {
        SharedPreferences sp = context.getSharedPreferences(Constants.MUSIC_POSITION, Constants.MODE_KEY);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(Constants.MUSIC_ITEM_POSITION, value);
        editor.apply();
    }

    public static int getMusicPosition(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Constants.MUSIC_POSITION, Constants.MODE_KEY);
        return sp.getInt(Constants.MUSIC_ITEM_POSITION, Constants.MODE_KEY);
    }

    /**
     * 是否是首次安装，本地数据库是否创建，
     * 等于 8 表示不是首次安装，数据库已经创建，直接进入MusicActivity。
     * 不等于就是首次安装，扫描要本地音乐
     *
     * @param context c
     * @param value   Constants.NUMBER_EIGHT
     */
    public static void setLoadMusicFlag(Context context, int value) {
        SharedPreferences sp = context.getSharedPreferences(Constants.MUSIC_LOAD, Constants.MODE_KEY);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(Constants.MUSIC_LOAD_FLAG, value);
        editor.apply();
    }

    public static int getLoadMusicFlag(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Constants.MUSIC_LOAD, Constants.MODE_KEY);
        return sp.getInt(Constants.MUSIC_LOAD_FLAG, Constants.MODE_KEY);
    }

    /**
     * 用于存储程序退出或关闭音乐界面时，音乐列表的分类标记，。
     * 1 歌曲名   2  评分   3  播放次数    4  添加时间    8 收藏    10:  专辑 歌手等精确条件
     *
     * @param context c
     * @param value   v
     */
    public static void setSortFlag(Context context, int value) {
        SharedPreferences sp = context.getSharedPreferences(Constants.MUSIC_DATA_FLAG, Constants.MODE_KEY);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(Constants.MUSIC_DATA_LIST_FLAG, value);
        editor.apply();
    }

    public static int getSortFlag(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Constants.MUSIC_DATA_FLAG, Constants.MODE_KEY);
        return sp.getInt(Constants.MUSIC_DATA_LIST_FLAG, Constants.MODE_KEY);
    }

    /**
     * 查询音乐的标识
     *
     * @param context c
     * @param value   v 1 艺术家  2  专辑   3    歌曲   4 播放列表
     */
    public static void setDataQueryFlag(Context context, int value) {
        SharedPreferences sp = context.getSharedPreferences(Constants.MUSIC_DATA_QUERY, Constants.MODE_KEY);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(Constants.MUSIC_DATA_QUERY_FLAG, value);
        editor.apply();
    }

    public static int getDataQueryFlag(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Constants.MUSIC_DATA_QUERY, Constants.MODE_KEY);
        return sp.getInt(Constants.MUSIC_DATA_QUERY_FLAG, Constants.MODE_KEY);
    }

    /**
     * 用于存储播放记录的查询标识
     *
     * @param context c
     * @param value   歌曲的具体查询标识
     */
    public static void setQueryFlag(Context context, String value) {
        SharedPreferences sp = context.getSharedPreferences(Constants.MUSIC_QUERY, Constants.MODE_KEY);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Constants.MUSIC_QUERY_FLAG, value);
        editor.apply();
    }

    public static String getQueryFlag(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Constants.MUSIC_QUERY, Constants.MODE_KEY);
        return sp.getString(Constants.MUSIC_QUERY_FLAG, "defult");
    }

    /**
     * 用于存储退出程序或关闭音乐界面时，音乐的播放状态 。 1：表示暂停时关闭 ， 2：表示播放时关闭
     *
     * @param context c
     * @param value   v
     */
    public static void setMusicPlayState(Context context, int value) {
        SharedPreferences sp = context.getSharedPreferences(Constants.MUSIC_PLAY_STATE, Constants.MODE_KEY);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(Constants.MUSIC_PLAY_STATE_KEY, value);
        editor.apply();
    }

    public static int getMusicPlayState(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Constants.MUSIC_PLAY_STATE, Constants.MODE_KEY);
        return sp.getInt(Constants.MUSIC_PLAY_STATE_KEY, Constants.MODE_KEY);
    }

    /**
     * 用于存储和获取用户是否有播放记录
     *
     * @param context c
     */
    public static void setMusicConfig(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Constants.MUSIC_CONFIG, Constants.MODE_KEY);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(Constants.MUSIC_REMENBER_FLAG, true);
        editor.apply();
    }

    public static boolean getMusicConfig(Context context, boolean b) {
        SharedPreferences sp = context.getSharedPreferences(Constants.MUSIC_CONFIG, Constants.MODE_KEY);

        return sp.getBoolean(Constants.MUSIC_REMENBER_FLAG, b);
    }

    /**
     * @param context     c
     * @param isLossFoucs 音频焦点管理
     */
    public static void setFoucesFlag(Context context, boolean isLossFoucs) {
        SharedPreferences sp = context.getSharedPreferences(Constants.MUSIC_FOCUS, Constants.MODE_KEY);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(Constants.MUSIC_FOCUS_KEY, isLossFoucs);
        editor.apply();
    }

    public static boolean getFoucsFlag(Context context, boolean isLossFoucs) {
        SharedPreferences sp = context.getSharedPreferences(Constants.MUSIC_FOCUS, Constants.MODE_KEY);

        return sp.getBoolean(Constants.MUSIC_FOCUS_KEY, isLossFoucs);
    }


    public static void setPicUrlFlag(Context context, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(Constants.PIC_URL_FLAG, Constants.MODE_KEY);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(Constants.PIC_URL_LIST_FLAG, value);
        editor.apply();
    }

    public static boolean getPicUrlFlag(Context context, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(Constants.PIC_URL_FLAG, Constants.MODE_KEY);
        return sp.getBoolean(Constants.PIC_URL_LIST_FLAG, value);
    }
}
