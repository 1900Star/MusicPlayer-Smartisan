package com.yibao.music;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.squareup.leakcanary.LeakCanary;
import com.yibao.music.model.greendao.DaoMaster;
import com.yibao.music.model.greendao.DaoSession;
import com.yibao.music.util.CrashHandler;
import com.yibao.music.util.RxBus;

import java.util.concurrent.TimeUnit;

import cn.bingoogolapple.swipebacklayout.BGASwipeBackHelper;
import okhttp3.OkHttpClient;

/**
 * 作者：Stran on 2017/3/23 15:12
 * 描述：${TODO}
 * 邮箱：strangermy@outlook.com
 */
public class MyApplication
        extends Application {
    private static MyApplication appContext;
    public static boolean isShowLog = true;

    private RxBus mRxBus;

    private DaoSession mDaoSession;

    public static MyApplication getIntstance() {
        if (appContext == null) {
            appContext = new MyApplication();
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
        mRxBus = new RxBus();
        BGASwipeBackHelper.init(this, null);

    }

    private void setUpDataBase() {
        DaoMaster.DevOpenHelper mHelper = new DaoMaster.DevOpenHelper(this, "favorite-db", null);
        SQLiteDatabase db = mHelper.getWritableDatabase();
        DaoMaster mMaster = new DaoMaster(db);
        mDaoSession = mMaster.newSession();


    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }


    public RxBus bus() {
        return mRxBus;
    }



    public static OkHttpClient defaultOkHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .build();
    }

}
