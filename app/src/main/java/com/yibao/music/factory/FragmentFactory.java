package com.yibao.music.factory;

import android.support.v4.app.Fragment;

import com.yibao.music.artisanlist.ArtistanListFragment;
import com.yibao.music.artisanlist.SongListFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Author：Sid
 * Des：${TODO}
 * Time:2017/5/7 14:55
 *
 * @author Stran
 */
public class FragmentFactory {

    private static Map<Integer, Fragment> mCacheFragmentMap = new HashMap<>();

    public static Fragment createFragment(int position) {

        Fragment fragment = null;


        //优先从集合中取出来
        if (mCacheFragmentMap.containsKey(position)) {
            fragment = mCacheFragmentMap.get(position);
            return fragment;
        }

        switch (position) {
            case 0:
                fragment = SongListFragment.newInstance();
                break;
            case 1:
                fragment = ArtistanListFragment.newInstance();
                break;
            default:
                break;
        }
        //保存fragment到集合中
        mCacheFragmentMap.put(position, fragment);

        return fragment;
    }
}
