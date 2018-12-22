package com.yibao.music.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.RemoteException;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.yibao.music.R;
import com.yibao.music.aidl.IMusicAidlInterface;
import com.yibao.music.aidl.MusicBean;
import com.yibao.music.service.AudioPlayService;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.StringUtil;

/**
 * @ Author: Luoshipeng
 * @ Name:   MediaSessionManager
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/11/18/ 17:57
 * @ Des:    TODO
 */
public class MediaSessionManager {

    private static final String TAG = "MediaSessionManager";

    private IMusicAidlInterface.Stub mAudioBinder;
    private MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder stateBuilder;
    private Context mContext;

    public MediaSessionManager(Context context, IMusicAidlInterface.Stub audioBinder) {
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
            LogUtil.d(e.toString());
        }
    }

    public void updatePlaybackState(boolean isPlaying) {
        if (mMediaSession != null) {
            int state = isPlaying ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED;
            try {
                stateBuilder.setState(state, mAudioBinder.getPosition(), 1.0f);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mMediaSession.setPlaybackState(stateBuilder.build());
        }
    }

    public void updateLocMsg() {
        MusicBean info = null;
        try {
            info = mAudioBinder.getMusicBean();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        MediaMetadataCompat.Builder metaData = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, StringUtil.getTitle(info))
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, StringUtil.getArtist(info))
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, info.getAlbum())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, info.getArtist())
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, info.getDuration())
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, getCoverBitmap(info));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                metaData.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, mAudioBinder.getMusicList().size());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        if (mMediaSession != null) {
            mMediaSession.setMetadata(metaData.build());
        }
    }


    private MediaSessionCompat.Callback sessionCb = new MediaSessionCompat.Callback() {
        @Override
        public void onPlay() {
            super.onPlay();
            try {
                mAudioBinder.start();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onPause() {
            super.onPause();
            try {
                mAudioBinder.pause();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
            try {
                mAudioBinder.playNext();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            try {
                mAudioBinder.playPre();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    };

    private Bitmap getCoverBitmap(MusicBean musicBean) {
        String albumArtPath = StringUtil.getAlbumArtPath(mContext, String.valueOf(musicBean.getAlbumId()));
        if (StringUtil.isReal(albumArtPath)) {
            return BitmapFactory.decodeFile(albumArtPath);
        } else {
            return BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.nina);
        }
    }

    public void release() {
        mMediaSession.setCallback(null);
        mMediaSession.setActive(false);
        mMediaSession.release();
    }
}
