package com.yibao.music.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yibao.music.R;

import com.yibao.music.adapter.SplashPagerAdapter;
import com.yibao.music.base.BaseActivity;
import com.yibao.music.model.MusicCountBean;
import com.yibao.music.model.MusicStatusBean;
import com.yibao.music.service.LoadMusicDataService;
import com.yibao.music.util.Constants;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.SharePrefrencesUtil;
import com.yibao.music.util.SystemUiVisibilityUtil;
import com.yibao.music.view.ProgressBtn;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Author：Sid
 * Des：${TODO}
 * Time:2017/4/22 02:00
 *
 * @author Stran
 */
public class SplashActivity
        extends BaseActivity {


    //    @BindView(R.id.iv_splash)
    ImageView mIvSplash;
    @BindView(R.id.tv_music_count)
    TextView mTvMusicCount;
    @BindView(R.id.music_count_pb)
    ProgressBtn mMusicLoadProgressBar;
    @BindView(R.id.vp_splash)
    ViewPager mVpSplash;
    private String mScanner;
    private boolean mIsFirstScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mBind = ButterKnife.bind(this);
        SystemUiVisibilityUtil.hideStatusBar(getWindow(), true);
        init();
    }

    private void init() {
        mScanner = getIntent().getStringExtra(Constants.SCANNER_MEDIA);
        SplashPagerAdapter splashPagerAdapter = new SplashPagerAdapter();
        mVpSplash.setAdapter(splashPagerAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initRxbusData();
    }

    @Override
    protected void refreshBtnAndNotify(MusicStatusBean musicStatusBean) {

    }

    @Override
    protected void updataCurrentPlayProgress() {

    }

    private void initRxbusData() {

        if (mScanner == null) {
            LogUtil.d("============空Scanner");
            mIsFirstScanner = true;
            // 是否是首次安装，本地数据库是否创建，等 8 表示不是首次安装，数据库已经创建，直接进入MusicActivity。
            if (SharePrefrencesUtil.getLoadMusicFlag(this) == Constants.NUMBER_EIGHT) {
                countDwonOpareton(true);
            } else {
                // 首次安装，创建本地数据库。
                startService(new Intent(this, LoadMusicDataService.class));
                updataLoadProgress();
            }

        } else {
            LogUtil.d("============手动Scanner");
            // 手动扫描歌曲
            mIsFirstScanner = false;
            Intent intent = new Intent(this, LoadMusicDataService.class);
            intent.putExtra(Constants.SCANNER_MEDIA, Constants.SCANNER_MEDIA);
            startService(intent);
            updataLoadProgress();
        }


    }

    private void updataLoadProgress() {
        mTvMusicCount.setVisibility(View.VISIBLE);
        mMusicLoadProgressBar.setVisibility(View.VISIBLE);
        mCompositeDisposable.add(mBus.toObserverable(MusicCountBean.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(musicCountBean -> {
                    // 初次启动时size为所有音乐的数量，当手动扫描时为新增歌曲的数量。
                    int size = musicCountBean.getSize();
                    int currentCount = musicCountBean.getCurrentCount();
                    if (size > 0) {
                        mMusicLoadProgressBar.setMax(size);
                        String s = "已经加载  " + currentCount + " 首本地音乐";
                        mTvMusicCount.setText(s);
                        mMusicLoadProgressBar.setProgress(currentCount);
                        if (currentCount == size) {
                            mTvMusicCount.setTextColor(getResources().getColor(R.color.lyricsSelected));
                            mTvMusicCount.setText("本地音乐加载完成 -_-");
                            // 初次扫描完成后进入MusicActivity
                            if (mIsFirstScanner) {
                                SharePrefrencesUtil.setLoadMusicFlag(SplashActivity.this, Constants.NUMBER_EIGHT);
                            } else {
                                String newSongCount = "新增 " + size + " 首歌曲";
                                mTvMusicCount.setText(newSongCount);
                            }
                            countDwonOpareton(mIsFirstScanner);
                        }
                    } else {
                        mTvMusicCount.setText(mIsFirstScanner ? "本地没有发现音乐,去下载歌曲后再来体验吧!" : "没有新增歌曲!");
                        countDwonOpareton(mIsFirstScanner);
                    }
                }));
    }

    private void countDwonOpareton(boolean b) {
        mCompositeDisposable.add(Observable.timer(1500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    if (b) {
                        startMusicActivity();
                    } else {
                        mTvMusicCount.setVisibility(View.GONE);
                        mMusicLoadProgressBar.setVisibility(View.GONE);
                    }
                }));

    }

    private void startMusicActivity() {
        SplashActivity.this.startActivity(new Intent(SplashActivity.this,
                MusicActivity.class));
        finish();
    }


    @OnClick(R.id.tv_music_count)
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.tv_music_count:
                break;
        }
    }
}
