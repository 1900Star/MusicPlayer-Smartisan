package com.yibao.music.model;

import com.yibao.music.model.greendao.MusicBeanDao;

/**
 * @ Author: Luoshipeng
 * @ Name:   QqBarUpdataBean
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/5/6/ 18:48
 * @ Des:    //TODO
 */
public class QqBarUpdataBean {
    private
    MusicBeanDao musicBeanDao;
    private MusicBean musicBean;
    private int sortListFlag;
    private int dataFlag;
    private String queryFlag;

    public QqBarUpdataBean(MusicBeanDao musicBeanDao, MusicBean musicBean, int sortListFlag, int dataFlag, String queryFlag) {
        this.musicBeanDao = musicBeanDao;
        this.musicBean = musicBean;
        this.sortListFlag = sortListFlag;
        this.dataFlag = dataFlag;
        this.queryFlag = queryFlag;
    }

    public void setMusicBean(MusicBean musicBean) {
        this.musicBean = musicBean;
    }

    public MusicBeanDao getMusicBeanDao() {
        return musicBeanDao;
    }

    public MusicBean getMusicBean() {
        return musicBean;
    }

    public int getSortListFlag() {
        return sortListFlag;
    }

    public int getDataFlag() {
        return dataFlag;
    }

    public String getQueryFlag() {
        return queryFlag;
    }
}
