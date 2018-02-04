package com.yibao.music.base;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.yibao.music.R;
import com.yibao.music.util.Constants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author：Sid
 * Des：${BaseAdapter}
 * Time:2017/6/2 13:07
 *
 * @author Stran
 */
public abstract class BaseRvAdapter<T>
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SectionIndexer

{
    public List<T> mList = null;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private static int mLoadMoreStatus = 0;
    private static final int Min_Num = 10;

    public BaseRvAdapter(List<T> list) {
        mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(getLayoutId(), parent, false);
            return getViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.load_more_footview, parent, false);
            return new LoadMoreHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // 处理加载更多时的各种状态
        if (holder instanceof LoadMoreHolder) {
            LoadMoreHolder loadMoreHolder = (LoadMoreHolder) holder;
            if (mLoadMoreStatus == Constants.LOADING_MORE_RV) {
                loadMoreHolder.mPbLoad.setVisibility(View.VISIBLE);
                loadMoreHolder.mTvLoadMoreStatus.setVisibility(View.INVISIBLE);
            } else if (mLoadMoreStatus == Constants.NOT_MORE_DATA_RV) {
                loadMoreHolder.mPbLoad.setVisibility(View.INVISIBLE);
                loadMoreHolder.mTvLoadMoreStatus.setVisibility(View.VISIBLE);
            } else if (mLoadMoreStatus == Constants.NOTHING_MORE_RV) {
                loadMoreHolder.mPbLoad.setVisibility(View.INVISIBLE);
                loadMoreHolder.mTvLoadMoreStatus.setVisibility(View.INVISIBLE);

            }
        } else {

            bindView(holder, mList.get(position));
        }
    }

    /**
     * 具体的视图数据绑定交给子类去做
     *
     * @param holder 子类提供一个holder
     * @param t      数据类型由子类决定
     */
    protected abstract void bindView(RecyclerView.ViewHolder holder, T t);

    /**
     * 根据子类提供的布局ID得到一个RecyclerView的Item视图，并将视图交给子类得到一个ViewHolder
     *
     * @param view 当前RecyclerView的Item视图
     * @return
     */
    protected abstract RecyclerView.ViewHolder getViewHolder(View view);


    @Override
    public int getItemCount() {

        return mList == null
                ? 0
                : mList.size();
    }


    @Override
    public int getItemViewType(int position) {
        if (mList.size()>= Min_Num &&position == getItemCount() - 1) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    /**
     * 子类提供一个布局ID
     *
     * @return
     */
    protected abstract int getLayoutId();

    public void clear() {
        mList.clear();
    }


    public void addHeader(List<T> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void addFooter(List<T> list) {
        if (list == null || list.size() == 0) {
            changeLoadMoreStatus(Constants.NOT_MORE_DATA_RV);
        } else {
            changeLoadMoreStatus(Constants.LOADING_MORE_RV);
            for (T t : list) {
                if (!mList.contains(t)) {
                    mList.add(t);
                }
            }
        }
        notifyDataSetChanged();


    }

    public void setNewData(List<T> data) {
        this.mList = data;
        notifyDataSetChanged();
    }

    public void addData(int position, List<T> data) {
        this.mList.addAll(position, data);
        this.notifyItemRangeInserted(position, data.size());
    }

    public void changeLoadMoreStatus(int loadStatus) {
        mLoadMoreStatus = loadStatus;
        notifyDataSetChanged();
    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    @Override
    public int getPositionForSection(int i) {
        return 0;
    }

    @Override
    public int getSectionForPosition(int i) {
        return 0;
    }


    public List<T> getData() {
        return mList;
    }


    static class LoadMoreHolder
            extends RecyclerView.ViewHolder {
        @BindView(R.id.pbLoad)
        ProgressBar mPbLoad;
        @BindView(R.id.tvLoadText)
        TextView mTvLoadMoreStatus;
        @BindView(R.id.loadLayout)
        LinearLayout mLoadLayout;

        LoadMoreHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }


    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
        if (params != null && params instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) params;
            p.setFullSpan(holder.getLayoutPosition() == getItemCount() - 1);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return getItemViewType(position) == TYPE_FOOTER
                            ? gridManager.getSpanCount()
                            : 1;
                }
            });
        }
    }


}
