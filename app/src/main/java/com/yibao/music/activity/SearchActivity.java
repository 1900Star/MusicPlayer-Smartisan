package com.yibao.music.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.yibao.music.R;
import com.yibao.music.adapter.SearchDetailsAdapter;
import com.yibao.music.base.BaseActivity;
import com.yibao.music.base.BaseObserver;
import com.yibao.music.base.factory.RecyclerFactory;
import com.yibao.music.base.listener.OnMusicItemClickListener;
import com.yibao.music.base.listener.TextChangedListener;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.MusicLyricBean;
import com.yibao.music.model.MusicStatusBean;
import com.yibao.music.model.SearchHistoryBean;
import com.yibao.music.service.AudioPlayService;
import com.yibao.music.service.AudioServiceConnection;
import com.yibao.music.util.Constants;
import com.yibao.music.util.LyricsUtil;
import com.yibao.music.util.MusicDaoUtil;
import com.yibao.music.util.MusicListUtil;
import com.yibao.music.util.StringUtil;
import com.yibao.music.util.TitleArtistUtil;
import com.yibao.music.view.FlowLayoutView;
import com.yibao.music.view.music.SmartisanControlBar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Stran
 */
public class SearchActivity extends BaseActivity implements OnMusicItemClickListener {
    @BindView(R.id.tv_search_cancel)
    TextView mTvSearchCancel;
    @BindView(R.id.iv_search_clear)
    ImageView mIvSearchClear;
    @BindView(R.id.iv_edit_clear)
    ImageView mIvEditClear;
    @BindView(R.id.edit_search)
    EditText mEditSearch;
    @BindView(R.id.tv_no_search_result)
    TextView mTvNoSearchResult;
    @BindView(R.id.ll_list_view)
    LinearLayout mLinearDetail;
    @BindView(R.id.ll_history)
    LinearLayout mLayoutHistory;
    @BindView(R.id.flowlayout)
    FlowLayoutView mFlowLayoutView;

