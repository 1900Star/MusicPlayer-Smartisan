package com.yibao.music.model.music;

/**
 * Author：Sid
 * Des：${用于BottomSheetAdapter中,点击item播放音乐}
 * Time:2017/8/13 06:53
 */
public class BottomSheetStatus {
    /**
     * position 用来表示播放音乐的Position
     */

    public int position;

    public BottomSheetStatus(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

}
