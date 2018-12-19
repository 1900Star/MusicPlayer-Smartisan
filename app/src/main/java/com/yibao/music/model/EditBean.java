package com.yibao.music.model;

/**
 * @ Author: Luoshipeng
 * @ Name:   EditBean
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/11/26/ 3:11
 * @ Des:    编辑按钮显示Text(编辑/完成)和列表多选状态显示
 */
public class EditBean {
    // 0 - 10 PlayListFragment
    // 2 - 12 SongCategoryFragment
    // 3 - 13 AlbumCategoryFragment
    // 进入列表的多选状态和批量删除标识
    private int currentIndex;

    public EditBean(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }
}
