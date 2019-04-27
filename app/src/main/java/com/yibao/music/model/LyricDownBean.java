package com.yibao.music.model;

import java.io.File;

/**
 * @author: Luoshipeng
 * @ Name:    LyricDownBean
 * @ Email:   strangermy98@gmail.com
 * @ GitHub:  https://github.com/1900Star
 * @ Time:    2019/4/14/ 22:58
 * @ Des:     TODO
 */
public class LyricDownBean {
    private boolean doneOK;
    private File lyricsFile;
    private String downMsg;

    public LyricDownBean(boolean doneOK, File lyricsFile, String downMsg) {
        this.doneOK = doneOK;
        this.lyricsFile = lyricsFile;
        this.downMsg = downMsg;
    }

    public boolean isDoneOK() {
        return doneOK;
    }

    public void setDoneOK(boolean doneOK) {
        this.doneOK = doneOK;
    }

    public File getLyricsFile() {
        return lyricsFile;
    }

    public void setLyricsFile(File lyricsFile) {
        this.lyricsFile = lyricsFile;
    }

    public String getDownMsg() {
        return downMsg;
    }

    public void setDownMsg(String downMsg) {
        this.downMsg = downMsg;
    }
}
