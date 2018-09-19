package com.yibao.music.base.factory;


import android.util.SparseArray;

import com.yibao.music.base.BaseMusicFragment;
import com.yibao.music.fragment.AboutMusicFragment;
import com.yibao.music.fragment.AlbumMusicFragment;
import com.yibao.music.fragment.ArtistanListMusicFragment;
import com.yibao.music.fragment.PlayListMusicFragment;
import com.yibao.music.fragment.SongMusicFragment;

/**
 * Author：Sid
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
                fragment = PlayListMusicFragment.newInstance();
                break;
            case 1:
                fragment = ArtistanListMusicFragment.newInstance();
                break;
            case 2:
                fragment = SongMusicFragment.newInstance();
                break;
            case 3:
                fragment = AlbumMusicFragment.newInstance();
                break;
            case 4:
                fragment = AboutMusicFragment.newInstance();
                break;
            default:
                break;
        }
        //保存fragment到集合中
        mTabFagArray.put(position, fragment);

        return fragment;
    }

    public static void clearMap() {
        if (mTabFagArray != null) {
            mTabFagArray.clear();
            mTabFagArray = null;
        }

    }
}
