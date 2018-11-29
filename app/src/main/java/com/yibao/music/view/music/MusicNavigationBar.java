package com.yibao.music.view.music;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yibao.music.R;
import com.yibao.music.base.BaseFragment;
import com.yibao.music.base.BaseMusicFragment;
import com.yibao.music.util.ColorUtil;
import com.yibao.music.util.Constants;
import com.yibao.music.util.SpUtil;

import java.util.HashMap;


/**
 * @ Author: Luoshipeng
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
        switch (view.getId()) {

            case R.id.music_bar_playlist:
                switchMusicTabbar(Constants.NUMBER_ZOER);
                break;
            case R.id.music_bar_artisanlist:
                switchMusicTabbar(Constants.NUMBER_ONE);
                break;
            case R.id.music_bar_songlist:
                switchMusicTabbar(Constants.NUMBER_TWO);
                break;
            case R.id.music_bar_albumlist:
                switchMusicTabbar(Constants.NUMBER_THRRE);
                break;
            case R.id.music_bar_about:
                switchMusicTabbar(Constants.NUMBER_FOUR);
                break;
            default:
                break;
        }
    }

    public void switchMusicTabbar(int flag) {
        switch (flag) {
            case Constants.NUMBER_ZOER:
                setAllTabbarNotPressed(flag, R.string.play_list);
                mMusicBarPlaylistIv.setBackgroundResource(R.drawable.tabbar_playlist_selector);
                mMusicBarPlaylistTv.setTextColor(ColorUtil.musicbarTvDown);
                mMusicBarPlaylist.setBackground(getResources().getDrawable(R.drawable.tabbar_bg_down));
                setDetailFragmentFlag(Constants.FRAGMENT_PLAYLIST, Constants.NUMBER_EIGHT);
                break;
            case Constants.NUMBER_ONE:
                setAllTabbarNotPressed(flag, R.string.music_artisan);
                mMusicBarArtisanlistIv.setBackgroundResource(R.drawable.tabbar_artisanlist_selector);
                mMusicBarArtisanlistTv.setTextColor(ColorUtil.musicbarTvDown);
                mMusicBarArtisanlist.setBackground(getResources().getDrawable(R.drawable.tabbar_bg_down));
                setDetailFragmentFlag(Constants.FRAGMENT_ARTIST, Constants.NUMBER_NINE);
                break;
            case Constants.NUMBER_TWO:
                setAllTabbarNotPressed(flag, R.string.music_song);
                mMusicBarSonglistIv.setBackgroundResource(R.drawable.tabbar_songlist_selector);
                mMusicBarSonglistTv.setTextColor(ColorUtil.musicbarTvDown);
                mMusicBarSonglist.setBackground(getResources().getDrawable(R.drawable.tabbar_bg_down));
                // 没有详情页面，直接返回桌面。
//                SpUtil.setDetailsFlag(getContext(), Constants.NUMBER_ZOER);
                setDetailFragmentFlag(Constants.FRAGMENT_SONG_CATEGORY, Constants.NUMBER_ELEVEN);

                break;
            case Constants.NUMBER_THRRE:
                setAllTabbarNotPressed(flag, R.string.music_album);
                mMusicBarAlbumlistIv.setBackgroundResource(R.drawable.tabbar_albumlist_selector);
                mMusicBarAlbumlist.setBackground(getResources().getDrawable(R.drawable.tabbar_bg_down));
                mMusicBarAlbumlistTv.setTextColor(ColorUtil.musicbarTvDown);
                setDetailFragmentFlag(Constants.FRAGMENT_ALBUM, Constants.NUMBER_TEN);
                break;
            case Constants.NUMBER_FOUR:
                setAllTabbarNotPressed(flag, R.string.about);
                mMusicBarStylelistIv.setBackgroundResource(R.drawable.tabbar_stylelist_selector);
                mMusicBarStylelistTv.setTextColor(ColorUtil.musicbarTvDown);
                mMusicBarAboutLl.setBackground(getResources().getDrawable(R.drawable.tabbar_bg_down));
                // 没有详情页面，直接返回桌面。
                SpUtil.setDetailsFlag(getContext(), 12);
                break;
            default:
                break;
        }

    }

    /**
     * 判断mDetailsMap中是否包含当前的Fragment页面,如果有，就说明有详情页面打开。
     *
     * @param className      展示详情的Fragment  Key + 10 表示有编辑状态被打开
     * @param detailsViewKey 这个Key必须为 8 (PlayListFragment)、9 (ArtistFragment)、10 (AlbumFragment)、11 (SongCategoryFragment)
     *                       这三个整数，这样展示详情的Fragment就能自己处理返回事件。
     *                       tabBar的index + 20  (20PlayFag 、22SongFrag、23AlbumFag)表示有编辑状态被打开，返回时需要先关闭编辑状态。
     *                       0 表示交返回事件还给Activity处理
     */
    private void setDetailFragmentFlag(String className, int detailsViewKey) {
        HashMap<String, BaseFragment> detailsViewMap = BaseMusicFragment.mDetailsViewMap;
        if (detailsViewMap != null) {
            if (detailsViewMap.containsKey(className) || detailsViewMap.containsKey(Constants.FRAGMENT_ALBUM)) {
                SpUtil.setDetailsFlag(getContext(), detailsViewKey);
            }
        }
    }

    /**
     * 将Tabbar全部置于未选种状态
     *
     * @param flag            将ViewPager切换到选中的Tag
     * @param titleResourceId title
     */
    private void setAllTabbarNotPressed(int flag, int titleResourceId) {
        if (mBarSelecteListener != null) {
            mBarSelecteListener.currentFlag(flag, titleResourceId);
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

    private OnNavigationBarSelecteListener mBarSelecteListener;

    public void setOnNavigationbarListener(OnNavigationBarSelecteListener selecteListener) {
        mBarSelecteListener = selecteListener;
    }

    public interface OnNavigationBarSelecteListener {
        void currentFlag(int currentSelecteFlag, int titleResourceId);

    }
}
