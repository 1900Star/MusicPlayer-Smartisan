package com.yibao.music.base.listener;

/**
 * @author: Luoshipeng
 * @ Name:    LyricsCallBack
 * @ Email:   strangermy98@gmail.com
 * @ GitHub:  https://github.com/1900Star
 * @ Time:    2019/4/14/ 1:27
 * @ Des:     TODO
 */
public interface LyricDownCallBack {
    /**
     *d
     * @param isDone 歌词是否下载成功
     * @param msg 失败信息
     */
    void downLyric(boolean isDone,String msg);
}
