package com.yibao.music.util;

import com.yibao.music.model.MusicBean;

/**
 * @ Author: Luoshipeng
 * @ Name:   MusicDataTranslateUtil
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/5/5/ 18:01
 * @ Des:    //TODO
 */
public class MusicDataTranslateUtil {
    public static MusicBean tanslateData(MusicBean musicData) {
        MusicBean musicBean = new MusicBean();
        musicBean.setCurrentLyrics(musicData.getCurrentLyrics());
        musicBean.setAlbumId(musicData.getAlbumId());
        musicBean.setTitle(musicData.getTitle());
        musicBean.setArtist(musicData.getArtist());
        return musicBean;
    }

}
