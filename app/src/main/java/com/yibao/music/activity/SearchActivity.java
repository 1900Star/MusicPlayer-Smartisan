package com.yibao.music.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.yibao.music.R;
import com.yibao.music.adapter.DetailsViewAdapter;
import com.yibao.music.base.BaseObserver;
import com.yibao.music.base.BaseTansitionActivity;
import com.yibao.music.base.factory.RecyclerFactory;
import com.yibao.music.base.listener.OnMusicItemClickListener;
import com.yibao.music.base.listener.TextChangedListener;
import com.yibao.music.fragment.dialogfrag.MoreMenuBottomDialog;
import com.yibao.music.model.MoreMenuStatus;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.MusicLyricBean;
import com.yibao.music.model.PlayStatusBean;
import com.yibao.music.model.SearchHistoryBean;
import com.yibao.music.service.AudioPlayService;
import com.yibao.music.service.AudioServiceConnection;
import com.yibao.music.util.Constants;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.LyricsUtil;
import com.yibao.music.util.MusicDaoUtil;
import com.yibao.music.util.SnakbarUtil;
import com.yibao.music.util.SoftKeybordUtil;
import com.yibao.music.util.StringUtil;
import com.yibao.music.util.TitleArtistUtil;
import com.yibao.music.view.FlowLayoutView;
import com.yibao.music.view.music.SmartisanControlBar;

import java.util.ArrayList;
import java.util.Collections;
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
public class SearchActivity extends BaseTansitionActivity implements OnMusicItemClickListener {
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
    @BindView(R.id.sticky_view_search)
    TextView mtvStickyView;
    @BindView(R.id.ll_list_view)
    LinearLayout mLinearDetail;
    @BindView(R.id.ll_history)
    LinearLayout mLayoutHistory;
    @BindView(R.id.flowlayout)
    FlowLayoutView mFlowLayoutView;

    @BindView(R.id.smartisan_control_bar)
    SmartisanControlBar mSmartisanControlBar;
    private DetailsViewAdapter mSearchDetailAdapter;
    private MusicBean mMusicBean;
    private AudioPlayService.AudioBinder audioBinder;
    private int lyricsFlag;
    private InputMethodManager mInputMethodManager;
    private Disposable mDisposableSoft;
    private AudioServiceConnection mServiceConnection;

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
        mMusicBean = getIntent().getParcelableExtra("musicBean");
        int pageType = getIntent().getIntExtra("pageType", 0);
        audioBinder = MusicActivity.getAudioBinder();
        mSmartisanControlBar.setPbColorAndPreBtnGone();
        setMusicInfo(mMusicBean);
        if (pageType > Constants.NUMBER_ZOER) {
            initSearch(mMusicBean.getArtist());
            mIvEditClear.setVisibility(View.VISIBLE);
        } else {
            // 主动弹出键盘
            mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mDisposableSoft = Observable.timer(50, TimeUnit.MILLISECONDS)
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
        // 搜索记录
        List<SearchHistoryBean> searchList = mSearchDao.queryBuilder().list();
        if (searchList != null && searchList.size() > 0) {
            mLayoutHistory.setVisibility(View.VISIBLE);
            Collections.sort(searchList);
            mFlowLayoutView.setData(searchList);
        }
        // 列表数据
        RecyclerView recyclerView = RecyclerFactory.creatRecyclerView(1, mSearchDetailAdapter);
        mLinearDetail.addView(recyclerView);

    }

    @Override
    protected void moreMenu(MoreMenuStatus moreMenuStatus) {
        super.moreMenu(moreMenuStatus);
        MusicBean musicBean = moreMenuStatus.getMusicBean();
        switch (moreMenuStatus.getPosition()) {
            case Constants.NUMBER_ZOER:
                startPlayListActivity(musicBean.getTitle());
                break;
            case Constants.NUMBER_ONE:
                SnakbarUtil.keepGoing(mSmartisanControlBar);
                break;
            case Constants.NUMBER_TWO:
                if (audioBinder != null) {
                    if (audioBinder.getPosition() == moreMenuStatus.getMusicPosition()) {
                        audioBinder.updataFavorite();
                        checkCurrentSongIsFavorite(musicBean, null, mSmartisanControlBar);
                    } else {
                        MusicBean bean = moreMenuStatus.getMusicBean();
                        bean.setIsFavorite(!bean.isFavorite());
                        mMusicDao.update(bean);
                    }
                } else {
                    SnakbarUtil.firstPlayMusic(mSmartisanControlBar);
                }

                break;
            case Constants.NUMBER_THRRE:
                SnakbarUtil.keepGoing(mSmartisanControlBar);
                break;
            case Constants.NUMBER_FOUR:
                mBus.post(Constants.NUMBER_ONE, moreMenuStatus);
                mMusicDao.delete(moreMenuStatus.getMusicBean());
                break;
            default:
                break;
        }
    }

    @Override
    protected void refreshBtnAndNotify(PlayStatusBean playStatusBean) {
        switch (playStatusBean.getType()) {
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
                MoreMenuBottomDialog.newInstance(musicBean, position, false).getBottomDialog(this));
        mEditSearch.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                String searchContent = s.toString();
                if (!Constants.NULL_STRING.equals(searchContent) && searchContent.length() > 0) {
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
        List<SearchHistoryBean> historyList = mSearchDao.queryBuilder().list();
        if (historyList != null && historyList.size() > 0) {
            mLayoutHistory.setVisibility(View.VISIBLE);
            mTvNoSearchResult.setVisibility(View.GONE);
            Collections.sort(historyList);
            mFlowLayoutView.clearView(historyList);
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
        MusicDaoUtil.getSearchResult(flag -> {
            mSearchDetailAdapter.setDataFlag(flag);
            mtvStickyView.setText(flag == 1 ? "艺术家" : flag == 2 ? "专辑" : "歌曲");
        }, mMusicDao, searchconditions)
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
                SoftKeybordUtil.showAndHintSoftInput(mInputMethodManager, 1, InputMethodManager.RESULT_UNCHANGED_SHOWN);
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
        mServiceConnection = new AudioServiceConnection();
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
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
        if (mDisposableSoft != null) {
            mDisposableSoft.dispose();
            mDisposableSoft = null;
        }
        if (mServiceConnection != null) {
            unbindService(mServiceConnection);
            mServiceConnection = null;
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
}
