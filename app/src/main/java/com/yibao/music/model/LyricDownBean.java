package com.yibao.music.model;

/**
 * @author: Luoshipeng
 * @ Name:    LyricDownBean
 * @ Email:   strangermy98@gmail.com
 * @ GitHub:  https://github.com/1900Star
 * @ Time:    2019/4/14/ 22:58
 * @ Des:     TODO
 */
public class LyricDownBean {
    private String songName;
    private String songArtist;
    private boolean doneOK;

    public LyricDownBean(String songName, String songArtist, boolean doneOK) {
        this.songName = songName;
        this.songArtist = songArtist;
        this.doneOK = doneOK;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    public boolean isDoneOK() {
        return doneOK;
    }

    public void setDoneOK(boolean doneOK) {
        this.doneOK = doneOK;
    }
}
