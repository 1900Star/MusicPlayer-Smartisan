package com.yibao.music.model;

/**
 * @ Author: Luoshipeng
 * @ Name:   EditBean
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/11/26/ 3:11
 * @ Des:    TODO
 */
public class EditBean {
    private int currentIndex;

    public EditBean(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }
}
