package com.yibao.music.util;

import android.annotation.SuppressLint;
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
        Snackbar snackbar = make(view, str, Snackbar.LENGTH_LONG);
        snackbar.getView()
                .setBackgroundColor(ColorUtil.successColor);
        snackbar.show();

    }

    /**
     * 列表名字不能为空提示
     */
    @SuppressLint("ResourceAsColor")
    public static void favoriteFailView(View view) {
        Snackbar snackbar = make(view, "列表名字不能为空！", Snackbar.LENGTH_SHORT);
        snackbar.getView()
                .setBackgroundColor(Color.parseColor("#325ab1"));
        snackbar.show();

    }

    /**
     * 图片已经下载过啦
     */
    public static void picAlreadyExists(View view) {
        Snackbar snackbar = make(view, "图片已经下载过啦  -_-", Snackbar.LENGTH_LONG);
        snackbar.getView()
                .setBackgroundColor(ColorUtil.picAlreadyExists);
        snackbar.show();

    }


}
