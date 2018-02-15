package com.yibao.music.base.listener;

/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.base.listener
 * @文件名: OnControlBarClickListener
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/12 14:23
 * @描述： {处理在fragemtn中物理返回键事件}
 */

public interface OnControlBarClickListener {
    /**
     * Activity根据 flag ,将BaseFragment 强转成指定的子类Fragment
     *
     * @return
     */
    void openPlayDialog();
}
