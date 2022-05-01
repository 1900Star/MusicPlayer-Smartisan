package com.yibao.music.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.yibao.music.fragment.AboutFragment;
import com.yibao.music.fragment.AlbumFragment;
import com.yibao.music.fragment.ArtistFragment;
import com.yibao.music.fragment.PlayListFragment;
import com.yibao.music.fragment.SongFragment;

/**
 * 作者：Stran on 2017/3/23 03:31
 * 描述：${TODO}
 * 邮箱：strangermy@outlook.com
 *
 * @author Stran
 */
public class MainViewPagerAdapter
        extends FragmentStateAdapter {


    public MainViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return getFragment(position);
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    private Fragment getFragment(int position) {
        Fragment fragment = SongFragment.Companion.newInstance();
        switch (position) {
            case 0:
                fragment = PlayListFragment.newInstance("lsp", null, false);
                break;
            case 1:
                fragment = ArtistFragment.Companion.newInstance();
                break;
            case 2:
                fragment = SongFragment.Companion.newInstance();
                break;
            case 3:
                fragment = AlbumFragment.Companion.newInstance();
                break;
            case 4:
                fragment = AboutFragment.Companion.newInstance();
                break;
            default:
                break;
        }
        return fragment;
    }
}
