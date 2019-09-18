package com.yibao.music.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.yibao.music.base.BasePagerAdapter;
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
        extends BasePagerAdapter {


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



}
