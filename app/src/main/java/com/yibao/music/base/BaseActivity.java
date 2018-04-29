package com.yibao.music.base;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.yibao.music.MusicApplication;
import com.yibao.music.R;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.greendao.MusicBeanDao;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.RxBus;
import com.yibao.music.util.StringUtil;

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
    private boolean mCurrentIsFavorite;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBus = MusicApplication.getIntstance()
                .bus();
        mMusicDao = MusicApplication.getIntstance().getMusicDao();
        mCompositeDisposable = new CompositeDisposable();
        registerHeadsetReceiver();

    }


    protected void checkCurrentIsFavorite(MusicBean currentMusicBean, ImageView qqFavorite, ImageView smartisanFavorite) {
        mCurrentIsFavorite = mMusicDao.load(currentMusicBean.getId()).isFavorite();
        if (mCurrentIsFavorite) {
            qqFavorite.setImageResource(R.mipmap.favorite_yes);
            smartisanFavorite.setImageResource(R.drawable.btn_favorite_red_selector);
        } else {
            qqFavorite.setImageResource(R.drawable.music_qqbar_favorite_selector);
            smartisanFavorite.setImageResource(R.drawable.btn_favorite_gray_selector);
        }
    }


    protected void favoriteMusic(MusicBean currentMusicBean, ImageView qqFavorite, ImageView smartisanFavorite) {
        mCurrentIsFavorite = mMusicDao.load(currentMusicBean.getId()).isFavorite();
        if (mCurrentIsFavorite) {
            currentMusicBean.setIsFavorite(false);
            mMusicDao.update(currentMusicBean);
            qqFavorite.setImageResource(R.drawable.music_qqbar_favorite_selector);
            smartisanFavorite.setImageResource(R.drawable.btn_favorite_gray_selector);

        } else {
            String time = StringUtil.getCurrentTime();
            currentMusicBean.setTime(time);
            currentMusicBean.setIsFavorite(true);
            mMusicDao.update(currentMusicBean);
            smartisanFavorite.setImageResource(R.drawable.btn_favorite_red_selector);
            qqFavorite.setImageResource(R.mipmap.favorite_yes);

        }
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

    // 需要监听耳机拔出的页面重写这个方法
    protected void headsetPullOut() {
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mDisposable != null) {
            mDisposable.dispose();
            mDisposable = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.dispose();
        mCompositeDisposable.clear();
//        if (mDisposable != null) {
//            mDisposable.dispose();
//        }
        if (mRxViewDisposable != null) {
            mRxViewDisposable.dispose();
        }

        unregisterReceiver(headsetReciver);
    }
}
