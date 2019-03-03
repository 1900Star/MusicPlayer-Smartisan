package com.yibao.music.model;

/**
 * Author：Sid
 * Des：${TODO}
 * Time:2017/8/13 06:53
 *
 * @author Stran
 */
public class PlayStatusBean {
    /**
     * 0 表示是通知栏控制播放和暂停
     * 1 表示在通知栏收藏音乐
     * 2 在通知栏关闭通知栏
     */

    private int type;

    public PlayStatusBean(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

}
