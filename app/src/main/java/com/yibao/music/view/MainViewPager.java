package com.yibao.music.view;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @ Author: Luoshipeng
 * @ Name:   MainViewPager
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/4/30/ 19:46
 * @ Des:    //TODO
 * @author Luoshipeng
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

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }
}
