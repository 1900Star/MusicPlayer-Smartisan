package com.yibao.music.artisanlist;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.yibao.music.R;
import com.yibao.music.model.MusicBean;
import com.yibao.music.service.AudioPlayService;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.StringUtil;

/**
 * Author：Sid
 * Des：${音乐通知栏}
 * Time:2017/9/3 02:12
 *
 * @author Stran
 */
public class MusicNoification {

    private static boolean p;

    public static Notification getNotification(Context context, RemoteViews remoteView, MusicBean info) {
        initRemotViews(context, remoteView, info);


        Notification.Builder builder = new Notification.Builder(context);
        Notification notification = builder.setSmallIcon(R.mipmap.gaki)
                .setOngoing(true)
                .setAutoCancel(true)
                .setContent(remoteView)
                .build();
        notification.bigContentView = remoteView;

        return notification;
    }

    private static void initRemotViews(Context context,
                                       RemoteViews remoteView,
                                       MusicBean info) {
        remoteView.setTextViewText(R.id.notify_song_name, info.getTitle());
        remoteView.setTextViewText(R.id.notify_song_artist, info.getArtist());
        //通知栏的专辑图片
        remoteView.setImageViewUri(R.id.widget_album, StringUtil.getAlbulm(info.getAlbumId()));
        remoteView.setImageViewResource(R.id.notify_close, R.mipmap.notifycation_close);
        remoteView.setImageViewResource(R.id.notify_prev, R.mipmap.notifycation_prev);
        remoteView.setImageViewResource(R.id.notify_next, R.mipmap.notifycation_next);
//        remoteView.setImageViewResource
// (R.id.widget_play, R.mipmap.notifycation_pause);
        remoteViewListenr(context, remoteView);

    }

    //通知栏的播放按钮监听
    public static void updatePlayBtn(RemoteViews remoteView, boolean plays) {
        if (plays) {
            remoteView.setImageViewResource(R.id.notify_play, R.mipmap.notifycation_play);
            p = false;
            LogUtil.d("===  play =");
        } else {
            LogUtil.d("===  pause =");
            remoteView.setImageViewResource(R.id.notify_play, R.mipmap.notifycation_pause);
            p = true;
        }
    }

    private static void remoteViewListenr(Context context, RemoteViews remoteViews) {
        Intent intent = new Intent(AudioPlayService.ACTION_MUSIC);

        //Root
        intent.putExtra(AudioPlayService.BUTTON_ID, AudioPlayService.ROOT);
        PendingIntent intentRoot = PendingIntent.getBroadcast(context,
                AudioPlayService.ROOT,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.music_notify_root, intentRoot);

        //Pre
        intent.putExtra(AudioPlayService.BUTTON_ID, AudioPlayService.PREV);
        PendingIntent intentPrev = PendingIntent.getBroadcast(context,
                AudioPlayService.PREV,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notify_prev, intentPrev);

        //Play
        intent.putExtra(AudioPlayService.BUTTON_ID, AudioPlayService.PLAY);
        PendingIntent intentPlay = PendingIntent.getBroadcast(context,
                AudioPlayService.PLAY,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notify_play, intentPlay);

        //Next
        intent.putExtra(AudioPlayService.BUTTON_ID, AudioPlayService.NEXT);
        PendingIntent intentNext = PendingIntent.getBroadcast(context,
                AudioPlayService.NEXT,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notify_next, intentNext);
        //Close
        intent.putExtra(AudioPlayService.BUTTON_ID, AudioPlayService.CLOSE);
        PendingIntent intentClose = PendingIntent.getBroadcast(context,
                AudioPlayService.CLOSE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notify_close, intentClose);


    }


}
