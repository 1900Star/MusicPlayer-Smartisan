package com.yibao.music.model;

import android.support.annotation.NonNull;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.util.Comparator;

/**
 * @ Author: Luoshipeng
 * @ Name:   SearchHistoryBean
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/9/14/ 19:19
 * @ Des:    TODO
 * @author Luoshipeng
 */
@Entity
public class SearchHistoryBean implements Comparable<SearchHistoryBean> {
    @Id(autoincrement = true)
    private Long id;
    private String searchContent;
    private String searchTime;
    private boolean isSelected;

    public SearchHistoryBean(String searchContent, String searchTime) {
        this.searchContent = searchContent;
        this.searchTime = searchTime;
    }

    @Generated(hash = 102597726)
    public SearchHistoryBean(Long id, String searchContent, String searchTime, boolean isSelected) {
        this.id = id;
        this.searchContent = searchContent;
        this.searchTime = searchTime;
        this.isSelected = isSelected;
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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public int compareTo(@NonNull SearchHistoryBean o) {
        return Long.compare(Long.parseLong(o.getSearchTime()), Long.parseLong(this.getSearchTime()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SearchHistoryBean that = (SearchHistoryBean) o;

        if (isSelected != that.isSelected) return false;
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
        result = 31 * result + (isSelected ? 1 : 0);
        return result;
    }

    public boolean getIsSelected() {
        return this.isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
