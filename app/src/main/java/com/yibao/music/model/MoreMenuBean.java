package com.yibao.music.model;

/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.model.song
 * @文件名: MusicCountBean
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/27 22:10
 * @描述： {TODO}
 */

public class MoreMenuBean {
    private int picId;
    private int nameId;

    public MoreMenuBean(int picId, int nameId) {
        this.picId = picId;
        this.nameId = nameId;
    }

    public int getPicId() {
        return picId;
    }


    public int getNameId() {
        return nameId;
    }

}
