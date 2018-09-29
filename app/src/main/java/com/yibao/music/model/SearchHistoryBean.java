package com.yibao.music.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.util.Comparator;

/**
 * @ Author: Luoshipeng
 * @ Name:   SearchHistoryBean
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/9/14/ 19:19
 * @ Des:    TODO
 */
@Entity
public class SearchHistoryBean implements Comparator<SearchHistoryBean> {
    @Id(autoincrement = true)
    private Long id;
    private String searchContent;
    private String searchTime;

    public SearchHistoryBean(String searchContent, String searchTime) {
        this.searchContent = searchContent;
        this.searchTime = searchTime;
    }


    @Generated(hash = 632630831)
    public SearchHistoryBean(Long id, String searchContent, String searchTime) {
        this.id = id;
        this.searchContent = searchContent;
        this.searchTime = searchTime;
    }


    @Generated(hash = 1570282321)
    public SearchHistoryBean() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSearchContent() {
        return searchContent;
    }

    public void setSearchContent(String searchContent) {
        this.searchContent = searchContent;
    }


    public String getSearchTime() {
        return this.searchTime;
    }


    public void setSearchTime(String searchTime) {
        this.searchTime = searchTime;
    }


    @Override
    public int compare(SearchHistoryBean o1, SearchHistoryBean o2) {
        return Integer.valueOf(o1.searchTime) - Integer.valueOf(o2.searchTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SearchHistoryBean that = (SearchHistoryBean) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (searchContent != null ? !searchContent.equals(that.searchContent) : that.searchContent != null)
            return false;
        return searchTime != null ? searchTime.equals(that.searchTime) : that.searchTime == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (searchContent != null ? searchContent.hashCode() : 0);
        result = 31 * result + (searchTime != null ? searchTime.hashCode() : 0);
        return result;
    }
}
