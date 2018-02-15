package com.yibao.music.fragment;

import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding2.view.RxView;
import com.yibao.music.MyApplication;
import com.yibao.music.R;
import com.yibao.music.artisan.MusicPlayDialogFag;
import com.yibao.music.artisanlist.MusicActivitybac;
import com.yibao.music.artisanlist.MusicPagerAdapter;
import com.yibao.music.artisanlist.MusicPagerListener;
import com.yibao.music.artisanlist.QqBarPagerAdapter;
import com.yibao.music.base.listener.MyAnimatorUpdateListener;
import com.yibao.music.base.listener.OnMusicItemClickListener;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.MusicDialogInfo;
import com.yibao.music.model.MusicStatusBean;
import com.yibao.music.service.AudioPlayService;
import com.yibao.music.util.AnimationUtil;
import com.yibao.music.util.ColorUtil;
import com.yibao.music.util.Constants;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.MusicListUtil;
import com.yibao.music.util.RxBus;
import com.yibao.music.util.SharePrefrencesUtil;
import com.yibao.music.util.StringUtil;
import com.yibao.music.util.ToastUtil;
import com.yibao.music.view.CircleImageView;
import com.yibao.music.view.MusicProgressView;
import com.yibao.music.view.ProgressBtn;

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
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.fragment
 * @文件名: ControlBarFragment
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/13 22:59
 * @描述： {TODO}
 */

public class ControlBarFragment extends Fragment implements OnMusicItemClickListener {

    /**
     * smartisan
     */
    @BindView(R.id.music_float_block_albulm)
    CircleImageView mMusicFloatBlockAlbulm;

    @BindView(R.id.music_float_song_name)
    TextView mMusicFloatSongName;
    @BindView(R.id.music_float_singer_name)
    TextView mMusicFloatSingerName;


    @BindView(R.id.music_floating_pre)
    ImageView mMusicFloatingPre;

    @BindView(R.id.music_floating_play)
    ImageView mMusicFloatingPlay;

    @BindView(R.id.music_floating_next)
    ImageView mMusicFloatingNext;

    @BindView(R.id.music_float_pb)
    ProgressBtn mMusicFloatPb;

    @BindView(R.id.smartisan_music_bar)
    RelativeLayout mSmartisanBar;

    /**
     * qq
     */
    @BindView(R.id.music_floating_vp)
    ViewPager mMusicFloatingVp;
    @BindView(R.id.music_floating_pager_play)
    MusicProgressView mMusicFloatingPagerPlay;

    @BindView(R.id.music_floating_pager_next)
    ImageView mMusicFloatingPagerNext;

    @BindView(R.id.qq_music_bar)
    LinearLayout mQqMusicBar;
    /**
     * music tab bar
     */
    @BindView(R.id.music_bar_playlist_iv)
    ImageView mMusicBarPlaylistIv;
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


