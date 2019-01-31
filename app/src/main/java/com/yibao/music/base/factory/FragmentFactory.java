package com.yibao.music.base.factory;


import android.util.SparseArray;

import com.yibao.music.base.BaseMusicFragment;
import com.yibao.music.fragment.AboutFragment;
import com.yibao.music.fragment.AlbumFragment;
import com.yibao.music.fragment.ArtistFragment;
import com.yibao.music.fragment.PlayListFragment;
import com.yibao.music.fragment.SongFragment;
import com.yibao.music.util.Constants;

/**
 * Des：${TODO}
 * Time:2017/5/7 14:55
 *
 * @author Stran
 */
public class FragmentFactory {

    private static SparseArray<BaseMusicFragment> mTabFagArray = new SparseArray<>();

    public static BaseMusicFragment createFragment(int position) {

        BaseMusicFragment fragment = null;
        mTabFagArray.get(position);

        //优先从集合中取出来
        if (mTabFagArray.get(position) != null) {
            fragment = mTabFagArray.get(position);
            return fragment;
        }

        switch (position) {
            case 0:
                fragment = PlayListFragment.newInstance("lsp",null,false);
                break;
            case 1:
                fragment = ArtistFragment.newInstance();
                break;
            case 2:
                fragment = SongFragment.newInstance();
                break;
            case 3:
                fragment = AlbumFragment.newInstance();
                break;
            case 4:
                fragment = AboutFragment.newInstance();
                break;
            default:
                break;
        }
        //保存fragment到集合中
        mTabFagArray.put(position, fragment);

        return fragment;
    }

}
