package com.yibao.music.base.listener;

/**
 * @ Name:   NotifycationChangeListener
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/9/23/ 15:38
 * @ Des:    TODO
 * @author Luoshipeng
 */
public interface NotifycationChangeListener {
    /**
     * show通知
     */
    void show();

    /**
     * hi通知
     */
    void hide();


    /**
     * 通知是否显示
     *
     * @return b
     */
    boolean visible();

    /**
     * 更新通知栏上的收藏按钮
     *
     * @param currentFavorite c
     */
    void updataFavoriteBtn(boolean currentFavorite);

}
