package com.yibao.music.artisanlist;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.yibao.music.MyApplication;
import com.yibao.music.R;
import com.yibao.music.album.MainActivity;
import com.yibao.music.base.BaseActivity;
import com.yibao.music.base.listener.OnMusicItemClickListener;
import com.yibao.music.model.MusicBean;
import com.yibao.music.service.AudioPlayService;
import com.yibao.music.util.Constants;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.MusicListUtil;
import com.yibao.music.util.RxBus;
import com.yibao.music.util.SharePrefrencesUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;

/**
 * @author Stran
 *         Des：${音乐列表界面}
 *         Time:2017/5/30 13:27
 */
public class MusicActivity
        extends BaseActivity
        implements OnMusicItemClickListener {
    @BindView(R.id.music_viewpager)
    ViewPager mViewPager;
    @BindView(R.id.tv_music_toolbar_title)
    TextView mTvMusicToolbarTitle;

    private CompositeDisposable disposables;
    private ArrayList<MusicBean> mMusicItems;
    private Unbinder mBind;
    private static AudioPlayService.AudioBinder audioBinder;
    private AudioServiceConnection mConnection;
    private RxBus mBus;
    private int mCurrentPosition;
    private int mPlayState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        mBind = ButterKnife.bind(this);
        mBus = MyApplication.getIntstance().bus();
        disposables = new CompositeDisposable();
        initView();
        initData();
    }


    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar_music);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

    }

    private void initData() {
        mMusicItems = MusicListUtil.getMusicDataList(this);

        MusicPagerAdapter musicPagerAdapter = new MusicPagerAdapter(getFragmentManager());
        mViewPager.setAdapter(musicPagerAdapter);

    }


    /**
     * PagerAdapter回调
     */
    @Override
    public void onOpenMusicPlayDialogFag() {


    }

    @Override
    public void switchViewPagerItem(int currentPage, int titleResourcesId) {
        mTvMusicToolbarTitle.setText(titleResourcesId);
        mViewPager.setCurrentItem(currentPage, false);

    }


    /**
     * 开启服务，播放音乐并且将数据传送过去
     *
     * @param position
     */
    @Override
    public void startMusicService(int position, int sortListFlag) {
        mCurrentPosition = position;
        Intent intent = new Intent();
        intent.setClass(this, AudioPlayService.class);
        intent.putExtra("position", mCurrentPosition);
        intent.putExtra("sortFlag", sortListFlag);
        mConnection = new AudioServiceConnection();
        bindService(intent, mConnection, Service.BIND_AUTO_CREATE);
        startService(intent);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.music_title_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_titlebar_search:
                LogUtil.d("==================search");
                Intent intent = new Intent();
                intent.setClass(this, MainActivity.class);
                intent.putExtra("position", 8);
                startActivity(intent);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    public static AudioPlayService.AudioBinder getAudioBinder() {
        return audioBinder;
    }

    @OnClick(R.id.tv_music_toolbar_title)
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.tv_music_toolbar_title:
                break;
        }
    }


    private class AudioServiceConnection
            implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            audioBinder = (AudioPlayService.AudioBinder) service;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }


    BroadcastReceiver headsetReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (audioBinder != null && audioBinder.isPlaying()) {
//                switchPlayState();
            }
        }
    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_HEADSETHOOK:
//                switchPlayState(); 播放暂停音乐
                break;
            case KeyEvent.KEYCODE_BACK:
                finish();
                break;
            default:
                break;
        }
        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (audioBinder != null && !audioBinder.isPlaying()) {
            audioBinder.closeNotificaction();
        }
        if (audioBinder != null) {
            mPlayState = audioBinder.isPlaying() ? Constants.NUMBER_TWO : Constants.NUMBER_ONE;
            SharePrefrencesUtil.setMusicPlayState(this, mPlayState);
        }
        unbindService(mConnection);
        mConnection = null;
        disposables.clear();
        unregisterReceiver(headsetReciver);
        mBind.unbind();

    }


}
