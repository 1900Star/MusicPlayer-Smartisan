package com.yibao.music.model.song;

/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.model.song
 * @文件名: MusicCountBean
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/27 22:10
 * @描述： {TODO}
 */

public class MusicCountBean {
    private int musicCount;
    private int size;

    public MusicCountBean(int musicCount, int size) {
        this.musicCount = musicCount;
        this.size = size;
    }

    public int getMusicCount() {
        return musicCount;
    }

    public void setMusicCount(int musicCount) {
        this.musicCount = musicCount;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
