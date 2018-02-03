package com.yibao.music.model.music;

import android.support.annotation.NonNull;

/**
 * Author：Sid
 * Des：${TODO}
 * Time:2017/9/14 00:58
 */
public class MusicLyrBean
        implements Comparable<MusicLyrBean> {
    private int startTime;
    private String content;
    public MusicLyrBean(int startTime, String content) {
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
    public int compareTo(@NonNull MusicLyrBean musicLrcBean) {
        return startTime - musicLrcBean.getStartTime();
    }
}
