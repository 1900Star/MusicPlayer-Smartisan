package com.yibao.music.base.listener;

/**
 * @ Name:   OnGlideLoadListener
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/12/15/ 21:21
 * @ Des:    根据列表的滚动状态决定Glide是否需要加载图片
 * @author Luoshipeng
 */
public interface OnGlideLoadListener {

    /**
     * 加载图片
     */
    void resumeRequests();

    /**
     * 停止加载图片
     */
    void pauseRequests();

}
