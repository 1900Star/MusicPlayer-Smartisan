package com.yibao.music.base;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.yibao.music.MusicApplication;
import com.yibao.music.R;
import com.yibao.music.activity.PlayActivity;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.greendao.MusicBeanDao;
import com.yibao.music.model.greendao.SearchHistoryBeanDao;
import com.yibao.music.service.AudioPlayService;
import com.yibao.music.util.ReadFavoriteFileUtil;
import com.yibao.music.util.RxBus;
import com.yibao.music.util.StringUtil;
import com.yibao.music.view.music.PlayNotifyManager;
import com.yibao.music.view.music.QqControlBar;
import com.yibao.music.view.music.SmartisanControlBar;

import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
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

public abstract class BaseActivity extends AppCompatActivity {

    protected RxBus mBus;
    protected MusicBeanDao mMusicDao;
    protected SearchHistoryBeanDao mSearchDao;
    protected CompositeDisposable mCompositeDisposable;
    protected Disposable mDisposableProgresse;
    protected Disposable mQqLyricsDisposable;
    protected Unbinder mBind;
    protected Disposable mRxViewDisposable;
    private boolean mCurrentIsFavorite;
    protected PlayNotifyManager mNotifyManager;
    protected boolean isNotifyShow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBus = MusicApplication.getIntstance()
                .bus();
        mMusicDao = MusicApplication.getIntstance().getMusicDao();
        mSearchDao = MusicApplication.getIntstance().getSearchDao();
        registerHeadsetReceiver();
        mCompositeDisposable = new CompositeDisposable();
    }

    protected void startPlayActivity(MusicBean musicBean) {
        Intent intent = new Intent(this, PlayActivity.class);
        intent.putExtra("currentBean", musicBean);
        startActivity(intent);
        overridePendingTransition(R.anim.dialog_push_in, 0);
    }

    protected void checkCurrentSongIsFavorite(MusicBean currentMusicBean, QqControlBar qqControlBar, SmartisanControlBar smartisanControlBar) {
        mCurrentIsFavorite = mMusicDao.load(currentMusicBean.getId()).isFavorite();
        smartisanControlBar.setFavoriteButtonState(mCurrentIsFavorite);
        if (qqControlBar != null) {
            qqControlBar.setFavoriteButtonState(mCurrentIsFavorite);
        }
    }


    protected void setSongfavoriteState(MusicBean currentMusicBean, QqControlBar qqControlBar, SmartisanControlBar smartisanControlBar) {
        mCurrentIsFavorite = mMusicDao.load(currentMusicBean.getId()).isFavorite();
        smartisanControlBar.setFavoriteButtonState(!mCurrentIsFavorite);
        if (qqControlBar != null) {
            qqControlBar.setFavoriteButtonState(!mCurrentIsFavorite);
        }
        currentMusicBean.setIsFavorite(!mCurrentIsFavorite);
        if (!mCurrentIsFavorite) {
            MusicBean newMusicBean = getCurrentMusicBean(currentMusicBean);
            mMusicDao.update(newMusicBean);
            updataFavoriteSong(newMusicBean, mCurrentIsFavorite);
        }
    }
    protected void showNotifycation(MusicBean musicBean,boolean isPlaying) {
        mNotifyManager = new PlayNotifyManager(this, musicBean,isPlaying);
        mNotifyManager.show();
        isNotifyShow = true;
    }
    protected void updataFavoriteSong(MusicBean musicBean, boolean currentIsFavorite) {
        if (currentIsFavorite) {
            mCompositeDisposable.add(ReadFavoriteFileUtil.deleteFavorite(musicBean.getTitle()).observeOn(AndroidSchedulers.mainThread()).subscribe(aBoolean -> {
                if (!aBoolean) {
                    Toast.makeText(BaseActivity.this, "该歌曲还没有添加到收藏文件", Toast.LENGTH_SHORT).show();
                }
            }));
        } else {
            //更新收藏文件  将歌名和收藏时间拼接储存，恢复的时候，歌名和时间以“T”为标记进行截取
            String songInfo = musicBean.getTitle() + "T" + musicBean.getTime();
            ReadFavoriteFileUtil.writeFile(songInfo);

        }

    }

    private MusicBean getCurrentMusicBean(MusicBean musicBean) {
        String time = StringUtil.getCurrentTime();
        musicBean.setTime(time);
        return musicBean;
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

    protected void disposableQqLyric() {
        if (mQqLyricsDisposable != null) {
            mQqLyricsDisposable.dispose();
            mQqLyricsDisposable = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        clearDisposableProgresse();
        disposableQqLyric();
    }

    protected void clearDisposableProgresse() {
        if (mDisposableProgresse != null) {
            mDisposableProgresse.dispose();
            mDisposableProgresse = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBind.unbind();
        unregisterReceiver(headsetReciver);
        if (mRxViewDisposable != null) {
            mRxViewDisposable.dispose();
        }
        if (mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
            mCompositeDisposable.clear();
            mCompositeDisposable = null;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.dialog_push_out);
    }
}
