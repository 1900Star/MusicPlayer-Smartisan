package com.yibao.music.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.yibao.music.MyApplication;
import com.yibao.music.R;
import com.yibao.music.artisanlist.MusicNoification;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.MusicStatusBean;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.SharePrefrencesUtil;

import java.util.ArrayList;
import java.util.Random;


/**
 * @author Stran
 */
public class MusicService
        extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private MediaPlayer mediaPlayer;
    private static int PLAY_MODE;
    //三种播放模式
    public static final int PLAY_MODE_ALL = 0;
    public static final int PLAY_MODE_SINGLE = 1;
    public static final int PLAY_MODE_RANDOM = 2;
    //音乐通知栏
    public static final int ROOT = 0;
    public static final int PREV = 1;
    public static final int PLAY = 2;
    public static final int NEXT = 3;
    public static final int CLOSE = 4;
    //广播匹配
    public final static String BUTTON_ID = "ButtonId";
    public static final String ACTION_MUSIC = "MUSIC";

    private int position = -2;
    private ArrayList<MusicBean> mMusicItem;
    private MusicBroacastReceiver mReceiver;
    private NotificationManager mNotificationManager;
    private RemoteViews mRemoteViews;
    private MusicBean mMusicInfo;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d("=============Service  onCreate");
        mediaPlayer = new MediaPlayer();
        mRemoteViews = new RemoteViews(getPackageName(), R.layout.music_notify);
        initBroadcast();
        //初始化播放模式
        PLAY_MODE = SharePrefrencesUtil.getMusicMode(this);
    }


    private void initBroadcast() {
        mReceiver = new MusicBroacastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_MUSIC);
        registerReceiver(mReceiver, filter);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            LogUtil.d(getClass().getSimpleName() + "    first  onStartCommand");
//            mMusicItem = MusicListUtil.getMusicDataList(this);
            mMusicItem = intent.getParcelableArrayListExtra("musicItem");
            int enterPosition = intent.getIntExtra("position", 0);
            LogUtil.d("onStartCommand  position ====    " + enterPosition);
            LogUtil.d("onStartCommand  MusicItem Size ====    " + mMusicItem.size());
            if (enterPosition != position && enterPosition != -1) {
                position = enterPosition;
                //执行播放
                play();
            } else if (enterPosition != -1 && enterPosition == position) {
                //通知播放界面更新
                LogUtil.d("Service position  " + position);
//                sendCureentMusicInfo();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 通知播放界面更新
     */
    private void sendCureentMusicInfo() {
        MusicBean musicBean = mMusicItem.get(position);
        musicBean.setCureetPosition(position);
        MyApplication.getIntstance()
                .bus()
                .post(musicBean);
    }

    private void play() {

        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mMusicInfo = mMusicItem.get(position);
        mediaPlayer = MediaPlayer.create(MusicService.this,
                Uri.parse(mMusicInfo.getSongUrl()));

        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        SharePrefrencesUtil.setMusicPosition(MusicService.this, position);
    }


    //准备完成回调
    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        //开启播放
        mediaPlayer.start();
        //通知播放界面更新
//        sendCureentMusicInfo();
        LogUtil.d("********     onPrepared");
        //显示音乐通知栏
        showNotification();
    }

    private void showNotification() {
        Notification notification = MusicNoification.getNotification(getApplicationContext(), mRemoteViews,
                mMusicInfo);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(0, notification);
    }

    //获取当前播放进度

    public int getProgress() {
        return mediaPlayer.getCurrentPosition();
    }

    //获取音乐总时长
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    //音乐播放完成监听
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        //自动播放下一首歌曲
        autoPlayNext();
    }


    //自动播放下一曲
    private void autoPlayNext() {
        switch (PLAY_MODE) {
            case PLAY_MODE_ALL:
                position = (position + 1) % mMusicItem.size();
                break;
            case PLAY_MODE_SINGLE:

                break;
            case PLAY_MODE_RANDOM:
                position = new Random().nextInt(mMusicItem.size());
                break;
            default:
                break;
        }
        play();
    }

    //获取当前的播放模式
    public int getPalyMode() {
        return PLAY_MODE;
    }

    //设置播放模式
    public void setPalyMode(int playmode) {
        PLAY_MODE = playmode;
        //保存播放模式

        SharePrefrencesUtil.setMusicMode(MusicService.this, PLAY_MODE);
    }

    //手动播放上一曲
    public void playPre() {
        switch (PLAY_MODE) {
            case PLAY_MODE_RANDOM:
                position = new Random().nextInt(mMusicItem.size());
                break;
            default:
                if (position == 0) {
                    position = mMusicItem.size() - 1;
                } else {
                    position--;
                }
                break;
        }
        play();
    }

    //手动播放下一曲
    public void playNext() {
        switch (PLAY_MODE) {
            case PLAY_MODE_RANDOM:
                position = new Random().nextInt(mMusicItem.size());
                break;
            default:
                position = (position + 1) % mMusicItem.size();
                break;
        }
        play();
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

    //跳转到指定位置进行播放
    public void seekTo(int progress) {
        mediaPlayer.seekTo(progress);
    }

    //position : 1 表示点击通知栏进入播放界面，0 表示在通知栏进行播放和暂停的控制，并不进入播放的界面。
    private void playStatus(int type) {
        switch (type) {
            case 0:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    MyApplication.getIntstance()
                            .bus()
                            .post(new MusicStatusBean(type, true));
                } else {
                    mediaPlayer.start();
                    MyApplication.getIntstance()
                            .bus()
                            .post(new MusicStatusBean(type, false));
                    LogUtil.d("PLAY");
                }
                break;
            case 1:
                if (mediaPlayer.isPlaying()) {
                    MyApplication.getIntstance()
                            .bus()
                            .post(new MusicStatusBean(type, true));
                } else {
                    MyApplication.getIntstance()
                            .bus()
                            .post(new MusicStatusBean(type, false));
                    LogUtil.d("PLAY");
                }

                break;
            default:
                break;
        }

    }

    public void closeNotificaction() {
        if (mNotificationManager != null) {
            mNotificationManager.cancelAll();
        }
    }

    //+++++++++++==================================================================================
    //控制通知栏的广播
    private class MusicBroacastReceiver
            extends BroadcastReceiver

    {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ACTION_MUSIC)) {
                int id = intent.getIntExtra(BUTTON_ID, 0);
                switch (id) {
                    case ROOT:
                        LogUtil.d("Root");
                        playStatus(1);
                        break;
                    case CLOSE:
                        LogUtil.d("CLOSE");
                        playStatus(0);
                        mNotificationManager.cancel(0);
                        break;
                    case PREV:
                        playPre();
                        updatePlayBtn();
                        break;
                    case PLAY:
                        playStatus(0);
                        updatePlayBtn();
                        break;
                    case NEXT:
                        playNext();
                        updatePlayBtn();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    //通知栏的播放按钮监听
    private void updatePlayBtn() {
        int viewResource = mediaPlayer.isPlaying() ? R.mipmap.notifycation_play : R.mipmap.notifycation_pause;
        mRemoteViews.setImageViewResource(R.id.widget_play, viewResource);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        stopSelf();
    }
}
