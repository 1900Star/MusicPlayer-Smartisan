package com.yibao.music.util;

import android.database.Cursor;
import android.provider.MediaStore;

import com.yibao.music.MyApplication;
import com.yibao.music.model.AlbumInfo;
import com.yibao.music.model.ArtistInfo;
import com.yibao.music.model.MusicBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author：Sid
 * Des：${ 音乐列表 }
 * Time:2017/9/3 14:38
 *
 * @author Stran
 */
public class MusicListUtil {


    /**
     * 从本地获取歌曲的信息，保存在List当中
     *
     * @return
     */
    public static ArrayList<MusicBean> getMusicDataList() {
        ArrayList<MusicBean> musicInfos = new ArrayList<>();
        Cursor cursor = MyApplication.getIntstance().getApplicationContext().getContentResolver()
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
        int addDed = cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED);
        int musicType = cursor.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE);
        int issueYear = cursor.getColumnIndex(MediaStore.Audio.Media.YEAR);
        for (int i = 0, p = cursor.getCount(); i < p; i++) {
            cursor.moveToNext();
            MusicBean info = new MusicBean();
            // 音乐id
            long id = cursor.getLong(mId);
            // 音乐标题
            String title = cursor.getString(mTitle);
            // 艺术家
            String artist = cursor.getString(mArtist);
            // 专辑
            String album = cursor.getString(mAlbum);
            long albumId = cursor.getInt(mAlbumID);
            // 时长
            long duration = cursor.getInt(mDuration);
            // 添加时间
            int addTime = (int) cursor.getLong(addDed);
            // 文件大小
            long size = cursor.getLong(mSize);
            //发行时间
            int year = cursor.getInt(issueYear);
            // 文件路径
            String url = cursor.getString(mUrl);
            //过滤掉小于2分钟的音乐,后续可以通过SharePreference让用户在UI界面自行选择。
            if (size > 21600) {
                info.setFirstChar(HanziToPinyins.stringToPinyinSpecial(title) + "");
                info.setTitle(title);
                info.setArtist(artist);
                info.setAlbum(album);
                info.setAlbumId(albumId);
                info.setDuration(duration);
                info.setAddTime(addTime);
                info.setSongUrl(url);
                info.setIssueYear(year);
                musicInfos.add(info);
            }
        }
        cursor.close();
        Collections.sort(musicInfos);
        LogUtil.d("musicUtil ========== " + musicInfos.size());
        return musicInfos;
    }

    /**
     * 按添加时间排序
     *
     * @param musicList c
     */
    public static ArrayList<MusicBean> sortMusicAddtime(ArrayList<MusicBean> musicList) {
        Collections.sort(musicList, (m1, m2) -> {
            if (m1 == m2) {
                return 0;
            }
            if (m1 == null) {
                return -1;
            }
            if (m2 == null) {
                return 1;
            }
            if (m1.equals(m2)) {
                return 0;
            }

            int value = Float.compare(m2.getAddTime(), m1.getAddTime());
            if (value != 0) {
                return value;
            }
            // https://stackoverflow.com/questions/28004269/java-collections-sort-comparison-method-violates-its-general-contract#
            // Warning, this line is not fool proof as unequal objects can have identical hash codes.
            return m1.hashCode() - m2.hashCode();
        });
        return musicList;
    }

    /**
     * 按ABCD 首字母排序
     *
     * @param musicList
     */
    public static ArrayList<MusicBean> sortMusicAbc(ArrayList<MusicBean> musicList) {
        String str = "#";
        Collections.sort(musicList, (m1, m2) -> {
            if (str.equals(m2.getFirstChar())) {
                return -1;
            }
            if (str.equals(m1.getFirstChar())) {
                return 1;
            }
            return m1.getFirstChar().compareTo(m2.getFirstChar());
        });
        return musicList;
    }


    //按艺术家分类
    public static ArrayList<ArtistInfo> getArtistList(ArrayList<MusicBean> list) {
        Map<String, List<MusicBean>> musicMap = new HashMap<>(16);
        ArrayList<ArtistInfo> singerInfoList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            MusicBean musicInfo = list.get(i);
            if (musicMap.containsKey(musicInfo.getArtist())) {
                ArrayList<MusicBean> singerList = (ArrayList<MusicBean>) musicMap.get(musicInfo.getArtist());
                singerList.add(musicInfo);
            } else {
                ArrayList<MusicBean> tempList = new ArrayList<>();
                tempList.add(musicInfo);
                musicMap.put(musicInfo.getArtist(), tempList);

            }
        }

        for (Map.Entry<String, List<MusicBean>> entry : musicMap.entrySet()) {

            ArtistInfo artistInfo = new ArtistInfo();
            artistInfo.setFirstChar(HanziToPinyins.stringToPinyinSpecial(entry.getKey()) + "");
            artistInfo.setArtist(entry.getKey());
            artistInfo.setSongCount(entry.getValue().size());
            artistInfo.setAlbumName(entry.getValue().get(0).getAlbum());
            artistInfo.setYear(entry.getValue().get(0).getIssueYear());
            artistInfo.setAlbumId(entry.getValue().get(0).getAlbumId());
            singerInfoList.add(artistInfo);
        }
        Collections.sort(singerInfoList);
        return singerInfoList;
    }

    //    //按专辑分组

    public static ArrayList<AlbumInfo> getAlbumList(ArrayList<MusicBean> list) {
        Map<String, List<MusicBean>> musicMap = new HashMap<>(16);
        ArrayList<AlbumInfo> albumInfoList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            MusicBean musicInfo = list.get(i);
            if (musicMap.containsKey(musicInfo.getAlbum())) {
                ArrayList<MusicBean> albumList = (ArrayList<MusicBean>) musicMap.get(musicInfo.getAlbum());
                albumList.add(musicInfo);
            } else {
                ArrayList<MusicBean> tempList = new ArrayList<>();
                tempList.add(musicInfo);
                musicMap.put(musicInfo.getAlbum(), tempList);
            }
        }

        for (Map.Entry<String, List<MusicBean>> entry : musicMap.entrySet()) {
            AlbumInfo albumInfo = new AlbumInfo();

            albumInfo.setAlbumName(entry.getKey());
            albumInfo.setArtist(entry.getValue().get(0).getArtist());
            albumInfo.setAlbumId(entry.getValue().get(0).getAlbumId());
            albumInfo.setSongName(entry.getValue().get(0).getTitle());
            albumInfo.setYear(entry.getValue().get(0).getIssueYear());
            albumInfo.setAlbumId(entry.getValue().get(0).getAlbumId());
            albumInfo.setFirstChar(HanziToPinyins.stringToPinyinSpecial(entry.getKey()) + "");
            albumInfo.setSongCount(entry.getValue().size());
            albumInfoList.add(albumInfo);
        }
        Collections.sort(albumInfoList);
        return albumInfoList;
    }

}
