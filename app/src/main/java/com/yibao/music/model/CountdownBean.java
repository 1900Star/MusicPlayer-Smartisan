package com.yibao.music.model;

/**
 * @ Author: Luoshipeng
 * @ Name:   CountdownBean
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2019/1/3/ 22:41
 * @ Des:    倒计时
 */
public class CountdownBean {
    private String countdownTime;

    public CountdownBean(String countdownTime) {
        this.countdownTime = countdownTime;
    }

    public String getCountdownTime() {
        return countdownTime;
    }

    public void setCountdownTime(String countdownTime) {
        this.countdownTime = countdownTime;
    }
}
