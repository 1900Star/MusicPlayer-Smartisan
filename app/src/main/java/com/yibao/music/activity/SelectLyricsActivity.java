package com.yibao.music.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.yibao.music.R;
import com.yibao.music.adapter.LyricsSearchPagerAdapter;
import com.yibao.music.base.BaseObserver;
import com.yibao.music.model.qq.OnlineSongLrc;
import com.yibao.music.model.qq.SearchLyricsBean;
import com.yibao.music.model.qq.SongLrc;
import com.yibao.music.network.RetrofitHelper;
import com.yibao.music.util.Constants;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.NetworkUtil;
import com.yibao.music.util.SoftKeybordUtil;
import com.yibao.music.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author luoshipeng
 * createDate：2019/12/26 0026 14:54
 * className   SelectLyricsActivity
 * Des：TODO
 */
public class SelectLyricsActivity extends AppCompatActivity {
    protected final String TAG = "====" + this.getClass().getSimpleName() + "    ";
    private ImageView mIvBack;
    private TextView mMainTitle;
    private TextView mTvSearchComplete;
    private EditText mEditSongName;
    private EditText mEditArtist;
    private ImageView mIvSearch;
    private TextView mTvLyricsCount;
    private ViewPager2 mViewPager2;
    private TextView mTvLyricsPageIndex;
    private List<SearchLyricsBean> mLyricsBeanList;
    private String mSongMid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_lyrics);
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
        String mSongName = getIntent().getStringExtra(Constants.SONG_NAME);
        String mSongArtist = getIntent().getStringExtra(Constants.SONG_ARTIST);
        LogUtil.d(TAG, mSongName + " == " + mSongArtist);
        if (mSongName != null && mSongArtist != null) {
            mEditSongName.setText(mSongName);
            mEditArtist.setText(mSongArtist);
//            mEditSinger.setSelection(mSongName.length());
//            mEditArtist.setSelection(mSongArtist.length());
        }
        searchLyrics(true);
    }

    private void initView() {
        mIvBack = findViewById(R.id.search_lyrics_titlebar_down);
        mMainTitle = findViewById(R.id.main_title);
        mTvSearchComplete = findViewById(R.id.tv_search_lyrics_complete);
        mTvLyricsCount = findViewById(R.id.tv_search_lyrics_count);
        mEditSongName = findViewById(R.id.edit_search_lyrics_name);
        mEditArtist = findViewById(R.id.edit_search_lyrics_artist);
        mIvSearch = findViewById(R.id.iv_search_lyrics);
        mViewPager2 = findViewById(R.id.vp2_search_lyrics);
        mTvLyricsPageIndex = findViewById(R.id.tv_lyrics_page_index);

    }


    private void searchLyrics(boolean isNeedArtist) {
        mLyricsBeanList.clear();
        if (NetworkUtil.isNetworkConnected()) {
            String songName = mEditSongName.getText().toString().trim();
            String singer = mEditArtist.getText().toString().trim();
            RetrofitHelper.getMusicService().getLrc(songName).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new BaseObserver<SongLrc>() {
                @Override
                public void onNext(SongLrc songLrc) {
                    List<SongLrc.DataBean.LyricBean.ListBean> list = songLrc.getData().getLyric().getList();

                    for (SongLrc.DataBean.LyricBean.ListBean listBean : list) {
                        String content = listBean.getContent();
                        String songSinger = listBean.getSinger().get(0).getName();
                        String songNames = listBean.getSongname();
                        if (isNeedArtist) {
                            if (!Constants.NO_LYRICS.equals(content) && !Constants.PURE_MUSIC.equals(content) && songName.equals(songNames) && songSinger.contains(singer)) {
                                SearchLyricsBean lyricsBean = new SearchLyricsBean(listBean.getSongmid(), listBean.getContent());
                                LogUtil.d(TAG, "AAAAAAAAAAAAAAAAAAAA");
                                if (!mLyricsBeanList.contains(lyricsBean)) {
                                    mLyricsBeanList.add(lyricsBean);
                                }
                            }

                        } else {
                            if (songName.equals(songNames)) {
                                LogUtil.d(TAG, "BBBBBBBBBBBBBBBBBBBBB");
                                SearchLyricsBean lyricsBean = new SearchLyricsBean(listBean.getSongmid(), listBean.getContent());
                                if (!mLyricsBeanList.contains(lyricsBean)) {
                                    mLyricsBeanList.add(lyricsBean);
                                }
                            }
                        }
                    }
                    setTvIndex(mLyricsBeanList.size());
                    LyricsSearchPagerAdapter pagerAdapter2 = new LyricsSearchPagerAdapter(SelectLyricsActivity.this, mLyricsBeanList);
                    mViewPager2.setAdapter(pagerAdapter2);

                }

                @Override
                public void onError(Throwable e) {
                    LogUtil.d(TAG, "歌词搜索加载错误 " + e.getMessage());
                }
            });
        } else {
            ToastUtil.show(this, Constants.NO_NETWORK);
        }

    }

    private void setTvIndex(int size) {
        String lyricsCount = "搜索到" + size + "个结果";
        mTvLyricsCount.setText(lyricsCount);
        if (size != 0) {
            mTvLyricsPageIndex.setText("1");
        }
    }

    private void searchComplete() {
        LogUtil.d(TAG, "搜索歌词完成");
        Intent intent = new Intent();
        intent.putExtra(Constants.SONGMID, mSongMid);
        setResult(Constants.SELECT_LYRICS, intent);
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
