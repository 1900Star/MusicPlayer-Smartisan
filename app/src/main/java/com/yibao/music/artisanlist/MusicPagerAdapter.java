package com.yibao.music.artisanlist;

import android.app.Fragment;
import android.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;

import com.yibao.music.album.BaseMusicStatePagerAdapter;
import com.yibao.music.factory.FragmentFactory;

/**
 * 作者：Stran on 2017/3/23 03:31
 * 描述：${TODO}
 * 邮箱：strangermy@outlook.com
 *
 * @author Stran
 */
public class MusicPagerAdapter
        extends BaseMusicStatePagerAdapter {

    public MusicPagerAdapter(FragmentManager fm) {
        super(fm);


    }

    @Override
    public Fragment getItem(int position) {
        return FragmentFactory.createFragment(position);
    }


    @Override
    public int getCount() {
        return 5;
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((Fragment) object).getView() == view;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }
}
