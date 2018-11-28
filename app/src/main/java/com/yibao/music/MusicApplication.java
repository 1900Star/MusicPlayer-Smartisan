package com.yibao.music;

import android.app.Activity;
import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.squareup.leakcanary.LeakCanary;
import com.yibao.music.model.greendao.AlbumInfoDao;
import com.yibao.music.model.greendao.DaoMaster;
import com.yibao.music.model.greendao.DaoSession;
import com.yibao.music.model.greendao.DaoUpgradeHelper;
import com.yibao.music.model.greendao.MusicBeanDao;
import com.yibao.music.model.greendao.MusicInfoDao;
import com.yibao.music.model.greendao.PlayListBeanDao;
import com.yibao.music.model.greendao.SearchHistoryBeanDao;
import com.yibao.music.util.CrashHandler;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.RxBus;

/**
 * 作者：Stran on 2017/3/23 15:12
 * 描述：${TODO}
 * 邮箱：strangermy@outlook.com
 *
 * @author Stran
 */
public class MusicApplication
        extends Application {
    private static MusicApplication appContext;
    public static boolean isShowLog = true;


    private DaoSession mDaoSession;
    private static MusicBeanDao musicBeanDao;

    public static MusicApplication getIntstance() {
        if (appContext == null) {
            appContext = new MusicApplication();
        }
        return appContext;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
        appContext = this;
        CrashHandler.getInstance()
                .init(this);
        setUpDataBase();
    }

    private void setUpDataBase() {
        DaoUpgradeHelper helper = new DaoUpgradeHelper(this, "favorite-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        mDaoSession = daoMaster.newSession();

    }

    public MusicBeanDao getMusicDao() {
        if (musicBeanDao == null) {
            synchronized ("MusicApplication.class") {
                if (musicBeanDao == null) {
                    musicBeanDao = mDaoSession.getMusicBeanDao();
                }
            }
        }
        return musicBeanDao;

    }

    public MusicInfoDao getMusicInfoDao() {
        return mDaoSession.getMusicInfoDao();
    }

    public SearchHistoryBeanDao getSearchDao() {
        return mDaoSession.getSearchHistoryBeanDao();
    }

    public PlayListBeanDao getPlayListDao() {
        return mDaoSession.getPlayListBeanDao();
    }

    public AlbumInfoDao getAlbumDao() {
        return mDaoSession.getAlbumInfoDao();
    }
}
