package com.yibao.music.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.yibao.music.base.BasePagerAdapter;
import com.yibao.music.fragment.SearchFragment;
import com.yibao.music.util.Constants;

/**
 * 作者：Stran on 2017/3/23 03:31
 * 描述：${TODO}
 * 邮箱：strangermy@outlook.com
 *
 * @author Stran
 */
public class SearchCategoryPagerAdapter
        extends BasePagerAdapter {
    private String mArtist;

    public SearchCategoryPagerAdapter(FragmentManager fm, String artist) {
        super(fm);
        mArtist = artist;
    }

    @Override
    public Fragment getItem(int position) {
        return SearchFragment.newInstance(position,mArtist);
    }


    @Override
    public int getCount() {
        return Constants.NUMBER_FOUR;
    }


}
