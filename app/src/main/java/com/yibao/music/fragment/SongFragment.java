package com.yibao.music.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yibao.music.R;
import com.yibao.music.adapter.SongViewPagerAdapter;
import com.yibao.music.base.BaseMusicFragment;
import com.yibao.music.util.ColorUtil;
import com.yibao.music.util.Constants;
import com.yibao.music.util.SpUtil;
import com.yibao.music.view.music.MusicToolBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.artisanlist
 * @文件名: SongFragment
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/4 21:45
 * @描述： {歌曲TAB}
 */

public class SongFragment extends BaseMusicFragment {

    @BindView(R.id.music_toolbar_list)
    MusicToolBar mMusicToolBar;
    @BindView(R.id.iv_music_category_paly)
    ImageView mIvMusicCategoryPaly;
    @BindView(R.id.tv_music_category_songname)
    TextView mMusicCategorySongName;
    @BindView(R.id.tv_music_category_score)
    TextView mMusicCategoryScore;
    @BindView(R.id.tv_music_category_frequency)
    TextView mMusicCategoryFrequency;
    @BindView(R.id.tv_music_category_addtime)
    TextView mMusicCategoryAddtime;
    @BindView(R.id.view_pager2_song)
    ViewPager2 mViewPager2;
    private int curentIndex = 0;
    private boolean isSelecteStatus = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.song_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        initData();
        initListener();
        return view;
    }

    @Override
    protected boolean getIsOpenDetail() {
        return isSelecteStatus;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMusicToolBar.setToolbarTitle(getString(R.string.music_song));
        initRxBusData();
        switchListCategory(curentIndex);
    }

    private void initRxBusData() {
        disposeToolbar();
        if (mEditDisposable == null) {
            mEditDisposable = mBus.toObservableType(Constants.FRAGMENT_SONG, Object.class).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(editBean -> {
                        mMusicToolBar.setTvEditText(R.string.tv_edit);
                        mMusicToolBar.setTvDeleteVisibility(View.GONE);
                        isSelecteStatus = false;
                    });
        }
    }

    private void initData() {
        switchListCategory(0);
        SongViewPagerAdapter pagerAdapter = new SongViewPagerAdapter(mActivity);
        mViewPager2.setOffscreenPageLimit(4);
        mViewPager2.setAdapter(pagerAdapter);
        mViewPager2.setUserInputEnabled(false);
        mViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switchListCategory(position);
            }
        });

    }

    private void initListener() {
        mMusicToolBar.setClickListener(new MusicToolBar.OnToolbarClickListener() {
            @Override
            public void clickEdit() {
                interceptBackEvent(isSelecteStatus ? Constants.NUMBER_ELEVEN : Constants.NUMBER_ZERO);
                mBus.post(Constants.SONG_FAG_EDIT, Constants.NUMBER_ONE);
                mMusicToolBar.setTvEditText(isSelecteStatus ? R.string.tv_edit : R.string.complete);
                mMusicToolBar.setTvDeleteVisibility(isSelecteStatus ? View.GONE : View.VISIBLE);
                isSelecteStatus = !isSelecteStatus;
            }

            @Override
            public void switchMusicControlBar() {
                switchControlBar();
            }

            @Override
            public void clickDelete() {
                mBus.post(Constants.SONG_FAG_EDIT, Constants.NUMBER_TWO);
            }
        });
    }

    @OnClick({R.id.iv_music_category_paly,
            R.id.tv_music_category_songname, R.id.tv_music_category_score, R.id.tv_music_category_frequency, R.id.tv_music_category_addtime})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_music_category_paly:
                randomPlayMusic();
                break;
            case R.id.tv_music_category_songname:
                switchListCategory(0);
                break;
            case R.id.tv_music_category_score:
                switchListCategory(1);
                break;
            case R.id.tv_music_category_frequency:
                switchListCategory(2);
                break;
            case R.id.tv_music_category_addtime:
                switchListCategory(3);
                break;
            default:
                break;
        }
    }


    private void switchListCategory(int flag) {
        curentIndex = flag;
        mViewPager2.setCurrentItem(flag, false);

        switch (flag) {
            case 0:
                setAllCategoryNotNormal(Constants.NUMBER_ONE);
                mMusicCategorySongName.setTextColor(ColorUtil.wihtle);
                mMusicCategorySongName.setBackgroundResource(R.drawable.btn_category_songname_down_selector);
                break;
            case 1:
                setAllCategoryNotNormal(Constants.NUMBER_TWO);
                mMusicCategoryScore.setTextColor(ColorUtil.wihtle);
                mMusicCategoryScore.setBackgroundResource(R.drawable.btn_category_score_down_selector);
                break;
            case 2:
                setAllCategoryNotNormal(Constants.NUMBER_THREE);
                mMusicCategoryFrequency.setTextColor(ColorUtil.wihtle);
                mMusicCategoryFrequency.setBackgroundResource(R.drawable.btn_category_score_down_selector);
                break;
            case 3:
                setAllCategoryNotNormal(Constants.NUMBER_FOUR);
                mMusicCategoryAddtime.setBackgroundResource(R.drawable.btn_category_views_down_selector);
                mMusicCategoryAddtime.setTextColor(ColorUtil.wihtle);
                break;
            default:
                break;
        }


    }


    private void setAllCategoryNotNormal(int playListFlag) {
        mMusicCategorySongName.setTextColor(ColorUtil.textName);
        mMusicCategorySongName.setBackgroundResource(R.drawable.btn_category_songname_selector);
        mMusicCategoryScore.setTextColor(ColorUtil.textName);
        mMusicCategoryScore.setBackgroundResource(R.drawable.btn_category_score_selector);
        mMusicCategoryFrequency.setTextColor(ColorUtil.textName);
        mMusicCategoryFrequency.setBackgroundResource(R.drawable.btn_category_score_selector);
        mMusicCategoryAddtime.setTextColor(ColorUtil.textName);
        mMusicCategoryAddtime.setBackgroundResource(R.drawable.btn_category_views_selector);
        SpUtil.setSortFlag(mActivity, playListFlag);
    }

    public static SongFragment newInstance() {

        return new SongFragment();
    }

}
