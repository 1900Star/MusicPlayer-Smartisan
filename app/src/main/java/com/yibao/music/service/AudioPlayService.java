package com.yibao.music.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.yibao.music.MusicApplication;
import com.yibao.music.R;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.MusicStatusBean;
import com.yibao.music.model.greendao.MusicBeanDao;
import com.yibao.music.util.Constants;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.MusicListUtil;
import com.yibao.music.util.SharePrefrencesUtil;
import com.yibao.music.view.music.MusicNoification;

import java.util.List;
import java.util.Random;


/**
 * @author Stran
 */
public class AudioPlayService
        extends Service {

    private MediaPlayer mediaPlayer;
    private AudioBinder mAudioBinder;
    private static int PLAY_MODE;

    /**
     * 三种播放模式
     */
    public static final int PLAY_MODE_ALL = 0;
    public static final int PLAY_MODE_SINGLE = 1;
    public static final int PLAY_MODE_RANDOM = 2;
    /**
     * 音乐通知栏
     */
    public static final int ROOT = 0;
    public static final int PREV = 1;
    public static final int PLAY = 2;
    public static final int NEXT = 3;
    public static final int CLOSE = 4;
    /**
     * 广播匹配
     */
    public final static String BUTTON_ID = "ButtonId";
    public final static String ACTION_MUSIC = "MUSIC";

    private int position = -2;
    private List<MusicBean> mMusicDataList;
    private MusicBroacastReceiver mReceiver;
    private NotificationManager manager;
    private RemoteViews mRemoteViews;
    private MusicBeanDao mMusicDao;
    private MusicBean mMusicBean;

    @Override
    public IBinder onBind(Intent intent) {
        return mAudioBinder;
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mAudioBinder = new AudioBinder();
        mRemoteViews = new RemoteViews(getPackageName(), R.layout.music_notify);

        mMusicDao = MusicApplication.getIntstance().getMusicDao();
        mMusicBean = new MusicBean();
        initBroadcast();
        //初始化播放模式
        PLAY_MODE = SharePrefrencesUtil.getMusicMode(this);
    }


    private void initBroadcast() {
        mReceiver = new MusicBroacastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_MUSIC);
        mRemoteViews = new RemoteViews(getPackageName(), R.layout.music_notify);
        registerReceiver(mReceiver, filter);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int enterPosition = intent.getIntExtra("position", 0);
        int sortListFlag = intent.getIntExtra("sortFlag", 0);
        int dataFlag = intent.getIntExtra("dataFlag", 0);
        String queryFlag = intent.getStringExtra("queryFlag");
        int sortFlag = sortListFlag == Constants.NUMBER_ZOER ? Constants.NUMBER_ONE : sortListFlag;
        LogUtil.d(" position  ==" + enterPosition + "   sortListFlag  ==" + sortFlag + "  dataFlag== " + dataFlag + "   queryFlag== " + queryFlag);
        getMusicDataList(sortFlag, dataFlag, queryFlag);
        if (enterPosition != position && enterPosition != -1) {

            position = enterPosition;
            //执行播放
            mAudioBinder.play();
        } else if (enterPosition != -1) {
            //通知播放界面更新
            sendCureentMusicInfo();
        }
        return START_NOT_STICKY;
    }

    private void getMusicDataList(int sortListFlag, int dataFlag, String queryFlag) {
        // 按歌曲名
        if (sortListFlag == Constants.NUMBER_ONE) {
            mMusicDataList = mMusicDao.queryBuilder().list();
            // 按评分
        } else if (sortListFlag == Constants.NUMBER_TWO) {
            LogUtil.d("");
            // 按播放次数
        } else if (sortListFlag == Constants.NUMBER_THRRE) {
            LogUtil.d("");
            // 按添加时间
        } else if (sortListFlag == Constants.NUMBER_FOUR) {
            mMusicDataList = MusicListUtil.sortMusicAddtime(mMusicDataList);
            // 收藏列表
        } else if (sortListFlag == Constants.NUMBER_EIGHT) {
            mMusicDataList = mMusicDao.queryBuilder().where(MusicBeanDao.Properties.IsFavorite.eq(true)).build().list();
            // 艺术家、l
        } else if (sortListFlag == Constants.NUMBER_TEN) {
            // 按艺术家查询列表
            if (dataFlag == Constants.NUMBER_ONE) {
                mMusicBean.setArtist(queryFlag);
                mMusicDataList = mMusicDao.queryBuilder().where(MusicBeanDao.Properties.Artist.eq(mMusicBean.getArtist())).build().list();
                // 按专辑名查询列表
            } else if (dataFlag == Constants.NUMBER_TWO) {
                mMusicBean.setAlbum(queryFlag);
                mMusicDataList = mMusicDao.queryBuilder().where(MusicBeanDao.Properties.Album.eq(mMusicBean.getAlbum())).build().list();
            }

        }

    }


    /**
     * 通知播放界面更新
     */
    private void sendCureentMusicInfo() {

        LogUtil.d("  发送的postion  " + position);
        MusicBean musicBean = mMusicDataList.get(position);

        musicBean.setCureetPosition(position);
        MusicApplication.getIntstance()
                .bus()
                .post(musicBean);

    }


    public class AudioBinder
            extends Binder
            implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
        private MusicBean mMusicInfo;

        private void play() {

            if (mediaPlayer != null) {
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            mMusicInfo = mMusicDataList.get(position);
            mediaPlayer = MediaPlayer.create(AudioPlayService.this,
                    Uri.parse(mMusicInfo.getSongUrl()));

            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            SharePrefrencesUtil.setMusicPosition(AudioPlayService.this, position);
        }


        //准备完成回调

        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            //开启播放
            mediaPlayer.start();
            //通知播放界面更新
            sendCureentMusicInfo();
            //显示音乐通知栏
            showNotification();
        }

        private void showNotification() {
            Notification notification = MusicNoification.getNotification(getApplicationContext(), mRemoteViews,
                    mMusicInfo);
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            manager.notify(0, notification);
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
                    position = (position + 1) % mMusicDataList.size();
                    break;
                case PLAY_MODE_SINGLE:

                    break;
                case PLAY_MODE_RANDOM:
                    position = new Random().nextInt(mMusicDataList.size());
                    break;
                default:
                    break;
            }
            play();
        }

        // 获取当前的播放模式

        public int getPalyMode() {
            return PLAY_MODE;
        }

        //设置播放模式

        public void setPalyMode(int playmode) {
            PLAY_MODE = playmode;
            //保存播放模式

            SharePrefrencesUtil.setMusicMode(AudioPlayService.this, PLAY_MODE);
        }

        //手动播放上一曲

        public void playPre() {
            switch (PLAY_MODE) {
                case PLAY_MODE_RANDOM:
                    position = new Random().nextInt(mMusicDataList.size());
                    break;
                default:
                    if (position == 0) {
                        position = mMusicDataList.size() - 1;
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
                    position = new Random().nextInt(mMusicDataList.size());
                    break;
                default:
                    position = (position + 1) % mMusicDataList.size();
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

        // type : 1 表示点击通知栏进入播放界面，0 表示在通知栏进行播放和暂停的控制，并不进入播放的界面。

        private void playStatus(int type) {
            switch (type) {
                case 0:
                    if (mAudioBinder.isPlaying()) {
                        mAudioBinder.pause();
                        MusicApplication.getIntstance()
                                .bus()
                                .post(new MusicStatusBean(type, true));
                    } else {
                        mAudioBinder.start();
                        MusicApplication.getIntstance()
                                .bus()
                                .post(new MusicStatusBean(type, false));
                        LogUtil.d("PLAY");
                    }
                    break;
                case 1:
                    if (mAudioBinder.isPlaying()) {
                        MusicApplication.getIntstance()
                                .bus()
                                .post(new MusicStatusBean(type, true));
                    } else {
                        MusicApplication.getIntstance()
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
            if (manager != null) {
                manager.cancelAll();
            }
        }
    }

    //控制通知栏的广播

    private class MusicBroacastReceiver
            extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ACTION_MUSIC)) {
                int id = intent.getIntExtra(BUTTON_ID, 0);
                switch (id) {
                    case ROOT:
                        LogUtil.d("Root");
                        mAudioBinder.playStatus(1);
                        break;
                    case CLOSE:
                        LogUtil.d("CLOSE");
                        mAudioBinder.playStatus
                                (0);
                        manager.cancel(0);
                        break;
                    case PREV:
                        mAudioBinder.playPre();
                        updatePlayBtn();
                        break;
                    case PLAY:
                        updatePlayBtn();
                        mAudioBinder.playStatus(0);
                        break;
                    case NEXT:
                        mAudioBinder.playNext();
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
        int viewResource = mAudioBinder.isPlaying() ? R.mipmap.notifycation_play : R.mipmap.notifycation_pause;
        mRemoteViews.setImageViewResource(R.id.notify_play, viewResource);
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
