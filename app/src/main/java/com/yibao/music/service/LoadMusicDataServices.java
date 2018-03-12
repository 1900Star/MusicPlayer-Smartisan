package com.yibao.music.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.yibao.music.MyApplication;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.song.MusicCountBean;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.MusicListUtil;
import com.yibao.music.util.ToastUtil;

import java.util.ArrayList;


/**
 * @项目名： BigGirl
 * @包名： com.yibao.biggirl.service
 * @文件名: LoadMusicDataServices
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/1/30 23:38
 * @描述： {TODO}
 */

public class LoadMusicDataServices extends IntentService {

    public LoadMusicDataServices() {
        super("LoadMusicDataServices");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        ArrayList<MusicBean> dataList = MusicListUtil.getMusicDataList();
        int size = dataList.size();
        int s = 0;
        for (MusicBean info : dataList) {
            s++;
            MyApplication.getIntstance().getMusicDao().insert(info);
            MyApplication.getIntstance().bus().post(new MusicCountBean(s, size));

        }
//        MyApplication.getIntstance()
//                .getDaoSession().getMusicBeanDao().deleteAll();
        ToastUtil.showNoMusic(this);
        LogUtil.d("LoadMusicDataServices===== 加载数据完成");

    }


}
