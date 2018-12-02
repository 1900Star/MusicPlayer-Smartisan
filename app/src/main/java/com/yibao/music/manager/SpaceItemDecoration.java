package com.yibao.music.manager;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @ Author: Luoshipeng
 * @ Name:   SpaceItemDecoration
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/12/2/ 0:33
 * @ Des:    TODO
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public SpaceItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//        //不是第一个的格子都设一个左边和底部的间距
//        outRect.left = space;
//        outRect.bottom = space;
//        //由于每行都只有3个，所以第一个都是3的倍数，把左边距设为0
//        if (parent.getChildLayoutPosition(view) % 3 == 0) {
//        }
            outRect.left = 0;
            outRect.top = 0;
            outRect.right = 0;
            outRect.bottom = 0;
    }
}
