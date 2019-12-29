package com.yibao.music.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.yibao.music.R;
import com.yibao.music.model.MusicBean;
import com.yibao.music.service.MusicPlayService;
import com.yibao.music.util.FileUtil;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.StringUtil;

/**
 * @author Luoshipeng
 * @ Name:   MediaSessionManager
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/11/18/ 17:57
 * @ Des:    TODO
 */
public class MediaSessionManager {

    private static final String TAG = " ==== " + MediaSessionManager.class.getSimpleName() + "  ";

    private MusicPlayService.AudioBinder mAudioBinder;
    private MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder stateBuilder;
    private Context mContext;

    public MediaSessionManager(Context context, MusicPlayService.AudioBinder audioBinder) {
        this.mContext = context;
        this.mAudioBinder = audioBinder;
        initSession();
    }

    private void initSession() {
        try {
            mMediaSession = new MediaSessionCompat(mContext, TAG);
            mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                    | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
            stateBuilder = new PlaybackStateCompat.Builder()
                    .setActions(PlaybackStateCompat.ACTION_PLAY
                            | PlaybackStateCompat.ACTION_PLAY_PAUSE
                            | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                            | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);
            mMediaSession.setPlaybackState(stateBuilder.build());
            mMediaSession.setCallback(sessionCb);
            mMediaSession.setActive(true);
        } catch (Exception e) {
            LogUtil.d(TAG, e.toString());
        }
    }

    public void updatePlaybackState(boolean isPlaying) {
        if (mMediaSession != null) {
            int state = isPlaying ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED;
            stateBuilder.setState(state, mAudioBinder.getPosition(), 1.0f);
            mMediaSession.setPlaybackState(stateBuilder.build());
        }
    }

    public void updateLocMsg() {
        MusicBean info = mAudioBinder.getMusicBean();
        MediaMetadataCompat.Builder metaData = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, StringUtil.getTitle(info))
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, StringUtil.getArtist(info))
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, info.getAlbum())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, info.getArtist())
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, info.getDuration())
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, getCoverBitmap(info));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            metaData.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, mAudioBinder.getMusicList().size());
        }
        if (mMediaSession != null) {
            mMediaSession.setMetadata(metaData.build());
        }
    }


    private MediaSessionCompat.Callback sessionCb = new MediaSessionCompat.Callback() {
        @Override
        public void onPlay() {
            super.onPlay();
            mAudioBinder.start();
        }

        @Override
        public void onPause() {
            super.onPause();
            mAudioBinder.pause();
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
            mAudioBinder.playNext();
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            mAudioBinder.playPre();
        }

    };

    private Bitmap getCoverBitmap(MusicBean musicBean) {
//        String albumArtPath = StringUtil.getAlbumArtPath(mContext, String.valueOf(musicBean.getAlbumId()));
        String albumArtPath = FileUtil.getNotifyAlbumUrl(mContext, musicBean);
        if (StringUtil.isReal(albumArtPath)) {
            return BitmapFactory.decodeFile(albumArtPath);
        } else {
            return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.nina);
        }
    }

    public void release() {
        mMediaSession.setCallback(null);
        mMediaSession.setActive(false);
        mMediaSession.release();
        mAudioBinder = null;
    }
}
