package com.yibao.music.base.listener;

/**
 * @ Name:   OnUpdataTitleListener
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/8/17/ 22:27
 * @ Des:    TODO
 * @author Luoshipeng
 */
public interface OnUpdateTitleListener {

    /**
     * 当前歌曲是否收藏
     */
    void checkCurrentFavorite();

    /**
     * 切换ControlBar
     */
    void switchControlBar();


}
