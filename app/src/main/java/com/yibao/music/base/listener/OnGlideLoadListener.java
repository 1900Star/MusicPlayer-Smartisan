package com.yibao.music.base.listener;

/**
 * @ Author: Luoshipeng
 * @ Name:   OnGlideLoadListener
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/12/15/ 21:21
 * @ Des:    根据列表的滚动状态决定Glide是否需要加载图片
 */
public interface OnGlideLoadListener {
    void resumeRequests();

    void pauseRequests();

}
