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
     */
    void startMusicService(int position);

    /**
     * 在详情列表播放音乐，列表数据的标识需要指定。
     *
     * @param sortFlag  列表类型
     * @param position  播放位置
     * @param dataFlag  数据列表的标识
     * @param queryFlag 具体查询的条 ( 按 歌手 或 专辑查询 )
     */
    void startMusicServiceFlag(int position, int sortFlag,int dataFlag, String queryFlag);

    /**
     * 打开播放界面  pagerAdapter的点击事件
     */
    void onOpenMusicPlayDialogFag();


}
