package com.yibao.music.base.listener;

import com.yibao.music.model.qq.AlbumSong;
import com.yibao.music.model.qq.SongLrc;

import java.util.List;

/**
 * @author luoshipeng
 * createDate：2019/12/10 0010 14:31
 * className   OnAlbumDetailListener
 * Des：TODO
 */
public interface OnSearchLyricsListener {
    void searchResult(List<SongLrc> songLrcList);
}
