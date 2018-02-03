package com.yibao.music.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;


/**
 * @项目名： BigGirl
 * @包名： com.yibao.biggirl.service
 * @文件名: BigServices
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/1/30 23:38
 * @描述： {TODO}
 */

public class BigServices extends Service {
    private MediaPlayer mediaPlayer;
    private AudioBinder mAudioBinder;

    private String mUrl;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mAudioBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mUrl = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Music/Song/New Divide.mp3";
        mAudioBinder = new AudioBinder();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mAudioBinder.play();
        return START_NOT_STICKY;
    }


    public class AudioBinder
            extends Binder
            implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener

    {

        private void play() {

            if (mediaPlayer != null) {
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            mediaPlayer = MediaPlayer.create(BigServices.this,
                    Uri.parse(mUrl));

            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
        }


        //准备完成回调
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            //开启播放
            mediaPlayer.start();

        }


        //音乐播放完成监听
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            //自动播放下一首歌曲
        }


        //true 当前正在播放
        public boolean isPlaying() {
            return mediaPlayer.isPlaying();
        }

        public void start() {
            mediaPlayer.start();
        }

        //暂停播放
        public void pause() {
            mediaPlayer.pause();

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        stopSelf();
    }
}
