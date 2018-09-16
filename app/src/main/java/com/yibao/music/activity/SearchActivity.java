package com.yibao.music.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yibao.music.R;
import com.yibao.music.adapter.DetailsListAdapter;
import com.yibao.music.adapter.SearchHistoryAdapter;
import com.yibao.music.adapter.SearchRvAdapter;
import com.yibao.music.base.BaseActivity;
import com.yibao.music.base.BaseObserver;
import com.yibao.music.base.BaseRvAdapter;
import com.yibao.music.base.factory.RecyclerFactory;
import com.yibao.music.base.listener.OnMusicItemClickListener;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.SearchHistoryBean;
import com.yibao.music.service.AudioPlayService;
import com.yibao.music.service.AudioServiceConnection;
import com.yibao.music.util.Constants;
import com.yibao.music.util.MusicDaoUtil;
import com.yibao.music.util.MusicListUtil;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

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
    //    @BindView(R.id.grid_search_item)
//    GridView mGridSearchItem;
    @BindView(R.id.edit_search)
    EditText mEditSearch;
    //    @BindView(R.id.rv_search_item)
//    RecyclerView mRecyclerSearchItem;
    @BindView(R.id.tv_no_search_result)
    TextView mTvNoSearchResult;
    @BindView(R.id.ll_query_view)
    LinearLayout mLinearDetail;
    @BindView(R.id.ll_history)
    LinearLayout mLayoutHistory;
    @BindView(R.id.flowlayout)
    TagFlowLayout mTagFlowLayout;
    private SearchHistoryAdapter mHistoryAdapter;
    private DetailsListAdapter mSearchDetailAdapter;
    private List<SearchHistoryBean> mSearchList;
    private SearchRvAdapter mSearchRvAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mBind = ButterKnife.bind(this);
        initData();
        initListener();

    }


    private void initData() {
        mSearchList = MusicListUtil.sortSearchHistory(mSearchDao.queryBuilder().list());
        if (mSearchList != null && mSearchList.size() > 0) {
            mLayoutHistory.setVisibility(View.VISIBLE);
//            mHistoryAdapter = new SearchHistoryAdapter(this, mSearchList);
//            mGridSearchItem.setAdapter(mHistoryAdapter);
            mSearchRvAdapter = new SearchRvAdapter(mSearchList);
            StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(4,
                    StaggeredGridLayoutManager.VERTICAL);
//            mRecyclerSearchItem.setLayoutManager(manager);
//            mRecyclerSearchItem.setAdapter(mSearchRvAdapter);
        }
//        mSearchDetailAdapter = new DetailsListAdapter(SearchActivity.this, null, Constants.NUMBER_ONE);
//        RecyclerView recyclerView = RecyclerFactory.creatRecyclerView(1, mSearchDetailAdapter);
//        mLinearDetail.addView(recyclerView);
        mTagFlowLayout.setAdapter(new TagAdapter<SearchHistoryBean>(mSearchList) {
            @Override
            public View getView(FlowLayout parent, int position, SearchHistoryBean bean) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.search_rv_item, mTagFlowLayout, false);
                TextView tv = view.findViewById(R.id.grid_tv);
                tv.setText(bean.getSearchContent());
                return view;
            }
        });


    }

    private void initListener() {
        mEditSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

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
        mTagFlowLayout.setOnTagClickListener((view, position, parent) -> {
            initSearch(mSearchList.get(position).getSearchContent());
            return true;
        });
//        mGridSearchItem.setOnItemClickListener((parent, view, position, id) -> {
//            String searchContent = mSearchList.get(position).getSearchContent();
//            initSearch(searchContent);
//        });
//        mSearchRvAdapter.setItemListener(bean -> initSearch(bean.getSearchContent()));
    }

    private void initSearch(String searchContent) {
        mEditSearch.setText(searchContent);
        mEditSearch.setSelection(searchContent.length());
        SearchActivity.this.searchMusic(searchContent);
    }

    private void historyViewVisibility() {
        List<SearchHistoryBean> historyList = MusicListUtil.sortSearchHistory(mSearchDao.queryBuilder().list());
        if (historyList != null && historyList.size() > 0) {
            mSearchRvAdapter.setNewData(historyList);
            mLayoutHistory.setVisibility(View.VISIBLE);
            mTvNoSearchResult.setVisibility(View.GONE);
        }
    }

    private void searchMusic(String searchconditions) {
        MusicDaoUtil.getQueryResult(mMusicDao, mSearchDao, searchconditions)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<List<MusicBean>>() {
                    @Override
                    public void onNext(List<MusicBean> musicBeanList) {
                        mSearchDetailAdapter.setNewData(musicBeanList);
                        mLayoutHistory.setVisibility(View.GONE);
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
//                mHistoryAdapter.updata(null);
                mSearchRvAdapter.setNewData(null);
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
