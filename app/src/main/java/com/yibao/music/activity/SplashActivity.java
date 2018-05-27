package com.yibao.music.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yibao.music.R;
import com.yibao.music.adapter.SplashPagerAdapter;
import com.yibao.music.base.BaseActivity;
import com.yibao.music.model.song.MusicCountBean;
import com.yibao.music.service.LoadMusicDataService;
import com.yibao.music.util.ColorUtil;
import com.yibao.music.util.Constants;
import com.yibao.music.util.SharePrefrencesUtil;
import com.yibao.music.util.SystemUiVisibilityUtil;
import com.yibao.music.view.ProgressBtn;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
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
    private Unbinder mBind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mBind = ButterKnife.bind(this);
        SystemUiVisibilityUtil.hideStatusBar(getWindow(), true);
        initRxbusData();
    }



    private void initRxbusData() {
        SplashPagerAdapter splashPagerAdapter = new SplashPagerAdapter();
        mVpSplash.setAdapter(splashPagerAdapter);

        if (SharePrefrencesUtil.getLoadMusicFlag(this) != Constants.NUMBER_EIGHT) {
            mTvMusicCount.setVisibility(View.VISIBLE);
            mMusicLoadProgressBar.setVisibility(View.VISIBLE);
            startService(new Intent(this, LoadMusicDataService.class));
            mCompositeDisposable.add(mBus.toObserverable(MusicCountBean.class).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(musicCountBean -> {
                int size = musicCountBean.getSize();
                int count = musicCountBean.getMusicCount();
                mMusicLoadProgressBar.setMax(size);
                String s = "已经加载  " + count + " 首本地音乐";

                mTvMusicCount.setText(s);
                mMusicLoadProgressBar.setProgress(count);
                if (count == size) {
                    mTvMusicCount.setTextColor(getResources().getColor(R.color.lyricsSelected));
                    mTvMusicCount.setText("本地音乐加载完成 -_-");
                    SplashActivity.this.startActivity(new Intent(SplashActivity.this,
                            MusicActivity.class));
                    SharePrefrencesUtil.setLoadMusicFlag(SplashActivity.this, Constants.NUMBER_EIGHT);
                    finish();
                }
            }));
        } else {

            mCompositeDisposable.add(Observable.timer(400, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> {
                        SplashActivity.this.startActivity(new Intent(SplashActivity.this,
                                MusicActivity.class));
                        finish();
                    }));
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBind.unbind();
        mCompositeDisposable.dispose();
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
