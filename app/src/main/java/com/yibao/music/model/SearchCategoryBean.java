package com.yibao.music.model;

/**
 * @author: Luoshipeng
 * @ Name:    SearchCategoryBean
 * @ Email:   strangermy98@gmail.com
 * @ GitHub:  https://github.com/1900Star
 * @ Time:    2019/3/17/ 0:20
 * @ Des:     TODO
 */
public class SearchCategoryBean {
    /**
     * 1 输入框清空 、 2 搜索关键字 、 3 输入框为空、   9  输入框清空点击  、10 输入框为空 隐藏列表 显示历史搜索
     */
    private int categoryFlag;
    private String searchCondition;

    public SearchCategoryBean(int categoryFlag, String searchCondition) {
        this.categoryFlag = categoryFlag;
        this.searchCondition = searchCondition;
    }

    public int getCategoryFlag() {
        return categoryFlag;
    }

    public String getSearchCondition() {
        return searchCondition;
    }
}
