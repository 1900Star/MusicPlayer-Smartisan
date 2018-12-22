package com.yibao.music.base.listener;

/**
 * @ Author: Luoshipeng
 * @ Name:   OnUpdataTitleListener
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/8/17/ 22:27
 * @ Des:    TODO
 */
public interface OnUpdataTitleListener {
    void updataTitle(String toolbarTitle, boolean isShowDetail);

    void changeTvEdit(String tvEdit);

    void setEditVisibility(int editVisibility);

    void setIvSearchVisibility(boolean isIvSearchVisibility);

    void checkCurrentFavorite();
}
