package com.yibao.music.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.yibao.music.MyApplication;
import com.yibao.music.R;
import com.yibao.music.artisanlist.MusicActivity;
import com.yibao.music.model.MusicBean;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.SystemUiVisibilityUtil;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
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
//        startService(new Intent(this, LoadMusicDataServices.class));


        mIvSplash.setOnClickListener(view -> Observable.just(MyApplication.getIntstance()
                .getDaoSession().getMusicBeanDao().queryBuilder().list()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<MusicBean>>() {
            @Override
            public void accept(List<MusicBean> musicBeans) throws Exception {
                Collections.sort(musicBeans);
                LogUtil.d(" 音乐的数量   ===     " + musicBeans.size());
            }
        }));
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
