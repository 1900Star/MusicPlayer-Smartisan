package com.yibao.music.view.music;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yibao.music.MusicApplication;
import com.yibao.music.R;
import com.yibao.music.adapter.QqBarPagerAdapter;
import com.yibao.music.base.listener.MusicPagerListener;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.greendao.MusicBeanDao;
import com.yibao.music.util.Constant;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.QueryMusicFlagListUtil;
import com.yibao.music.util.SpUtils;
import com.yibao.music.view.MusicProgressView;

import java.util.List;

/**
 * @author Luoshipeng
 * @ Name:   QqControlBar
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/8/11/ 21:50
 * @ Des:    TODO
 */
public class QqControlBar extends LinearLayout implements View.OnClickListener {
    LinearLayout mQqMusicBar;
    MusicProgressView mButtonPlay;
    ImageView mButtonFavorite;
    ViewPager mSlideViewPager;
    private QqBarPagerAdapter mPagerAdapter;

    public QqControlBar(Context context) {
        super(context);
        initView();
    }

    public QqControlBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.muisc_tabbar_qq, this, true);
        mQqMusicBar = findViewById(R.id.qq_music_bar);
        mSlideViewPager = findViewById(R.id.qq_music_vp);
        mButtonPlay = findViewById(R.id.music_floating_pager_play);
        mButtonFavorite = findViewById(R.id.music_floating_pager_favorite);
        initData();
        initListener();
    }


    private void initData() {
        mPagerAdapter = new QqBarPagerAdapter(getContext(), null);
        mSlideViewPager.setAdapter(mPagerAdapter);

    }

    private void initListener() {
        mButtonFavorite.setOnClickListener(this);
        mButtonPlay.setOnClickListener(this);
        mSlideViewPager.addOnPageChangeListener(new MusicPagerListener() {
            @Override
            public void onPageSelected(int position) {
//                if (mSelectListener != null) {
//                    mSelectListener.selectPosition(position);
//                    setPagerCurrentItem(position);
//                }
            }
        });
    }

    //**************按钮状态********************
    public void setPlayButtonState(int resourceId) {
        mButtonPlay.setIcon(resourceId);
    }

    public void updatePlayButtonState(boolean isPlaying) {
        mButtonPlay.setIcon(isPlaying ? R.drawable.btn_playing_pause_selector : R.drawable.btn_playing_play_selector);
    }

    public void setFavoriteButtonState(boolean isFavorite) {
        mButtonFavorite.setImageResource(isFavorite ? R.drawable.btn_favorite_red_selector : R.drawable.btn_favorite_gray_selector);
    }

    // **************ViewPager数据********************

    /**
     * 更新viewpager 数据
     */
    public void updatePagerData(List<MusicBean> musicItems, int currentPosition) {
        setPagerData();
    }

    /**
     * 设置ViewPager数据
     */
    public void setPagerData() {
        SpUtils sp = new SpUtils(MusicApplication.getInstance(), Constant.MUSIC_CONFIG);
        int position = sp.getInt(Constant.MUSIC_POSITION);
        List<MusicBean> currentList = getCurrentList();
        mPagerAdapter = new QqBarPagerAdapter(getContext(), currentList);
        mSlideViewPager.setAdapter(mPagerAdapter);
        mSlideViewPager.setCurrentItem(position, false);
    }

    /**
     * 设置当前播放位置数据
     */
    public void setPagerCurrentItem() {
        setPagerData();
    }


    protected List<MusicBean> getCurrentList() {
        SpUtils sp = new SpUtils(MusicApplication.getInstance(), Constant.MUSIC_CONFIG);
        int pageType = sp.getInt(Constant.PAGE_TYPE);
        String condition = sp.getString(Constant.CONDITION);
        MusicBeanDao mMusicDao = MusicApplication.getInstance().getMusicDao();
//        LogUtil.d("lsp", " QQBar 数据源 pageType  ==   " + pageType + "  condition  =  " + condition );

        return QueryMusicFlagListUtil.getMusicDataList(mMusicDao.queryBuilder(), pageType, condition);
    }


    //**************歌曲进度********************
    public void setMaxProgress(int maxProgress) {
        mButtonPlay.setMax(maxProgress);
    }

    public void setProgress(int currentProgress) {
        mButtonPlay.setProgress(currentProgress);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.music_floating_pager_play) {
            controlBarClick(Constant.NUMBER_ONE);
        } else if (id == R.id.music_floating_pager_favorite) {
            controlBarClick(Constant.NUMBER_TWO);
        }
    }


    private void controlBarClick(int clickFlag) {
        if (mButtonClickListener != null) {
            mButtonClickListener.click(clickFlag);
        }
    }

    //**************按钮点击监听********************

    private OnButtonClickListener mButtonClickListener;

    public void setOnButtonClickListener(OnButtonClickListener buttonClickListener) {
        mButtonClickListener = buttonClickListener;
    }

    public interface OnButtonClickListener {
        void click(int clickFlag);
    }

    private OnPagerSelectListener mSelectListener;

    public void setOnPagerSelectListener(OnPagerSelectListener selectListener) {
        mSelectListener = selectListener;
    }

    public interface OnPagerSelectListener {
        /**
         * p
         *
         * @param currentPosition d
         */
        void selectPosition(int currentPosition);
    }
}
