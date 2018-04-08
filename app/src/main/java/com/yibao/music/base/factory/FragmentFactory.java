package com.yibao.music.base.factory;

import com.yibao.music.base.BaseFragment;
import com.yibao.music.fragment.AlbumFragment;
import com.yibao.music.fragment.ArtistanListFragment;
import com.yibao.music.fragment.AboutFragment;
import com.yibao.music.fragment.PlayListFragment;
import com.yibao.music.fragment.SongFragment;

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

    private static Map<Integer, BaseFragment> mCacheFragmentMap = new HashMap<>(16);

    public static BaseFragment createFragment(int position) {

        BaseFragment fragment = null;


        //优先从集合中取出来
        if (mCacheFragmentMap.containsKey(position)) {
            fragment = mCacheFragmentMap.get(position);
            return fragment;
        }

        switch (position) {
            case 0:
                fragment = PlayListFragment.newInstance();
                break;
            case 1:
                fragment = ArtistanListFragment.newInstance();
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
        mCacheFragmentMap.put(position, fragment);

        return fragment;
    }
}
