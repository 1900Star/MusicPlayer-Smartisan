package com.yibao.music.activity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;

import com.yibao.music.IMusicAidlInterface;
import com.yibao.music.service.AidlService;

import flow.Flow;

/**
 * @author Stran
 */
public class SearchActivity extends AppCompatActivity {
    IMusicAidlInterface.Stub mStub;
    @Override protected void attachBaseContext(Context baseContext) {
        baseContext = Flow.configure(baseContext, this).install();
        super.attachBaseContext(baseContext);
    }

    @Override public void onBackPressed() {
//        if (!Flow.get(this).goBack()) {
//            super.onBackPressed();
//        }
//        try {
//            Intent intent = new Intent(this, AidlService.class);
//
//            int add = mStub.add(2, 9);
//            bindService(intent, mServiceConnection, Service.BIND_AUTO_CREATE);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
    }
    ServiceConnection mServiceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mStub = (IMusicAidlInterface.Stub) iBinder;

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
}