    @BindView(R.id.smartisan_control_bar)
    SmartisanControlBar mSmartisanControlBar;
    private SearchDetailsAdapter mSearchDetailAdapter;
    private MusicBean mMusicBean;
    private AudioPlayService.AudioBinder audioBinder;
    private ArrayList<MusicLyricBean> mLyricList;
    private int lyricsFlag;

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
        audioBinder = MusicActivity.getAudioBinder();
        mMusicBean = getIntent().getParcelableExtra("musicBean");
        mSmartisanControlBar.setPbolorAndPreBtnGone();
        setMusicInfo(mMusicBean);
        if (audioBinder != null) {
            mSmartisanControlBar.updatePlayBtnStatus(audioBinder.isPlaying());
            mSmartisanControlBar.initAnimation();
        } else {
            mSmartisanControlBar.setVisibility(View.GONE);
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
                .subscribe(o -> startPlayActivity(mMusicBean)));
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
            mLyricList = LyricsUtil.getLyricList(musicItem.getTitle(), musicItem.getArtist());
        }
    }


    private void initData() {
//        mSearchList = MusicListUtil.sortSearchHistory(mSearchDao.queryBuilder().list());
        List<SearchHistoryBean> searchList = mSearchDao.queryBuilder().list();
//        Collections.sort(mSearchList);
        if (searchList != null && searchList.size() > 0) {
            mLayoutHistory.setVisibility(View.VISIBLE);
        }
        mSearchDetailAdapter = new SearchDetailsAdapter(SearchActivity.this, null, Constants.NUMBER_THRRE);
        RecyclerView recyclerView = RecyclerFactory.creatRecyclerView(1, mSearchDetailAdapter);
        mLinearDetail.addView(recyclerView);
        mFlowLayoutView.setData(searchList);

    }

    @Override
    protected void refreshBtnAndNotify(MusicStatusBean musicStatusBean) {
        switch (musicStatusBean.getType()) {
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
        mEditSearch.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                String searchContent = s.toString();
                if (!"".equals(searchContent) && searchContent.length() > 0) {
                    searchMusic(searchContent);
                    mIvEditClear.setVisibility(View.VISIBLE);
                } else {
                    historyViewVisibility();
                    mLinearDetail.setVisibility(View.INVISIBLE);
                    mTvNoSearchResult.setVisibility(View.GONE);
                    mIvEditClear.setVisibility(View.GONE);
                }
            }
        });
        mFlowLayoutView.setItemClickListener((position, bean) -> initSearch(bean.getSearchContent()));
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
//        mNotifyManager.updataPlayBtn(audioBinder.isPlaying());
        mSmartisanControlBar.animatorOnResume(audioBinder.isPlaying());
        mSmartisanControlBar.updatePlayBtnStatus(audioBinder.isPlaying());
    }

    /**
     * 点击搜索记录
     *
     * @param searchContent 搜索内容
     */
    private void initSearch(String searchContent) {
        mEditSearch.setText(searchContent);
        mEditSearch.setSelection(searchContent.length());
        SearchActivity.this.searchMusic(searchContent);
    }

    private void historyViewVisibility() {
        List<SearchHistoryBean> historyList = MusicListUtil.sortSearchHistory(mSearchDao.queryBuilder().list());
        if (historyList != null && historyList.size() > 0) {
            mLayoutHistory.setVisibility(View.VISIBLE);
            mFlowLayoutView.clearView(historyList);
            mTvNoSearchResult.setVisibility(View.GONE);
        } else {
            mLayoutHistory.setVisibility(View.GONE);
        }
    }

    /**
     * 搜索音乐
     *
     * @param searchconditions 搜索关键字
     */
    private void searchMusic(String searchconditions) {
        MusicDaoUtil.getSearchResult(flag -> mSearchDetailAdapter.setDataFlag(flag), mMusicDao, mSearchDao, searchconditions)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<List<MusicBean>>() {
                    @Override
                    public void onNext(List<MusicBean> musicBeanList) {
                        mSearchDetailAdapter.setNewData(musicBeanList);
                        mLayoutHistory.setVisibility(View.GONE);
                        mTvNoSearchResult.setVisibility(View.GONE);
                        mLinearDetail.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mLayoutHistory.setVisibility(View.GONE);
                        mLinearDetail.setVisibility(View.GONE);
                        mTvNoSearchResult.setVisibility(View.VISIBLE);
                        mSmartisanControlBar.setVisibility(View.VISIBLE);
                    }

                });
    }

    @OnClick({R.id.tv_search_cancel, R.id.iv_edit_clear, R.id.iv_search_clear})
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.tv_search_cancel:
                finish();
                break;
            case R.id.iv_edit_clear:
                mEditSearch.setText(null);
                if (mSearchDetailAdapter != null) {
                    mSearchDetailAdapter.clear();
                }
                mLinearDetail.setVisibility(View.INVISIBLE);
                historyViewVisibility();
                break;
            case R.id.iv_search_clear:
                mSearchDao.deleteAll();
                mFlowLayoutView.removeAllViews();
                mLayoutHistory.setVisibility(View.INVISIBLE);
                mLinearDetail.setVisibility(View.INVISIBLE);
                break;
        }
    }

    @Override
    public void startMusicServiceFlag(int position, int dataFlag, String queryFlag) {
        Intent intent = new Intent(this, AudioPlayService.class);
        intent.putExtra("sortFlag", Constants.NUMBER_TEN);
        intent.putExtra("dataFlag", dataFlag);
        intent.putExtra("queryFlag", queryFlag);
        intent.putExtra("position", position);
        AudioServiceConnection serviceConnection = new AudioServiceConnection();
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
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

    private void updataLyric() {
        disposableQqLyric();
        if (mQqLyricsDisposable == null) {
            mQqLyricsDisposable = Observable.interval(0, 2800, TimeUnit.MICROSECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(musicBeanList -> {
                        if (mLyricList != null && mLyricList.size() > 1 && lyricsFlag < mLyricList.size()) {
                            //通过集合，播放过的歌词就从集合中删除
                            MusicLyricBean lyrBean = mLyricList.get(lyricsFlag);
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
}
