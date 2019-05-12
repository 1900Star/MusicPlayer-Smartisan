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
    private String downMsg;

    public LyricDownBean(boolean doneOK, String downMsg) {
        this.doneOK = doneOK;
        this.downMsg = downMsg;
    }

    public boolean isDoneOK() {
        return doneOK;
    }

    public void setDoneOK(boolean doneOK) {
        this.doneOK = doneOK;
    }

    public String getDownMsg() {
        return downMsg;
    }

    public void setDownMsg(String downMsg) {
        this.downMsg = downMsg;
    }
}
