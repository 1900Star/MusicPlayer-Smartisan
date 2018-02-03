package com.yibao.music.util;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

/**
 * Author：Sid
 * Des：${RecyclerView滚动监听帮助类}
 * Time:2017/7/19 04:18
 */
public class RvScrollHelper {


    private static boolean isLastCountItem=true;


    public static boolean scrollHelper(FloatingActionButton fab, RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                int lastPosition = -1;
                switch (newState) {
                    //当前状态为停止滑动状态SCROLL_STATE_IDLE时
                    case RecyclerView.SCROLL_STATE_IDLE:
                        fab.setVisibility(View.VISIBLE);
                        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                        if (layoutManager instanceof GridLayoutManager) {
                            //通过LayoutManager找到当前显示的最后的item的position
                            lastPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
                        } else if (layoutManager instanceof LinearLayoutManager) {
                            lastPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                            //因为StaggeredGridLayoutManager的特殊性可能导致最后显示的item存在多个，所以这里取到的是一个数组
                            //得到这个数组后再取到数组中position值最大的那个就是最后显示的position值了
                            int[] lastPositions = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                            ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(
                                    lastPositions);
                            lastPosition = findMax(lastPositions);
                        }

                        //时判断界面显示的最后item的position是否等于itemCount总数-1也就是最后一个item的position
                        //如果相等则说明已经滑动到最后了
                        if (lastPosition == recyclerView.getLayoutManager()
                                                        .getItemCount() - 1)
                        {
                            isLastCountItem = true;
                        }
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        fab.setVisibility(View.INVISIBLE);
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        fab.setVisibility(View.INVISIBLE);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                //得到当前显示的最后一个item的view
                View lastChildView = recyclerView.getLayoutManager()
                                                 .getChildAt(recyclerView.getLayoutManager()
                                                                         .getChildCount() - 1);
                //得到lastChildView的bottom坐标值
                int lastChildBottom = lastChildView.getBottom();
                //得到Recyclerview的底部坐标减去底部padding值，也就是显示内容最底部的坐标
                int recyclerBottom = recyclerView.getBottom() - recyclerView.getPaddingBottom();
                //通过这个lastChildView得到这个view当前的position值
                int lastPosition = recyclerView.getLayoutManager()
                                               .getPosition(lastChildView);

                //判断lastChildView的bottom值跟recyclerBottom
                //判断lastPosition是不是最后一个position
                //如果两个条件都满足则说明是真正的滑动到了底部
                if (lastChildBottom == recyclerBottom && lastPosition == recyclerView.getLayoutManager()
                                                                                     .getItemCount() - 1)
                {
                    //                    mProgressBar.setVisibility(View.VISIBLE);
                    isLastCountItem = true;

                }
            }

        });
        LogUtil.d("这是最后一个了  :" + isLastCountItem);
        return isLastCountItem;
    }

    //找到数组中的最大值
    private static int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

}
