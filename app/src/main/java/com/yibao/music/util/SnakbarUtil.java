package com.yibao.music.util;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;

import static android.support.design.widget.Snackbar.make;


/**
 * 作者：Stran on 2017/3/28 01:31
 * 描述：${各种需求的Snakbar}
 * 邮箱：strangermy@outlook.com
 */
public class SnakbarUtil {


    /**
     * 收藏成功提示
     */
    public static void favoriteSuccessView(View view, String str) {
        Snackbar snackbar = make(view, str, Snackbar.LENGTH_SHORT);
        snackbar.getView()
                .setBackgroundColor(ColorUtil.successColor);
        snackbar.show();

    }

    /**
     * 先播放音乐
     */
    public static void firstPlayMusic(View view) {
        Snackbar snackbar = make(view, "请先播放音乐", Snackbar.LENGTH_LONG);
        snackbar.getView()
                .setBackgroundColor(ColorUtil.successColor);
        snackbar.show();

    }

    public static void keepGoing(View view) {
        Snackbar snackbar = make(view, "建设中 -_-", Snackbar.LENGTH_LONG);
        snackbar.getView()
                .setBackgroundColor(Color.parseColor("#325ab1"));
        snackbar.show();

    }

    /**
     * 列表名字不能为空提示
     */
    public static void favoriteFailView(View view) {
        Snackbar snackbar = make(view, "列表名的长度不能超过21个字符", Snackbar.LENGTH_LONG);
        snackbar.getView()
                .setBackgroundColor(Color.parseColor("#325ab1"));
        snackbar.show();


    }

    public static void noFavoriteMusic(View view) {
        Snackbar snackbar = make(view, "当前没有收藏歌曲  -_-", Snackbar.LENGTH_LONG);
        snackbar.getView()
                .setBackgroundColor(ColorUtil.errorColor);
        snackbar.show();

    }


}
