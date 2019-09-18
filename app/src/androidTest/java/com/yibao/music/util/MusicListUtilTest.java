package com.yibao.music.util;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.yibao.music.model.AlbumInfo;
import com.yibao.music.model.ArtistInfo;
import com.yibao.music.model.MusicBean;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * @ Author: Luoshipeng
 * @ Name:   MusicListUtilTest
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/5/16/ 21:01
 * @ Des:    //TODO
 */
@RunWith(AndroidJUnit4.class)
public class MusicListUtilTest {

    private Context mAppContext;
    private List<MusicBean> mMusicDataList;

    @Before
    public void setup() {
        mAppContext = InstrumentationRegistry.getTargetContext();
        mMusicDataList = MusicListUtil.getMusicDataList();
    }

    @Test
    public void checkContxtIsNull() {
        assertNotNull(mAppContext);
    }

    @Test
    public void getMusicDataTest() {
        for (MusicBean info : mMusicDataList) {
            System.out.println(info.getTitle());
        }
        assertNotNull(mMusicDataList);
    }

    @Test
    public void sortAbcList() {
        List<MusicBean> abcList = MusicListUtil.sortMusicAbc(mMusicDataList);
        assertNotNull(abcList);
    }

    @Test
    public void sortAddTimeList() {
//        List<MusicBean> abcList = MusicListUtil.sortMusicList(mMusicDataList);
//        assertNotNull(abcList);
    }

    @Test
    public void sortArtistList() {
        List<ArtistInfo> artistList = MusicListUtil.getArtistList(mMusicDataList);
        assertNotNull(artistList);
    }

    @Test
    public void sortAlbumList() {
        List<AlbumInfo> albumList = MusicListUtil.getAlbumList(mMusicDataList);
        assertNotNull(albumList);
    }

    @Test
    public void stringToLong() {
//        Long longTime = StringUtil.getLongTime("2009-12-02 10:17:51");
//        assertNotNull(longTime);
    }

    @Test
    public void subName() {
        String str = "童话T2009-12-02 10:17:51";
        String s = str.substring(str.lastIndexOf("T") + 1);
        assertNotNull(s);
    }

    @Test
    public void substringTime() {
        String str = "童话T2009-12-02 10:17:51";
        String s = str.substring(0, str.lastIndexOf("T"));
        assertNotNull(s);
    }

    @Test
    public void substringSongTitle() {
        String name = "周杰伦 - 一路向北 [mqms2]";
        String title = name.substring(name.lastIndexOf("-") + 2, name.lastIndexOf("[mqms2]") - 1);
        String artist = name.substring(0, name.indexOf("-") - 1);
        assertNotNull(title);
        assertNotNull(artist);
    }

    @After
    public void complete() {
        System.out.println("歌曲列表数据测试完成！");
    }

}