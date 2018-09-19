package com.yibao.music.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * Author：Sid
 * Des：${TODO}
 * Time:2017/11/23 23:16
 * @author Stran
 */
public class AudioServiceConnection
        implements ServiceConnection
{


    private AudioPlayService.AudioBinder audioBinder;

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        audioBinder = (AudioPlayService.AudioBinder) iBinder;
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }
}
