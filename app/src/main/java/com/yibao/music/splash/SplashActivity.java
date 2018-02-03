package com.yibao.music.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.yibao.biggirl.R;
import com.yibao.music.music.musiclist.MusicListActivity;
import com.yibao.music.util.NetworkUtil;
import com.yibao.music.util.SnakbarUtil;
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
        if (NetworkUtil.isNetworkConnected(this)) {
            Observable.timer(2, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> {
                        SplashActivity.this.startActivity(new Intent(SplashActivity.this,
                                MusicListActivity.class));
                        finish();
                    });

        } else {
            SnakbarUtil.netErrorsLong(mIvSplash);
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBind.unbind();
    }


}
