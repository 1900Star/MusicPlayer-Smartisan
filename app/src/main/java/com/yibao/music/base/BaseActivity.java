package com.yibao.music.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.yibao.music.MusicApplication;
import com.yibao.music.R;
import com.yibao.music.activity.PlayActivity;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.PlayStatusBean;
import com.yibao.music.model.greendao.MusicBeanDao;
import com.yibao.music.model.greendao.PlayListBeanDao;
import com.yibao.music.model.greendao.SearchHistoryBeanDao;
import com.yibao.music.util.RxBus;
import com.yibao.music.view.music.QqControlBar;
import com.yibao.music.view.music.SmartisanControlBar;

import java.util.concurrent.TimeUnit;

import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


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
    protected PlayListBeanDao mPlayListDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBus = RxBus.getInstance();
        mMusicDao = MusicApplication.getIntstance().getMusicDao();
        mSearchDao = MusicApplication.getIntstance().getSearchDao();
        mPlayListDao = MusicApplication.getIntstance().getPlayListDao();
    }


    @Override
    protected void onResume() {
        super.onResume();
        subscribe();
    }

    private void subscribe() {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(mBus.toObserverable(MusicBean.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updataCurrentPlayInfo));
        upDataPlayProgress();
        mCompositeDisposable.add(mBus.toObserverable(PlayStatusBean.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::refreshBtnAndNotify));
    }

    protected abstract void refreshBtnAndNotify(PlayStatusBean playStatusBean);

    protected void upDataPlayProgress() {
        if (mDisposableProgresse == null) {
            mDisposableProgresse = Observable.interval(0, 2800, TimeUnit.MICROSECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> BaseActivity.this.updataCurrentPlayProgress());
            mCompositeDisposable.add(mDisposableProgresse);
        }

    }

    protected abstract void updataCurrentPlayProgress();

    /**
     * @param musicItem 当前播放的歌曲信息，用于更新进度和动画状态,需要用的界面复写这个方法
     */
    protected void updataCurrentPlayInfo(MusicBean musicItem) {
        upDataPlayProgress();
    }

    protected void startPlayActivity() {
        startActivity(new Intent(this, PlayActivity.class));
        overridePendingTransition(R.anim.dialog_push_in, 0);
    }

    protected void checkCurrentSongIsFavorite(MusicBean currentMusicBean, QqControlBar qqControlBar, SmartisanControlBar smartisanControlBar) {
        boolean favorite = mMusicDao.load(currentMusicBean.getId()).getIsFavorite();
        smartisanControlBar.setFavoriteButtonState(favorite);
        if (qqControlBar != null) {
            qqControlBar.setFavoriteButtonState(favorite);
        }
    }

    protected boolean getFavoriteState(MusicBean musicBean) {
        return mMusicDao.load(musicBean.getId()).isFavorite();
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
        if (mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
            mCompositeDisposable.clear();
            mCompositeDisposable = null;
        }
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
        if (mRxViewDisposable != null) {
            mRxViewDisposable.dispose();
        }
    }
}
