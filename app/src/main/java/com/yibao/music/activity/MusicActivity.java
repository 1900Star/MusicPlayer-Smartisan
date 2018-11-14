package com.yibao.music.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.yibao.music.R;
import com.yibao.music.adapter.MusicPagerAdapter;
import com.yibao.music.base.BaseActivity;
import com.yibao.music.base.listener.OnMusicItemClickListener;
import com.yibao.music.base.listener.UpdataTitleListener;
import com.yibao.music.fragment.AlbumFragment;
import com.yibao.music.fragment.ArtistFragment;
import com.yibao.music.fragment.PlayListFragment;
import com.yibao.music.model.DetailsFlagBean;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.MusicLyricBean;
import com.yibao.music.model.PlayStatusBean;
import com.yibao.music.service.AudioPlayService;
import com.yibao.music.util.Constants;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.LyricsUtil;
import com.yibao.music.util.QueryMusicFlagListUtil;
import com.yibao.music.util.SnakbarUtil;
import com.yibao.music.util.SpUtil;
import com.yibao.music.util.StringUtil;
import com.yibao.music.util.TitleArtistUtil;
import com.yibao.music.util.ToastUtil;
import com.yibao.music.view.MainViewPager;
import com.yibao.music.view.music.MusicNavigationBar;
import com.yibao.music.view.music.QqControlBar;
import com.yibao.music.view.music.SmartisanControlBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * @author Stran
 * Des：${音乐列表界面}
 * Time:2017/5/30 13:27
 */
