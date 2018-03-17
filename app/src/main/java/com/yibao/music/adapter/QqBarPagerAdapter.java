package com.yibao.music.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding2.view.RxView;
import com.yibao.music.R;
import com.yibao.music.base.listener.MyAnimatorUpdateListener;
import com.yibao.music.base.listener.OnMusicItemClickListener;
import com.yibao.music.model.MusicBean;
import com.yibao.music.util.AnimationUtil;
import com.yibao.music.util.StringUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 作者：Stran on 2017/3/23 03:31
 * 描述：${TODO}
 * 邮箱：strangermy@outlook.com
 *
 * @author Stran
 */
public class QqBarPagerAdapter
        extends PagerAdapter {
    private Context mContext;
    private List<MusicBean> mList;
    private ObjectAnimator mAnimator;
    private MyAnimatorUpdateListener mAnimationListener;

    public QqBarPagerAdapter(Context context, List<MusicBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    public void setData(List<MusicBean> list) {
        if (mList != null) {
            mList.clear();
        }
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList != null ? mList.size() : 0;

    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (mAnimator != null || mAnimationListener != null) {
            mAnimator.cancel();
            mAnimationListener.pause();
        }
        container.removeView((View) object);

    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_music_pager, container, false);
        MusicBean info = mList.get(position);
        initView(info, view);
        view.setTag(position);

        initListener(view);
        container.addView(view);
        return view;
    }
    // 可以通过接口把view传过去，在Activity里面进行动画的控制

    private void initListener(View view) {
        RxView.clicks(view)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> {
                    if (mContext instanceof OnMusicItemClickListener) {
                        ((OnMusicItemClickListener) mContext).onOpenMusicPlayDialogFag();
                    }
                });
    }


    private void initView(MusicBean musicInfo, View view) {
        ImageView mAlbulm = view.findViewById(R.id.iv_pager_albulm);
        TextView songName = view.findViewById(R.id.tv_pager_song_name);
        TextView artName = view.findViewById(R.id.tv_pager_art_name);
        Uri albumUri = StringUtil.getAlbulm(musicInfo.getAlbumId());
        Glide.with(mContext)
                .load(albumUri.toString())
                .asBitmap()
                .error(R.drawable.sidebar_cover)
                .into(mAlbulm);
        songName.setText(musicInfo.getTitle());
        artName.setText(musicInfo.getArtist());
        if (mAnimator == null || mAnimationListener == null) {
            mAnimator = AnimationUtil.getRotation(mAlbulm);
            mAnimationListener = new MyAnimatorUpdateListener(mAnimator);
            mAnimator.start();
            mAnimationListener.play();
        } else {
            mAnimator.resume();
        }

    }


}
