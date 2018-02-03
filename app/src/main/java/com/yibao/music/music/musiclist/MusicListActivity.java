package com.yibao.music.music.musiclist;

import android.animation.ObjectAnimator;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding2.view.RxView;
import com.yibao.music.MyApplication;
import com.yibao.biggirl.R;
import com.yibao.music.base.listener.MyAnimatorUpdateListener;
import com.yibao.music.base.listener.OnMusicListItemClickListener;
import com.yibao.music.model.music.MusicBean;
import com.yibao.music.model.music.MusicDialogInfo;
import com.yibao.music.model.music.MusicStatusBean;
import com.yibao.music.music.musicplay.MusicPlayDialogFag;
import com.yibao.music.service.AudioPlayService;
import com.yibao.music.util.AnimationUtil;
import com.yibao.music.util.ColorUtil;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.MusicListUtil;
import com.yibao.music.util.RandomUtil;
import com.yibao.music.util.RxBus;
import com.yibao.music.util.SharePrefrencesUtil;
import com.yibao.music.util.StringUtil;
import com.yibao.music.util.ToastUtil;
import com.yibao.music.view.CircleImageView;
import com.yibao.music.view.MusicProgressView;
import com.yibao.music.view.ProgressBtn;
import com.yibao.music.view.music.MusicView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Stran
 *         Des：${音乐列表界面}
 *         Time:2017/5/30 13:27
 */
