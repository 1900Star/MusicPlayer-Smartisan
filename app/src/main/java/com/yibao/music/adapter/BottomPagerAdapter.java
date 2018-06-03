package com.yibao.music.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.yibao.music.base.factory.RecyclerFactory;
import com.yibao.music.base.listener.MyAnimatorUpdateListener;
import com.yibao.music.model.MusicBean;
import com.yibao.music.util.Constants;
import com.yibao.music.util.MusicListUtil;

import java.util.List;

/**
 * 作者：Stran on 2017/3/23 03:31
 * 描述：${TODO}
 * 邮箱：strangermy@outlook.com
 *
 * @author Stran
 */
public class BottomPagerAdapter
        extends PagerAdapter {
    private Context mContext;
    private List<List<MusicBean>> mList;


    public BottomPagerAdapter(Context context, List<List<MusicBean>> list) {
        this.mContext = context;
        this.mList = list;

    }


    @Override
    public int getCount() {
        return mList != null ? 2 : 0;

    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((View) object);

    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        BottomSheetAdapter adapter = new BottomSheetAdapter(mContext,mList.get(position));
        RecyclerView recyclerView = RecyclerFactory.creatRecyclerView(Constants.NUMBER_ONE, adapter);
        container.addView(recyclerView);
        return recyclerView;
    }


}
