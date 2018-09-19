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

import com.yibao.music.R;
import com.yibao.music.adapter.SearchDetailsAdapter;
import com.yibao.music.base.BaseActivity;
import com.yibao.music.base.BaseObserver;
import com.yibao.music.base.factory.RecyclerFactory;
import com.yibao.music.base.listener.OnMusicItemClickListener;
import com.yibao.music.base.listener.TextChangedListener;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.SearchHistoryBean;
import com.yibao.music.service.AudioPlayService;
import com.yibao.music.service.AudioServiceConnection;
import com.yibao.music.util.Constants;
import com.yibao.music.util.MusicDaoUtil;
import com.yibao.music.util.MusicListUtil;
import com.yibao.music.view.FlowLayoutView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;

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
    private SearchDetailsAdapter mSearchDetailAdapter;
    private List<SearchHistoryBean> mSearchList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mBind = ButterKnife.bind(this);
        initView();
        initData();
        initListener();

    }

    private void initView() {

    }


    private void initData() {
//        mSearchList = MusicListUtil.sortSearchHistory(mSearchDao.queryBuilder().list());
        mSearchList = mSearchDao.queryBuilder().list();
//        Collections.sort(mSearchList);
        if (mSearchList != null && mSearchList.size() > 0) {
            mLayoutHistory.setVisibility(View.VISIBLE);
        }
        mSearchDetailAdapter = new SearchDetailsAdapter(SearchActivity.this, null, Constants.NUMBER_ONE);
        RecyclerView recyclerView = RecyclerFactory.creatRecyclerView(1, mSearchDetailAdapter);
        mLinearDetail.addView(recyclerView);
        mFlowLayoutView.setData(mSearchList);

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
                    mLinearDetail.setVisibility(View.GONE);
                    mTvNoSearchResult.setVisibility(View.GONE);
                    mIvEditClear.setVisibility(View.GONE);
                }
            }
        });
        mFlowLayoutView.setItemClickListener((position, bean) -> initSearch(bean.getSearchContent()));
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
     * @param searchconditions 搜索关键字
     */
    private void searchMusic(String searchconditions) {
        MusicDaoUtil.getSearchResult(mMusicDao, mSearchDao, searchconditions)
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
                mLinearDetail.setVisibility(View.GONE);
                historyViewVisibility();
                break;
            case R.id.iv_search_clear:
                mSearchDao.deleteAll();
                mFlowLayoutView.removeAllViews();
                mLayoutHistory.setVisibility(View.GONE);
                mLinearDetail.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void startMusicService(int position) {

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
//        SoftKeybordUtil.showAndHintSoftInput(mInputMethodManager, 1, InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    @Override
    public void onOpenMusicPlayDialogFag() {

    }
}
