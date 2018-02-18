package com.yibao.music.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.yibao.music.MyApplication;
import com.yibao.music.R;
import com.yibao.music.artisanlist.MusicActivity;
import com.yibao.music.base.listener.SeekBarChangeListtener;
import com.yibao.music.model.greendao.MusicBeanDao;
import com.yibao.music.service.AudioPlayService;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.RxBus;
import com.yibao.music.util.ToastUtil;

import io.reactivex.disposables.CompositeDisposable;


/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.base
 * @文件名: BaseActivity
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/18 11:09
 * @描述： {TODO}
 */

public abstract class BaseActivity extends AppCompatActivity {

    private VolumeReceiver mVolumeReceiver;
    protected AudioManager mAudioManager;
    protected int mMaxVolume;
    protected int mCurrentVolume;
    protected RxBus mBus;
    protected AudioPlayService.AudioBinder audioBinder;
    protected CompositeDisposable disposables;
    protected MusicBeanDao mMusicDao;
    private PowerManager.WakeLock mWakeLock;
    private boolean isScreenAlwaysOn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
    }

    private void init() {

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "Music Lock");
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        audioBinder = MusicActivity.getAudioBinder();
        mBus = MyApplication.getIntstance()
                .bus();
        disposables = new CompositeDisposable();
        mMusicDao = MyApplication.getIntstance().getMusicDao();
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

    public void registerVolumeReceiver() {
        mVolumeReceiver = new VolumeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.media.VOLUME_CHANGED_ACTION");
        registerReceiver(mVolumeReceiver, filter);
    }

    //音量监听广播

    private class VolumeReceiver
            extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //如果系统音量发生变化就更新Seekbar
            String volumeAction = "android.media.VOLUME_CHANGED_ACTION";
            if (volumeAction.equals(intent.getAction())) {
                mCurrentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                updataVolumeProgresse(mCurrentVolume);

            }
        }
    }

    /**
     *  更新音量的Seekbar
     * @param currVolume
     */
    public abstract void updataVolumeProgresse(int currVolume);

    public class SeekBarListener
            extends SeekBarChangeListtener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
            super.onProgressChanged(seekBar, progress, b);

            switch (seekBar.getId()) {
                case R.id.sb_progress:
                    if (!b) {
                        return;
                    }
                    // 更新音乐播放进度
                    audioBinder.seekTo(progress);
                    // 更新音乐进度数值
                    updataMusicProgress(progress);
                    break;
                // 更新音乐  SeekBar
                case R.id.sb_volume:
                    LogUtil.d("==============音乐控制 =========="+progress);
                    updataVolumeProgresse(progress);
                    break;
                default:
                    break;
            }
        }
    }

    protected abstract void updataMusicProgress(int progress);

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.dialog_push_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mVolumeReceiver);
    }
}
