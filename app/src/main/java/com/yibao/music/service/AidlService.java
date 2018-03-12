package com.yibao.music.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.yibao.music.IMusicAidlInterface;


/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.service
 * @文件名: AidlService
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.strangermy98@gmail.com
 * @创建时间: 2018/3/5 23:57
 * @描述： {TODO}
 */

public class AidlService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {


        return mStub;
    }

    static class Mybinder extends Binder {
        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {


            return true;
        }
    }
    final IMusicAidlInterface.Stub mStub = new IMusicAidlInterface.Stub() {


        @Override
        public int add(int x, int y) throws RemoteException {
            return x + y;
        }

        @Override
        public int min(int x, int y) throws RemoteException {
            return x - y;
        }

    };

}
