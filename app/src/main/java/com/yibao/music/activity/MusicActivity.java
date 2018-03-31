package com.yibao.music.activity;

import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.yibao.music.R;
import com.yibao.music.adapter.MusicPagerAdapter;
import com.yibao.music.adapter.QqBarPagerAdapter;
import com.yibao.music.album.MainActivity;
import com.yibao.music.artisanlist.MusicPagerListener;
import com.yibao.music.base.BaseActivity;
import com.yibao.music.base.BaseFragment;
import com.yibao.music.base.listener.MyAnimatorUpdateListener;
import com.yibao.music.base.listener.OnBackHandlePressedListener;
import com.yibao.music.base.listener.OnMusicItemClickListener;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.MusicStatusBean;
import com.yibao.music.model.greendao.MusicBeanDao;
import com.yibao.music.model.song.MusicFavoriteBean;
import com.yibao.music.service.AudioPlayService;
import com.yibao.music.util.AnimationUtil;
import com.yibao.music.util.ColorUtil;
import com.yibao.music.util.Constants;
import com.yibao.music.util.ImageUitl;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.MusicListUtil;
import com.yibao.music.util.SharePrefrencesUtil;
import com.yibao.music.util.StringUtil;
import com.yibao.music.util.ToastUtil;
import com.yibao.music.view.CircleImageView;
import com.yibao.music.view.MusicProgressView;
import com.yibao.music.view.ProgressBtn;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Stran
 *         Des：${音乐列表界面}
 *         Time:2017/5/30 13:27
 */
