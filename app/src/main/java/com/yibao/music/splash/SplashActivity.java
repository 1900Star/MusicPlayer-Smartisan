package com.yibao.music.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.yibao.music.R;
import com.yibao.music.artisanlist.MusicActivity;
import com.yibao.music.util.SystemUiVisibilityUtil;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
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
        extends AppCompatActivity {


    @BindView(R.id.iv_splash)
    ImageView mIvSplash;
    private Unbinder mBind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mBind = ButterKnife.bind(this);
        SystemUiVisibilityUtil.hideStatusBar(getWindow(), true);
//        boolean isScanMusic = SharePrefrencesUtil.getMusicConfig(this, false);
//        if (!isScanMusic) {
//        startService(new Intent(this, LoadMusicDataServices.class));
//        }


        Observable.timer(400, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    SplashActivity.this.startActivity(new Intent(SplashActivity.this,
                            MusicActivity.class));
                    finish();
                });
//
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBind.unbind();
    }


}
