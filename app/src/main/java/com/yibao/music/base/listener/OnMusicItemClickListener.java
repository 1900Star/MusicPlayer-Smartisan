package com.yibao.music.base.listener;

/**
 * Des：${TODO}
 * Time:2017/5/14 09:49
 * @author Stran
 */
public interface OnMusicItemClickListener {
    /**
     * 在主列表播放音乐
     *
     * @param position 播放位置
     * @param pageType 页面标识
     */
    void startMusicService(int position, int pageType);

    /**
     * 在详情列表播放音乐，列表数据的标识需要指定。
     *
     * @param pageType  页面标识
     * @param position  播放位置
     * @param condition  关键字  歌手、专辑名字
     */
    void startMusicServiceFlag(int position, int pageType, String condition);

    /**
     * 打开播放界面  pagerAdapter的点击事件
     */
    void onOpenMusicPlayDialogFag();


}