public class MusicActivity
        extends BaseActivity
        implements OnMusicItemClickListener, OnBackHandlePressedListener {

    @BindView(R.id.tv_music_toolbar_title)
    TextView mTvMusicToolbarTitle;
    /**
     * smartisan
     */
    @BindView(R.id.music_float_song_name)
    TextView mMusicFloatSongName;
    @BindView(R.id.music_float_singer_name)
    TextView mMusicFloatSingerName;
    @BindView(R.id.music_floating_pre)
    ImageView mMusicFloatingPre;
    @BindView(R.id.music_floating_next)
    ImageView mMusicFloatingNext;
    @BindView(R.id.music_floating_play)
    ImageView mMusicFloatingPlay;
    @BindView(R.id.music_float_block_albulm)
    CircleImageView mMusicFloatBlockAlbulm;
    @BindView(R.id.smartisan_music_bar)
    RelativeLayout mSmartisanMusicBar;

    /**
     * QQ
     */
    @BindView(R.id.qq_music_bar)
    LinearLayout mQqMusicBar;
    @BindView(R.id.music_floating_pager_play)
    MusicProgressView mMusicPagerPlay;
    @BindView(R.id.music_floating_pager_favorite)
    ImageView mMusicQqBarFavorite;
    @BindView(R.id.music_float_pb)
    ProgressBtn mPb;
    @BindView(R.id.music_viewpager)
    ViewPager mMusicViewPager;
    @BindView(R.id.qq_music_vp)
    ViewPager mMusicSlideViewPager;
    @BindView(R.id.music_bar_playlist_iv)
    ImageView mMusicBarPlaylistIv;

    /**
     * tabbar
     */
    @BindView(R.id.music_bar_playlist_tv)
    TextView mMusicBarPlaylistTv;
    @BindView(R.id.music_bar_playlist)
    LinearLayout mMusicBarPlaylist;
    @BindView(R.id.music_bar_artisanlist_iv)
    ImageView mMusicBarArtisanlistIv;
    @BindView(R.id.music_bar_artisanlist_tv)
    TextView mMusicBarArtisanlistTv;
    @BindView(R.id.music_bar_artisanlist)
    LinearLayout mMusicBarArtisanlist;
    @BindView(R.id.music_bar_songlist_iv)
    ImageView mMusicBarSonglistIv;
    @BindView(R.id.music_bar_songlist_tv)
    TextView mMusicBarSonglistTv;
    @BindView(R.id.music_bar_songlist)
    LinearLayout mMusicBarSonglist;
    @BindView(R.id.music_bar_albumlist_iv)
    ImageView mMusicBarAlbumlistIv;
    @BindView(R.id.music_bar_albumlist_tv)
    TextView mMusicBarAlbumlistTv;
    @BindView(R.id.music_bar_albumlist)
    LinearLayout mMusicBarAlbumlist;
    @BindView(R.id.music_bar_stylelist_iv)
    ImageView mMusicBarStylelistIv;
    @BindView(R.id.music_bar_stylelist_tv)
    TextView mMusicBarStylelistTv;
    @BindView(R.id.music_bar_stylelist)
    LinearLayout mMusicBarStylelist;


    private List<MusicBean> mMusicItems;
    private Unbinder mBind;
    private ObjectAnimator mAnimator;
    private static AudioPlayService.AudioBinder audioBinder;
    private MyAnimatorUpdateListener mAnimatorListener;
    private AudioServiceConnection mConnection;
    private MusicBean mCurrentMusicBean;
    private int mCurrentPosition;
    private boolean mMusicConfig;
    private boolean isChangeFloatingBlock;
    private int mPlayState;

    private QqBarPagerAdapter mQqBarPagerAdapter;
    private BaseFragment mBaseFragment;
    private boolean isNotDetailsPage = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        mBind = ButterKnife.bind(this);
        initView();
        initData();
        initRxBusData();
        initMusicConfig();
        initListener();
    }


    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar_music);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> MusicActivity.this.finish());
    }

    public void checkCurrentIsFavorite() {
        if (!mCurrentMusicBean.isFavorite()) {
            mMusicQqBarFavorite.setImageResource(R.mipmap.favorite_yes);
        } else {
            mMusicQqBarFavorite.setImageResource(R.drawable.music_favorite_selector);

        }
    }

    private void initData() {
        mMusicItems = initMusicData();
        // 初始化 MusicPagerAdapter 主页面
        MusicPagerAdapter musicPagerAdapter = new MusicPagerAdapter(getFragmentManager());
        mMusicViewPager.setAdapter(musicPagerAdapter);
        mMusicViewPager.setCurrentItem(Constants.NUMBER_TWO);
        // 初始化 QqBarPagerAdapter
        mQqBarPagerAdapter = new QqBarPagerAdapter(this, null);
        mMusicSlideViewPager.setAdapter(mQqBarPagerAdapter);
    }

    /**
     * getSpMusicFlag()先获取上次播放列表的标记，根据标记初始化对应的列表数据 。
     * 1 歌曲名   2  评分   3  播放次数        4  添加时间
     *
     * @return h
     */
    private List<MusicBean> initMusicData() {
        int spMusicFlag = getSpMusicFlag();
        if (spMusicFlag == Constants.NUMBER_THRRE) {
            return MusicListUtil.sortMusicAddtime(mMusicDao.queryBuilder().list());
        } else if (spMusicFlag == Constants.NUMBER_ONE) {
            return mMusicItems = mMusicDao.queryBuilder().list();
        } else if (spMusicFlag == Constants.NUMBER_TEN) {

            return mMusicItems = mMusicDao.queryBuilder().list();
        } else if (spMusicFlag == Constants.NUMBER_EIGHT) {
            return mMusicItems = mMusicDao.queryBuilder().where(MusicBeanDao.Properties.IsFavorite.eq(true)).build().list();
        }
        return mMusicItems = mMusicDao.queryBuilder().list();
    }


    private void initMusicConfig() {
        mMusicConfig = SharePrefrencesUtil.getMusicConfig(this, false);
        if (mMusicConfig) {
            mCurrentPosition = SharePrefrencesUtil.getMusicPosition(this);
            mPlayState = SharePrefrencesUtil.getMusicPlayState(this);
            LogUtil.d("======= mPlayStae  " + mPlayState);
            if (mPlayState == Constants.NUMBER_ONE) {
                // 读取用户的播放记录，设置UI显示，做好播放的准备。(暂停和播放两种状态)
                MusicBean musicBean = mMusicItems.get(mCurrentPosition);
                perpareItem(musicBean);
            } else if (mPlayState == Constants.NUMBER_TWO) {
                executStartServiceAndInitAnimation();
            }
        } else {
            LogUtil.d("用户 ++++  nothing ");
        }


    }

    private void executStartServiceAndInitAnimation() {
        startMusicService(mCurrentPosition);
        mMusicFloatingPlay.setImageResource(R.drawable.btn_playing_pause_selector);
        mMusicPagerPlay.setIcon(R.mipmap.notifycation_pause);
        mPlayState = Constants.NUMBER_THRRE;
    }

    private void initListener() {
        openMusicPlayDialogFag();
        mTvMusicToolbarTitle.setOnClickListener(view -> switchMusicControlBar());
        mMusicSlideViewPager.addOnPageChangeListener(new MusicPagerListener() {
            @Override
            public void onPageSelected(int position) {
                startMusicService(position);
            }
        });
    }

    /**
     * 切换音乐控制面板的样式
     */
    private void switchMusicControlBar() {
        if (isChangeFloatingBlock) {
            mQqMusicBar.setVisibility(View.INVISIBLE);
            mSmartisanMusicBar.setVisibility(View.VISIBLE);
        } else {
            mQqMusicBar.setVisibility(View.VISIBLE);
            mSmartisanMusicBar.setVisibility(View.INVISIBLE);
            mQqBarPagerAdapter.setData(initMusicData());
            mMusicSlideViewPager.setCurrentItem(mCurrentPosition, false);
            mQqBarPagerAdapter.notifyDataSetChanged();
        }
        isChangeFloatingBlock = !isChangeFloatingBlock;
    }


    /**
     * 在主列表播放音乐
     * 开启服务，播放音乐并且将数据标记传送过去
     *
     * @param position 当前点击的曲目
     */
    @Override
    public void startMusicService(int position) {
        int spMusicFlag = getSpMusicFlag();
        LogUtil.d(" 290  MusicActivity  mFlag==      " + spMusicFlag);
        if (spMusicFlag != Constants.NUMBER_TEN) {
            mCurrentPosition = position;
            Intent musicIntent = new Intent(this, AudioPlayService.class);
            musicIntent.putExtra("sortFlag", spMusicFlag);
            musicIntent.putExtra("position", mCurrentPosition);
            mConnection = new AudioServiceConnection();
            bindService(musicIntent, mConnection, Context.BIND_AUTO_CREATE);
            startService(musicIntent);
        }
    }

    /**
     * 在详情页面播放音乐
     *
     * @param position  播放位置
     * @param dataFlag  数据列表的标识
     * @param queryFlag 具体查询的条 ( 按 歌手 或 专辑查询 )
     */
    @Override
    public void startMusicServiceFlag(int position, int dataFlag, String queryFlag) {
        mCurrentPosition = position;
        Intent intent = new Intent(this, AudioPlayService.class);
        LogUtil.d(" 306  MusicActivity  dataFlag == queryFlag     " + dataFlag + "  ===  " + queryFlag);
        intent.putExtra("sortFlag", Constants.NUMBER_TEN);
        intent.putExtra("dataFlag", dataFlag);
        intent.putExtra("queryFlag", queryFlag);
        intent.putExtra("position", mCurrentPosition);
        AudioServiceConnection serviceConnection = new AudioServiceConnection();
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        startService(intent);

    }

    private int getSpMusicFlag() {

        return SharePrefrencesUtil.getMusicDataListFlag(this);
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
        RxView.clicks(mSmartisanMusicBar)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> {
                    if (mMusicConfig) {
                        MusicActivity.this.readyMusic();
                    } else {
                        ToastUtil.showNoMusic(MusicActivity.this);
                    }
                });
    }

    private void readyMusic() {
        Intent intent = new Intent(this, PlayActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("info", mCurrentMusicBean);
        intent.putExtra("bundle", bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.dialog_push_in, 0);

    }

    private void initRxBusData() {
        //接收service发出的数据，时时更新播放歌曲 进度 歌名 歌手信息
        mCompositeDisposable.add(mBus.toObserverable(MusicBean.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(musicItem -> {
                    // 将MusicConfig设置为ture
                    SharePrefrencesUtil.setMusicConfig(MusicActivity.this);
                    mMusicConfig = true;
                    // 更新歌曲的信息
                    MusicActivity.this.perpareItem(musicItem);
                    //更新播放状态按钮
                    MusicActivity.this.updatePlayBtnStatus();
                    //初始化动画
                    MusicActivity.this.initAnimation();
                    //更新歌曲的进度
                    MusicActivity.this.updataProgress();

                }));
        /*
         position = bean.getPosition() 用来判断触发消息的源头，
         < 0 >表示是通知栏播放和暂停按钮发出，
         同时MusicPlayDialogFag在播放和暂停的时候也会发出通知并且type也是< 0 >，
         MuiscListActivity会接收到两个地方发出的播放状态的消息,用于控制播放按钮的显示状态
         < 1 >表示从通知栏打开音列表，即整个通知栏布局的监听。
         < 2 >表示在通知栏关闭通知栏
         < 3 > 切换列表数据
         < 4 >
         */
        mCompositeDisposable.add(mBus.toObserverable(MusicStatusBean.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(MusicActivity.this::refreshBtnAndNotify));
        /* 更新当前歌曲的收藏状态*/
        mCompositeDisposable.add(mBus.toObserverable(MusicFavoriteBean.class).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(musicFavoriteBean -> MusicActivity.this.checkCurrentIsFavorite()));
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
                startActivity(new Intent(this, MusicActivity.class));
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
     * @param musicItem g
     */
    private void perpareItem(MusicBean musicItem) {
        mCurrentMusicBean = musicItem;
        checkCurrentIsFavorite();
        mQqBarPagerAdapter.setData(initMusicData());
        mMusicSlideViewPager.setCurrentItem(musicItem.getCureetPosition(), false);
        //更新音乐标题
        String songName = musicItem.getTitle();
        mMusicFloatSongName.setText(songName);
        //更新歌手名称
        String artistName = musicItem.getArtist();
        mMusicFloatSingerName.setText(artistName);
        //设置专辑
        Uri albumUri = StringUtil.getAlbulm(musicItem.getAlbumId());
        ImageUitl.loadPic(this, albumUri.toString(), mMusicFloatBlockAlbulm);
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
        if (mPlayState == Constants.NUMBER_ONE) {
            LogUtil.d(" PlayState == 1 ==================");
            executStartServiceAndInitAnimation();
        } else if (mPlayState == Constants.NUMBER_TWO) {
            mPlayState = Constants.NUMBER_THRRE;
        } else {
            if (audioBinder == null) {
                ToastUtil.showNoMusic(this);
            } else if (audioBinder.isPlaying()) {
                // 当前播放  暂停
                audioBinder.pause();
                mAnimator.pause();
            } else if (!audioBinder.isPlaying()) {
                // 当前暂停  播放
                audioBinder.start();
                mAnimator.resume();
            }
            //更新播放状态按钮
            updatePlayBtnStatus();
        }
    }

    @OnClick({R.id.music_floating_pre,
            R.id.music_floating_play,
            R.id.music_floating_next,
            R.id.music_floating_pager_play,
            R.id.music_floating_pager_favorite,
            R.id.music_bar_playlist,
            R.id.music_bar_artisanlist, R.id.music_bar_songlist, R.id.music_bar_albumlist, R.id.music_bar_stylelist})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.music_bar_playlist:
                switchMusicTabbar(0);
                break;
            case R.id.music_bar_artisanlist:
                switchMusicTabbar(1);
                break;
            case R.id.music_bar_songlist:
                switchMusicTabbar(2);
                break;
            case R.id.music_bar_albumlist:
                switchMusicTabbar(3);
                break;
            case R.id.music_bar_stylelist:
                switchMusicTabbar(4);
                break;
            default:
                if (mMusicConfig) {
                    switch (view.getId()) {
                        case R.id.music_floating_pre:
                            audioBinder.playPre();
                            break;
                        case R.id.music_floating_pager_favorite:
                            favoritMusic();
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

    private void favoritMusic() {
        if (mCurrentMusicBean.isFavorite()) {
            mCurrentMusicBean.setIsFavorite(false);
            mMusicDao.update(mCurrentMusicBean);
            mMusicQqBarFavorite.setImageResource(R.drawable.music_favorite_selector);

        } else {
            String time = StringUtil.getCurrentTime();
            mCurrentMusicBean.setTime(time);
            mCurrentMusicBean.setIsFavorite(true);
            mMusicDao.update(mCurrentMusicBean);

            mMusicQqBarFavorite.setImageResource(R.mipmap.favorite_yes);

        }
    }

    private void switchMusicTabbar(int flag) {
        switch (flag) {
            case 0:
                setAllTabbarNotPressed(flag, R.string.play_list);
                mMusicBarPlaylist.setBackgroundResource(R.drawable.tabbar_bg_down);
                mMusicBarPlaylistIv.setBackgroundResource(R.drawable.tabbar_playlist_selector);
                mMusicBarPlaylistTv.setTextColor(ColorUtil.musicbarTvDown);
                break;
            case 1:
                setAllTabbarNotPressed(flag, R.string.music_artisan);
                mMusicBarArtisanlist.setBackgroundResource(R.drawable.tabbar_bg_down);
                mMusicBarArtisanlistIv.setBackgroundResource(R.drawable.tabbar_artisanlist_selector);
                mMusicBarArtisanlistTv.setTextColor(ColorUtil.musicbarTvDown);
                break;
            case 2:
                setAllTabbarNotPressed(flag, R.string.music_song);
                mMusicBarSonglist.setBackgroundResource(R.drawable.tabbar_bg_down);
                mMusicBarSonglistIv.setBackgroundResource(R.drawable.tabbar_songlist_selector);
                mMusicBarSonglistTv.setTextColor(ColorUtil.musicbarTvDown);

                break;
            case 3:
                setAllTabbarNotPressed(flag, R.string.music_album);
                mMusicBarAlbumlist.setBackgroundResource(R.drawable.tabbar_bg_down);
                mMusicBarAlbumlistIv.setBackgroundResource(R.drawable.tabbar_albumlist_selector);
                mMusicBarAlbumlistTv.setTextColor(ColorUtil.musicbarTvDown);
                break;
            case 4:
                setAllTabbarNotPressed(flag, R.string.music_folder);
                mMusicBarStylelist.setBackgroundResource(R.drawable.tabbar_bg_down);
                mMusicBarStylelistIv.setBackgroundResource(R.drawable.tabbar_stylelist_selector);
                mMusicBarStylelistTv.setTextColor(ColorUtil.musicbarTvDown);

                break;
            default:
                break;
        }


    }

    /**
     * 将Tabbar全部置于未选种状态
     *
     * @param flag
     * @param titleResourceId
     */
    private void setAllTabbarNotPressed(int flag, int titleResourceId) {
        mTvMusicToolbarTitle.setText(titleResourceId);
        mMusicViewPager.setCurrentItem(flag, false);
        mMusicBarPlaylist.setBackgroundColor(ColorUtil.wihtle);
        mMusicBarPlaylistIv.setBackgroundResource(R.drawable.tabbar_playlist_down_selector);
        mMusicBarPlaylistTv.setTextColor(mNormalTabbarColor);

        mMusicBarArtisanlist.setBackgroundColor(ColorUtil.wihtle);
        mMusicBarArtisanlistIv.setBackgroundResource(R.drawable.tabbar_artisanlist_down_selector);
        mMusicBarArtisanlistTv.setTextColor(mNormalTabbarColor);

        mMusicBarSonglist.setBackgroundColor(ColorUtil.wihtle);
        mMusicBarSonglistIv.setBackgroundResource(R.drawable.tabbar_songlist_down_selector);
        mMusicBarSonglistTv.setTextColor(mNormalTabbarColor);

        mMusicBarAlbumlist.setBackgroundColor(ColorUtil.wihtle);
        mMusicBarAlbumlistIv.setBackgroundResource(R.drawable.tabbar_albumlist_down_selector);
        mMusicBarAlbumlistTv.setTextColor(mNormalTabbarColor);

        mMusicBarStylelist.setBackgroundColor(ColorUtil.wihtle);
        mMusicBarStylelistIv.setBackgroundResource(R.drawable.tabbar_stylelist_down_selector);
        mMusicBarStylelistTv.setTextColor(mNormalTabbarColor);
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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
        if (mAnimator != null && audioBinder.isPlaying()) {
            mAnimator.resume();
        }
    }

    @Override
    protected void headsetPullOut() {
        super.headsetPullOut();
        if (audioBinder != null && audioBinder.isPlaying()) {
            switchPlayState();
        }
    }


//    @Override
//    public void onBackPressed() {
//        LogUtil.d("=====onBackPressed=      " + mBaseFragment.backPressed());
//        if (!mBaseFragment.backPressed() || mBaseFragment == null) {
//            super.onBackPressed();
//            LogUtil.d("================hhhhhhhhhhhhhhhhhhhhh");
//        } else if (mBaseFragment.backPressed()) {
//            LogUtil.d("================kkkkkkkkkkkkkk");
//
//        }
//
//
//    }

    @Override
    public void putFragment(BaseFragment fragment) {
        mBaseFragment = fragment;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindAudioService();
        handleAftermath();
        mBind.unbind();

    }

    public void unbindAudioService() {
        if (mConnection != null) {
            unbindService(mConnection);
            mConnection = null;
        }

    }

    private void handleAftermath() {
        if (mAnimator != null) {
            mAnimator.cancel();
        }
        if (mAnimatorListener != null) {
            mAnimatorListener.pause();
        }
        if (audioBinder != null && !audioBinder.isPlaying()) {
            audioBinder.closeNotificaction();
        }
        if (audioBinder != null) {
            mPlayState = audioBinder.isPlaying() ? Constants.NUMBER_TWO : Constants.NUMBER_ONE;
            SharePrefrencesUtil.setMusicPlayState(this, mPlayState);
        }
//        stopService(new Intent(this, AudioPlayService.class));
    }
}
