package com.yibao.music.artisanlist;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding2.view.RxView;
import com.yibao.music.R;
import com.yibao.music.base.listener.OnMusicItemClickListener;
import com.yibao.music.model.MusicBean;
import com.yibao.music.util.StringUtil;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * 作者：Stran on 2017/3/23 03:31
 * 描述：${TODO}
 * 邮箱：strangermy@outlook.com
 * @author Stran
 */
public class QqBarPagerAdapter
        extends android.support.v4.view.PagerAdapter {
    private Context mContext;
    private ArrayList<MusicBean> mList;
    private int mcurrentPostition;

    public QqBarPagerAdapter(Context context, ArrayList<MusicBean> list, int currentPosition) {
        this.mContext = context;
        this.mList = list;
        this.mcurrentPostition = currentPosition;
    }

    public void setData(ArrayList<MusicBean> list) {
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
        ImageView albulm = view.findViewById(R.id.iv_pager_albulm);
        TextView songName = view.findViewById(R.id.tv_pager_song_name);
        TextView artName = view.findViewById(R.id.tv_pager_art_name);
        Uri albumUri = StringUtil.getAlbulm(musicInfo.getAlbumId());
        Glide.with(mContext)
                .load(albumUri.toString())
                .asBitmap()
                .error(R.drawable.sidebar_cover)
                .into(albulm);
        songName.setText(musicInfo.getTitle());
        artName.setText(musicInfo.getArtist());

    }


}
