package com.yibao.music.base.listener;

/**
 * @author: Luoshipeng
 * @ Name:    LyricsCallBack
 * @ Email:   strangermy98@gmail.com
 * @ GitHub:  https://github.com/1900Star
 * @ Time:    2019/4/14/ 1:27
 * @ Des:     TODO
 */
public interface LyricsCallBack {
    /**
     * @param b 下载成功 失败
     * @param lyricsUri 歌词下载地址
     */
    void lyricsUri(boolean b,String lyricsUri);
}
