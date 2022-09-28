package com.yibao.music.base.listener;

/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.base.listener
 * @文件名: OnCheckFavoriteListener
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/17 22:36
 * @描述： {清空收藏列表后更新PlayAcitivity和MusicActivity界面的收藏图片的状态}
 */

public interface OnCheckFavoriteListener {
    /**
     * 更新当前收藏状态
     */
    void updateFavoriteStatus();

}
