package com.yibao.music.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.yibao.music.base.BasePagerAdapter;
import com.yibao.music.fragment.AlbumCategoryFragment;
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

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return AlbumCategoryFragment.newInstance(position);
    }


    @Override
    public int getCount() {
        return Constants.NUMBER_TWO;
    }


}
