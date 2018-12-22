package com.yibao.music.model;

/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.model
 * @文件名: AddAndDeleteListBean
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/25 23:29
 * @描述： {仅作为通知添加列表和删除更新的消息}
 */

public class AddAndDeleteListBean {
    // 1 表示添加列表    2 表示删除列表  3 清除收藏列表   4 表示重命名列表   5 侧滑删除收藏歌曲
    private int operationType;
    private String listTitle;
    private int mPosition;
    private String mSongTitle;

    public AddAndDeleteListBean(int operationType) {
        this.operationType = operationType;
    }

    public AddAndDeleteListBean(int operationType, String listTitle) {
        this.operationType = operationType;
        this.listTitle = listTitle;
    }

    public AddAndDeleteListBean(int operationType, int position,String songTitle) {
        this.operationType = operationType;
        this.mPosition = position;
        this.mSongTitle = songTitle;
    }

    public int getOperationType() {
        return operationType;
    }

    public String getListTitle() {
        return listTitle;
    }

    public void setListTitle(String listTitle) {
        this.listTitle = listTitle;
    }

    public int getPosition() {
        return mPosition;
    }

    public String getSongTitle() {
        return mSongTitle;
    }
}
