package com.yibao.music.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.yibao.music.MusicApplication;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.greendao.MusicBeanDao;
import com.yibao.music.model.song.MusicCountBean;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.MusicListUtil;
import com.yibao.music.util.RxBus;

import java.util.List;


/**
 * @项目名： BigGirl
 * @包名： com.yibao.biggirl.service
 * @文件名: LoadMusicDataServices
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/1/30 23:38
 * @描述： {TODO}
 */

public class LoadMusicDataService extends IntentService {

    private MusicBeanDao mMusicDao;
    private int songCount = 0;
    private RxBus mBus;

    @Override
    public void onCreate() {
        super.onCreate();
        mMusicDao = MusicApplication.getIntstance().getMusicDao();
        mBus = MusicApplication.getIntstance().bus();
    }

    public LoadMusicDataService() {
        super("LoadMusicDataServices");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        List<MusicBean> dataList = MusicListUtil.getMusicDataList();
        int songSum = dataList.size();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            dataList.forEach((MusicBean bean) -> sendLoadProgress(songSum, bean));
        } else {
            for (MusicBean musicInfo : dataList) {
                sendLoadProgress(songSum, musicInfo);
            }
        }
        LogUtil.d("LoadMusicDataServices===== 加载数据完成");

    }

    private void sendLoadProgress(int songSum, MusicBean bean) {
        songCount++;
        mMusicDao.insert(bean);

        mBus.post(new MusicCountBean(songCount, songSum));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }
}
