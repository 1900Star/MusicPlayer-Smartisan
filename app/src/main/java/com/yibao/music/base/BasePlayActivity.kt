package com.yibao.music.base

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Bundle
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.widget.ImageView
import android.widget.SeekBar
import androidx.viewbinding.ViewBinding
import com.yibao.music.R
import com.yibao.music.activity.MusicActivity
import com.yibao.music.base.listener.OnCheckFavoriteListener
import com.yibao.music.base.listener.SeekBarChangeListtener
import com.yibao.music.service.MusicPlayService
import com.yibao.music.service.MusicPlayService.AudioBinder
import com.yibao.music.util.ToastUtil
import com.yibao.music.view.music.LyricsView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.base
 * @文件名: BasePlayActivity
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.strangermy98@gmail.com
 * @创建时间: 2018/2/18 11:09
 * @描述： {仅仅针对 PlayActivity抽出的基类,目的在于减少PlayActivity中的代码}
 */
abstract class BasePlayActivity<T : ViewBinding> : BaseTransitionActivity<T>(),
    OnCheckFavoriteListener {

    protected lateinit var mAudioManager: AudioManager

    @JvmField
    protected var mMaxVolume = 0

    @JvmField
    protected var audioBinder: AudioBinder? = null
    private var mWakeLock: WakeLock? = null
    protected var isScreenAlwaysOn = false
    private var mVolumeReceiver: VolumeReceiver? = null

    @JvmField
    protected var mDisposableLyrics: Disposable? = null

    @JvmField
    protected var mVolume = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        registerVolumeReceiver()
    }

    @SuppressLint("InvalidWakeLockTag")
    private fun init() {
        audioBinder = MusicActivity.getAudioBinder()
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        mWakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "Music  Lock")
        mAudioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
    }

    /**
     * 根据进度滚动歌词
     */
    protected fun startRollPlayLyrics(lyricsView: LyricsView) {
        if (mDisposableLyrics == null) {
            mDisposableLyrics = Observable.interval(30, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    lyricsView.rollText(
                        audioBinder!!.progress, audioBinder!!.duration
                    )
                }
//            mCompositeDisposable.add(mDisposableLyrics)
        }
    }

    /**
     * 音乐的播放模式 : 全部循环-单曲循环-随机播放
     */
    protected fun switchPlayMode(imageView: ImageView) {
        //获取当前的播放模式
        val playMode = audioBinder!!.playMode
        when (playMode) {
            MusicPlayService.PLAY_MODE_ALL -> audioBinder!!.playMode =
                MusicPlayService.PLAY_MODE_SINGLE
            MusicPlayService.PLAY_MODE_SINGLE -> audioBinder!!.playMode =
                MusicPlayService.PLAY_MODE_RANDOM
            MusicPlayService.PLAY_MODE_RANDOM -> audioBinder!!.playMode =
                MusicPlayService.PLAY_MODE_ALL
            else -> {}
        }
        //根据当前模式,更新播放模式图片
        updatePlayModeImage(audioBinder!!.playMode, imageView)
    }

    /**
     * 更新播放模式图片
     *
     * @param playMode 播放模式
     */
    protected fun updatePlayModeImage(playMode: Int, mMusicPlayerMode: ImageView) {
        when (playMode) {
            MusicPlayService.PLAY_MODE_ALL -> mMusicPlayerMode.setImageResource(R.drawable.audio_playmode_allrepeat_selector)
            MusicPlayService.PLAY_MODE_SINGLE -> mMusicPlayerMode.setImageResource(R.drawable.audio_playmode_single_selector)
            MusicPlayService.PLAY_MODE_RANDOM -> mMusicPlayerMode.setImageResource(R.drawable.audio_playmode_random_selector)
            else -> {}
        }
    }

    /**
     * 打开歌词时，可以保持屏幕常亮
     * 屏幕常亮默认设置为30分钟
     */
    protected fun screenAlwaysOnSwitch(mIvSecreenSunSwitch: ImageView) {
        mIvSecreenSunSwitch.setImageResource(if (isScreenAlwaysOn) R.drawable.sun_always_of_selector else R.drawable.sun_always_on_selector)
        ToastUtil.showScreenOnAndOff(this, isScreenAlwaysOn)
        if (isScreenAlwaysOn) {
            mWakeLock!!.release()
        } else {
            val screenTime = 30 * 60 * 1000L
            mWakeLock!!.acquire(screenTime)
        }
        isScreenAlwaysOn = !isScreenAlwaysOn
    }

    // 注册音量监听广播
    protected fun registerVolumeReceiver() {
        mVolumeReceiver = VolumeReceiver()
        val filter = IntentFilter()
        filter.addAction("android.media.VOLUME_CHANGED_ACTION")
        registerReceiver(mVolumeReceiver, filter)
    }

    // 音量监听广播
    inner class VolumeReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val volumeAction = "android.media.VOLUME_CHANGED_ACTION"
            if (volumeAction == intent.action) {
                val mAudioManager = context.getSystemService(AUDIO_SERVICE) as AudioManager
                // 当前的媒体音量
                val currVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                // 如果系统音量发生变化就更新Seekbar
                updateVolumeProgress(currVolume)
            }
        }
    }

    /**
     * 系统音量发生变化时更新音量的Seekbar
     *
     * @param currVolume c
     */
    abstract fun updateVolumeProgress(currVolume: Int)

    /**
     * 音乐进度条和音量条的监听器
     */
    inner class SeekBarListener : SeekBarChangeListtener() {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, b: Boolean) {
            super.onProgressChanged(seekBar, progress, b)
            updateMusicBarAndVolumeBar(seekBar, progress, b)
        }
    }

    /**
     * 更新音乐进度条和音量条，子类去具体操作。
     *
     * @param seekBar  s
     * @param progress p
     * @param b        b
     */
    protected abstract fun updateMusicBarAndVolumeBar(seekBar: SeekBar?, progress: Int, b: Boolean)

    /**
     * 停止更新
     */
    override fun onPause() {
        super.onPause()
        clearDisposableLyric()
        if (mWakeLock != null) {
            if (mWakeLock!!.isHeld) {
                mWakeLock!!.release()
                mWakeLock = null
            }
        }
    }

    protected fun clearDisposableLyric() {
        if (mDisposableLyrics != null) {
            mDisposableLyrics!!.dispose()
            mDisposableLyrics = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mVolumeReceiver)
        if (audioBinder != null) {
            audioBinder = null
        }
    }
}