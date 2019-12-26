package com.yibao.music.model.qq;

public class SearchLyricsBean {
    private String songMid;
    private String lyrics;

    public SearchLyricsBean(String songMid, String lyrics) {
        this.songMid = songMid;
        this.lyrics = lyrics;
    }

    public String getSongMid() {
        return songMid;
    }

    public void setSongMid(String songMid) {
        this.songMid = songMid;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    @Override
    public String toString() {
        return "SearchLyricsBean{" +
                "songMid='" + songMid + '\'' +
                ", lyrics='" + lyrics + '\'' +
                '}';
    }
}
