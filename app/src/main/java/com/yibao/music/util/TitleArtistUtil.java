package com.yibao.music.util;

import com.yibao.music.model.TitleAndArtistBean;

/**
 * @ Author: Luoshipeng
 * @ Name:   TitleArtistUtil
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/5/5/ 18:01
 * @ Des:    //TODO
 */
class TitleArtistUtil {
    /**
     * 将 歌名和歌手 封装到 SearchHistoryBean，这里暂时用SearchHistoryBean。
     * @param songName 需要截取的歌名
     * @return 返回一个封装好的bean
     */
    static TitleAndArtistBean getBean(String songName) {
        TitleAndArtistBean historyBean = new TitleAndArtistBean();
//     通过QQ音乐下载的歌曲的特殊歌曲名：   String songName = "周杰伦 - 一路向北 [mqms2]";
        String musicName = songName.substring(songName.lastIndexOf("-") + 2, songName.lastIndexOf("[mqms2]") - 1);
        String artist = songName.substring(0, songName.indexOf("-") - 1);
        historyBean.setSongName(musicName);
        historyBean.setSongArtist(artist);
        return historyBean;
    }

}
