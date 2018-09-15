package com.yibao.music.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yibao.music.R;
import com.yibao.music.base.BaseRvAdapter;
import com.yibao.music.model.SearchHistoryBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @ Author: Luoshipeng
 * @ Name:   SearchRvAdapter
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/9/15/ 23:54
 * @ Des:    TODO
 */
public class SearchRvAdapter extends BaseRvAdapter<SearchHistoryBean> {
    public SearchRvAdapter(List<SearchHistoryBean> list) {
        super(list);
    }

    @Override
    protected String getLastItemDes() {
        return null;
    }

    @Override
    protected void bindView(RecyclerView.ViewHolder holder, SearchHistoryBean searchHistoryBean) {
        if (holder instanceof RvHolder) {
            RvHolder rvHolder = (RvHolder) holder;
            rvHolder.mGridTv.setText(searchHistoryBean.getSearchContent());
        }
    }

    @Override
    protected RecyclerView.ViewHolder getViewHolder(View view) {
        return new RvHolder(view);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.search_rv_item;
    }

    static class RvHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.grid_tv)
        TextView mGridTv;
        @BindView(R.id.grid_view_item)
        LinearLayout mGridViewItem;

        RvHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
