package com.yibao.music.view.music;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.RemoteViews;

import com.yibao.music.R;
import com.yibao.music.activity.MusicActivity;
import com.yibao.music.base.listener.NotifycationChangeListener;
import com.yibao.music.model.MusicBean;
import com.yibao.music.service.AudioPlayService;
import com.yibao.music.util.StringUtil;


/**
 * @author Stran
 * Des：${通知管理}
 * Time:2017/5/30 13:27
 */

public class MusicNotifyManager implements
        NotifycationChangeListener {

    /**
     * 广播匹配
     */
    private final static String BUTTON_ID = "ButtonId";
    private final static String ACTION_MUSIC = "MUSIC";

    /**
     * 音乐通知栏
     */
    private static final int FAVORITE = 0;
    private static final int PREV = 1;
    private static final int PLAY = 2;
    private static final int NEXT = 3;
    private static final int CLOSE = 4;

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
        PendingIntent startMainActivity = PendingIntent.getActivity(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
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
                isFavorite ? R.mipmap.favorite_yes :
                        R.drawable.favorite_normal);
        // Pre
        Intent pre = new Intent(ACTION_MUSIC);
        pre.putExtra(AudioPlayService.BUTTON_ID, PREV);
        PendingIntent p3 = PendingIntent.getBroadcast(activity, PREV, pre, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.play_notify_pre, p3);
        // favorite
        Intent favorite = new Intent(ACTION_MUSIC);
        favorite.putExtra(BUTTON_ID, FAVORITE);
        PendingIntent p4 = PendingIntent.getBroadcast(activity, FAVORITE, favorite, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.play_notify_favorite, p4);

        return view;
    }

    private RemoteViews createContentView() {
        final RemoteViews view = new RemoteViews(activity.getPackageName(), R.layout.play_notify_view);
        setCommonView(view);
        setCommonClickPending(view);
        return view;
    }

    // 图片，歌名，艺术家，播放按钮
    private void setCommonView(RemoteViews view) {
        final String name = mMusicBean.getTitle();
        final String arts = mMusicBean.getArtist();
        String albumArtPath = StringUtil.getAlbumArtPath(activity, String.valueOf(mMusicBean.getAlbumId()));
        final Bitmap cover = createCover(albumArtPath);
        isFavorite = mMusicBean.getIsFavorite();
        view.setImageViewBitmap(R.id.play_notify_cover, cover);
        view.setTextViewText(R.id.play_notify_name, name);
        view.setTextViewText(R.id.play_notify_arts, arts);
        view.setImageViewResource(R.id.play_notify_play,
                isPlay ? R.drawable.ic_pause
                        : R.drawable.ic_play_arrow);

    }

    // 播放或暂停，下一曲，关闭
    private void setCommonClickPending(RemoteViews view) {
        // Play
        Intent playOrPause = new Intent(ACTION_MUSIC);
        playOrPause.putExtra(BUTTON_ID, PLAY);
        PendingIntent p1 = PendingIntent.getBroadcast(activity, PLAY, playOrPause, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.play_notify_play, p1);
        // Next
        Intent next = new Intent(ACTION_MUSIC);
        next.putExtra(BUTTON_ID, NEXT);
        PendingIntent p2 = PendingIntent.getBroadcast(activity, NEXT, next, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.play_notify_next, p2);
        // Close
        Intent close = new Intent(ACTION_MUSIC);
        close.putExtra(BUTTON_ID, CLOSE);
        PendingIntent p3 = PendingIntent.getBroadcast(activity, CLOSE, close, PendingIntent.FLAG_UPDATE_CURRENT);
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

    @Override
    public void updataPlayBtn(boolean isPlaying) {
        isPlay = isPlaying;
        show();
    }
}