public class MusicActivity
        extends BaseActivity
        implements OnMusicItemClickListener, UpdataTitleListener {

    @BindView(R.id.tv_music_toolbar_title)
    TextView mTvMusicToolbarTitle;

    @BindView(R.id.music_navigation_bar)
    MusicNavigationBar mMusicNavigationBar;

    @BindView(R.id.music_viewpager)
    MainViewPager mMusicViewPager;

    @BindView(R.id.smartisan_control_bar)
    SmartisanControlBar mSmartisanControlBar;

    @BindView(R.id.qq_control_bar)
    QqControlBar mQqControlBar;


    private static AudioPlayService.AudioBinder audioBinder;
    private AudioServiceConnection mConnection;
    private MusicBean mCurrentMusicBean;
    private int mCurrentPosition;
    private boolean mMusicConfig;
    private boolean isShowQqBar;
    private int mPlayState;

    private int lyricsFlag = 0;
    private ArrayList<MusicLyricBean> mLyricList;
    private int mTitleResourceId = R.string.music_song;
    // 切换Tab时更改TiTle的标记,打开详情页面时正确显示Title
    private MusicBean mQqBarBean;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        mBind = ButterKnife.bind(this);
        initView();
        initData();
        initMusicConfig();
        initListener();
    }


    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar_music);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.music_titlebar_back_selector);
        toolbar.setNavigationOnClickListener(v -> MusicActivity.this.onBackPressed());
    }


    private void initData() {
        LogUtil.d("DetailsFlag  ==      " + SpUtil.getDetailFlag(this));
        List<MusicBean> initMusicList = audioBinder != null ? audioBinder.getMusicList() : QueryMusicFlagListUtil.getDataList(getSpMusicFlag(), mMusicDao);
        mCurrentPosition = SpUtil.getMusicPosition(this);
        mCurrentMusicBean = initMusicList.get(mCurrentPosition > initMusicList.size() ? 0 : mCurrentPosition);
        // 初始化 MusicPagerAdapter 主页面
        MusicPagerAdapter musicPagerAdapter = new MusicPagerAdapter(getSupportFragmentManager());
        mMusicViewPager.setAdapter(musicPagerAdapter);
        mMusicViewPager.setCurrentItem(Constants.NUMBER_TWO);
        mMusicViewPager.setOffscreenPageLimit(5);
    }

    private void initMusicConfig() {
        mMusicConfig = SpUtil.getMusicConfig(this, false);
        if (mMusicConfig) {
            mPlayState = SpUtil.getMusicPlayState(this);
            LogUtil.d("======= mPlayStae  " + mPlayState);
            if (mPlayState == Constants.NUMBER_ONE) {
                // 读取用户的播放记录，设置UI显示，做好播放的准备。(暂停和播放两种状态)
                perpareItem(mCurrentMusicBean);
            } else if (mPlayState == Constants.NUMBER_TWO) {
                executStartServiceAndInitAnimation();
            }
        } else {
            LogUtil.d("用户 ++++  nothing ");
        }


    }

    private void executStartServiceAndInitAnimation() {
        startMusicService(mCurrentPosition);
        mSmartisanControlBar.setPlayButtonState(R.drawable.btn_playing_pause_selector);
        mQqControlBar.setPlayButtonState(R.mipmap.notifycation_pause);
        mPlayState = Constants.NUMBER_THRRE;
    }

    @Override
    protected void refreshBtnAndNotify(PlayStatusBean bean) {
        switch (bean.getType()) {
            case Constants.NUMBER_ZOER:
                mSmartisanControlBar.animatorOnResume(audioBinder.isPlaying());
                updatePlayBtnStatus();
                break;
            case Constants.NUMBER_ONE:
                checkCurrentSongIsFavorite(mCurrentMusicBean, mQqControlBar, mSmartisanControlBar);
                break;
            case Constants.NUMBER_TWO:
                updatePlayBtnStatus();
                mSmartisanControlBar.animatorOnPause();
                break;
            default:
                break;
        }
    }

    private void initListener() {
        mMusicNavigationBar.setOnNavigationbarListener((currentSelecteFlag, titleResourceId) -> {
            mTitleResourceId = titleResourceId;
            if (currentSelecteFlag == 2 || currentSelecteFlag == 4) {
                mTvMusicToolbarTitle.setText(titleResourceId);
            } else {
                changeToolBarTitle(currentSelecteFlag);
            }
            mMusicViewPager.setCurrentItem(currentSelecteFlag, false);
        });
        mSmartisanControlBar.setClickListener(clickFlag -> {
            if (mMusicConfig) {
                if (clickFlag == Constants.NUMBER_THRRE) {
                    switchPlayState();
                } else {
                    if (audioBinder != null) {
                        switch (clickFlag) {
                            case Constants.NUMBER_ONE:
                                audioBinder.updataFavorite();
                                checkCurrentSongIsFavorite(mCurrentMusicBean, mQqControlBar, mSmartisanControlBar);
                                break;
                            case Constants.NUMBER_TWO:
                                clearDisposableProgresse();
                                audioBinder.playPre();
//                        restoreMuiscBean(mCurrentPosition + 1);
                                break;
                            case Constants.NUMBER_FOUR:
                                clearDisposableProgresse();
                                audioBinder.playNext();
//                        restoreMuiscBean(mCurrentPosition - 1);
                                break;
                            default:
                                break;
                        }
                    } else {
                        SnakbarUtil.favoriteSuccessView(mSmartisanControlBar, "请先播放音乐!");
                    }
                }
            } else {
                ToastUtil.showNoMusic(MusicActivity.this);
            }
        });
        mQqControlBar.setOnButtonClickListener(clickFlag -> {
            if (mMusicConfig) {
                switch (clickFlag) {
                    case Constants.NUMBER_ONE:
                        switchPlayState();
                        break;
                    case Constants.NUMBER_TWO:
                        if (audioBinder != null) {
                            audioBinder.updataFavorite();
                            checkCurrentSongIsFavorite(mCurrentMusicBean, mQqControlBar, mSmartisanControlBar);
                        } else {
                            SnakbarUtil.favoriteSuccessView(mSmartisanControlBar, "请先播放音乐!");
                        }
                    default:
                        break;
                }
            } else {
                ToastUtil.showNoMusic(MusicActivity.this);
            }
        });
        mTvMusicToolbarTitle.setOnClickListener(view -> switchMusicControlBar());
        mQqControlBar.setOnPagerSelecteListener(position -> {
            MusicActivity.this.disposableQqLyric();
            int detailFlag = SpUtil.getDetailFlag(MusicActivity.this);
            LogUtil.d("datailsFlag ===== " + detailFlag);
            if (detailFlag > 0) {
                // 播放列表
                if (detailFlag == Constants.NUMBER_EIGHT) {
                    LogUtil.d("QqBar  PlayMusicFragment 页面");
                    // 艺术家
                } else if (detailFlag == Constants.NUMBER_NINE) {
                    MusicActivity.this.startMusicServiceFlag(position, Constants.NUMBER_ONE, audioBinder.getMusicList().get(position).getArtist());
                    // 专辑
                } else if (detailFlag == Constants.NUMBER_TEN) {
                    MusicActivity.this.startMusicServiceFlag(position, Constants.NUMBER_TWO, audioBinder.getMusicList().get(position).getAlbum());
                    // 歌曲
                } else {
                    MusicActivity.this.startMusicServiceFlag(position, Constants.NUMBER_THRRE, audioBinder.getMusicList().get(position).getTitle());
                }
            } else if (SpUtil.getMusicDataListFlag(MusicActivity.this) == 8) {
                MusicActivity.this.startMusicService(position);
            } else {
                MusicActivity.this.startMusicService(position);
            }
//                restoreMuiscBean(position);
        });
    }

    private void changeToolBarTitle(int currentSelecteFlag) {
        switch (currentSelecteFlag) {
            case Constants.NUMBER_ZOER:
                setToolBarTitle(PlayListFragment.isShowDetailsView, PlayListFragment.detailsViewTitle);
                break;
            case Constants.NUMBER_ONE:
                setToolBarTitle(ArtistFragment.isShowDetailsView, ArtistFragment.detailsViewTitle);
                break;
            case Constants.NUMBER_THRRE:
                setToolBarTitle(AlbumFragment.isShowDetailsView, AlbumFragment.detailsViewTitle);
                break;
            default:
                break;
        }
    }

    private void setToolBarTitle(boolean isShowDetailsView, String detailsViewTitle) {
        if (isShowDetailsView) {
            if (detailsViewTitle != null) {
                mTvMusicToolbarTitle.setText(detailsViewTitle);
            }
        } else {
            mTvMusicToolbarTitle.setText(mTitleResourceId);
        }
    }


    /**
     * 切换当前播放状态
     * mPlayState  将音乐的播放状态记录到本地，方便用户下次打开时进行UI初始化操作。
     * <p>
     * mPlayState = 1 ：表示用户点击暂停后，并退出音乐播放器。下次打开播放器的界面时，
     * 不会自动播放上一次记录的歌曲，需要点击播放按钮，才能播放上一次记录的歌曲。
     * <p>
     * mPlayState = 2 ：表示在播放时退出音乐播放器的界面，只是短暂的离开，但并没有退出程序(程序并没有被后台杀死)，
     * 下次打开播放器的界面时，继续自动播放当前的歌曲。
     */
    private void switchPlayState() {
        if (mPlayState == Constants.NUMBER_ONE) {
            executStartServiceAndInitAnimation();
        } else if (mPlayState == Constants.NUMBER_TWO) {
            mPlayState = Constants.NUMBER_THRRE;
        } else {
            if (audioBinder == null) {
                ToastUtil.showNoMusic(this);
            } else if (audioBinder.isPlaying()) {
                // 当前播放  暂停
                audioBinder.pause();
                clearDisposableProgresse();
            } else if (!audioBinder.isPlaying()) {
                // 当前暂停  播放
                audioBinder.start();
                upDataPlayProgress();

            }
            mSmartisanControlBar.animatorOnResume(audioBinder.isPlaying());
            //更新播放状态按钮
            updatePlayBtnStatus();
        }
    }


    /**
     * 在主列表播放音乐
     * 开启服务，播放音乐并且将数据标记传送过去
     *
     * @param position 当前点击的曲目
     */
    @Override
    public void startMusicService(int position) {
        LogUtil.d("StartMusicService =====   ====== " + getSpMusicFlag());
        int sortFlag = getSpMusicFlag();
        mCurrentPosition = position;
        Intent musicIntent = new Intent(this, AudioPlayService.class);
        musicIntent.putExtra("sortFlag", sortFlag);
        musicIntent.putExtra("position", mCurrentPosition);
        mConnection = new AudioServiceConnection();
        startService(musicIntent);
        bindService(musicIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 在详情页面播放音乐回调
     * <p>
     * sortFlag        列表的排序方式
     *
     * @param position  播放位置
     * @param dataFlag  数据列表的标识
     * @param queryFlag 具体查询的条 ( 按 歌手 或 专辑查询 )
     */
    @Override
    public void startMusicServiceFlag(int position, int dataFlag, String queryFlag) {
        mCurrentPosition = position;
        Intent intent = new Intent(this, AudioPlayService.class);
        intent.putExtra("sortFlag", Constants.NUMBER_TEN);
        intent.putExtra("dataFlag", dataFlag);
        intent.putExtra("queryFlag", queryFlag);
        intent.putExtra("position", mCurrentPosition);
        mConnection = new AudioServiceConnection();
        startService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private int getSpMusicFlag() {
        return SpUtil.getMusicDataListFlag(this);
    }

    /**
     * PagerAdapter回调
     */
    @Override
    public void onOpenMusicPlayDialogFag() {
        readyMusic();
    }

    // 防止快速点击
    private void openMusicPlayDialogFag() {
        mCompositeDisposable.add(RxView.clicks(mSmartisanControlBar)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> readyMusic()));
    }

    private void readyMusic() {
        if (audioBinder != null) {
            if (mMusicConfig) {
                startPlayActivity();
            } else {
                ToastUtil.showNoMusic(MusicActivity.this);
            }
        } else {
            SnakbarUtil.favoriteSuccessView(mSmartisanControlBar, "请先播放音乐!");
        }
    }

    // 接收service发出的数据，时时更新播放歌曲 进度 歌名 歌手信息
    @Override
    protected void updataCurrentPlayInfo(MusicBean musicItem) {
        // 将MusicConfig设置为ture
        SpUtil.setMusicConfig(MusicActivity.this);
        mMusicConfig = true;
        // 更新歌曲的信息
        MusicActivity.this.perpareItem(musicItem);
        // 设置歌曲最大进度
        setDuration();
        // 更新播放按钮状态
        MusicActivity.this.updatePlayBtnStatus();
        // 初始化动画
        mSmartisanControlBar.initAnimation();
        //更新歌曲的进度
        upDataPlayProgress();
        // 打开通知栏
        if (mLyricList != null) {
            mLyricList.clear();
        }
        // 获取歌词的List
        if (isShowQqBar) {
            mLyricList = LyricsUtil.getLyricList(musicItem.getTitle(), musicItem.getArtist());
            mQqControlBar.setPagerData(audioBinder.getMusicList());
            mQqControlBar.setPagerCurrentItem(audioBinder.getPosition());
            setQqPagerLyric();
        }
    }

    /**
     * 切换音乐控制面板的样式
     */
    private void switchMusicControlBar() {
        if (isShowQqBar) {
            mQqControlBar.setVisibility(View.INVISIBLE);
            mSmartisanControlBar.setVisibility(View.VISIBLE);
            disposableQqLyric();
        } else {
            if (audioBinder != null) {
                List<MusicBean> musicList = audioBinder.getMusicList();
                mQqControlBar.updaPagerData(musicList, audioBinder.getPosition());
            }
            mQqControlBar.setVisibility(View.VISIBLE);
            mSmartisanControlBar.setVisibility(View.INVISIBLE);

            //TODO 这里做更新歌词的操作
            setQqPagerLyric();
        }
        isShowQqBar = !isShowQqBar;
    }

    /**
     * QQbar时时更新歌词
     */
    //TODO
    private void setQqPagerLyric() {
        disposableQqLyric();
        if (mQqLyricsDisposable == null) {
            mQqLyricsDisposable = Observable.interval(0, 1600, TimeUnit.MICROSECONDS)
//                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(musicBeanList -> {
                        if (mLyricList != null && mLyricList.size() > 1 && lyricsFlag < mLyricList.size()) {
                            //通过集合，播放过的歌词就从集合中删除
                            MusicLyricBean lyrBean = mLyricList.get(lyricsFlag);
                            String lyrics = lyrBean.getContent();
                            int progress = audioBinder.getProgress();
                            int startTime = lyrBean.getStartTime();
                            List<MusicBean> musicList = audioBinder.getMusicList();
                            if (progress > startTime) {
                                LogUtil.d("歌词List的长度    ==  " + mLyricList.size());
                                if (mCurrentPosition < musicList.size()) {
                                    mQqBarBean = musicList.get(mCurrentPosition);
                                    mQqBarBean.setCurrentLyrics(lyrics);
                                    musicList.set(mCurrentPosition, mQqBarBean);
                                }
                                LogUtil.d("当前的位置 ===  " + mCurrentPosition);
                                LogUtil.d("当前的进度 ===  " + progress);
                                LogUtil.d("当前的时间和歌词 ===  " + startTime + " ==  " + lyrics);
                                mQqControlBar.updaPagerData(musicList, mCurrentPosition);
                                lyricsFlag++;
                            }
                        }
                    });
        } else {
            LogUtil.d("=============没有时间和歌词 ");
            mQqControlBar.setPagerData(audioBinder.getMusicList());
        }

    }

    private void setDuration() {
        int duration = audioBinder.getDuration();
        mSmartisanControlBar.setMaxProgress(duration);
        mQqControlBar.setMaxProgress(duration);
    }


    /**
     * 设置歌曲名和歌手名
     *
     * @param musicItem g
     */
    private void perpareItem(MusicBean musicItem) {
        mCurrentMusicBean = musicItem;
        checkCurrentSongIsFavorite(mCurrentMusicBean, mQqControlBar, mSmartisanControlBar);
        // 更新音乐标题
        musicItem = TitleArtistUtil.getMusicBean(musicItem);
        mSmartisanControlBar.setSongName(musicItem.getTitle());
        // 更新歌手名称
        mSmartisanControlBar.setSingerName(mCurrentMusicBean.getArtist());
        // 设置专辑
        String albumUri = StringUtil.getAlbulm(mCurrentMusicBean.getAlbumId());
        mSmartisanControlBar.setAlbulmUrl(albumUri);
    }

    private void updataQqBar() {
        if (isShowQqBar) {
            mQqControlBar.updaPagerData(audioBinder.getMusicList(), audioBinder.getPosition());
            setQqPagerLyric();
        }
    }

    @Override
    protected void updataCurrentPlayProgress() {
        if (audioBinder != null && audioBinder.isPlaying()) {
            mSmartisanControlBar.setSongProgress(audioBinder.getProgress());
            mQqControlBar.setProgress(audioBinder.getProgress());
        }
    }

    private void updatePlayBtnStatus() {
        //根据当前播放状态设置图片
        mSmartisanControlBar.updatePlayBtnStatus(audioBinder.isPlaying());
        mQqControlBar.updatePlayButtonState(audioBinder.isPlaying());
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
                Intent intent = new Intent(this, SearchActivity.class);
                intent.putExtra("musicBean", mCurrentMusicBean);
                startActivity(intent);
                overridePendingTransition(R.anim.dialog_push_in, 0);
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
            audioBinder = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSmartisanControlBar.animatorOnPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (audioBinder != null) {
            perpareItem(audioBinder.getMusicBean());
            mSmartisanControlBar.animatorOnResume(audioBinder.isPlaying());
            checkCurrentSongIsFavorite(mCurrentMusicBean, mQqControlBar, mSmartisanControlBar);
            updatePlayBtnStatus();
            updataCurrentPlayProgress();
            setDuration();
            updataQqBar();

        }
        openMusicPlayDialogFag();

    }

    @Override
    public void updataTitle(String toolbarTitle, boolean isShowDetail) {
        mTvMusicToolbarTitle.setText(toolbarTitle);
    }


    @Override
    public void onBackPressed() {
        int detailFlag = SpUtil.getDetailFlag(this);
        if (detailFlag > Constants.NUMBER_ZOER) {
            mBus.post(new DetailsFlagBean(detailFlag));
            mTvMusicToolbarTitle.setText(mTitleResourceId);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindAudioService();
        handleAftermath();

    }

    public void unbindAudioService() {
        if (mConnection != null) {
            unbindService(mConnection);
            mConnection = null;
        }

    }

    private void handleAftermath() {
        if (mSmartisanControlBar != null) {
            mSmartisanControlBar.animatorStop();
        }
        if (audioBinder != null) {
            mPlayState = audioBinder.isPlaying() ? Constants.NUMBER_TWO : Constants.NUMBER_ONE;
            SpUtil.setMusicPlayState(this, mPlayState);
        }
        unbindAudioService();
//        stopService(new Intent(this, AudioPlayService.class));
    }


}
