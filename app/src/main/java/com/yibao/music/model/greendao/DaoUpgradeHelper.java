package com.yibao.music.model.greendao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.github.yuweiguocn.library.greendao.MigrationHelper;

import org.greenrobot.greendao.database.Database;

/**
 * @ Author: Luoshipeng
 * @ Name:   DaoUpgradeHelper
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/11/25/ 20:57
 * @ Des:    GreenDao数据库升级辅助类
 */
public class DaoUpgradeHelper extends DaoMaster.OpenHelper {
    public DaoUpgradeHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {

            @Override
            public void onCreateAllTables(Database db, boolean ifNotExists) {
                DaoMaster.createAllTables(db, ifNotExists);
            }

            @Override
            public void onDropAllTables(Database db, boolean ifExists) {
                DaoMaster.dropAllTables(db, ifExists);
            }
        }, MusicBeanDao.class, MusicInfoDao.class, SearchHistoryBeanDao.class);
    }
}
