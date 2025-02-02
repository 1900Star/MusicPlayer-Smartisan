package com.yibao.music.view.music;

import android.content.Context;
import android.graphics.Color;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yibao.music.R;
import com.yibao.music.util.ColorUtil;
import com.yibao.music.util.Constant;


/**
 * @author Luoshipeng
 * @ Name:   NavigationBar
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/8/11/ 0:18
 * @ Des:     底部的导航Tab
 */
public class MusicNavigationBar extends LinearLayout implements View.OnClickListener {
    ImageView mMusicBarPlaylistIv;
    TextView mMusicBarPlaylistTv;
    LinearLayout mMusicBarPlaylist;
    ImageView mMusicBarArtisanlistIv;
    TextView mMusicBarArtisanlistTv;
    LinearLayout mMusicBarArtisanlist;
    ImageView mMusicBarSonglistIv;
    TextView mMusicBarSonglistTv;
    LinearLayout mMusicBarSonglist;
    ImageView mMusicBarAlbumlistIv;
    TextView mMusicBarAlbumlistTv;
    LinearLayout mMusicBarAlbumlist;
    ImageView mMusicBarStylelistIv;
    TextView mMusicBarStylelistTv;
    LinearLayout mMusicBarAboutLl;
    private final int mNormalTabbarColor = Color.parseColor("#939396");

    public MusicNavigationBar(Context context) {
        super(context);
        initView();
    }


    public MusicNavigationBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.music_bar_playlist) {
            switchMusicTabBar(Constant.NUMBER_ZERO);
        } else if (id == R.id.music_bar_artisanlist) {
            switchMusicTabBar(Constant.NUMBER_ONE);
        } else if (id == R.id.music_bar_songlist) {
            switchMusicTabBar(Constant.NUMBER_TWO);
        } else if (id == R.id.music_bar_albumlist) {
            switchMusicTabBar(Constant.NUMBER_THREE);
        } else if (id == R.id.music_bar_about) {
            switchMusicTabBar(Constant.NUMBER_FOUR);
        }
    }

    public void switchMusicTabBar(int flag) {
        setAllTabbarNotPressed(flag);
        switch (flag) {
            case Constant.NUMBER_ZERO:
                setIcon2Color(mMusicBarPlaylistIv, mMusicBarPlaylistTv, R.drawable.tabbar_playlist_selector);
                setBg(mMusicBarPlaylist);
                break;
            case Constant.NUMBER_ONE:
                setBg(mMusicBarArtisanlist);
                setIcon2Color(mMusicBarArtisanlistIv,
                        mMusicBarArtisanlistTv, R.drawable.tabbar_artisanlist_selector);
                break;
            case Constant.NUMBER_TWO:
                setIcon2Color(mMusicBarSonglistIv, mMusicBarSonglistTv, R.drawable.tabbar_songlist_selector);
                setBg(mMusicBarSonglist);
                break;
            case Constant.NUMBER_THREE:
                setIcon2Color(mMusicBarAlbumlistIv, mMusicBarAlbumlistTv, R.drawable.tabbar_albumlist_selector);
                setBg(mMusicBarAlbumlist);
                break;
            case Constant.NUMBER_FOUR:
                setIcon2Color(mMusicBarStylelistIv, mMusicBarStylelistTv, R.drawable.tabbar_stylelist_selector);
                setBg(mMusicBarAboutLl);
                break;
            default:
                break;
        }

    }

    private void setBg(LinearLayout layout) {
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.tabbar_bg_down, null);
        layout.setBackground(drawable);
    }

    private void setIcon2Color(ImageView iv, TextView tv, int ivRes) {
        iv.setBackgroundResource(ivRes);
        tv.setTextColor(ColorUtil.musicbarTvDown);
    }

    /**
     * 将Tabbar全部置于未选种状态
     *
     * @param flag 将ViewPager切换到选中的Tag
     */
    private void setAllTabbarNotPressed(int flag) {
        if (mBarSelectListener != null) {
            mBarSelectListener.currentFlag(flag);
        }
        mMusicBarPlaylist.setBackgroundColor(ColorUtil.wihtle);
        mMusicBarPlaylistIv.setBackgroundResource(R.drawable.tabbar_playlist_down_selector);
        mMusicBarPlaylistTv.setTextColor(mNormalTabbarColor);

        mMusicBarArtisanlist.setBackgroundColor(ColorUtil.wihtle);
        mMusicBarArtisanlistIv.setBackgroundResource(R.drawable.tabbar_artisanlist_down_selector);
        mMusicBarArtisanlistTv.setTextColor(mNormalTabbarColor);

        mMusicBarSonglist.setBackgroundColor(ColorUtil.wihtle);
        mMusicBarSonglistIv.setBackgroundResource(R.drawable.tabbar_songlist_down_selector);
        mMusicBarSonglistTv.setTextColor(mNormalTabbarColor);

        mMusicBarAlbumlist.setBackgroundColor(ColorUtil.wihtle);
        mMusicBarAlbumlistIv.setBackgroundResource(R.drawable.tabbar_albumlist_down_selector);
        mMusicBarAlbumlistTv.setTextColor(mNormalTabbarColor);

        mMusicBarAboutLl.setBackgroundColor(ColorUtil.wihtle);
        mMusicBarStylelistIv.setBackgroundResource(R.drawable.tabbar_stylelist_down_selector);
        mMusicBarStylelistTv.setTextColor(mNormalTabbarColor);
    }

    private void initListener() {
        mMusicBarAboutLl.setOnClickListener(this);
        mMusicBarPlaylist.setOnClickListener(this);
        mMusicBarSonglist.setOnClickListener(this);
        mMusicBarAlbumlist.setOnClickListener(this);
        mMusicBarArtisanlist.setOnClickListener(this);
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.music_navigationbar, this, true);

        mMusicBarPlaylistIv = findViewById(R.id.music_bar_playlist_iv);
        mMusicBarPlaylistTv = findViewById(R.id.music_bar_playlist_tv);
        mMusicBarPlaylist = findViewById(R.id.music_bar_playlist);

        mMusicBarArtisanlistIv = findViewById(R.id.music_bar_artisanlist_iv);
        mMusicBarArtisanlistTv = findViewById(R.id.music_bar_artisanlist_tv);
        mMusicBarArtisanlist = findViewById(R.id.music_bar_artisanlist);

        mMusicBarSonglistIv = findViewById(R.id.music_bar_songlist_iv);
        mMusicBarSonglistTv = findViewById(R.id.music_bar_songlist_tv);
        mMusicBarSonglist = findViewById(R.id.music_bar_songlist);

        mMusicBarAlbumlistIv = findViewById(R.id.music_bar_albumlist_iv);
        mMusicBarAlbumlistTv = findViewById(R.id.music_bar_albumlist_tv);
        mMusicBarAlbumlist = findViewById(R.id.music_bar_albumlist);

        mMusicBarStylelistIv = findViewById(R.id.music_bar_stylelist_iv);
        mMusicBarStylelistTv = findViewById(R.id.music_bar_stylelist_tv);
        mMusicBarAboutLl = findViewById(R.id.music_bar_about);
        initListener();
    }

    private OnNavigationBarSelectListener mBarSelectListener;

    public void setOnNavigationBarListener(OnNavigationBarSelectListener selectListener) {
        mBarSelectListener = selectListener;
    }

    public interface OnNavigationBarSelectListener {
        void currentFlag(int currentSelectFlag);

    }
}
