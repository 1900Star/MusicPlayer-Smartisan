package com.yibao.music.base.listener;

/**
 * @ Author: Luoshipeng
 * @ Name:   NotifycationChangeListener
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/9/23/ 15:38
 * @ Des:    TODO
 */
public interface NotifycationChangeListener {
    void show();

    void hide();

    boolean visible();

    void updataFavoriteBtn(boolean currentFavorite);

//    void updataPlayBtn(boolean isPlaying);

}
