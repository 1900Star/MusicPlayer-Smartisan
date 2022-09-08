package com.yibao.music.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;
import com.yibao.music.adapter.SplashPagerAdapter;
import com.yibao.music.base.BaseActivity;
import com.yibao.music.databinding.ActivitySplashBinding;
import com.yibao.music.fragment.dialogfrag.PermissionsDialog;
import com.yibao.music.fragment.dialogfrag.ScannerConfigDialog;
import com.yibao.music.model.MusicCountBean;
import com.yibao.music.service.LoadMusicDataService;
import com.yibao.music.util.ColorUtil;
import com.yibao.music.util.Constant;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.ServiceUtil;
import com.yibao.music.util.SharedPreferencesUtil;
import com.yibao.music.util.SpUtil;
import com.yibao.music.util.SystemUiVisibilityUtil;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author lsp
 * Des：${TODO}
 * Time:2017/4/22 02:00
 */
@SuppressLint("CustomSplashScreen")
public class SplashActivity
        extends BaseActivity {

    private String mScanner;
    private boolean mIsFirstScanner;
    private ActivitySplashBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initPermission();
        initView();

    }

    @SuppressLint("WrongConstant")
    private void initPermission() {
        String[] permissionArr = {Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE};
        AndPermission.with(this).runtime()
                .permission(permissionArr)
                .onGranted(permissions -> loadMusicData())
                .onDenied(permissions -> againPermission())
                .start();
    }

    private void initView() {
        SystemUiVisibilityUtil.hideStatusBar(getWindow(), true);
        mScanner = getIntent().getStringExtra(Constant.SCANNER_MEDIA);
        SplashPagerAdapter splashPagerAdapter = new SplashPagerAdapter(SpUtil.getPicUrlFlag(this, false));
        mBinding.vpSplash.setAdapter(splashPagerAdapter);
    }


    @Override
    protected void onResume() {
        super.onResume();

        mCompositeDisposable.add(mBus.toObserverable(String.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    // 首次安装，开启服务加载本地音乐，创建本地数据库。
                    if (!ServiceUtil.isServiceRunning(getApplicationContext(), Constant.LOAD_SERVICE_NAME)) {

                        startService(new Intent(getApplicationContext(), LoadMusicDataService.class));
                    }
                    updateLoadProgress();
                }));

    }

    private void againPermission() {
        LogUtil.d(TAG, "没有读取和写入的权限!");
        PermissionsDialog.newInstance("请打开存储的读取和写入权限，否则无法加载音乐!").show(getSupportFragmentManager(), "permissions");

    }

    private void loadMusicData() {
        if (mScanner == null) {
            mIsFirstScanner = true;
            // 是否是首次安装，本地数据库是否创建，等于 8 表示不是首次安装，数据库已经创建，直接进入MusicActivity。
            if (mSps.getInt(Constant.MUSIC_LOAD) == Constant.NUMBER_EIGHT) {
                countDownOperation(true);
            } else {
                LogUtil.d(TAG, "首次安装，开启服务加载本地音乐，创建本地数据库。");
                ScannerConfigDialog.newInstance(true).show(getSupportFragmentManager(), "auto_config");
//                // 首次安装，开启服务加载本地音乐，创建本地数据库。
                if (!ServiceUtil.isServiceRunning(this, Constant.LOAD_SERVICE_NAME)) {
                    startService(new Intent(this, LoadMusicDataService.class));
                }
                updateLoadProgress();
                mSps.putValues(new SharedPreferencesUtil.ContentValue(Constant.MUSIC_LOAD, Constant.NUMBER_EIGHT));
            }

        } else {
            // 手动扫描歌曲
            mIsFirstScanner = false;
            Intent intent = new Intent(this, LoadMusicDataService.class);
            intent.putExtra(Constant.SCANNER_MEDIA, Constant.SCANNER_MEDIA);
            startService(intent);
            updateLoadProgress();
        }


    }

    private void updateLoadProgress() {
        mBinding.tvMusicCount.setVisibility(View.VISIBLE);
        mBinding.musicCountPb.setVisibility(View.VISIBLE);
        mCompositeDisposable.add(mBus.toObserverable(MusicCountBean.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(musicCountBean -> {
                    // 初次启动时size为所有音乐的数量，当手动扫描时为新增歌曲的数量。
                    int size = musicCountBean.getSize();
                    String str;
                    int currentCount = musicCountBean.getCurrentCount();
                    if (size > 0) {
                        mBinding.musicCountPb.setMax(size);
                        str = "已经加载  " + currentCount + " 首本地音乐";
                        mBinding.tvMusicCount.setText(str);
                        mBinding.musicCountPb.setProgress(currentCount);
                        if (currentCount == size) {
                            mBinding.tvMusicCount.setTextColor(ColorUtil.lyricsSelecte);
                            str = "本地音乐加载完成 -_-  共" + size + "首歌";
                            mBinding.tvMusicCount.setText(str);
                            // 初次扫描完成后进入MusicActivity
                            if (mIsFirstScanner) {
                                // 初次加载的标记
                                SpUtil.setLoadMusicFlag(SplashActivity.this, Constant.NUMBER_EIGHT);
                            } else {
                                // 手动扫描新增歌曲数量
                                str = "新增 " + size + " 首歌曲";
                                mBinding.tvMusicCount.setText(str);
                            }
                            countDownOperation(mIsFirstScanner);
                        }
                    } else {
                        mBinding.tvMusicCount.setText(mIsFirstScanner ? "本地没有发现音乐,去下载歌曲后再来体验吧!" : "没有新增歌曲!");
                        countDownOperation(false);
                    }
                }));
    }

    /**
     * 倒计时操作
     *
     * @param b true 表示初次安装，自动扫描完成后直接进入MusicActivity 。 false 表示手动扫描，完成后停在SplashActivity页面。
     */
    private void countDownOperation(boolean b) {
        if (b) {
            startMusicActivity();
        } else {
            mBinding.tvMusicCount.setVisibility(View.GONE);
            mBinding.musicCountPb.setVisibility(View.GONE);
        }
    }

    private void startMusicActivity() {
        SplashActivity.this.startActivity(new Intent(SplashActivity.this,
                MusicActivity.class));
        finish();
    }


}
