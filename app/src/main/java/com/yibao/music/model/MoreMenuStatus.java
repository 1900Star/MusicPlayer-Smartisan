package com.yibao.music.model;

/**
 * @ Author: Luoshipeng
 * @ Name:   MoreMenuStatus
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/12/2/ 23:36
 * @ Des:    TODO
 */
public class MoreMenuStatus {
    private int position;
    private MusicBean musicBean;

    public MoreMenuStatus(int position, MusicBean musicBean) {
        this.position = position;
        this.musicBean = musicBean;
    }

    public int getPosition() {
        return position;
    }

    public MusicBean getMusicBean() {
        return musicBean;
    }
}
