package com.yibao.music.model;

/**
 * Author：Sid
 * Des：${TODO}
 * Time:2017/8/13 06:53
 *
 * @author Stran
 */
public class MusicStatusBean {
    /**
     * position 用来判断触发 MusicPlayDialogFag弹出的源头，
     * < 0 >表示是通知栏播放和暂停按钮发出，
     * 1 表示从通知栏的音乐控制面板触发弹出。从通知栏打开音列表，即整个通知栏布局的监听。
     * 2 表示终止播放音乐 ,在通知栏关闭通知栏
     */

    public int type;
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
