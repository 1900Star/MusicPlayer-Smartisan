package com.yibao.music.util;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.yibao.music.model.music.MusicBean;

import java.util.ArrayList;

/**
 * Author：Sid
 * Des：${ 音乐列表 }
 * Time:2017/9/3 14:38
 */
public class MusicListUtil {


    /**
     * 从本地获取歌曲的信息，保存在List当中
     *
     * @return
     */
    public static ArrayList<MusicBean> getMusicList(Context context) {
        Cursor cursor = context.getContentResolver()
                .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        null,
                        null,
                        null,
                        MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        int mId = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
        int mTitle = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
        int mArtist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        int mAlbum = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
        int mAlbumID = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
        int mDuration = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
        int mSize = cursor.getColumnIndex(MediaStore.Audio.Media.SIZE);
        int mUrl = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);

        //        int mIsMusic  = cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC);
        ArrayList<MusicBean> musicInfos = new ArrayList<>();
        for (int i = 0, p = cursor.getCount(); i < p; i++) {
            cursor.moveToNext();
            MusicBean info = new MusicBean();
            long id = cursor.getLong(mId); // 音乐id
            String title = cursor.getString(mTitle); // 音乐标题
            String artist = cursor.getString(mArtist); // 艺术家
            String album = cursor.getString(mAlbum); // 专辑
            long albumId = cursor.getInt(mAlbumID);
            long duration = cursor.getLong(mDuration); // 时长
            long size = cursor.getLong(mSize); // 文件大小
            String url = cursor.getString(mUrl); // 文件路径
            //过滤掉小于2分钟的音乐
            if (size > 21600) {
                info.setTitle(title);
                info.setArtist(artist);
                info.setAlbum(album);
                info.setAlbumId(albumId);
//                info.setTime(size);
                info.setSongUrl(url);
                musicInfos.add(info);
            }
        }
        cursor.close();
        return musicInfos;
    }
}
