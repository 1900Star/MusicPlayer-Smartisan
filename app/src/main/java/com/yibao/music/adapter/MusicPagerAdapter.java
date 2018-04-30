package com.yibao.music.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import com.yibao.music.base.factory.FragmentFactory;
import com.yibao.music.util.Constants;

/**
 * 作者：Stran on 2017/3/23 03:31
 * 描述：${TODO}
 * 邮箱：strangermy@outlook.com
 *
 * @author Stran
 */
public class MusicPagerAdapter
        extends FragmentPagerAdapter {

    public MusicPagerAdapter(FragmentManager fm) {
        super(fm);


    }

    @Override
    public Fragment getItem(int position) {
        return FragmentFactory.createFragment(position);
    }


    @Override
    public int getCount() {
        return Constants.NUMBER_FIEV;
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((Fragment) object).getView() == view;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }
}
