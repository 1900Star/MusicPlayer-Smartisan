package com.yibao.music.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.yibao.music.R;
import com.yibao.music.adapter.DetailsViewAdapter;
import com.yibao.music.adapter.SongCategoryPagerAdapter;
import com.yibao.music.base.BaseTansitionActivity;
import com.yibao.music.base.listener.MusicPagerListener;
import com.yibao.music.base.listener.OnFlowLayoutClickListener;
import com.yibao.music.base.listener.OnMusicItemClickListener;
import com.yibao.music.base.listener.TextChangedListener;
import com.yibao.music.fragment.dialogfrag.MoreMenuBottomDialog;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.MusicLyricBean;
import com.yibao.music.service.AudioPlayService;
import com.yibao.music.util.ColorUtil;
import com.yibao.music.util.Constants;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.SoftKeybordUtil;
import com.yibao.music.util.StringUtil;
import com.yibao.music.util.TitleArtistUtil;
import com.yibao.music.view.MainViewPager;
import com.yibao.music.view.music.SmartisanControlBar;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Stran
 */
public class SearchActivity extends BaseTansitionActivity implements OnMusicItemClickListener, OnFlowLayoutClickListener {
    @BindView(R.id.tv_search_cancel)
    TextView mTvSearchCancel;
    @BindView(R.id.iv_edit_clear)
    ImageView mIvEditClear;
    @BindView(R.id.edit_search)
    EditText mEditSearch;

    @BindView(R.id.vp_search)
    MainViewPager mViewPager;
    @BindView(R.id.tv_search_all)
    TextView mTvSearchAll;
    @BindView(R.id.tv_search_song)
    TextView mTvSearchSong;
    @BindView(R.id.tv_search_album)
    TextView mTvSearchAlbum;
    @BindView(R.id.tv_search_artist)
    TextView mTvSearchArtist;

