package com.yibao.music.base;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.yibao.music.R;

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
    protected List<T> mList = null;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private OnItemListener mListener;

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
        bindView(holder, mList.get(position));
        if (holder instanceof LoadMoreHolder) {
            LoadMoreHolder moreHolder = (LoadMoreHolder) holder;
            String count = (mList.size() - 1) + getLastItemDes();
            moreHolder.mSongCount.setText(count);
        }
    }

    /**
     * 获取列表的类型，根据类型设置最后一个item的文字内容。
     *
     * @return
     */

    protected abstract String getLastItemDes();

    /**
     * 具体的视图数据绑定交给子类去做
     *
     * @param holder 子类提供一个holder
     * @param t      数据类型由子类决定
     */
    protected abstract void bindView(RecyclerView.ViewHolder holder, T t);

    /**
     * 根据子类提供的布局ID得到一个RecyclerView的Item视图，并将视图交给子类的ViewHolder
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
        if (position == getItemCount() - 1) {
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
        notifyDataSetChanged();
    }


    public void addHeader(List<T> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void addFooter(List<T> list) {

        for (T t : list) {
            if (!mList.contains(t)) {
                mList.add(t);
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
        @BindView(R.id.tv_song_count)
        TextView mSongCount;
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

    /**
     * Item的点击事件
     */
    protected void openDetails() {
        if (mListener != null) {
            mListener.showDetailsView();
        }
    }

    public void setItemListener(OnItemListener listener) {
        this.mListener = listener;
    }

    public interface OnItemListener {
        /**
         * 打开列表详情
         */
        void showDetailsView();
    }
}
