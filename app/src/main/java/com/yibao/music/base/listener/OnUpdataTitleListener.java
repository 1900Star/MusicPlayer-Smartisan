package com.yibao.music.base.listener;

/**
 * @ Author: Luoshipeng
 * @ Name:   OnUpdataTitleListener
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/8/17/ 22:27
 * @ Des:    TODO
 */
public interface OnUpdataTitleListener {

    void checkCurrentFavorite();

    void switchControlBar();

    void handleBack(int detailFlag);
}
