package com.yibao.music.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.yibao.music.base.BasePagerAdapter;
import com.yibao.music.fragment.SearchFragment;
import com.yibao.music.fragment.SongCategoryFragment;
import com.yibao.music.util.Constants;

/**
 * 作者：Stran on 2017/3/23 03:31
 * 描述：${TODO}
 * 邮箱：strangermy@outlook.com
 *
 * @author Stran
 */
public class SongCategoryPagerAdapter
        extends BasePagerAdapter {
    /**
     * 1 SongFragment   2  SearchFragment
     */
    private int mFlag;

    public SongCategoryPagerAdapter(FragmentManager fm, int flag) {
        super(fm);
        mFlag = flag;
    }

    @Override
    public Fragment getItem(int position) {
        return mFlag == Constants.NUMBER_ONE ? SongCategoryFragment.newInstance(position) : SearchFragment.newInstance(position);
    }


    @Override
    public int getCount() {
        return Constants.NUMBER_FOUR;
    }


}
