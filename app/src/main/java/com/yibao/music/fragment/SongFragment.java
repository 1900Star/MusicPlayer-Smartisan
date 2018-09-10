package com.yibao.music.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yibao.music.R;
import com.yibao.music.adapter.SongAdapter;
import com.yibao.music.adapter.SongCategoryPagerAdapter;
import com.yibao.music.artisanlist.MusicPagerListener;
import com.yibao.music.base.BaseFragment;
import com.yibao.music.model.MusicBean;
import com.yibao.music.util.ColorUtil;
import com.yibao.music.util.Constants;
import com.yibao.music.util.MusicListUtil;
import com.yibao.music.util.SharePrefrencesUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.artisanlist
 * @文件名: SongFragment
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/4 21:45
 * @描述： {显示当前音乐列表}
 */

public class SongFragment extends BaseFragment {
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
    @BindView(R.id.vp_song_fag)
    ViewPager mViewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.song_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        initData();
        initListener();

        return view;
    }

    private void initData() {

    }

    private void initListener() {
        SongCategoryPagerAdapter adapter = new SongCategoryPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new MusicPagerListener() {
            @Override
            public void onPageSelected(int position) {
                switchListCategory(position);
            }
        });
    }


    @OnClick({R.id.iv_music_category_paly,
            R.id.tv_music_category_songname, R.id.tv_music_category_score, R.id.tv_music_category_frequency, R.id.tv_music_category_addtime, R.id.musci_view})
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
            case R.id.musci_view:
                break;
            default:
                break;
        }
    }


    private void switchListCategory(int flag) {
        mViewPager.setCurrentItem(flag, false);
        switch (flag) {
            case 0:
                setAllCategoryNotNormal(Constants.NUMBER_ONE);
                mMusicCategorySongName.setTextColor(ColorUtil.wihtle);
                mMusicCategorySongName.setBackgroundResource(R.drawable.btn_category_songname_down_selector);
//                initData(true, Constants.NUMBER_ZOER);
                break;
            case 1:
                setAllCategoryNotNormal(Constants.NUMBER_TWO);
                mMusicCategoryScore.setTextColor(ColorUtil.wihtle);
                mMusicCategoryScore.setBackgroundResource(R.drawable.btn_category_score_down_selector);
                break;
            case 2:
                setAllCategoryNotNormal(Constants.NUMBER_THRRE);
                mMusicCategoryFrequency.setTextColor(ColorUtil.wihtle);
                mMusicCategoryFrequency.setBackgroundResource(R.drawable.btn_category_score_down_selector);
                break;
            case 3:
                setAllCategoryNotNormal(Constants.NUMBER_FOUR);
                mMusicCategoryAddtime.setBackgroundResource(R.drawable.btn_category_views_down_selector);
                mMusicCategoryAddtime.setTextColor(ColorUtil.wihtle);
//                initData(false, Constants.NUMBER_ONE);
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
        SharePrefrencesUtil.setMusicDataListFlag(mActivity, playListFlag);
    }


    public static SongFragment newInstance() {

        return new SongFragment();
    }

}
