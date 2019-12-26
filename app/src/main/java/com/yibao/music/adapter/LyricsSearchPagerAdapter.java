package com.yibao.music.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.yibao.music.base.BasePagerAdapter;
import com.yibao.music.fragment.LyricsFragment;
import com.yibao.music.fragment.SongCategoryFragment;
import com.yibao.music.model.qq.SearchLyricsBean;
import com.yibao.music.util.Constants;

import java.util.List;

/**
 * 作者：Stran on 2017/3/23 03:31
 * 描述：${TODO}
 * 邮箱：strangermy@outlook.com
 *
 * @author Stran
 */
public class LyricsSearchPagerAdapter
        extends BasePagerAdapter {
    private List<SearchLyricsBean> mLyricsList;

    public LyricsSearchPagerAdapter(FragmentManager fm, List<SearchLyricsBean> lyricsList) {
        super(fm);
        mLyricsList = lyricsList;
    }

    @Override
    public Fragment getItem(int position) {
        return LyricsFragment.newInstance(position, mLyricsList.get(position).getLyrics());
    }


    @Override
    public int getCount() {
        return mLyricsList != null ? mLyricsList.size() : 0;
    }


}
