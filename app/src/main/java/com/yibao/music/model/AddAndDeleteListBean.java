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
    // 1 表示添加列表    2 表示删除列表
    private int operationType;

    public AddAndDeleteListBean(int operationType) {
        this.operationType = operationType;
    }

    public int getOperationType() {
        return operationType;
    }

}
