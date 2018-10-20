package com.yibao.music.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;

import com.yibao.music.MusicApplication;
import com.yibao.music.activity.SplashActivity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Author：Sid
 * Des：${异常处理器}
 * Time:2017/9/2 19:58
 */
public class CrashHandler
        implements Thread.UncaughtExceptionHandler {

    // 崩溃日志本地保存地址
    private static final String CRASH_LOG_PATH = Environment.getExternalStorageDirectory()
            .getPath() + "/CrashLog/log/";
    private static final String TAG = "CrashHandler";
    private static final String FILE_NAME = "crash";
    private static final String FILE_NAME_SUFFIX = ".txt";
    private static CrashHandler sInstane = new CrashHandler();
    private Context mContext;
    private Thread.UncaughtExceptionHandler mDefaultCrashHandler;


    public static CrashHandler getInstance() {
        return sInstane;
    }

    public void init(Context context) {
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context.getApplicationContext();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        try {

            dumpExceptionToSdCard(ex);  //导出异常信息到SD卡中

            uploadExceptionToServer();  //将异常信息上传到服务器

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (mDefaultCrashHandler != null) {
            mDefaultCrashHandler.uncaughtException(thread, ex);
        } else {
            Process.killProcess(Process.myPid());
        }
    }

    private void uploadExceptionToServer() {
        //将异常信息上传到服务器
    }

    private void dumpExceptionToSdCard(Throwable ex)
            throws PackageManager.NameNotFoundException {
        SpUtil.setMusicPlayState(mContext, 1);
        if (!Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED)) {
            if (MusicApplication.isShowLog) {
                LogUtil.d(TAG, "sdcard unmounted,skip dump exception");
                return;
            }
        }
        File dir = new File(CRASH_LOG_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        long current = System.currentTimeMillis();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss").format(new Date(current));
        File file = new File(CRASH_LOG_PATH + FILE_NAME + time + FILE_NAME_SUFFIX);
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            pw.println(time);
            dumpPhoneInfo(pw);
            pw.println();
            ex.printStackTrace(pw);
            pw.close();

        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.d(TAG, "dump crash info failed");
        }
    }

    private void dumpPhoneInfo(PrintWriter pw)
            throws PackageManager.NameNotFoundException {
        PackageManager pm = mContext.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(),
                PackageManager.GET_ACTIVITIES);
        pw.print("App Version: ");
        pw.print(pi.versionName);
        pw.print('_');
        pw.println(pi.versionCode);
        //安卓版本号
        pw.print("OS Version: ");
        pw.print(Build.VERSION.RELEASE);
        pw.print("_");
        pw.println(Build.VERSION.SDK_INT);
        //手机制造厂
        pw.print("Vendor: ");
        pw.println(Build.MANUFACTURER);
        //手机型号
        pw.print("Model: ");
        pw.println(Build.MODEL);
        //CPU架构
        pw.print("CPU ABI: ");
        pw.print(Build.CPU_ABI);


    }


}
