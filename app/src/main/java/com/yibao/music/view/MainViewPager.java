package com.yibao.music.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * @ Author: Luoshipeng
 * @ Name:   MainViewPager
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/4/30/ 19:46
 * @ Des:    //TODO
 */
public class MainViewPager extends ViewPager {
    public MainViewPager(@NonNull Context context) {
        super(context);
    }

    public MainViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     *
     * @param ev d
     * @return  返回True时加上这行代码  mMusicNavigationBar.switchMusicTabbar(position);
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

}
