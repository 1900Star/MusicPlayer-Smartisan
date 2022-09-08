package com.yibao.music.manager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import android.widget.RemoteViews;

import com.yibao.music.R;
import com.yibao.music.activity.MusicActivity;
import com.yibao.music.base.listener.NotifycationChangeListener;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.TitleAndArtistBean;
import com.yibao.music.util.CheckBuildVersionUtil;
import com.yibao.music.util.Constant;
import com.yibao.music.util.FileUtil;
import com.yibao.music.util.TitleArtistUtil;


/**
 * @author Stran
 * Des：${通知管理}
 * Time:2017/5/30 13:27
 */

public class MusicNotifyManager implements
        NotifycationChangeListener {
    private static final int PLAY_NOTIFY_ID = 0x1213;
    private final Context activity;
    private final NotificationManager manager;

    private boolean isPlay;
    private boolean isFavorite = false;
    private MusicBean mMusicBean;
    private final String channelId = "music";

    public MusicNotifyManager(Context activity, MusicBean musicBean, boolean isPlay) {
        this.activity = activity;
        this.mMusicBean = musicBean;
        this.isPlay = isPlay;
        this.manager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
    }


    private Notification buildNotification() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity);
        Intent intent = new Intent(activity, MusicActivity.class);
        int flag = CheckBuildVersionUtil.getNotifyFlag();

        PendingIntent startMainActivity = PendingIntent.getActivity(activity, 0, intent, flag);
        builder.setContentIntent(startMainActivity)
                .setTicker(activity.getString(R.string.app_name))
                .setSmallIcon(R.drawable.noalbumcover_120)
                .setWhen(System.currentTimeMillis())
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setCustomContentView(createContentView())
                .setCustomBigContentView(createContentBigView())
                .setChannelId(channelId)
                .setPriority(Notification.PRIORITY_HIGH);

        return builder.build();
    }

    private RemoteViews createContentBigView() {
        final RemoteViews view = new RemoteViews(activity.getPackageName(), R.layout.play_notify_big_view);
        setCommonView(view);
        setCommonClickPending(view);

        view.setImageViewResource(R.id.play_notify_favorite,
                isFavorite ? R.drawable.btn_favorite_red_selector :
                        R.drawable.btn_favorite_gray_selector);
        // Pre
        Intent pre = new Intent(Constant.ACTION_MUSIC);
        pre.putExtra(Constant.NOTIFY_BUTTON_ID, Constant.PREV);
        int flag = CheckBuildVersionUtil.getNotifyFlag();

        PendingIntent p3 = PendingIntent.getBroadcast(activity, Constant.PREV, pre, flag);
        view.setOnClickPendingIntent(R.id.play_notify_pre, p3);
        // favorite
        Intent favorite = new Intent(Constant.ACTION_MUSIC);
        favorite.putExtra(Constant.NOTIFY_BUTTON_ID, Constant.FAVORITE);
        PendingIntent p4 = PendingIntent.getBroadcast(activity, Constant.FAVORITE, favorite, flag);
        view.setOnClickPendingIntent(R.id.play_notify_favorite, p4);

        return view;
    }

    private RemoteViews createContentView() {
        final RemoteViews view = new RemoteViews(activity.getPackageName(), R.layout.play_notify_view);
        setCommonView(view);
        setCommonClickPending(view);
        return view;
    }

    /**
     * 图片，歌名，艺术家，播放按钮
     *
     * @param view v
     */
    private void setCommonView(RemoteViews view) {
        String musicName;
        String musicArtist;
        String musicTitle = mMusicBean.getTitle();

        if (musicTitle.contains(Constant.MQMS2)) {
            TitleAndArtistBean bean = TitleArtistUtil.getBean(musicTitle);
            musicName = bean.getSongName();
            musicArtist = bean.getSongArtist();
        } else {
            musicName = musicTitle;
            String artist = mMusicBean.getArtist();
            musicArtist = "<unknown>".equals(artist) ? "Smartisan" : artist;
        }
        String notifyAlbumUrl = FileUtil.getNotifyAlbumUrl(activity, mMusicBean);
        final Bitmap cover = createCover(notifyAlbumUrl);
        isFavorite = mMusicBean.getIsFavorite();
        view.setImageViewBitmap(R.id.play_notify_cover, cover);
        view.setTextViewText(R.id.play_notify_name, musicName);
        view.setTextViewText(R.id.play_notify_arts, musicArtist);
        view.setImageViewResource(R.id.play_notify_play,
                isPlay ? R.drawable.btn_playing_pause_selector
                        : R.drawable.btn_playing_play_selector);

    }

    /**
     * 播放或暂停，下一曲，关闭
     *
     * @param view v
     */
    private void setCommonClickPending(RemoteViews view) {
        int flag = CheckBuildVersionUtil.getNotifyFlag();
        // Play
        Intent playOrPause = new Intent(Constant.ACTION_MUSIC);
        playOrPause.putExtra(Constant.NOTIFY_BUTTON_ID, Constant.PLAY);
        PendingIntent p1 = PendingIntent.getBroadcast(activity, Constant.PLAY, playOrPause, flag);
        view.setOnClickPendingIntent(R.id.play_notify_play, p1);
        // Next
        Intent next = new Intent(Constant.ACTION_MUSIC);
        next.putExtra(Constant.NOTIFY_BUTTON_ID, Constant.NEXT);
        PendingIntent p2 = PendingIntent.getBroadcast(activity, Constant.NEXT, next, flag);
        view.setOnClickPendingIntent(R.id.play_notify_next, p2);
        // Close
        Intent close = new Intent(Constant.ACTION_MUSIC);
        close.putExtra(Constant.NOTIFY_BUTTON_ID, Constant.CLOSE);
        PendingIntent p3 = PendingIntent.getBroadcast(activity, Constant.CLOSE, close, flag);
        view.setOnClickPendingIntent(R.id.play_notify_close, p3);
    }


    @NonNull
    private Bitmap createCover(String path) {
        Bitmap b = BitmapFactory.decodeFile(path);
        if (b == null) {
            b = BitmapFactory.decodeResource(activity.getResources(), R.drawable.noalbumcover_220);
        }
        return b;
    }

    @Override
    public void show() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "music", NotificationManager.IMPORTANCE_LOW);
            channel.enableVibration(false);
            manager.createNotificationChannel(channel);
        }
        Notification nf = buildNotification();
        manager.notify(PLAY_NOTIFY_ID, nf);
    }

    @Override
    public void hide() {
        manager.cancelAll();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean visible() {
        return manager.areNotificationsEnabled();
    }

    @Override
    public void updataFavoriteBtn(boolean isCurrentFavorite) {
        mMusicBean.setIsFavorite(!isCurrentFavorite);
        show();
    }
//
//    @Override
//    public void updataPlayBtn(boolean isPlaying) {
//        isPlay = isPlaying;
//        show();
//    }
}