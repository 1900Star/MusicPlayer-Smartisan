package com.yibao.music.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;

import com.yibao.music.MusicApplication;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.greendao.MusicBeanDao;
import com.yibao.music.model.MusicCountBean;
import com.yibao.music.util.Constants;
import com.yibao.music.util.FileUtil;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.MusicListUtil;
import com.yibao.music.util.ReadFavoriteFileUtil;
import com.yibao.music.util.RxBus;
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
    private int currentCount = 0;
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
        // 手动扫描本地歌曲
        if (getIsNeedAgainScaner(intent)) {
            // 数据库的歌曲和最新媒体库中歌曲数量比较
            List<MusicBean> beanList = mMusicDao.queryBuilder().build().list();
            int newSong = songSum - beanList.size();
            // newSong新增歌曲的数量，大于0表示有新的歌曲。
            if (newSong > 0) {
                // 对集合按添加时间排序，这样新增的歌曲就会排在最前面，通过循环将前面新增的歌曲添加到本地数据库
                List<MusicBean> newList = MusicListUtil.sortMusicAddTime(dataList, 1);
                for (int i = 0; i < newSong; i++) {
                    sendLoadProgress(newSong, newList.get(i));
                }
            } else {
                LogUtil.d("没有新增歌曲!");
                mBus.post(new MusicCountBean(Constants.NUMBER_ZOER, Constants.NUMBER_ZOER));
            }
        } else {
            if (songSum > 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    dataList.forEach((MusicBean bean) -> sendLoadProgress(songSum, bean));
                } else {
                    for (MusicBean musicInfo : dataList) {
                        sendLoadProgress(songSum, musicInfo);
                    }
                }
                LogUtil.d("lsp", "LoadMusicDataServices===== 加载数据完成");
                recoverFavoriteMusic(dataList);
            } else {
                // 本地没有发现歌曲
                mBus.post(new MusicCountBean(Constants.NUMBER_ZOER, Constants.NUMBER_ZOER));
            }
        }
    }

    private boolean getIsNeedAgainScaner(Intent intent) {
        if (intent != null) {
            String scanner = intent.getStringExtra(Constants.SCANNER_MEDIA);
            return scanner != null;
        }
        return false;
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
            LogUtil.d("自动恢复收藏列表");
        } else {
            LogUtil.d("没有发现歌曲收藏文件");
//            ToastUtil.showNotFoundFavoriteFile(this);

        }
    }

    private void sendLoadProgress(int songSum, MusicBean bean) {
        currentCount++;
        mMusicDao.insert(bean);
        mBus.post(new MusicCountBean(currentCount, songSum));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }
}
