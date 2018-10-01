package com.yibao.music.base.listener;

/**
 * @ Author: Luoshipeng
 * @ Name:   OnSearchFlagListener
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/10/1/ 21:39
 * @ Des:    TODO
 */
public interface OnSearchFlagListener {
    // 用来区分搜索的标识：1 Artist 、 2 Album 、  3 SongName
    void setSearchFlag(int searchFlag);
}
