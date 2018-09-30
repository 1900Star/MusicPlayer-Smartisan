package com.yibao.music.service;

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

import com.yibao.music.MusicApplication;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.MusicStatusBean;
import com.yibao.music.model.QqBarUpdataBean;
import com.yibao.music.model.greendao.MusicBeanDao;
import com.yibao.music.util.Constants;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.QueryMusicFlagListUtil;
import com.yibao.music.util.RxBus;
import com.yibao.music.util.SharePrefrencesUtil;

import java.util.List;
import java.util.Random;


/**
 * @author Stran
 * Des：${控制音乐的Service}
 * Time:2017/5/30 13:27
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
    private static final int FAVORITE = 0;
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
    private MusicBeanDao mMusicDao;
    private MusicBean mMusicBean;
    private RxBus mBus;

    public void setData(List<MusicBean> list) {
        mMusicDataList = list;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAudioBinder;
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAudioBinder = new AudioBinder();
        mBus = MusicApplication.getIntstance()
                .bus();
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
        // 更新QQBar
        mBus.post(new QqBarUpdataBean(mMusicDao, mMusicBean, sortFlag, dataFlag, queryFlag));
        mMusicDataList = QueryMusicFlagListUtil.getMusicDataList(mMusicDao, mMusicBean, sortFlag, dataFlag, queryFlag);
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


    /**
     * 通知播放界面更新
     */
    private void sendCureentMusicInfo() {

        LogUtil.d("  发送的postion  " + position);
        MusicBean musicBean = mMusicDataList.get(position);

        musicBean.setCureetPosition(position);
        mBus.post(musicBean);

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

        public MusicBean getTitleAndArtist() {
            return mMusicInfo;
        }
        //准备完成回调

        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            //开启播放
            mediaPlayer.start();
            //通知播放界面更新
            sendCureentMusicInfo();
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
    }

    //控制通知栏的广播
    private class MusicBroacastReceiver
            extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(ACTION_MUSIC)) {
                    int id = intent.getIntExtra(BUTTON_ID, 0);
                    switch (id) {
                        case FAVORITE:
                            mBus.post(new MusicStatusBean(1));
                            break;
                        case CLOSE:
                            mBus.post(new MusicStatusBean(2));
//                            onDestroy();
                            break;
                        case PREV:
                            mAudioBinder.playPre();
                            break;
                        case PLAY:
                            if (mAudioBinder.isPlaying()) {
                                mAudioBinder.pause();
                            } else {
                                mAudioBinder.start();
                            }
                            mBus.post(new MusicStatusBean(0));
                            break;
                        case NEXT:
                            mAudioBinder.playNext();
                            break;
                        default:
                            break;
                    }
                }
            }
        }
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
