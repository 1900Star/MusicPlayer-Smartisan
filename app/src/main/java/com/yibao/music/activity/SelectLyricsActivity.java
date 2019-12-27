package com.yibao.music.activity;

import android.os.Bundle;
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
    private EditText mEditSinger;
    private EditText mEditArtist;
    private ImageView mIvSearch;
    private TextView mTvLyricsCount;
    private String mSongName;
    private String mSongArtist;
    private ViewPager2 mViewPager2;
    private TextView mTvLyricsPageIndex;

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
        mIvSearch.setOnClickListener(v -> getLyrics());
        mViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mTvLyricsPageIndex.setText(String.valueOf(position + 1));
            }
        });
    }

    private void intData() {
        mSongName = getIntent().getStringExtra(Constants.SONG_NAME);
        mSongArtist = getIntent().getStringExtra(Constants.SONG_ARTIST);
        LogUtil.d(TAG, mSongName + " == " + mSongArtist);
        if (mSongName != null && mSongArtist != null) {
            mEditSinger.setText(mSongName);
            mEditArtist.setText(mSongArtist);
            mEditSinger.setSelection(mSongName.length());
            mEditArtist.setSelection(mSongArtist.length());
        }
        searchLyrics();
    }

    private void initView() {
        mIvBack = findViewById(R.id.search_lyrics_titlebar_down);
        mMainTitle = findViewById(R.id.main_title);
        mTvSearchComplete = findViewById(R.id.tv_search_lyrics_complete);
        mTvLyricsCount = findViewById(R.id.tv_search_lyrics_count);
        mTvSearchComplete.setOnClickListener(v -> searchComplete());
        mEditSinger = findViewById(R.id.edit_search_lyrics_name);
        mEditArtist = findViewById(R.id.edit_search_lyrics_artist);
        mIvSearch = findViewById(R.id.iv_search_lyrics);
        mViewPager2 = findViewById(R.id.vp2_search_lyrics);
        mTvLyricsPageIndex = findViewById(R.id.tv_lyrics_page_index);

    }

    private void getLyrics() {
        RetrofitHelper.getSingerMusicService().getOnlineSongLrc("001j5Qxi4J50nw").subscribeOn(Schedulers.io()).subscribe(new BaseObserver<OnlineSongLrc>() {
            @Override
            public void onNext(OnlineSongLrc onlineSongLrc) {
                LogUtil.d(TAG, onlineSongLrc.getLyric());
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                LogUtil.d(TAG, e.getMessage());
            }
        });
    }

    private void searchLyrics() {
        if (NetworkUtil.isNetworkConnected()) {
            String songName = mEditSinger.getText().toString().trim();
            String singer = mEditArtist.getText().toString().trim();
            RetrofitHelper.getMusicService().getLrc(songName).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new BaseObserver<SongLrc>() {
                @Override
                public void onNext(SongLrc songLrc) {
                    List<SongLrc.DataBean.LyricBean.ListBean> list = songLrc.getData().getLyric().getList();
                    List<SearchLyricsBean> lyricsBeanList = new ArrayList<>();
                    for (SongLrc.DataBean.LyricBean.ListBean listBean : list) {
                        String content = listBean.getContent();
                        String songSinger = listBean.getSinger().get(0).getName();
                        boolean result = !Constants.NO_LYRICS.equals(content) && !Constants.PURE_MUSIC.equals(content) && singer.contains(songSinger) && listBean.getSongname().equals(mSongName);
                        if (result) {
                            SearchLyricsBean lyricsBean = new SearchLyricsBean(listBean.getSongmid(), listBean.getContent());
                            if (!lyricsBeanList.contains(lyricsBean)) {
                                lyricsBeanList.add(lyricsBean);
                            }
                        }
                    }
                    setTvIndex(lyricsBeanList.size());
                    LyricsSearchPagerAdapter pagerAdapter2 = new LyricsSearchPagerAdapter(SelectLyricsActivity.this, lyricsBeanList);
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
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.dialog_push_out);
    }
}
