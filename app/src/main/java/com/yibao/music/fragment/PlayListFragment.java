package com.yibao.music.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.yibao.music.R;
import com.yibao.music.adapter.PlayListAdapter;
import com.yibao.music.base.BaseFragment;
import com.yibao.music.base.BaseRvAdapter;
import com.yibao.music.base.factory.RecyclerFactory;
import com.yibao.music.base.listener.UpdataTitleListener;
import com.yibao.music.fragment.dialogfrag.AddListDialog;
import com.yibao.music.fragment.dialogfrag.DeletePlayListDialog;
import com.yibao.music.model.AddAndDeleteListBean;
import com.yibao.music.model.MusicInfo;
import com.yibao.music.model.UpdataToolbaTitle;
import com.yibao.music.util.Constants;
import com.yibao.music.util.MusicListUtil;
import com.yibao.music.util.SharePrefrencesUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.playlist
 * @文件名: PlayListFragment
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.strangermy98@gmail.com
 * @创建时间: 2018/2/9 16:07
 * @描述： {个人播放列表}
 */

public class PlayListFragment extends BaseFragment {
    @BindView(R.id.ll_add_new_play_list)
    LinearLayout mLlAddNewPlayList;
    @BindView(R.id.play_list_content)
    LinearLayout mPlayListContent;
    @BindView(R.id.album_details_head_content)
    LinearLayout mDetailsView;
    private PlayListAdapter mAdapter;

    private int mDeletePosition;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.play_list_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        initData();
        receiveRxbuData();
        initListener();

        return view;
    }

    private void initData() {
        List<MusicInfo> playList = MusicListUtil.sortNewListAddTime(mMusicInfoDao.queryBuilder().list());
        mAdapter = new PlayListAdapter(playList);
        RecyclerView recyclerView = RecyclerFactory.creatRecyclerView(Constants.NUMBER_ONE, mAdapter);
        mPlayListContent.addView(recyclerView);

    }

    /**
     * 新增和删除列表
     */
    private void receiveRxbuData() {
        mDisposable.add(mBus.toObserverable(AddAndDeleteListBean.class)
                .subscribeOn(Schedulers.io()).map(addAndDeleteListBean -> {
                    if (addAndDeleteListBean.getOperationType() == Constants.NUMBER_TWO) {
                        mAdapter.removeItem(mDeletePosition);
                    }
                    return MusicListUtil.sortNewListAddTime(mMusicInfoDao.queryBuilder().list());
                })
                .observeOn(AndroidSchedulers.mainThread()).subscribe(newPlayList -> {
                    mAdapter.setNewData(newPlayList);
                })
        );
    }

    private void initListener() {
        mAdapter.setItemListener(str -> PlayListFragment.this.switchShowDetailsView());
        // 长按删除
        mAdapter.setItemLongClickListener((musicInfo, currentPosition) -> {
            mDeletePosition = currentPosition;
            musicInfo.setPlayStatus(Constants.NUMBER_TWO);
            DeletePlayListDialog.newInstance(musicInfo).show(mActivity.getFragmentManager(), "deleteList");
        });
    }

    private void switchShowDetailsView() {
        if (isShowDetailsView) {
            mLlAddNewPlayList.setVisibility(View.VISIBLE);
            mDetailsView.setVisibility(View.GONE);

        } else {
            mLlAddNewPlayList.setVisibility(View.INVISIBLE);
            mDetailsView.setVisibility(View.VISIBLE);
            SharePrefrencesUtil.setDetailsFlag(mActivity, Constants.NUMBER_EIGHT);
            if (!mDetailsViewMap.containsKey(mClassName)) {
                mDetailsViewMap.put(mClassName, this);
            }

            if (mContext instanceof UpdataTitleListener) {
                ((UpdataTitleListener) mContext).updataTitle("列表");
            }
        }
        isShowDetailsView = !isShowDetailsView;
    }


    @OnClick(R.id.ll_add_new_play_list)
    public void onClick(View v) {
        switch (v.getId()) {
            // 打开新建播放列表的Dialog
            case R.id.ll_add_new_play_list:
                AddListDialog.newInstance().show(mActivity.getFragmentManager(), "addList");
                break;
            default:
                break;
        }
    }

    public static PlayListFragment newInstance() {
        return new PlayListFragment();
    }

    @Override
    protected void handleDetailsBack(int detailFlag) {
        super.handleDetailsBack(detailFlag);
        if (detailFlag == Constants.NUMBER_EIGHT) {
            mDetailsView.setVisibility(View.GONE);
            mLlAddNewPlayList.setVisibility(View.VISIBLE);
            if (mDetailsViewMap.containsKey(mClassName)) {
                mDetailsViewMap.remove(mClassName);
            }
            isShowDetailsView = !isShowDetailsView;

        }


    }

}
