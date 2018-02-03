package com.yibao.music.base;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.yibao.biggirl.R;
import com.yibao.music.factory.RecyclerFactory;
import com.yibao.music.util.LogUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Des：${BaseRecyclerFragment}
 * Time:2017/6/4 21:55
 *
 * @author Stran
 */
public abstract class BaseRecyclerFragment
        extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener

{

    public int page = 1;
    public int size = 20;

    public FloatingActionButton mFab;
    public SwipeRefreshLayout mSwipeRefresh;
    public LinearLayout mFagContent;

    /**
     * 下拉刷新数据
     */
    protected abstract void refreshData();

    /**
     * RecyclerView上拉加载更多数据
     */
    protected abstract void loadMoreData();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.girls_frag);
        mSwipeRefresh = getViewById(R.id.swipe_refresh);
        mFagContent = getViewById(R.id.fag_content);
        mFab = getViewById(R.id.fab_fag);

        mSwipeRefresh.setColorSchemeColors(Color.BLUE, Color.RED, Color.YELLOW);
        mSwipeRefresh.setOnRefreshListener(this);
        mSwipeRefresh.setRefreshing(true);
    }

    /**
     * 得到一个RecyclerView   实现了加载更多
     *
     * @param fab
     * @param rvType
     * @param adapter
     * @return
     */
    public RecyclerView getRecyclerView(ImageView fab, int rvType, RecyclerView.Adapter<RecyclerView.ViewHolder> adapter) {
        RecyclerView recyclerView = RecyclerFactory.creatRecyclerView(rvType, adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                int lastPosition = -1;
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        Glide.with(mActivity).resumeRequests();
                        fab.setVisibility(android.view.View.VISIBLE);
                        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                        if (layoutManager instanceof GridLayoutManager) {
                            //通过LayoutManager找到当前显示的最后的item的position
                            lastPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
                        } else if (layoutManager instanceof LinearLayoutManager) {
                            lastPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                            int[] lastPositions = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                            ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(
                                    lastPositions);
                            lastPosition = findMax(lastPositions);
                        }
                        if (lastPosition == recyclerView.getLayoutManager()
                                .getItemCount() - 1) {
                            page++;
                            loadMoreData();

                        }
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        //当列表滑动停止时可以调用resumeRequests()恢复请求
                        Glide.with(mActivity).pauseRequests();
                        fab.setVisibility(android.view.View.INVISIBLE);
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        Glide.with(mActivity).pauseRequests();
                        fab.setVisibility(android.view.View.INVISIBLE);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                //得到当前显示的最后一个item的view
                View lastChildView = recyclerView.getLayoutManager().getChildAt(recyclerView.getLayoutManager().getChildCount() - 1);
                //得到lastChildView的bottom坐标值
                int lastChildBottom = lastChildView.getBottom();
                //得到Recyclerview的底部坐标减去底部padding值，也就是显示内容最底部的坐标
                int recyclerBottom = recyclerView.getBottom() - recyclerView.getPaddingBottom();
                //通过这个lastChildView得到这个view当前的position值
                int lastPosition = recyclerView.getLayoutManager().getPosition(lastChildView);

                //判断lastChildView的bottom值跟recyclerBottom
                //判断lastPosition是不是最后一个position
                //如果两个条件都满足则说明是真正的滑动到了底部,这时候就可以去加载更多了。
                if (lastChildBottom == recyclerBottom && lastPosition == recyclerView.getLayoutManager().getItemCount() - 1) {
//                    page++;
//                    loadMoreData();
                    LogUtil.d("");


                }
            }

        });


        return recyclerView;
    }

    @Override
    public void onRefresh() {

        Observable.timer(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    page = 1;
                    refreshData();
                });
    }


    /**
     * 找到数组中的最大值
     */
    public int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    @Override
    public void onResume() {
        super.onResume();
        Glide.with(mActivity).resumeRequests();
    }

    @Override
    public void onPause() {
        super.onPause();
        Glide.with(mActivity).pauseRequests();
    }
}
