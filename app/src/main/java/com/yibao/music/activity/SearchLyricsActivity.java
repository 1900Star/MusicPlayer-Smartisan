package com.yibao.music.activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.yibao.music.R;
import com.yibao.music.adapter.SearchLyricsPagerAdapter;
import com.yibao.music.base.BaseObserver;
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
public class SearchLyricsActivity extends AppCompatActivity {
    protected final String TAG = "====" + this.getClass().getSimpleName() + "    ";
    private ImageView mIvBack;
    private TextView mTvSearchComplete;
    private EditText mEditSongName;
    private EditText mEditArtist;
    private ImageView mIvSearch;
    private TextView mTvLyricsCount;
    private ViewPager2 mViewPager2;
    private TextView mTvLyricsPageIndex;
    private List<SearchLyricsBean> mLyricsBeanList;
    private String mSongMid;
    private ImageView mIvLoading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_lyrics);
        initView();
        intData();
        initListener();

    }

    private void initListener() {
        mIvBack.setOnClickListener(v -> finish());
        mIvSearch.setOnClickListener(v -> searchLyrics(mEditSongName.getText().toString().trim().isEmpty()));
        mTvSearchComplete.setOnClickListener(v -> searchComplete());
        mViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                SearchLyricsBean searchLyricsBean = mLyricsBeanList.get(position);
                mSongMid = searchLyricsBean.getSongMid();
                LogUtil.d(TAG, mSongMid);
                mTvLyricsPageIndex.setText(String.valueOf(position + 1));
            }
        });
    }

    private void intData() {
        mLyricsBeanList = new ArrayList<>();
        String mSongName = getIntent().getStringExtra(Constant.SONG_NAME);
        String mSongArtist = getIntent().getStringExtra(Constant.SONG_ARTIST);
        LogUtil.d(TAG, mSongName + " == " + mSongArtist);
        if (mSongName != null && mSongArtist != null) {
            mEditSongName.setText(mSongName);
            mEditArtist.setText(mSongArtist);
        }
        searchLyrics(false);
    }

    private void initView() {
        mIvBack = findViewById(R.id.search_lyrics_titlebar_down);
        TextView mMainTitle = findViewById(R.id.main_title);
        mIvLoading = findViewById(R.id.iv_search_lyrics_loading);
        mTvSearchComplete = findViewById(R.id.tv_search_lyrics_complete);
        mTvLyricsCount = findViewById(R.id.tv_search_lyrics_count);
        mEditSongName = findViewById(R.id.edit_search_lyrics_name);
        mEditArtist = findViewById(R.id.edit_search_lyrics_artist);
        mIvSearch = findViewById(R.id.iv_search_lyrics);
        mViewPager2 = findViewById(R.id.vp2_search_lyrics);
        mTvLyricsPageIndex = findViewById(R.id.tv_lyrics_page_index);

    }


    private void searchLyrics(boolean isNeedArtist) {
        showProgress();
        mLyricsBeanList.clear();
        if (NetworkUtil.isNetworkConnected()) {
            String songName = mEditSongName.getText().toString().trim();
            String singer = mEditArtist.getText().toString().trim();
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
                    mIvLoading.setVisibility(View.GONE);
                    setTvIndex(mLyricsBeanList.size());
                    SearchLyricsPagerAdapter pagerAdapter2 = new SearchLyricsPagerAdapter(SearchLyricsActivity.this, mLyricsBeanList);
                    mViewPager2.setAdapter(pagerAdapter2);

                }

                @Override
                public void onError(Throwable e) {
                    LogUtil.d(TAG, "歌词搜索加载错误 " + e.getMessage());
                }
            });
        } else {
            mIvLoading.setVisibility(View.GONE);
            ToastUtil.show(this, Constant.NO_NETWORK);
        }

    }

    private void showProgress() {
        mIvLoading.setVisibility(View.VISIBLE);
        AnimationDrawable animation = (AnimationDrawable) mIvLoading.getBackground();
        animation.start();
    }

    private void setTvIndex(int size) {
        String lyricsCount = size > 1 ? "搜索到" + size + "个结果 (右滑查看多个歌词)" : "搜索到" + size + "个结果";
//        String lyricsCount = "搜索到" + size + "个结果";
        mTvLyricsCount.setText(lyricsCount);
        if (size != 0) {
            mTvLyricsPageIndex.setText("1");
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
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.dialog_push_out);
    }
}
