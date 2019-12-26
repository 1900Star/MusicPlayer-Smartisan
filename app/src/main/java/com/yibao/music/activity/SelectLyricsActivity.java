package com.yibao.music.activity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.yibao.music.R;
import com.yibao.music.adapter.LyricsSearchPagerAdapter;
import com.yibao.music.base.BaseObserver;
import com.yibao.music.base.listener.OnSearchLyricsListener;
import com.yibao.music.model.qq.SongLrc;
import com.yibao.music.network.QqMusicRemote;
import com.yibao.music.network.RetrofitHelper;
import com.yibao.music.util.Constants;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.ThreadPoolProxyFactory;

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
    private Toolbar mSearchLyricsToolbar;
    private ImageView mIvBack;
    private TextView mMainTitle;
    private TextView mTvSearchComplete;
    private EditText mEditSinger;
    private EditText mEditArtist;
    private ImageView mIvSearch;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_lyrics);
        initView();
        intData();
    }

    private void intData() {
        String songName = getIntent().getStringExtra(Constants.SONG_NAME);
        String songArtist = getIntent().getStringExtra(Constants.SONG_ARTIST);
        LogUtil.d(TAG, songName + " == " + songArtist);
        if (songName != null && songArtist != null) {
            mEditSinger.setText(songName);
            mEditArtist.setText(songArtist);
        }
        LyricsSearchPagerAdapter pagerAdapter = new LyricsSearchPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(pagerAdapter);
    }

    private void initView() {
        mIvBack = findViewById(R.id.search_lyrics_titlebar_down);
        mMainTitle = findViewById(R.id.main_title);
        mTvSearchComplete = findViewById(R.id.tv_search_lyrics_complete);
        mTvSearchComplete.setOnClickListener(v -> searchComplete());
        mIvBack.setOnClickListener(v -> finish());
        mEditSinger = findViewById(R.id.edit_search_lyrics_name);
        mEditArtist = findViewById(R.id.edit_search_lyrics_artist);
        mIvSearch = findViewById(R.id.iv_search_lyrics);
        mViewPager = findViewById(R.id.vp_search_lyrics);
        mIvSearch.setOnClickListener(v -> searchLyrics());
    }

    private void searchLyrics() {
        LogUtil.d(TAG, "搜索歌词");
        String songName = mEditSinger.getText().toString().trim();
        String singer = mEditArtist.getText().toString().trim();
        ThreadPoolProxyFactory.newInstance().execute(() -> QqMusicRemote.searchLyrics(songName, songLrcList -> {
            SongLrc.DataBean.LyricBean lyric = songLrcList.get(0).getData().getLyric();
            SongLrc.DataBean.LyricBean.ListBean listBean = lyric.getList().get(0);
            LogUtil.d(TAG, listBean.getSinger().get(0).getName());
            LogUtil.d(TAG, listBean.getDownload_url());
            LogUtil.d(TAG, listBean.getContent());
        }));
//        RetrofitHelper.getMusicService().getLrc(songName).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread()).subscribe(new BaseObserver<SongLrc>() {
//            @Override
//            public void onComplete() {
//                super.onComplete();
//                LogUtil.d(TAG,"请求完成");
//            }
//
//            @Override
//            public void onNext(SongLrc songLrc) {
//                super.onNext(songLrc);
//                LogUtil.d(TAG, "KeyWord  " + songLrc.getData().getKeyword());
//                List<SongLrc.DataBean.LyricBean.ListBean> list = songLrc.getData().getLyric().getList();
//                for (SongLrc.DataBean.LyricBean.ListBean listBean : list) {
////                    LogUtil.d(TAG, listBean.getSinger().get(0).getName());
////                    LogUtil.d(TAG, listBean.getDownload_url());
//                    LogUtil.d(TAG, listBean.getContent());
//                }
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                super.onError(e);
//            }
//        });
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
