package com.yibao.music.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.yibao.music.MusicApplication;
import com.yibao.music.R;
import com.yibao.music.base.listener.UpdataTitleListener;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.greendao.MusicBeanDao;
import com.yibao.music.util.ReadFavoriteFileUtil;
import com.yibao.music.util.RxBus;
import com.yibao.music.util.StringUtil;
import com.yibao.music.view.music.QqControlBar;
import com.yibao.music.view.music.SmartisanControlBar;

import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


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
    protected CompositeDisposable mCompositeDisposable;
    protected Disposable mDisposable;
    protected Disposable qqLyricsDisposable;
    protected Unbinder mBind;
    protected Disposable mDisposablesLyric;
    protected Disposable mRxViewDisposable;
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


    protected void checkCurrentSongIsFavorite(MusicBean currentMusicBean, QqControlBar qqControlBar, SmartisanControlBar smartisanControlBar) {
        mCurrentIsFavorite = mMusicDao.load(currentMusicBean.getId()).isFavorite();
        smartisanControlBar.setFavoriteButtonState(mCurrentIsFavorite);
        qqControlBar.setFavoriteButtonState(mCurrentIsFavorite);
    }


    protected void setSongfavoriteState(MusicBean currentMusicBean, QqControlBar qqControlBar, SmartisanControlBar smartisanControlBar) {
        mCurrentIsFavorite = mMusicDao.load(currentMusicBean.getId()).isFavorite();
        smartisanControlBar.setFavoriteButtonState(!mCurrentIsFavorite);
        qqControlBar.setFavoriteButtonState(!mCurrentIsFavorite);
        currentMusicBean.setIsFavorite(!mCurrentIsFavorite);
        MusicBean newMusicBean = getCurrentMusicBean(currentMusicBean);
        mMusicDao.update(mCurrentIsFavorite ? currentMusicBean : getCurrentMusicBean(currentMusicBean));
        updataFavoriteFile(mCurrentIsFavorite ? currentMusicBean : newMusicBean, mCurrentIsFavorite);
    }

    protected void updataFavoriteFile(MusicBean musicBean, boolean currentIsFavorite) {
        //更新收藏文件  后期将时间也拼接上去，恢复的时间通过截取字符串获取
        if (currentIsFavorite) {
            mCompositeDisposable.add(ReadFavoriteFileUtil.deleteFavorite(musicBean.getTitle()).observeOn(AndroidSchedulers.mainThread()).subscribe(aBoolean -> {
                if (!aBoolean) {
                    Toast.makeText(BaseActivity.this, "该歌曲还没有添加到收藏文件", Toast.LENGTH_SHORT).show();
                }
            }));
        } else {
            String songInfo = musicBean.getTitle() + "T" + musicBean.getTime();
            ReadFavoriteFileUtil.writeFile(songInfo);

        }

    }

    private MusicBean getCurrentMusicBean(MusicBean musicBean) {
        String time = StringUtil.getCurrentTime();
        musicBean.setTime(time);
        mMusicDao.update(musicBean);
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
        if (qqLyricsDisposable != null) {
            qqLyricsDisposable.dispose();
            qqLyricsDisposable = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mDisposable != null) {
            mDisposable.dispose();
            mDisposable = null;
        }
//        if (mDisposablesLyric != null) {
//            mDisposablesLyric.dispose();
//            mDisposablesLyric = null;
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBind.unbind();
        unregisterReceiver(headsetReciver);
        mCompositeDisposable.dispose();
        mCompositeDisposable.clear();
        if (mRxViewDisposable != null) {
            mRxViewDisposable.dispose();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.dialog_push_out);
    }
}