    @BindView(R.id.smartisan_control_bar)
    SmartisanControlBar mSmartisanControlBar;
    private DetailsViewAdapter mSearchDetailAdapter;
    private MusicBean mMusicBean;
    private AudioPlayService.AudioBinder audioBinder;
    private int lyricsFlag;
    private InputMethodManager mInputMethodManager;
    private Disposable mDisposableSoftKeyboard;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mBind = ButterKnife.bind(this);
        init();
        initData();
        initListener();
    }

    private void init() {
        mSearchDetailAdapter = new DetailsViewAdapter(SearchActivity.this, null, Constants.NUMBER_THRRE);
        int pageType = getIntent().getIntExtra("pageType", 0);
        audioBinder = MusicActivity.getAudioBinder();
        mSmartisanControlBar.setPbColorAndPreBtnGone();
        if (pageType > Constants.NUMBER_ZERO) {
            mMusicBean = getIntent().getParcelableExtra("musicBean");
            setMusicInfo(mMusicBean);
            mIvEditClear.setVisibility(View.VISIBLE);
        } else {
            // 主动弹出键盘
            mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mDisposableSoftKeyboard = Observable.timer(50, TimeUnit.MILLISECONDS)
                    .subscribe(aLong -> SoftKeybordUtil.showAndHintSoftInput(mInputMethodManager, 2, InputMethodManager.SHOW_FORCED));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (audioBinder != null) {
            mMusicBean = audioBinder.getMusicBean();
            setMusicInfo(mMusicBean);
            checkCurrentSongIsFavorite(mMusicBean, null, mSmartisanControlBar);
            mSmartisanControlBar.updatePlayBtnStatus(audioBinder.isPlaying());
            mSmartisanControlBar.animatorOnResume(audioBinder.isPlaying());
            updataLyric();
            setDuration();
        }
        mCompositeDisposable.add(RxView.clicks(mSmartisanControlBar)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> startPlayActivity()));

    }


    @Override
    protected void updataCurrentPlayInfo(MusicBean musicItem) {
        mMusicBean = musicItem;
        SearchActivity.this.setMusicInfo(musicItem);
        mSmartisanControlBar.initAnimation();
        if (audioBinder != null) {
            setDuration();
            SearchActivity.this.checkCurrentSongIsFavorite(mMusicBean, null, mSmartisanControlBar);
            mSmartisanControlBar.updatePlayBtnStatus(audioBinder.isPlaying());
            updataLyric();
        }
    }

    @Override
    protected void updataCurrentPlayProgress() {
        if (audioBinder != null) {
            mSmartisanControlBar.setSongProgress(audioBinder.getProgress());
        }
    }

    private void setDuration() {
        int duration = audioBinder.getDuration();
        mSmartisanControlBar.setMaxProgress(duration);
    }

    private void setMusicInfo(MusicBean musicItem) {
        if (musicItem != null) {
            mSmartisanControlBar.setVisibility(View.VISIBLE);
            musicItem = TitleArtistUtil.getMusicBean(musicItem);
            mSmartisanControlBar.setSongName(musicItem.getTitle());
            mSmartisanControlBar.setSingerName(musicItem.getArtist());
            mSmartisanControlBar.setAlbulmUrl(StringUtil.getAlbulm(musicItem.getAlbumId()));
        }
        if (audioBinder != null) {
            mSmartisanControlBar.updatePlayBtnStatus(audioBinder.isPlaying());
            mSmartisanControlBar.initAnimation();
        } else {
            mSmartisanControlBar.setVisibility(View.GONE);
        }
    }


    private void initData() {
        // ViewPager
        SongCategoryPagerAdapter pagerAdapter = new SongCategoryPagerAdapter(getSupportFragmentManager(), Constants.NUMBER_TWO);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.addOnPageChangeListener(new MusicPagerListener() {
            @Override
            public void onPageSelected(int position) {
                switchListCategory(position);
            }
        });
    }

    private void switchListCategory(int position) {
        mViewPager.setCurrentItem(position, false);
        switch (position) {
            case 0:
                setAllCategoryNotNormal();
                mTvSearchAll.setTextColor(ColorUtil.wihtle);
                mTvSearchAll.setBackgroundResource(R.drawable.btn_category_songname_down_selector);
                break;
            case 1:
                setAllCategoryNotNormal();
                mTvSearchSong.setTextColor(ColorUtil.wihtle);
                mTvSearchSong.setBackgroundResource(R.drawable.btn_category_score_down_selector);
                break;
            case 2:
                setAllCategoryNotNormal();
                mTvSearchAlbum.setTextColor(ColorUtil.wihtle);
                mTvSearchAlbum.setBackgroundResource(R.drawable.btn_category_score_down_selector);
                break;
            case 3:
                setAllCategoryNotNormal();
                mTvSearchArtist.setBackgroundResource(R.drawable.btn_category_views_down_selector);
                mTvSearchArtist.setTextColor(ColorUtil.wihtle);
                break;
            default:
                break;
        }
    }

    private void setAllCategoryNotNormal() {
        mTvSearchAll.setTextColor(ColorUtil.textName);
        mTvSearchAll.setBackgroundResource(R.drawable.btn_category_songname_selector);
        mTvSearchSong.setTextColor(ColorUtil.textName);
        mTvSearchSong.setBackgroundResource(R.drawable.btn_category_score_selector);
        mTvSearchAlbum.setTextColor(ColorUtil.textName);
        mTvSearchAlbum.setBackgroundResource(R.drawable.btn_category_score_selector);
        mTvSearchArtist.setTextColor(ColorUtil.textName);
        mTvSearchArtist.setBackgroundResource(R.drawable.btn_category_views_selector);
    }

    @Override
    protected void refreshBtnAndNotify(int playStatus) {
        switch (playStatus) {
            case 0:
                mSmartisanControlBar.animatorOnResume(audioBinder.isPlaying());
                mSmartisanControlBar.updatePlayBtnStatus(audioBinder.isPlaying());
                break;
            case 1:
                checkCurrentSongIsFavorite(mMusicBean, null, mSmartisanControlBar);
                break;
            case 2:
                mSmartisanControlBar.updatePlayBtnStatus(audioBinder.isPlaying());
                mSmartisanControlBar.animatorOnPause();
                break;
            default:
                break;
        }
    }

    private void initListener() {
        mSearchDetailAdapter.setOnItemMenuListener((int position, MusicBean musicBean) ->
                MoreMenuBottomDialog.newInstance(musicBean, position, false, false).getBottomDialog(this));
        mEditSearch.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                String searchContent = s.toString();
                boolean conditionOK = !Constants.NULL_STRING.equals(searchContent) && searchContent.length() > 0;
                if (conditionOK) {
                    mBus.post(Constants.SEARCH_CONDITION, s.toString());
                    mIvEditClear.setVisibility(View.VISIBLE);
                } else {
                    mIvEditClear.setVisibility(View.GONE);
                    mBus.post(Constants.SEARCH_CONDITION,Constants.NULL_STRING);
                }
                findViewById(R.id.search_category_root).setVisibility(conditionOK ? View.VISIBLE : View.GONE);
            }
        });
        mSmartisanControlBar.setClickListener(clickFlag -> {
            switch (clickFlag) {
                case Constants.NUMBER_ONE:
                    audioBinder.updataFavorite();
                    checkCurrentSongIsFavorite(mMusicBean, null, mSmartisanControlBar);
                    break;
                case Constants.NUMBER_TWO:
                    audioBinder.playPre();
                    break;
                case Constants.NUMBER_THRRE:
                    switchPlayState();
                    break;
                case Constants.NUMBER_FOUR:
                    audioBinder.playNext();
                    break;
                default:
                    break;
            }
        });
    }

    private void switchPlayState() {
        if (audioBinder.isPlaying()) {
            audioBinder.pause();
        } else {
            audioBinder.start();
        }
        mSmartisanControlBar.animatorOnResume(audioBinder.isPlaying());
        mSmartisanControlBar.updatePlayBtnStatus(audioBinder.isPlaying());
    }


    @OnClick({R.id.tv_search_cancel, R.id.iv_edit_clear,
            R.id.tv_search_all, R.id.tv_search_song, R.id.tv_search_album, R.id.tv_search_artist})
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.tv_search_cancel:
                SoftKeybordUtil.showAndHintSoftInput(mInputMethodManager, 1, InputMethodManager.RESULT_UNCHANGED_SHOWN);
                finish();

                break;
            case R.id.iv_edit_clear:
                mEditSearch.setText(null);
                if (mSearchDetailAdapter != null) {
                    mSearchDetailAdapter.clear();
                }
                findViewById(R.id.search_category_root).setVisibility(View.VISIBLE);
                mBus.post(Constants.SEARCH_CONDITION, Constants.CLEAR_CONDITON);
                break;
            case R.id.tv_search_all:
                LogUtil.d("====== id " + v.getId());
                switchListCategory(0);
                break;
            case R.id.tv_search_song:
                LogUtil.d("====== id " + v.getId());
                switchListCategory(1);
                break;
            case R.id.tv_search_album:
                LogUtil.d("====== id " + v.getId());
                switchListCategory(2);
                break;
            case R.id.tv_search_artist:
                LogUtil.d("====== id " + v.getId());
                switchListCategory(3);
                break;
        }
    }

    @Override
    public void startMusicServiceFlag(int position, int sortFlag, int dataFlag, String queryFlag) {
        Intent intent = new Intent(this, AudioPlayService.class);
        intent.putExtra("sortFlag", sortFlag);
        intent.putExtra("dataFlag", dataFlag);
        intent.putExtra("queryFlag", queryFlag);
        intent.putExtra("position", position);
        startService(intent);
    }

    @Override
    public void startMusicService(int position) {

    }

    @Override
    public void onOpenMusicPlayDialogFag() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        mSmartisanControlBar.animatorOnPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposableSoftKeyboard != null) {
            mDisposableSoftKeyboard.dispose();
            mDisposableSoftKeyboard = null;
        }
        if (audioBinder != null) {
            audioBinder = null;
        }
    }

    private void updataLyric() {
        disposableQqLyric();
        if (mQqLyricsDisposable == null) {
            mQqLyricsDisposable = Observable.interval(0, 2800, TimeUnit.MICROSECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(musicBeanList -> {
                        List<MusicLyricBean> lyricList = audioBinder.getLyricList();
                        if (lyricList != null && lyricList.size() > 1 && lyricsFlag < lyricList.size()) {
                            //通过集合，播放过的歌词就从集合中删除
                            MusicLyricBean lyrBean = lyricList.get(lyricsFlag);
                            String content = lyrBean.getContent();
                            int progress = audioBinder.getProgress();
                            int startTime = lyrBean.getStartTime();
                            if (progress > startTime) {
                                mSmartisanControlBar.setSingerName(content);
                                lyricsFlag++;
                            }
                        }
                    });
        }

    }

    @Override
    public void click(String songName) {
        mEditSearch.setText(songName);
        mEditSearch.setSelection(songName.length());

    }
}
