package com.yibao.music.model;

/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.model
 * @文件名: AddNewListBean
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/25 23:29
 * @描述： {仅作为打开哪个详情页面的标记，8 ：playListFragment, 9 : artistListFragmnet, 10 : albumListFragment}
 */

public class DetailsFlagBean {
    private int detailFlag;

    public DetailsFlagBean(int detailFlag) {
        this.detailFlag = detailFlag;
    }

    public int getDetailFlag() {
        return detailFlag;
    }
}
