package com.yibao.music.activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;

import androidx.viewpager2.widget.ViewPager2;

import com.yibao.music.R;
import com.yibao.music.adapter.SearchLyricsPagerAdapter;
import com.yibao.music.base.BaseObserver;
import com.yibao.music.base.bindings.BaseBindingActivity;
import com.yibao.music.databinding.ActivitySearchLyricsBinding;
import com.yibao.music.model.qq.SearchLyricsBean;
import com.yibao.music.model.qq.SongLrc;
import com.yibao.music.network.RetrofitHelper;
import com.yibao.music.util.Constant;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.NetworkUtil;
import com.yibao.music.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author lsp
 * createDate：2019/12/26 0026 14:54
 * className   SearchLyricsActivity
 * Des：TODO
 */
public class SearchLyricsActivity extends BaseBindingActivity<ActivitySearchLyricsBinding> {
    protected final String TAG = "====" + this.getClass().getSimpleName() + "    ";

    private List<SearchLyricsBean> mLyricsBeanList;
    private String mSongMid;


    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        mLyricsBeanList = new ArrayList<>();
        String mSongName = getIntent().getStringExtra(Constant.SONG_NAME);
        String mSongArtist = getIntent().getStringExtra(Constant.SONG_ARTIST);
        LogUtil.d(TAG, mSongName + " == " + mSongArtist);
        if (mSongName != null && mSongArtist != null) {
            mBinding.editSearchLyricsName.setText(mSongName);
            mBinding.editSearchLyricsArtist.setText(mSongArtist);
        }
        searchLyrics(false);

    }


    @Override
    public void initListener() {
        mBinding.ivSearchDown.setOnClickListener(v -> finish());
        mBinding.ivSearchLyrics.setOnClickListener(v -> searchLyrics(mBinding.editSearchLyricsName.getText().toString().trim().isEmpty()));
        mBinding.tvSearchLyricsComplete.setOnClickListener(v -> searchComplete());
        mBinding.vp2SearchLyrics.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                SearchLyricsBean searchLyricsBean = mLyricsBeanList.get(position);
                mSongMid = searchLyricsBean.getSongMid();
                LogUtil.d(TAG, mSongMid);
                mBinding.tvLyricsPageIndex.setText(String.valueOf(position + 1));
            }
        });
    }


    private void searchLyrics(boolean isNeedArtist) {
        showProgress();
        mLyricsBeanList.clear();
        if (NetworkUtil.isNetworkConnected()) {
            String songName = mBinding.editSearchLyricsName.getText().toString().trim();
            String singer = mBinding.editSearchLyricsArtist.getText().toString().trim();
            RetrofitHelper.getMusicService().getLrc(songName).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new BaseObserver<SongLrc>() {
                @Override
                public void onNext(SongLrc songLrc) {
                    List<SongLrc.DataBean.LyricBean.ListBean> list = songLrc.getData().getLyric().getList();
                    for (SongLrc.DataBean.LyricBean.ListBean listBean : list) {
                        String content = listBean.getContent();
                        String songSinger = listBean.getSinger().get(0).getName();
                        String songNames = listBean.getSongname();
                        if (isNeedArtist) {
                            if (!Constant.NO_LYRICS.equals(content) && !Constant.PURE_MUSIC.equals(content) && songName.equals(songNames) && songSinger.contains(singer)) {
                                SearchLyricsBean lyricsBean = new SearchLyricsBean(listBean.getSongmid(), listBean.getContent());
                                if (!mLyricsBeanList.contains(lyricsBean)) {
                                    mLyricsBeanList.add(lyricsBean);
                                }
                            }

                        } else {
                            if (!Constant.NO_LYRICS.equals(content) && !Constant.PURE_MUSIC.equals(content) && songName.equals(songNames)) {

                                SearchLyricsBean lyricsBean = new SearchLyricsBean(listBean.getSongmid(), listBean.getContent());
                                if (!mLyricsBeanList.contains(lyricsBean)) {
                                    mLyricsBeanList.add(lyricsBean);
                                }
                            }
                        }
                    }
                    mBinding.ivSearchLyricsLoading.setVisibility(View.GONE);
                    setTvIndex(mLyricsBeanList.size());
                    SearchLyricsPagerAdapter pagerAdapter2 = new SearchLyricsPagerAdapter(SearchLyricsActivity.this, mLyricsBeanList);
                    mBinding.vp2SearchLyrics.setAdapter(pagerAdapter2);

                }

                @Override
                public void onError(Throwable e) {
                    LogUtil.d(TAG, "歌词搜索加载错误 " + e.getMessage());
                }
            });
        } else {
            mBinding.ivSearchLyricsLoading.setVisibility(View.GONE);
            ToastUtil.show(this, Constant.NO_NETWORK);
        }

    }

    private void showProgress() {
        mBinding.ivSearchLyricsLoading.setVisibility(View.VISIBLE);
        AnimationDrawable animation = (AnimationDrawable) mBinding.ivSearchLyricsLoading.getBackground();
        animation.start();
    }

    private void setTvIndex(int size) {
        String lyricsCount = size > 1 ? "搜索到" + size + "个结果 (右滑查看多个歌词)" : "搜索到" + size + "个结果";
//        String lyricsCount = "搜索到" + size + "个结果";
        mBinding.tvSearchLyricsCount.setText(lyricsCount);
        if (size != 0) {
            mBinding.tvLyricsPageIndex.setText("1");
        }
    }

    private void searchComplete() {
        LogUtil.d(TAG, "搜索歌词完成");
        Intent intent = new Intent();
        intent.putExtra(Constant.SONGMID, mSongMid);
        setResult(Constant.SELECT_LYRICS, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.dialog_push_out);
    }


}
