package com.yibao.music.base.listener;

/**
 * Author：Sid
 * Des：${TODO}
 * Time:2017/5/14 09:49
 */
public interface OnMusicItemClickListener {
    void startMusicService(int position, int sortListFlag);

    void onOpenMusicPlayDialogFag();

    void switchViewPagerItem(int page, int titleResourcesId);

}
