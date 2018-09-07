package com.yibao.music.model;

import android.support.annotation.NonNull;

/**
 * Author：Sid
 * Des：${TODO}
 * Time:2017/9/14 00:58
 * @author Stran
 */
public class MusicLyricBean
        implements Comparable<MusicLyricBean> {
    private int startTime;
    private String content;
    public MusicLyricBean(int startTime, String content) {
        this.startTime = startTime;
        this.content = content;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int compareTo(@NonNull MusicLyricBean musicLrcBean) {
        return startTime - musicLrcBean.getStartTime();
    }
}
