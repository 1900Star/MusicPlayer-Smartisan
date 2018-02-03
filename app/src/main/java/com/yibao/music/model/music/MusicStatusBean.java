package com.yibao.music.model.music;

/**
 * Author：Sid
 * Des：${TODO}
 * Time:2017/8/13 06:53
 */
public class MusicStatusBean {
    /**
     * position 用来判断触发 MusicPlayDialogFag弹出的源头，0 表示正常弹出，
     * 1 表示从通知栏的音乐控制面板触发弹出。
     * 2 表示终止播放音乐
     */

    public int     type;
    public boolean isPlay;

    public MusicStatusBean(int type, boolean isPlay) {
        this.type = type;
        this.isPlay = isPlay;
    }

    public int getType() {
        return type;
    }

    public boolean isPlay() {
        return isPlay;
    }
}