    private Unbinder unbinder;
    private CompositeDisposable disposables;
    private ArrayList<MusicBean> mMusicItems;
    private ObjectAnimator mAnimator;
    private static AudioPlayService.AudioBinder audioBinder;
    private MyAnimatorUpdateListener mAnimatorListener;
    private Disposable mDisposable;
    private RxBus mBus;
    private MusicBean mItem;
    private int mCurrentPosition;
    private boolean mMusicConfig;
    private boolean isChangeFloatingBlock;
    private int mPlayState;
    private int mNormalTabbarColor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.control_bar_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        mBus = MyApplication.getIntstance().bus();
        disposables = new CompositeDisposable();
        AudioServiceConnection connection = new AudioServiceConnection();
        initView();
        initData();
//        initRxBusData();
//        initMusicConfig();
//        initListener();
        return view;
    }


    private void initView() {

        mNormalTabbarColor = Color.parseColor("#939396");

    }

    private void initData() {
        mMusicItems = MusicListUtil.getMusicDataList(getActivity());

        MusicPagerAdapter musicPagerAdapter = new MusicPagerAdapter(getFragmentManager());
        mMusicFloatingVp.setAdapter(musicPagerAdapter);

    }


    private void initMusicConfig() {
        mMusicConfig = SharePrefrencesUtil.getMusicConfig(getActivity(), false);
        if (mMusicConfig) {
            mCurrentPosition = SharePrefrencesUtil.getMusicPosition(getActivity());
            mPlayState = SharePrefrencesUtil.getMusicPlayState(getActivity());
            LogUtil.d("======= mPlayStae  " + mPlayState);
            if (mPlayState == Constants.NUMBER_ONE) {
                // 读取用户的播放记录，设置UI显示，做好播放的准备。(暂停和播放两种状态)
                MusicBean musicInfo = mMusicItems.get(mCurrentPosition);
                perpareItem(musicInfo);
//                mMusicConfig = false;
            } else if (mPlayState == Constants.NUMBER_TWO) {
                executStartServiceAndInitAnimation();
            }
        } else {
            LogUtil.d("用户 ++++  nothing ");
        }
        QqBarPagerAdapter qqBarPagerAdapter = new QqBarPagerAdapter(getActivity(), mMusicItems, mCurrentPosition);
        mMusicFloatingVp.setAdapter(qqBarPagerAdapter);

    }

    private void executStartServiceAndInitAnimation() {
        startMusicService(mCurrentPosition, Constants.NUMBER_ZOER);
        initAnimation();
        mMusicFloatingPlay.setImageResource(R.drawable.btn_playing_pause_selector);
        mMusicFloatingPagerPlay.setIcon(R.mipmap.notifycation_pause);
        mPlayState = Constants.NUMBER_THRRE;
    }

    private void initListener() {
        openMusicPlayDialogFag();
        mMusicFloatingVp.addOnPageChangeListener(new MusicPagerListener() {
            @Override
            public void onPageSelected(int position) {
                startMusicService(position, Constants.NUMBER_ZOER);
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
            ToastUtil.showNoMusic(getActivity());
        }

    }

    @Override
    public void switchViewPagerItem(int page,int titleId) {

    }


    private void openMusicPlayDialogFag() {
        RxView.clicks(mSmartisanBar)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> {
                    if (mMusicConfig) {
                        readyMusic();
                    } else {
                        ToastUtil.showNoMusic(getActivity());
                    }
                });
    }

    private void readyMusic() {
        MusicDialogInfo info = new MusicDialogInfo(mMusicItems, mItem);
        MusicPlayDialogFag playDialogFag = MusicPlayDialogFag.newInstance(info);
        if (playDialogFag != null) {
            LogUtil.d(" MusicPlayDialogFag ========已经添加 ");
            getFragmentManager().beginTransaction().remove(playDialogFag).commit();
//            mPlayDialogFag.show(getFragmentManager(), "music");
        }
//        else {
//            mPlayDialogFag = MusicPlayDialogFag.newInstance(info);
//        }
        playDialogFag.show(getFragmentManager(), "music");


    }

    private void initRxBusData() {
        //接收service发出的数据，时时更新播放歌曲 进度 歌名 歌手信息
        disposables.add(mBus.toObserverable(MusicBean.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(musicItem -> {
                    // 将MusicConfig设置为ture
                    SharePrefrencesUtil.setMusicConfig(getActivity());
                    mMusicConfig = true;
                    perpareItem(musicItem);
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
                .subscribe(this::refreshBtnAndNotify));


    }

    private void refreshBtnAndNotify(MusicStatusBean bean) {
        LogUtil.d("msictactiv==================  " + bean.getType());
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
                startActivity(new Intent(getActivity(), MusicActivitybac.class));
                break;
            case 2:
                getActivity().finish();
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
        mMusicFloatingVp.setCurrentItem(musicItem.getCureetPosition(), false);
        mItem = musicItem;
        //更新音乐标题
        String songName = musicItem.getTitle();
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
        mMusicFloatingPagerPlay.setMax(duration);
        mMusicFloatPb.setMax(duration);
        if (mDisposable == null) {

            mDisposable = Observable.interval(0, 2800, TimeUnit.MICROSECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> {
                        mMusicFloatingPagerPlay.setProgress(audioBinder.getProgress());
                        mMusicFloatPb.setProgress(audioBinder.getProgress());
                    });
        }

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
        intent.setClass(getActivity(), AudioPlayService.class);
        intent.putExtra("position", mCurrentPosition);
        intent.putExtra("sortFlag", sortListFlag);

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
            mMusicFloatingPagerPlay.setIcon(R.mipmap.notifycation_pause);
        } else {

            mMusicFloatingPlay.setImageResource(R.drawable.btn_playing_play_selector);
            mMusicFloatingPagerPlay.setIcon(R.mipmap.notifycation_play);
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
                ToastUtil.showNoMusic(getActivity());
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


    @OnClick({R.id.music_floating_pre,
            R.id.music_floating_play,
            R.id.music_floating_next,
            R.id.music_floating_pager_play,
            R.id.music_floating_pager_next,
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
                    ToastUtil.showNoMusic(getActivity());
                }
                break;
        }


    }

    private void switchPagerItem(int flag, int resourcesId) {
        if (getActivity() instanceof OnMusicItemClickListener) {
        ((OnMusicItemClickListener) getActivity()).switchViewPagerItem(flag,resourcesId);

        }
    }

    private void switchMusicTabbar(int flag) {
        switch (flag) {
            case 0:
               switchPagerItem(flag,R.string.play_list);
                mMusicBarPlaylist.setBackgroundResource(R.drawable.tabbar_bg_down);
                mMusicBarPlaylistIv.setBackgroundResource(R.drawable.tabbar_playlist_selector);
                mMusicBarPlaylistTv.setTextColor(ColorUtil.musicbarTvDown);

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

                break;
            case 1:
                switchPagerItem(flag,R.string.music_artisan);
                mMusicBarPlaylist.setBackgroundColor(ColorUtil.wihtle);
                mMusicBarPlaylistIv.setBackgroundResource(R.drawable.tabbar_playlist_down_selector);
                mMusicBarPlaylistTv.setTextColor(mNormalTabbarColor);

                mMusicBarArtisanlist.setBackgroundResource(R.drawable.tabbar_bg_down);
                mMusicBarArtisanlistIv.setBackgroundResource(R.drawable.tabbar_artisanlist_selector);
                mMusicBarArtisanlistTv.setTextColor(ColorUtil.musicbarTvDown);

                mMusicBarSonglist.setBackgroundColor(ColorUtil.wihtle);
                mMusicBarSonglistIv.setBackgroundResource(R.drawable.tabbar_songlist_down_selector);
                mMusicBarSonglistTv.setTextColor(mNormalTabbarColor);

                mMusicBarAlbumlist.setBackgroundColor(ColorUtil.wihtle);
                mMusicBarAlbumlistIv.setBackgroundResource(R.drawable.tabbar_albumlist_down_selector);
                mMusicBarAlbumlistTv.setTextColor(mNormalTabbarColor);

                mMusicBarStylelist.setBackgroundColor(ColorUtil.wihtle);
                mMusicBarStylelistIv.setBackgroundResource(R.drawable.tabbar_stylelist_down_selector);
                mMusicBarStylelistTv.setTextColor(mNormalTabbarColor);


                break;
            case 2:
                switchPagerItem(flag,R.string.music_song);
                mMusicBarPlaylist.setBackgroundColor(ColorUtil.wihtle);
                mMusicBarPlaylistIv.setBackgroundResource(R.drawable.tabbar_playlist_down_selector);
                mMusicBarPlaylistTv.setTextColor(mNormalTabbarColor);

                mMusicBarArtisanlist.setBackgroundColor(ColorUtil.wihtle);
                mMusicBarArtisanlistIv.setBackgroundResource(R.drawable.tabbar_artisanlist_down_selector);
                mMusicBarArtisanlistTv.setTextColor(mNormalTabbarColor);


                mMusicBarSonglist.setBackgroundResource(R.drawable.tabbar_bg_down);

                mMusicBarSonglistIv.setBackgroundResource(R.drawable.tabbar_songlist_selector);
                mMusicBarSonglistTv.setTextColor(ColorUtil.musicbarTvDown);

                mMusicBarAlbumlist.setBackgroundColor(ColorUtil.wihtle);
                mMusicBarAlbumlistIv.setBackgroundResource(R.drawable.tabbar_albumlist_down_selector);
                mMusicBarAlbumlistTv.setTextColor(mNormalTabbarColor);

                mMusicBarStylelist.setBackgroundColor(ColorUtil.wihtle);
                mMusicBarStylelistIv.setBackgroundResource(R.drawable.tabbar_stylelist_down_selector);
                mMusicBarStylelistTv.setTextColor(mNormalTabbarColor);

                break;
            case 3:
                switchPagerItem(flag,R.string.music_album);
                mMusicBarPlaylist.setBackgroundColor(ColorUtil.wihtle);
                mMusicBarPlaylistIv.setBackgroundResource(R.drawable.tabbar_playlist_down_selector);
                mMusicBarPlaylistTv.setTextColor(mNormalTabbarColor);

                mMusicBarArtisanlist.setBackgroundColor(ColorUtil.wihtle);
                mMusicBarArtisanlistIv.setBackgroundResource(R.drawable.tabbar_artisanlist_down_selector);
                mMusicBarArtisanlistTv.setTextColor(mNormalTabbarColor);


                mMusicBarSonglist.setBackgroundColor(ColorUtil.wihtle);
                mMusicBarSonglistIv.setBackgroundResource(R.drawable.tabbar_songlist_down_selector);
                mMusicBarSonglistTv.setTextColor(mNormalTabbarColor);

                mMusicBarAlbumlist.setBackgroundResource(R.drawable.tabbar_bg_down);
                mMusicBarAlbumlistIv.setBackgroundResource(R.drawable.tabbar_albumlist_selector);
                mMusicBarAlbumlistTv.setTextColor(ColorUtil.musicbarTvDown);


                mMusicBarStylelist.setBackgroundColor(ColorUtil.wihtle);
                mMusicBarStylelistIv.setBackgroundResource(R.drawable.tabbar_stylelist_down_selector);
                mMusicBarStylelistTv.setTextColor(mNormalTabbarColor);
                break;
            case 4:
                switchPagerItem(flag,R.string.music_folder);
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

                mMusicBarStylelist.setBackgroundResource(R.drawable.tabbar_bg_down);
                mMusicBarStylelistIv.setBackgroundResource(R.drawable.tabbar_stylelist_selector);
                mMusicBarStylelistTv.setTextColor(ColorUtil.musicbarTvDown);

                break;
            default:
                break;
        }


    }


    private void switchMusicControlBar() {
        if (isChangeFloatingBlock) {
            mQqMusicBar.setVisibility(View.INVISIBLE);
            mSmartisanBar.setVisibility(View.VISIBLE);
            isChangeFloatingBlock = false;
        } else {
            mQqMusicBar.setVisibility(View.VISIBLE);
            mSmartisanBar.setVisibility(View.INVISIBLE);
            isChangeFloatingBlock = true;
        }
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
    public void onPause() {
        super.onPause();
        if (mAnimator != null) {
            mAnimator.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAnimator != null) {
            mAnimator.resume();
        }
    }

    public static ControlBarFragment newInstance() {


        return new ControlBarFragment();
    }

    /**
     * 耳机插入和拔出监听
     */


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        boolean b = mAnimator != null ||
                mAnimatorListener != null || mDisposable != null;
        if (b) {
            mAnimator.cancel();
            mAnimatorListener.pause();
            mDisposable.dispose();
        }
        if (audioBinder != null && !audioBinder.isPlaying()) {
            audioBinder.closeNotificaction();
        }
        if (audioBinder != null) {
            mPlayState = audioBinder.isPlaying() ? Constants.NUMBER_TWO : Constants.NUMBER_ONE;
            SharePrefrencesUtil.setMusicPlayState(getActivity(), mPlayState);
        }
//        unbindService(mConnection);
//        mConnection = null;
        disposables.clear();
//        unregisterReceiver(headsetReciver);
        unbinder.unbind();

    }

}
