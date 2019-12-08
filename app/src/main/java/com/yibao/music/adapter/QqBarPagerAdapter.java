package com.yibao.music.adapter;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.yibao.music.R;
import com.yibao.music.base.listener.MyAnimatorUpdateListener;
import com.yibao.music.base.listener.OnMusicItemClickListener;
import com.yibao.music.model.MusicBean;
import com.yibao.music.util.FileUtil;
import com.yibao.music.util.ImageUitl;
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_music_pager, container, false);
        MusicBean info = mList.get(position);
        initView(info, view);
        initListener(view);
        container.addView(view);
        return view;
    }

    @SuppressLint("CheckResult")
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
        TextView tvSongName = view.findViewById(R.id.tv_pager_song_name);
        TextView tvArtist = view.findViewById(R.id.tv_pager_art_name);
        String albumUri = FileUtil.getAlbumUrl(musicInfo,1);
        ImageUitl.loadPlaceholder(mContext, albumUri, mAlbulm);
        String currentLyrics = musicInfo.getCurrentLyrics();
        tvSongName.setText(StringUtil.getTitle(musicInfo));
        tvArtist.setText(currentLyrics != null ? currentLyrics : StringUtil.getArtist(musicInfo));
//        if (currentLyrics != null) {
//            if (mAnimator == null || mAnimationListener == null) {
//                mAnimator = AnimationUtil.getRotation(mAlbulm);
//                mAnimationListener = new MyAnimatorUpdateListener(mAnimator);
//                mAnimator.start();
//                mAnimationListener.play();
//            } else {
//                mAnimator.resume();
//            }
//        }
    }


}
