package com.yibao.music.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.yibao.music.fragment.SearchFragment;
import com.yibao.music.fragment.SongCategoryFragment;

/**
 * 作者：Stran on 2017/3/23 03:31
 * 描述：${TODO}
 * 邮箱：strangermy@outlook.com
 *
 * @author Stran
 */
public class SearchPagerAdapter
        extends FragmentStateAdapter {

    private String mArtist;
    public SearchPagerAdapter(@NonNull FragmentActivity fragmentActivity,String artist) {
        super(fragmentActivity);
        mArtist = artist;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return SearchFragment.newInstance(position,mArtist);
    }

    @Override
    public int getItemCount() {
        return 4;
    }

}
