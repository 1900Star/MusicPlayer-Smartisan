package com.yibao.music.manager

import android.app.Notification
import com.yibao.music.model.MusicBean
import com.yibao.music.base.listener.NotificationChangeListener
import android.app.NotificationManager
import android.content.Intent
import com.yibao.music.activity.MainActivity
import com.yibao.music.util.VersionUtil
import android.app.PendingIntent
import com.yibao.music.R
import android.widget.RemoteViews
import com.yibao.music.util.TitleArtistUtil
import com.yibao.music.util.FileUtil
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.app.NotificationChannel
import android.content.Context
import androidx.core.app.NotificationCompat
import com.yibao.music.util.Constant

/**
 * @author Stran
 * Des：${通知管理}
 * Time:2017/5/30 13:27
 */
class MusicNotifyManager(
    private val activity: Context, private val mMusicBean: MusicBean, private val isPlay: Boolean
) : NotificationChangeListener {
    private var mNotifyManager: NotificationManager =
        activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private var isFavorite = false
    private val channelId = "music"
    private val channelName = "artist_music"

    private fun buildNotification(): Notification {

        val builder = NotificationCompat.Builder(activity, channelId)
        val intent = Intent(activity, MainActivity::class.java)
        val flag = VersionUtil.getNotifyFlag()
        val startMainActivity = PendingIntent.getActivity(activity, 0, intent, flag)
        builder.setContentIntent(startMainActivity).setTicker(activity.getString(R.string.app_name))
            .setSmallIcon(R.drawable.noalbumcover_120).setWhen(System.currentTimeMillis())
            .setOngoing(true).setOnlyAlertOnce(true).setCustomContentView(createContentView())
            .setCustomBigContentView(createContentBigView())
            .setStyle(NotificationCompat.DecoratedCustomViewStyle()).priority =
            NotificationCompat.PRIORITY_HIGH
        return builder.build()
    }

    private fun createContentBigView(): RemoteViews {
        val view = RemoteViews(activity.packageName, R.layout.play_notify_big_view)
        setCommonView(view)
        setCommonClickPending(view)
        view.setImageViewResource(
            R.id.play_notify_favorite,
            if (isFavorite) R.drawable.btn_favorite_red_selector else R.drawable.btn_favorite_gray_selector
        )
        // Pre
        val pre = Intent(Constant.ACTION_MUSIC)
        pre.putExtra(Constant.NOTIFY_BUTTON_ID, Constant.PREV)
        val flag = VersionUtil.getNotifyFlag()
        val p3 = PendingIntent.getBroadcast(activity, Constant.PREV, pre, flag)
        view.setOnClickPendingIntent(R.id.play_notify_pre, p3)
        // favorite
        val favorite = Intent(Constant.ACTION_MUSIC)
        favorite.putExtra(Constant.NOTIFY_BUTTON_ID, Constant.FAVORITE)
        val p4 = PendingIntent.getBroadcast(activity, Constant.FAVORITE, favorite, flag)
        view.setOnClickPendingIntent(R.id.play_notify_favorite, p4)
        return view
    }

    private fun createContentView(): RemoteViews {
        val view = RemoteViews(activity.packageName, R.layout.play_notify_view)
        setCommonView(view)
        setCommonClickPending(view)
        return view
    }

    /**
     * 图片，歌名，艺术家，播放按钮
     *
     * @param view v
     */
    private fun setCommonView(view: RemoteViews) {
        val musicName: String
        val musicArtist: String
        val musicTitle = mMusicBean.title
        if (musicTitle.contains(Constant.MQMS2)) {
            val bean = TitleArtistUtil.getBean(musicTitle)
            musicName = bean.songName
            musicArtist = bean.songArtist
        } else {
            musicName = musicTitle
            val artist = mMusicBean.artist
            musicArtist = if ("<unknown>" == artist) "Smartisan" else artist
        }
        val notifyAlbumUrl = FileUtil.getNotifyAlbumUrl(activity, mMusicBean)
        if (notifyAlbumUrl != null) {
            val cover = createCover(notifyAlbumUrl)
            view.setImageViewBitmap(R.id.play_notify_cover, cover)
        }
        isFavorite = mMusicBean.getIsFavorite()
        view.setTextViewText(R.id.play_notify_name, musicName)
        view.setTextViewText(R.id.play_notify_arts, musicArtist)
        view.setImageViewResource(
            R.id.play_notify_play,
            if (isPlay) R.drawable.btn_playing_pause_selector else R.drawable.btn_playing_play_selector
        )

    }

    /**
     * 播放或暂停，下一曲，关闭
     *
     * @param view v
     */
    private fun setCommonClickPending(view: RemoteViews) {
        val flag = VersionUtil.getNotifyFlag()
        // Play
        val playOrPause = Intent(Constant.ACTION_MUSIC)
        playOrPause.putExtra(Constant.NOTIFY_BUTTON_ID, Constant.PLAY)
        val p1 = PendingIntent.getBroadcast(activity, Constant.PLAY, playOrPause, flag)
        view.setOnClickPendingIntent(R.id.play_notify_play, p1)
        // Next
        val next = Intent(Constant.ACTION_MUSIC)
        next.putExtra(Constant.NOTIFY_BUTTON_ID, Constant.NEXT)
        val p2 = PendingIntent.getBroadcast(activity, Constant.NEXT, next, flag)
        view.setOnClickPendingIntent(R.id.play_notify_next, p2)
        // Close
        val close = Intent(Constant.ACTION_MUSIC)
        close.putExtra(Constant.NOTIFY_BUTTON_ID, Constant.CLOSE)
        val p3 = PendingIntent.getBroadcast(activity, Constant.CLOSE, close, flag)
        view.setOnClickPendingIntent(R.id.play_notify_close, p3)
    }

    private fun createCover(path: String): Bitmap {
        BitmapFactory.decodeFile(path) ?: return BitmapFactory.decodeFile(path)
        return BitmapFactory.decodeResource(activity.resources, R.drawable.noalbumcover_220)
    }

    override fun show() {
        val channel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        channel.enableVibration(false)
        mNotifyManager.createNotificationChannel(channel)
        mNotifyManager.notify(1, buildNotification())
//        with(NotificationManagerCompat.from(activity)) {
//            notify(PLAY_NOTIFY_ID, buildNotification())
//        }
    }

    override fun hide() {
        mNotifyManager.cancelAll()
    }

    override fun visible(): Boolean {

        return false
    }

    override fun updateFavoriteBtn(isCurrentFavorite: Boolean) {
        mMusicBean.setIsFavorite(!isCurrentFavorite)
        show()
    } //

    //    @Override
    //    public void updataPlayBtn(boolean isPlaying) {
    //        isPlay = isPlaying;
    //        show();
    //    }
    companion object {
        private const val PLAY_NOTIFY_ID = 0x1213
    }
}