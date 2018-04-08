package com.yibao.music.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.yibao.music.MusicApplication;
import com.yibao.music.model.greendao.MusicBeanDao;
import com.yibao.music.util.RxBus;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.base
 * @文件名: BaseActivity
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/20 13:07
 * @描述： {TODO}
 */

public class BaseActivity extends AppCompatActivity {

    public RxBus mBus;
    public MusicBeanDao mMusicDao;
    public CompositeDisposable mCompositeDisposable;
    public Disposable mDisposable;
    public Disposable mRxViewDisposable;
    public final int mNormalTabbarColor = Color.parseColor("#939396");
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBus = MusicApplication.getIntstance()
                .bus();
        mMusicDao = MusicApplication.getIntstance().getMusicDao();
        mCompositeDisposable = new CompositeDisposable();
        registerHeadsetReceiver();

    }

    /**
     * 耳机插入和拔出监听
     */
    private void registerHeadsetReceiver() {
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(headsetReciver, intentFilter);
    }

    BroadcastReceiver headsetReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            headsetPullOut();
        }
    };

    protected void headsetPullOut() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.dispose();
        mCompositeDisposable.clear();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
        if (mRxViewDisposable != null) {
            mRxViewDisposable.dispose();
        }

        unregisterReceiver(headsetReciver);
    }
}
