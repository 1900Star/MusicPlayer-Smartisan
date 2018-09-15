package com.yibao.music.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.yibao.music.base.BasePagerAdapter;
import com.yibao.music.fragment.AlbumCategoryFragment;
import com.yibao.music.fragment.SongCategoryFragment;
import com.yibao.music.util.Constants;

/**
 * 作者：Stran on 2017/3/23 03:31
 * 描述：${TODO}
 * 邮箱：strangermy@outlook.com
 *
 * @author Stran
 */
public class AlbumCategoryPagerAdapter
        extends BasePagerAdapter {


    public AlbumCategoryPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return AlbumCategoryFragment.newInstance(position);
    }


    @Override
    public int getCount() {
        return Constants.NUMBER_TWO;
    }


}
