package com.yibao.music.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.jakewharton.rxbinding2.view.RxView;
import com.yibao.music.R;
import com.yibao.music.adapter.SearchPagerAdapter;
import com.yibao.music.base.BaseTransitionActivity;
import com.yibao.music.base.listener.OnFlowLayoutClickListener;
import com.yibao.music.base.listener.OnMusicItemClickListener;
import com.yibao.music.base.listener.TextChangedListener;
import com.yibao.music.databinding.ActivitySearchBinding;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.MusicLyricBean;
import com.yibao.music.model.SearchCategoryBean;
import com.yibao.music.service.MusicPlayService;
import com.yibao.music.util.ColorUtil;
import com.yibao.music.util.Constants;
import com.yibao.music.util.FileUtil;
import com.yibao.music.util.LyricsUtil;
import com.yibao.music.util.SoftKeybordUtil;
import com.yibao.music.util.TitleArtistUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author lsp
 */
public class SearchActivity extends BaseTransitionActivity implements OnMusicItemClickListener, OnFlowLayoutClickListener, View.OnClickListener {
    private ActivitySearchBinding mBinding;
    private MusicBean mMusicBean;
    private MusicPlayService.AudioBinder audioBinder;
    private int lyricsFlag;
    private InputMethodManager mInputMethodManager;
    private Disposable mDisposableSoftKeyboard;
    private String mSearchCondition;
    private int currentCategoryPosition = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mBinding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        init();
        initListener();
    }

    @Override
    protected void updateLyricsView(boolean lyricsOK, String downMsg) {
        if (lyricsOK) {
            updateLyric();
        }
    }

    private void init() {
        int pageType = getIntent().getIntExtra("pageType", 0);
        audioBinder = MusicActivity.getAudioBinder();
        mBinding.smartisanControlBar.setPbColorAndPreBtnGone();
        SearchPagerAdapter pagerAdapter;
        if (pageType > Constants.NUMBER_ZERO) {
            mMusicBean = getIntent().getParcelableExtra("musicBean");
            mBinding.editSearch.setText(mMusicBean.getArtist());
            mBinding.editSearch.setSelection(mMusicBean.getArtist().length());
            mBinding.searchCategoryRoot.getRoot().setVisibility(View.VISIBLE);
            // ViewPager
            pagerAdapter = new SearchPagerAdapter(this, mMusicBean.getArtist());
            switchListCategory(3);
            setMusicInfo(mMusicBean);
            mBinding.ivEditClear.setVisibility(View.VISIBLE);
        } else {
            pagerAdapter = new SearchPagerAdapter(this, null);
            // 主动弹出键盘
            mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mDisposableSoftKeyboard = Observable.timer(50, TimeUnit.MILLISECONDS)
                    .subscribe(aLong -> SoftKeybordUtil.showAndHintSoftInput(mInputMethodManager, 2, InputMethodManager.SHOW_FORCED));
        }

        mBinding.vpSearch.setAdapter(pagerAdapter);
        mBinding.vpSearch.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switchListCategory(position);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (audioBinder != null) {
            mMusicBean = audioBinder.getMusicBean();
            setMusicInfo(mMusicBean);
            checkCurrentSongIsFavorite(mMusicBean, null, mBinding.smartisanControlBar);
            mBinding.smartisanControlBar.updatePlayBtnStatus(audioBinder.isPlaying());
            mBinding.smartisanControlBar.animatorOnResume(audioBinder.isPlaying());
            updateLyric();
            setDuration();
        }
        mCompositeDisposable.add(RxView.clicks(mBinding.smartisanControlBar)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> startPlayActivity()));

    }


    @Override
    protected void updateCurrentPlayInfo(MusicBean musicItem) {
        mMusicBean = musicItem;
        SearchActivity.this.setMusicInfo(musicItem);
        mBinding.smartisanControlBar.initAnimation();
        if (audioBinder != null) {
            setDuration();
            SearchActivity.this.checkCurrentSongIsFavorite(mMusicBean, null, mBinding.smartisanControlBar);
            mBinding.smartisanControlBar.updatePlayBtnStatus(audioBinder.isPlaying());
            updateLyric();
        }
    }

    @Override
    protected void updateCurrentPlayProgress() {
        if (audioBinder != null) {
            mBinding.smartisanControlBar.setSongProgress(audioBinder.getProgress());
        }
    }

    private void setDuration() {
        int duration = audioBinder.getDuration();
        mBinding.smartisanControlBar.setMaxProgress(duration);
    }

    private void setMusicInfo(MusicBean musicItem) {
        if (musicItem != null) {
            mBinding.smartisanControlBar.setVisibility(View.VISIBLE);
            musicItem = TitleArtistUtil.getMusicBean(musicItem);
            mBinding.smartisanControlBar.setSongName(musicItem.getTitle());
            mBinding.smartisanControlBar.setSingerName(musicItem.getArtist());
            mBinding.smartisanControlBar.setAlbulmUrl(FileUtil.getAlbumUrl(musicItem, 1));
        }
        if (audioBinder != null) {
            mBinding.smartisanControlBar.updatePlayBtnStatus(audioBinder.isPlaying());
            mBinding.smartisanControlBar.initAnimation();
        } else {
            mBinding.smartisanControlBar.setVisibility(View.GONE);
        }
    }


    private void switchListCategory(int position) {
        currentCategoryPosition = position;
        mBinding.vpSearch.setCurrentItem(position, false);
        mBus.post(new SearchCategoryBean(getDataFlag(), mSearchCondition));
        switch (position) {
            case 0:
                setAllCategoryNotNormal();
                mBinding.searchCategoryRoot.tvSearchAll.setTextColor(ColorUtil.wihtle);
                mBinding.searchCategoryRoot.tvSearchAll.setBackgroundResource(R.drawable.btn_category_songname_down_selector);
                break;
            case 1:
                setAllCategoryNotNormal();
                mBinding.searchCategoryRoot.tvSearchSong.setTextColor(ColorUtil.wihtle);
                mBinding.searchCategoryRoot.tvSearchSong.setBackgroundResource(R.drawable.btn_category_score_down_selector);
                break;
            case 2:
                setAllCategoryNotNormal();
                mBinding.searchCategoryRoot.tvSearchAlbum.setTextColor(ColorUtil.wihtle);
                mBinding.searchCategoryRoot.tvSearchAlbum.setBackgroundResource(R.drawable.btn_category_score_down_selector);
                break;
            case 3:
                setAllCategoryNotNormal();
                mBinding.searchCategoryRoot.tvSearchArtist.setBackgroundResource(R.drawable.btn_category_views_down_selector);
                mBinding.searchCategoryRoot.tvSearchArtist.setTextColor(ColorUtil.wihtle);
                break;
            default:
                break;
        }
    }

    private void setAllCategoryNotNormal() {
        mBinding.searchCategoryRoot.tvSearchAll.setTextColor(ColorUtil.textName);
        mBinding.searchCategoryRoot.tvSearchAll.setBackgroundResource(R.drawable.btn_category_songname_selector);
        mBinding.searchCategoryRoot.tvSearchSong.setTextColor(ColorUtil.textName);
        mBinding.searchCategoryRoot.tvSearchSong.setBackgroundResource(R.drawable.btn_category_score_selector);
        mBinding.searchCategoryRoot.tvSearchAlbum.setTextColor(ColorUtil.textName);
        mBinding.searchCategoryRoot.tvSearchAlbum.setBackgroundResource(R.drawable.btn_category_score_selector);
        mBinding.searchCategoryRoot.tvSearchArtist.setTextColor(ColorUtil.textName);
        mBinding.searchCategoryRoot.tvSearchArtist.setBackgroundResource(R.drawable.btn_category_views_selector);
    }

    @Override
    protected void refreshBtnAndNotify(int playStatus) {
        switch (playStatus) {
            case 0:
                mBinding.smartisanControlBar.animatorOnResume(audioBinder.isPlaying());
                mBinding.smartisanControlBar.updatePlayBtnStatus(audioBinder.isPlaying());
                break;
            case 1:
                checkCurrentSongIsFavorite(mMusicBean, null, mBinding.smartisanControlBar);
                break;
            case 2:
                mBinding.smartisanControlBar.updatePlayBtnStatus(audioBinder.isPlaying());
                mBinding.smartisanControlBar.animatorOnPause();
                break;
            default:
                break;
        }
    }

    private int getDataFlag() {
        return mSearchCondition != null ? 1 : currentCategoryPosition == 0 || currentCategoryPosition == 1 ?
                Constants.NUMBER_THREE : currentCategoryPosition == 2 ? 2 : currentCategoryPosition == 3 ? 1 : 4;
    }


    private void switchPlayState() {
        if (audioBinder.isPlaying()) {
            audioBinder.pause();
        } else {
            audioBinder.start();
        }
        mBinding.smartisanControlBar.animatorOnResume(audioBinder.isPlaying());
        mBinding.smartisanControlBar.updatePlayBtnStatus(audioBinder.isPlaying());
    }

    private void initListener() {
        mBinding.editSearch.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                mSearchCondition = s.toString();
                boolean conditionOk = !Constants.NULL_STRING.equals(mSearchCondition) && mSearchCondition.length() > 0;
                mBus.post(new SearchCategoryBean(conditionOk ? getDataFlag() : Constants.NUMBER_TEN, mSearchCondition));
                mBinding.ivEditClear.setVisibility(conditionOk ? View.VISIBLE : View.GONE);
                mBinding.searchCategoryRoot.getRoot().setVisibility(conditionOk ? View.VISIBLE : View.GONE);
            }
        });
        mBinding.smartisanControlBar.setClickListener(clickFlag -> {
            switch (clickFlag) {
                case Constants.NUMBER_ONE:
                    audioBinder.updataFavorite();
                    checkCurrentSongIsFavorite(mMusicBean, null, mBinding.smartisanControlBar);
                    break;
                case Constants.NUMBER_TWO:
                    audioBinder.playPre();
                    break;
                case Constants.NUMBER_THREE:
                    switchPlayState();
                    break;
                case Constants.NUMBER_FOUR:
                    audioBinder.playNext();
                    break;
                default:
                    break;
            }
        });

        mBinding.tvSearchCancel.setOnClickListener(this);
        mBinding.ivEditClear.setOnClickListener(this);
        mBinding.searchCategoryRoot.tvSearchAll.setOnClickListener(this);
        mBinding.searchCategoryRoot.tvSearchSong.setOnClickListener(this);
        mBinding.searchCategoryRoot.tvSearchArtist.setOnClickListener(this);
        mBinding.searchCategoryRoot.tvSearchAlbum.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_search_cancel) {
            SoftKeybordUtil.showAndHintSoftInput(mInputMethodManager, 1, InputMethodManager.RESULT_UNCHANGED_SHOWN);
            finish();
        } else if (id == R.id.iv_edit_clear) {
            mBinding.editSearch.setText(null);
            findViewById(R.id.search_category_root).setVisibility(View.GONE);
            mBus.post(new SearchCategoryBean(Constants.NUMBER_NINE, null));
        } else if (id == R.id.tv_search_all) {
            switchListCategory(0);
        } else if (id == R.id.tv_search_song) {
            switchListCategory(1);
        } else if (id == R.id.tv_search_album) {
            switchListCategory(2);
        } else if (id == R.id.tv_search_artist) {
            switchListCategory(3);
        }
    }

    @Override
    public void startMusicServiceFlag(int position, int sortFlag, int dataFlag, String queryFlag) {
        Intent intent = new Intent(this, MusicPlayService.class);
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
        mBinding.smartisanControlBar.animatorOnPause();
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

    private void updateLyric() {
        List<MusicLyricBean> lyricList = LyricsUtil.getLyricList(mMusicBean);
        disposableQqLyric();
        if (mQqLyricsDisposable == null) {
            mQqLyricsDisposable = Observable.interval(0, 2800, TimeUnit.MICROSECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(musicBeanList -> {
                        if (lyricList.size() > 1 && lyricsFlag < lyricList.size()) {
                            //通过集合，播放过的歌词就从集合中删除
                            MusicLyricBean lyrBean = lyricList.get(lyricsFlag);
                            String content = lyrBean.getContent();
                            int progress = audioBinder.getProgress();
                            int startTime = lyrBean.getStartTime();
                            if (progress > startTime) {
                                mBinding.smartisanControlBar.setSingerName(content);
                                lyricsFlag++;
                            }
                        }
                    });
        }

    }

    @Override
    public void click(String songName) {
        mBinding.editSearch.setText(songName);
        mBinding.editSearch.setSelection(songName.length());

    }

}
