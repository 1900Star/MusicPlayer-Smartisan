package com.yibao.music.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.yibao.music.R;
import com.yibao.music.artisanlist.MusicActivity;
import com.yibao.music.base.listener.SeekBarChangeListtener;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.MusicStatusBean;
import com.yibao.music.service.AudioPlayService;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.ToastUtil;
import com.yibao.music.view.music.LyricsView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.base
 * @文件名: BasePlayActivity
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/18 11:09
 * @描述： {仅仅针对 PlayActivity抽出的基类,目的在于减少PlayActivity中的代码}
 */

public abstract class BasePlayActivity extends BaseActivity {

    protected AudioManager mAudioManager;
    protected int mMaxVolume;
    protected AudioPlayService.AudioBinder audioBinder;
    protected CompositeDisposable mCompositeDisposable;
    private PowerManager.WakeLock mWakeLock;
    private boolean isScreenAlwaysOn;
    private VolumeReceiver mVolumeReceiver;
    protected Disposable mDisposablePlayTime;
    protected Disposable mDisposableLyrics;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        registerVolumeReceiver();


    }

    @Override
    protected void onStart() {
        super.onStart();
        audioBinder = MusicActivity.getAudioBinder();

    }



    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.d("===========baseplay Acitivyt   onResume");
        upDataPlayProgress();
        updataMusicTitle();
        recivewServiecInfo();

    }


    /**
     * 接收Service发出的播放状态
     *
     */
    private void recivewServiecInfo() {
        mCompositeDisposable.add(mBus.toObserverable(MusicStatusBean.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::refreshAllPlayBtn));


    }

    /**
     * 接收Service发的信息，时时更新播放按钮的状态
     *
     * @param musicStatusBean
     */
    protected abstract void refreshAllPlayBtn(MusicStatusBean musicStatusBean);


    private void updataMusicTitle() {
        mCompositeDisposable.add(mBus.toObserverable(MusicBean.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updataCurrentTitle));

    }

    /**
     * 更新音乐的Title和歌手
     *
     * @param info
     */
    protected abstract void updataCurrentTitle(MusicBean info);

    /**
     * 停止更新
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mDisposablePlayTime != null) {
            mDisposablePlayTime.dispose();
        }

    }

    private void upDataPlayProgress() {
        mDisposablePlayTime = Observable.interval(0, 2800, TimeUnit.MICROSECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> updataCurrentPlayProgress(audioBinder.getProgress()));
    }

    /**
     * 时时更新播放进度
     *
     * @param progress
     */
    protected abstract void updataCurrentPlayProgress(int progress);

    private void init() {
        audioBinder = MusicActivity.getAudioBinder();
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "Music  Lock");
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mCompositeDisposable = new CompositeDisposable();
    }

    /**
     * 根据进度滚动歌词
     */
    protected void startRollPlayLyrics(LyricsView lyricsView) {
        mDisposableLyrics = Observable.interval(50, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> lyricsView.rollText(audioBinder.getProgress(), audioBinder.getDuration()));

    }


    /**
     * 音乐的播放模式 : 全部循环-单曲循环-随机播放
     */
    protected void switchPlayMode(ImageView imageView) {
        //获取当前的播放模式
        int playMode = audioBinder.getPalyMode();
        //根据当前播放模式进行其它模式切换
        switch (playMode) {
            case AudioPlayService.PLAY_MODE_ALL:
                audioBinder.setPalyMode(AudioPlayService.PLAY_MODE_SINGLE);
                break;
            case AudioPlayService.PLAY_MODE_SINGLE:
                audioBinder.setPalyMode(AudioPlayService.PLAY_MODE_RANDOM);
                break;
            case AudioPlayService.PLAY_MODE_RANDOM:
                audioBinder.setPalyMode(AudioPlayService.PLAY_MODE_ALL);
                break;
            default:
                break;
        }
        //根据当前模式,更新播放模式图片
        updatePlayModeImage(audioBinder.getPalyMode(), imageView);
    }


    /**
     * 更新播放模式图片
     *
     * @param playMode
     */
    protected void updatePlayModeImage(int playMode, ImageView mMusicPlayerMode) {
        switch (playMode) {
            case AudioPlayService.PLAY_MODE_ALL:
                mMusicPlayerMode.setImageResource(R.drawable.audio_playmode_allrepeat_selector);
                break;
            case AudioPlayService.PLAY_MODE_SINGLE:
                mMusicPlayerMode.setImageResource(R.drawable.audio_playmode_single_selector);
                break;
            case AudioPlayService.PLAY_MODE_RANDOM:
                mMusicPlayerMode.setImageResource(R.drawable.audio_playmode_random_selector);
                break;
            default:
                break;
        }
    }


    /**
     * 打开歌词时，可以保持屏幕常亮
     * 屏幕常亮默认设置为30分钟
     */

    protected void screenAlwaysOnSwitch(ImageView mIvSecreenSunSwitch) {
        if (isScreenAlwaysOn) {
            mWakeLock.release();
            mIvSecreenSunSwitch.setImageResource(R.drawable.sun_always_of_selector);
            ToastUtil.showScreenOf(this);
            isScreenAlwaysOn = false;
        } else {
            long screenTime = 30 * 60 * 1000L;
            mWakeLock.acquire(screenTime);
            mIvSecreenSunSwitch.setImageResource(R.drawable.sun_always_on_selector);
            ToastUtil.showScreenOn(this);
            isScreenAlwaysOn = true;
        }
    }

    // 注册音量监听广播

    protected void registerVolumeReceiver() {
        mVolumeReceiver = new VolumeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.media.VOLUME_CHANGED_ACTION");
        registerReceiver(mVolumeReceiver, filter);
    }
    // 音量监听广播

    public class VolumeReceiver
            extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String volumeAction = "android.media.VOLUME_CHANGED_ACTION";
            if (volumeAction.equals(intent.getAction())) {
                AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                // 当前的媒体音量
                int currVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                // 如果系统音量发生变化就更新Seekbar
                updataVolumeProgresse(currVolume);
            }
        }
    }

    /**
     * 系统音量发生变化时更新音量的Seekbar
     *
     * @param currVolume
     */
    public abstract void updataVolumeProgresse(int currVolume);

    /**
     * 音乐进度条和音量条的监听器
     */
    public class SeekBarListener
            extends SeekBarChangeListtener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
            super.onProgressChanged(seekBar, progress, b);

            updataMusicBarAndVolumeBar(seekBar, progress, b);
        }
    }

    /**
     * 更新音乐进度条和音量条，子类去具体操作。
     *
     * @param seekBar
     * @param progress
     * @param b
     */
    protected abstract void updataMusicBarAndVolumeBar(SeekBar seekBar, int progress, boolean b);
//


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.dialog_push_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposableLyrics != null && mCompositeDisposable != null) {
            mDisposablePlayTime.dispose();
            mCompositeDisposable.clear();

        }
        if (mDisposableLyrics != null) {
            mDisposableLyrics.dispose();
        }
        unregisterReceiver(mVolumeReceiver);
    }
}