public class MusicListActivity
        extends AppCompatActivity
        implements OnMusicListItemClickListener {


    @BindView(R.id.iv_music_category_paly)
    ImageView mMusicCategoryPlay;
    @BindView(R.id.tv_music_category_songname)
    TextView mMusicCategorySongName;
    @BindView(R.id.tv_music_category_score)
    TextView mMusicCategoryScore;
    @BindView(R.id.tv_music_category_frequency)
    TextView mMusicCategoryFrequency;
    @BindView(R.id.tv_music_category_addtime)
    TextView mMusicCategoryAddtime;
    @BindView(R.id.tv_music_toolbar_title)
    TextView mTvMusicToolbarTitle;
    @BindView(R.id.music_float_song_name)
    TextView mMusicFloatSongName;
    @BindView(R.id.music_float_singer_name)
    TextView mMusicFloatSingerName;
    @BindView(R.id.music_floating_pre)
    ImageView mMusicFloatingPre;
    @BindView(R.id.music_floating_next)
    ImageView mMusicFloatingNext;
    @BindView(R.id.music_floating_block)
    RelativeLayout mMusicFloatBlock;
    @BindView(R.id.music_pager_block)
    LinearLayout mMusicPagerBlock;
    @BindView(R.id.music_floating_play)
    ImageView mMusicFloatingPlay;
    @BindView(R.id.music_floating_pager_play)
    MusicProgressView mMusicPagerPlay;
    @BindView(R.id.music_floating_pager_next)
    ImageView mMusicFloatingPagerPlayNext;
    @BindView(R.id.music_float_block_albulm)
    CircleImageView mMusicFloatBlockAlbulm;
    @BindView(R.id.music_float_pb)
    ProgressBtn mPb;
    @BindView(R.id.music_floating_vp)
    ViewPager mMusicSlideViewPager;

    private static AudioPlayService.AudioBinder audioBinder;
    @BindView(R.id.musci_view)
    MusicView mMusciView;
    private CompositeDisposable disposables;
    private ArrayList<MusicBean> mMusicItems;
    private Unbinder mBind;
    private ObjectAnimator mAnimator;
    private MyAnimatorUpdateListener mAnimatorListener;
    private AudioServiceConnection mConnection;
    private Disposable mDisposable;
    private RxBus mBus;
    private MusicBean mItem;
    private int mCurrentPosition;
    private boolean mMusicConfig;
    private boolean isChangeFloatingBlock;
    private int mPlayState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);
        mBind = ButterKnife.bind(this);
        mBus = MyApplication.getIntstance().bus();
        disposables = new CompositeDisposable();
        initView();
        initData();
        initMusicConfig();
        initRxBusData();
        initListener();
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar_music);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());
        mPb.setColor(ColorUtil.errorColor);
    }

    private void initData() {
        mMusicItems = MusicListUtil.getMusicList(this);
        MusicListAdapter adapter = new MusicListAdapter(this, mMusicItems);
        mMusciView.setAdapter(this, adapter);
        adapter.notifyDataSetChanged();

    }


    private void initMusicConfig() {
        mMusicConfig = SharePrefrencesUtil.getMusicConfig(this, false);
        if (mMusicConfig) {
            mCurrentPosition = SharePrefrencesUtil.getMusicPosition(this);
            mPlayState = SharePrefrencesUtil.getMusicPlayState(this);
            LogUtil.d("======= mPlayStae  " + mPlayState);
            if (mPlayState == 1) {
                // 读取用户的播放记录，设置UI显示，做好播放的准备。(暂停和播放两种状态)
                MusicBean musicInfo = mMusicItems.get(mCurrentPosition);
                perpareItem(musicInfo);
            } else if (mPlayState == 2) {
                executStartServiceAndInitAnimation();
            }
        } else {
            LogUtil.d("用户 ++++  nothing ");
        }
        MusicPagerAdapter musicPagerAdapter = new MusicPagerAdapter(this, mMusicItems, mCurrentPosition);
        mMusicSlideViewPager.setAdapter(musicPagerAdapter);

    }

    private void executStartServiceAndInitAnimation() {
        startMusicService(mCurrentPosition);
        initAnimation();
        mMusicFloatingPlay.setImageResource(R.drawable.btn_playing_pause_selector);
        mMusicPagerPlay.setIcon(R.mipmap.notifycation_pause);
        mPlayState = 3;
    }

    private void initListener() {
        openMusicPlayDialogFag();
        mTvMusicToolbarTitle.setOnClickListener(view -> switchControlBlock());
        mMusicSlideViewPager.addOnPageChangeListener(new MusicPagerListener() {
            @Override
            public void onPageSelected(int position) {
                startMusicService(position);
            }
        });
    }

    /**
     * PagerAdapter回调
     */
    @Override
    public void onOpenMusicPlayDialogFag() {
        if (mMusicConfig) {
            readyMusic();
        } else {
            ToastUtil.showNoMusic(this);
        }

    }

    private void openMusicPlayDialogFag() {
        RxView.clicks(mMusicFloatBlock)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> {
                    if (mMusicConfig) {
                        MusicListActivity.this.readyMusic();
                    } else {
                        ToastUtil.showNoMusic(MusicListActivity.this);
                    }
                });
    }

    private void readyMusic() {
        MusicDialogInfo info = new MusicDialogInfo(mMusicItems, mItem);
        MusicPlayDialogFag.newInstance(info)
                .show(getSupportFragmentManager(), "music");
    }

    private void initRxBusData() {
        //接收service发出的数据，时时更新播放歌曲 进度 歌名 歌手信息
        disposables.add(mBus.toObserverable(MusicBean.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(musicItem -> {
                    // 将MusicConfig设置为ture
                    SharePrefrencesUtil.setMusicConfig(this);
                    mMusicConfig = true;
                    MusicListActivity.this.perpareItem(musicItem);
                    //更新播放状态按钮
                    updatePlayBtnStatus();
                    //初始化动画
                    initAnimation();
                    //更新歌曲的进度
                    updataProgress();

                }));
        /*
         position = bean.getPosition() 用来判断触发消息的源头，
         < 0 >表示是通知栏播放和暂停按钮发出，
         同时MusicPlayDialogFag在播放和暂停的时候也会发出通知并且type也是< 0 >，
         MuiscListActivity会接收到两个地方发出的播放状态的消息,用于控制播放按钮的显示状态
         < 1 >表示从通知栏打开音列表，即整个通知栏布局的监听。
         < 2 >表示在通知栏关闭通知栏
         */
        disposables.add(mBus.toObserverable(MusicStatusBean.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(MusicListActivity.this::refreshBtnAndNotify));

    }

    private void refreshBtnAndNotify(MusicStatusBean bean) {
        switch (bean.getType()) {
            case 0:
                if (bean.isPlay()) {
                    audioBinder.pause();
                    mAnimator.pause();
                } else {
                    audioBinder.start();
                    mAnimator.resume();
                }
                updatePlayBtnStatus();
                break;
            case 1:
                startActivity(new Intent(this, MusicListActivity.class));
                break;
            case 2:
                finish();
                break;
            default:
                break;
        }
    }


    /**
     * 设置歌曲名和歌手名
     *
     * @param musicItem
     */
    private void perpareItem(MusicBean musicItem) {
        mMusicSlideViewPager.setCurrentItem(musicItem.getCureetPosition(), false);
        mItem = musicItem;
        //更新音乐标题
        String songName = StringUtil.getSongName(musicItem.getTitle());
        mMusicFloatSongName.setText(songName);
        //更新歌手名称
        String artistName = musicItem.getArtist();
        mMusicFloatSingerName.setText(artistName);
        //设置专辑
        Uri albumUri = StringUtil.getAlbulm(musicItem.getAlbumId());
        Glide.with(this)
                .load(albumUri.toString())
                .asBitmap()
                .error(R.drawable.sidebar_cover)
                .into(mMusicFloatBlockAlbulm);
    }

    private void updataProgress() {
        int duration = audioBinder.getDuration();
        mPb.setMax(duration);
        mMusicPagerPlay.setMax(duration);
        if (mDisposable == null) {

            mDisposable = Observable.interval(0, 2800, TimeUnit.MICROSECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> {
                        mPb.setProgress(audioBinder.getProgress());
                        mMusicPagerPlay.setProgress(audioBinder.getProgress());
                    });
        }

    }

    /**
     * 开启服务，播放音乐并且将数据传送过去
     *
     * @param position
     */
    @Override
    public void startMusicService(int position) {
        mCurrentPosition = position;
        //获取音乐列表
        Intent intent = new Intent();
        intent.setClass(this, AudioPlayService.class);
        intent.putParcelableArrayListExtra("musicItem", mMusicItems);
        intent.putExtra("position", mCurrentPosition);
        mConnection = new AudioServiceConnection();
        bindService(intent, mConnection, Service.BIND_AUTO_CREATE);
        startService(intent);

    }

    private void initAnimation() {
        if (mAnimator == null || mAnimatorListener == null) {
            mAnimator = AnimationUtil.getRotation(mMusicFloatBlockAlbulm);
            mAnimatorListener = new MyAnimatorUpdateListener(mAnimator);
            mAnimator.start();
        }
        mAnimator.resume();
    }


    private void updatePlayBtnStatus() {
        //根据当前播放状态设置图片
        if (audioBinder.isPlaying()) {
            mMusicFloatingPlay.setImageResource(R.drawable.btn_playing_pause_selector);
            mMusicPagerPlay.setIcon(R.mipmap.notifycation_pause);
        } else {

            mMusicFloatingPlay.setImageResource(R.drawable.btn_playing_play_selector);
            mMusicPagerPlay.setIcon(R.mipmap.notifycation_play);
        }
//        更新通知栏的按钮状态
//        MusicNoification.updatePlayBtn(audioBinder.isPlaying());
    }

//****************************************************************************
//****************************************************************************
//****************************************************************************
//****************************************************************************

    /**
     * 切换当前播放状态
     * mPlayState 记录音乐的播放状态到本地，方便用户下次打开时进行UI初始化操作。
     * <p>
     * mPlayState = 1 ：表示用户点击暂停后，并且退出音乐播放器，下次打开播放器的界面时，
     * 不会自动播放上一次记录的歌曲，需要点击播放按钮，才能播放上一次记录的歌曲。
     * <p>
     * mPlayState = 2 ：表示在播放时退出音乐播放器的界面，只是短暂的离开，但并没有退出程序，
     * 下次打开播放器的界面时，继续自动播放当前的歌曲。
     */
    private void switchPlayState() {
        if (mPlayState == 1) {
            LogUtil.d(" PlayState == 1 ==================");
            executStartServiceAndInitAnimation();
        } else if (mPlayState == 2) {
            mPlayState = 3;
        } else {
            if (audioBinder == null) {
                ToastUtil.showNoMusic(this);
            } else if (audioBinder.isPlaying()) {
                // 当前播放  暂停
                audioBinder.pause();
                mAnimator.pause();
                MyApplication.getIntstance()
                        .bus()
                        .post(new MusicStatusBean(0, true));
            } else if (!audioBinder.isPlaying()) {
                // 当前暂停  播放
                audioBinder.start();
                mAnimator.resume();
                MyApplication.getIntstance()
                        .bus()
                        .post(new MusicStatusBean(0, false));
            }
            //更新播放状态按钮
            updatePlayBtnStatus();
        }
    }


    @OnClick({R.id.iv_music_category_paly,
            R.id.tv_music_category_songname,
            R.id.tv_music_category_score,
            R.id.tv_music_category_frequency,
            R.id.tv_music_category_addtime,
            R.id.music_floating_pre,
            R.id.music_floating_play,
            R.id.music_floating_next,
            R.id.music_floating_pager_play,
            R.id.music_floating_pager_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_music_category_paly:
                int position = RandomUtil.getRandomPostion(mMusicItems);
                startMusicService(position);
//                audioBinder.setPalyMode(AudioPlayService.PLAY_MODE_RANDOM);

                break;
            case R.id.tv_music_category_songname:
                switchListCategory(1);
                break;
            case R.id.tv_music_category_score:
                switchListCategory(2);
                break;
            case R.id.tv_music_category_frequency:
                switchListCategory(3);
                break;
            case R.id.tv_music_category_addtime:
                switchListCategory(4);
                break;
            default:
                if (mMusicConfig) {
                    switch (view.getId()) {
                        case R.id.music_floating_pre:
                            audioBinder.playPre();
                            break;
                        case R.id.music_floating_pager_next:
                            audioBinder.playNext();
                            break;
                        case R.id.music_floating_pager_play:
                            switchPlayState();
                            break;
                        case R.id.music_floating_play:
                            switchPlayState();
                            break;
                        case R.id.music_floating_next:
                            audioBinder.playNext();
                            break;
                        default:
                            break;
                    }
                } else {
                    ToastUtil.showNoMusic(this);
                }
                break;
        }


    }

    private void switchListCategory(int flag) {
        switch (flag) {
            case 1:
                mMusicCategorySongName.setTextColor(ColorUtil.wihtle);
                mMusicCategorySongName.setBackgroundResource(R.drawable.btn_category_songname_down_selector);
                mMusicCategoryScore.setTextColor(ColorUtil.textName);
                mMusicCategoryScore.setBackgroundResource(R.drawable.btn_category_score_selector);
                mMusicCategoryFrequency.setTextColor(ColorUtil.textName);
                mMusicCategoryFrequency.setBackgroundResource(R.drawable.btn_category_score_selector);
                mMusicCategoryAddtime.setTextColor(ColorUtil.textName);
                mMusicCategoryAddtime.setBackgroundResource(R.drawable.btn_category_views_selector);
                break;
            case 2:
                mMusicCategoryScore.setTextColor(ColorUtil.wihtle);
                mMusicCategoryScore.setBackgroundResource(R.drawable.btn_category_score_down_selector);
                mMusicCategorySongName.setTextColor(ColorUtil.textName);
                mMusicCategorySongName.setBackgroundResource(R.drawable.btn_category_songname_selector);
                mMusicCategoryFrequency.setTextColor(ColorUtil.textName);
                mMusicCategoryFrequency.setBackgroundResource(R.drawable.btn_category_score_selector);
                mMusicCategoryAddtime.setTextColor(ColorUtil.textName);
                mMusicCategoryAddtime.setBackgroundResource(R.drawable.btn_category_views_selector);

                break;
            case 3:
                mMusicCategoryFrequency.setTextColor(ColorUtil.wihtle);
                mMusicCategoryFrequency.setBackgroundResource(R.drawable.btn_category_score_down_selector);
                mMusicCategorySongName.setTextColor(ColorUtil.textName);
                mMusicCategorySongName.setBackgroundResource(R.drawable.btn_category_songname_selector);
                mMusicCategoryScore.setTextColor(ColorUtil.textName);
                mMusicCategoryScore.setBackgroundResource(R.drawable.btn_category_score_selector);
                mMusicCategoryAddtime.setTextColor(ColorUtil.textName);
                mMusicCategoryAddtime.setBackgroundResource(R.drawable.btn_category_views_selector);

                break;
            case 4:
                mMusicCategoryAddtime.setTextColor(ColorUtil.wihtle);
                mMusicCategoryAddtime.setBackgroundResource(R.drawable.btn_category_views_down_selector);
                mMusicCategorySongName.setTextColor(ColorUtil.textName);
                mMusicCategorySongName.setBackgroundResource(R.drawable.btn_category_songname_selector);
                mMusicCategoryScore.setTextColor(ColorUtil.textName);
                mMusicCategoryScore.setBackgroundResource(R.drawable.btn_category_score_selector);
                mMusicCategoryFrequency.setTextColor(ColorUtil.textName);
                mMusicCategoryFrequency.setBackgroundResource(R.drawable.btn_category_score_selector);

                break;
            default:
                break;
        }


    }

    private void switchControlBlock() {
        if (isChangeFloatingBlock) {
            mMusicPagerBlock.setVisibility(View.INVISIBLE);
            mMusicFloatBlock.setVisibility(View.VISIBLE);
            isChangeFloatingBlock = false;
        } else {
            mMusicPagerBlock.setVisibility(View.VISIBLE);
            mMusicFloatBlock.setVisibility(View.INVISIBLE);
            isChangeFloatingBlock = true;
        }
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

                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    public static AudioPlayService.AudioBinder getAudioBinder() {
        return audioBinder;
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

    @Override
    protected void onPause() {
        super.onPause();
        if (mAnimator != null) {
            mAnimator.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerHeadsetReceiver();
        if (mAnimator != null) {
            mAnimator.resume();
        }
    }

    /**
     * 耳机插入和拔出监听
     */
    private void registerHeadsetReceiver() {
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(headsetReciver, intentFilter);
    }

    BroadcastReceiver headsetReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (audioBinder != null && audioBinder.isPlaying()) {
                switchPlayState();
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_HEADSETHOOK:
                switchPlayState();
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
        boolean b = mAnimator != null ||
                mAnimatorListener != null || mDisposable != null || mConnection != null;
        if (b) {
            mAnimator.cancel();
            mAnimatorListener.pause();
            mDisposable.dispose();
            unbindService(mConnection);
            mConnection = null;
        }
        disposables.clear();
        mBind.unbind();
        if (audioBinder != null) {
            audioBinder.closeNotificaction();
        }
        unregisterReceiver(headsetReciver);
        if (audioBinder != null) {
            mPlayState = audioBinder.isPlaying() ? 2 : 1;
            SharePrefrencesUtil.setMusicPlayState(this, mPlayState);

        }

    }


}
