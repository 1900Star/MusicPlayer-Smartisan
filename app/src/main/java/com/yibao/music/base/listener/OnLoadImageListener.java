package com.yibao.music.base.listener;

/**
 * @author luoshipeng
 * createDate：2019/12/4 0004 10:09
 * className   OnLoadImageListener
 * Des：Glide 加载图片结果 监听
 */
public interface OnLoadImageListener {
    /**
     *专辑图片加载结果
     * @param isSuccess 图片是否加载成功
     */
   void loadResult(boolean isSuccess);

}
