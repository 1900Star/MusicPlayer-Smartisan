package com.yibao.music.service;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.yibao.music.MusicApplication;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.MusicCountBean;
import com.yibao.music.model.greendao.MusicBeanDao;
import com.yibao.music.util.CollectionUtil;
import com.yibao.music.util.Constants;
import com.yibao.music.util.FileUtil;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.MusicListUtil;
import com.yibao.music.util.ReadFavoriteFileUtil;
import com.yibao.music.util.RxBus;
import com.yibao.music.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


/**
 * @项目名： BigGirl
 * @包名： com.yibao.biggirl.service
 * @文件名: LoadMusicDataServices
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/1/30 23:38
 * @描述： {加载音乐的后台Service}
 */

public class LoadMusicDataService extends IntentService {
    private static final String TAG = " ==== " + LoadMusicDataService.class.getSimpleName() + "  ";
    private MusicBeanDao mMusicDao;
    private int currentCount = 0;
    private RxBus mBus;

    @Override
    public void onCreate() {
        super.onCreate();
        mMusicDao = MusicApplication.getIntstance().getMusicDao();
        mBus = RxBus.getInstance();
    }

    public LoadMusicDataService() {
        super("LoadMusicDataServices");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        List<MusicBean> newList = MusicListUtil.getMusicDataList();
        int songSum = newList.size();
        // 手动扫描本地歌曲
        if (getIsNeedAgainScanner(intent)) {
            // 数据库的歌曲和最新媒体库中歌曲数量比较
            List<MusicBean> oldList = mMusicDao.queryBuilder().build().list();
            List<MusicBean> different = (List<MusicBean>) CollectionUtil.getDifferent(newList, oldList);
            newList.retainAll(different);
            for (MusicBean musicBean : newList) {
                sendLoadProgress(newList.size(), musicBean);
            }
            oldList.retainAll(different);
            for (MusicBean musicBean : oldList) {
                mMusicDao.delete(musicBean);
            }
            mBus.post(new MusicCountBean(Constants.NUMBER_ZERO, Constants.NUMBER_ZERO));
        } else {
            // 首次安装自动扫描本地歌曲并创建本地数据库
            if (songSum > 0) {
                for (MusicBean musicInfo : newList) {
                    sendLoadProgress(songSum, musicInfo);
                }
                LogUtil.d(TAG, "LoadMusicDataServices===== 加载数据完成");
                recoverFavoriteMusic(newList);
            } else {
                // 本地没有发现歌曲
                mBus.post(new MusicCountBean(Constants.NUMBER_ZERO, Constants.NUMBER_ZERO));
            }
        }
    }

    /**
     * 是否为手动扫描
     *
     * @param intent i
     * @return true 为手动扫描   false 为自动扫描
     */
    private boolean getIsNeedAgainScanner(Intent intent) {
        if (intent != null) {
            String scanner = intent.getStringExtra(Constants.SCANNER_MEDIA);
            return scanner != null;
        }
        return false;
    }

    /**
     * 收藏歌曲的时候，会将歌曲的名字和收藏时间以字符串的形式储存在本地的一个文件中（favorite.txt），
     * 这样即使程序卸载重新安装也能恢复之前收藏过的歌曲,只要收藏了歌曲这个文件就会创建。
     *
     * @param musicBeanList 收藏List
     */
    private void recoverFavoriteMusic(List<MusicBean> musicBeanList) {
        if (FileUtil.getFavoriteFile()) {
            HashMap<String, String> songInfoMap = new HashMap<>(16);
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
            LogUtil.d(TAG, "自动恢复收藏列表");
        } else {
            LogUtil.d(TAG, "没有发现歌曲收藏文件");
        }
    }

    /**
     * 发送本地音乐总数量和当前已加载的音乐数量
     *
     * @param songSum 总数量
     * @param bean    当前MusicBean
     */
    private void sendLoadProgress(int songSum, MusicBean bean) {
        currentCount++;
        mBus.post(new MusicCountBean(currentCount, songSum));
        mMusicDao.insertOrReplace(bean);
    }

}
