package com.yibao.music.base.listener;

/**
 * Author：Sid
 * Des：${TODO}
 * Time:2017/5/14 09:49
 */
public interface OnMusicListItemClickListener {
    void startMusicService(int position);
    void onOpenMusicPlayDialogFag();
    void onOpenAlbumDetailsFragment(String s);
}
