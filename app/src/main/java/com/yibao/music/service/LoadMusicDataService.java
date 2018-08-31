package com.yibao.music.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;

import com.yibao.music.MusicApplication;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.greendao.MusicBeanDao;
import com.yibao.music.model.song.MusicCountBean;
import com.yibao.music.util.FileUtil;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.MusicListUtil;
import com.yibao.music.util.ReadFavoriteFileUtil;
import com.yibao.music.util.RxBus;
import com.yibao.music.util.StringUtil;
import com.yibao.music.util.ToastUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import io.reactivex.disposables.CompositeDisposable;


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
    private CompositeDisposable mDisposable;

    @Override
    public void onCreate() {
        super.onCreate();
        mMusicDao = MusicApplication.getIntstance().getMusicDao();
        mBus = MusicApplication.getIntstance().bus();
        mDisposable = new CompositeDisposable();
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
        LogUtil.d("lsp", "LoadMusicDataServices===== 加载数据完成");
        recoverFavoriteMusic(dataList);

    }

    private void recoverFavoriteMusic(List<MusicBean> musicBeanList) {
        if (FileUtil.getFavoriteFile()) {
            HashMap<String, String> songInfoMap = new HashMap<>();
            Set<String> stringSet = ReadFavoriteFileUtil.stringToSet();
            for (String s : stringSet) {
                String songName = s.substring(0, s.lastIndexOf("T"));
                String favoriteTime = s.substring(s.lastIndexOf("T") + 1);
                songInfoMap.put(songName, favoriteTime);
            }
            for (MusicBean musicBean : musicBeanList) {
                String favoriteTime = songInfoMap.get(musicBean.getTitle());
                if (favoriteTime != null) {
                    musicBean.setTime(favoriteTime);
                    musicBean.setIsFavorite(true);
                    mMusicDao.update(musicBean);
                }
            }
            LogUtil.d("lsp", "自动恢复收藏列表");
        } else {
            LogUtil.d("lsp", "没有发现歌曲收藏文件");
            ToastUtil.showNotFoundFavoriteFile(this);

        }
    }

    private void sendLoadProgress(int songSum, MusicBean bean) {
        songCount++;
        mMusicDao.insert(bean);

        mBus.post(new MusicCountBean(songCount, songSum));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDisposable.clear();
        mDisposable = null;
        stopSelf();
    }
}
