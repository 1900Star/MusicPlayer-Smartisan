package com.yibao.music.util;

import android.support.design.widget.Snackbar;
import android.view.View;

import static android.support.design.widget.Snackbar.LENGTH_LONG;
import static android.support.design.widget.Snackbar.make;
import static com.yibao.music.util.ColorUtil.picAlreadyExists;


/**
 * 作者：Stran on 2017/3/28 01:31
 * 描述：${各种需求的Snakbar}
 * 邮箱：strangermy@outlook.com
 */
public class SnakbarUtil {


    /**
     * 下载成功提示
     */
    public static void showSuccessView(View view) {
        Snackbar snackbar = make(view, "图片保存成功 -_-", Snackbar.LENGTH_LONG);
        snackbar.getView()
                .setBackgroundColor(ColorUtil.successColor);
        snackbar.show();

    }

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
     * 收藏失败提示
     */
    public static void favoriteFailView(View view, String str) {
        Snackbar snackbar = make(view, str, Snackbar.LENGTH_LONG);
        snackbar.getView()
                .setBackgroundColor(ColorUtil.errorColor);
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

    /**
     * 下载失败提示
     */
    public static void showDownPicFail(View view) {

        Snackbar snackbar = make(view, "图片保存失败 -_-", Snackbar.LENGTH_LONG);
        snackbar.getView()
                .setBackgroundColor(ColorUtil.errorColor);
        snackbar.show();

    }

    /**
     * 分享失败提示
     */
    public static void showSharePicFail(View view) {

        Snackbar snackbar = make(view, "分享失败 -_-", Snackbar.LENGTH_LONG);
        snackbar.getView()
                .setBackgroundColor(ColorUtil.errorColor);
        snackbar.show();

    }


    /**
     * GoogleMap提示
     */
    public static void mapPoint(View view)
    {

        Snackbar snackbar = make(view, "当前设备不支持完整的谷歌服务!", Snackbar.LENGTH_LONG);
        snackbar.getView()
                .setBackgroundColor(picAlreadyExists);
        snackbar.show();

    }

    /**
     * 网络异常提示
     */
    public static void netErrors(View view)
    {

        Snackbar snackbar = make(view, "网络异常，请检查您的网络连接 -_-", LENGTH_LONG);
        snackbar.getView()
                .setBackgroundColor(ColorUtil.errorColor);

        snackbar.show();
    }

    /**
     * 网络异常长时提示
     */
    public static void netErrorsLong(View view)
    {

        Snackbar snackbar = make(view, "网络异常，请检查您的网络连接 ,之后重试 ！-_-", Snackbar.LENGTH_INDEFINITE);
        snackbar.getView()
                .setBackgroundColor(ColorUtil.errorColor);

        snackbar.show();
    }

    /**
     * 退出程序
     */
    public static void finishActivity(View view)
    {

        Snackbar snackbar = make(view, "再按一次我就离开了 -_-", Snackbar.LENGTH_SHORT);
        snackbar.getView()
                .setBackgroundColor(ColorUtil.exitColor);
        snackbar.show();

    }

    /**
     * 关闭Snakbar
     */
    public static void setWallpaer(View view)
    {
        Snackbar snackbar = make(view, "壁纸设置成功  -_-", Snackbar.LENGTH_LONG);
        snackbar.getView()
                .setBackgroundColor(ColorUtil.successColor);
        snackbar.show();

    }


}
