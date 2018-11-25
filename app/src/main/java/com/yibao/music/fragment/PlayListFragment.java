package com.yibao.music.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.yibao.music.R;
import com.yibao.music.adapter.PlayListAdapter;
import com.yibao.music.base.BaseMusicFragment;
import com.yibao.music.base.BaseRvAdapter;
import com.yibao.music.base.factory.RecyclerFactory;
import com.yibao.music.base.listener.UpdataTitleListener;
import com.yibao.music.fragment.dialogfrag.AddListDialog;
import com.yibao.music.fragment.dialogfrag.DeletePlayListDialog;
import com.yibao.music.model.AddAndDeleteListBean;
import com.yibao.music.model.PlayListBean;
import com.yibao.music.model.greendao.PlayListBeanDao;
import com.yibao.music.util.Constants;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.SpUtil;
import com.yibao.music.util.StringUtil;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
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

public class PlayListFragment extends BaseMusicFragment {
    @BindView(R.id.ll_add_new_play_list)
    LinearLayout mLlAddNewPlayList;
    @BindView(R.id.play_list_content)
    LinearLayout mPlayListContent;
    @BindView(R.id.album_details_head_content)
    LinearLayout mDetailsView;
    private PlayListAdapter mAdapter;
    public static boolean isShowDetailsView = false;
    private int mDeletePosition;
    public static String detailsViewTitle;
    private boolean isItemSelectStatus = true;
    private List<PlayListBean> mPlayList;
    private int mEditPosition;
    private int mSelectCount;

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
        mPlayList = mPlayListDao.queryBuilder().list();
        Collections.sort(mPlayList);
        mAdapter = new PlayListAdapter(mPlayList);
        RecyclerView recyclerView = RecyclerFactory.creatRecyclerView(Constants.NUMBER_ONE, mAdapter);
        mPlayListContent.addView(recyclerView);
    }

    /**
     * 新增和删除列表
     */
    private void receiveRxbuData() {
        mDisposable.add(mBus.toObserverable(AddAndDeleteListBean.class)
                        .subscribeOn(Schedulers.io()).map(bean -> {
                            int operationType = bean.getOperationType();
                            if (operationType == Constants.NUMBER_TWO) {
                                mAdapter.removeItem(mDeletePosition);
                            } else if (operationType == Constants.NUMBER_FOUR) {
                                PlayListBean playListBean = mPlayList.get(mEditPosition);
                                playListBean.setTitle(bean.getListTitle());
//                        mPlayListDao.queryBuilder().where(PlayListBeanDao.Properties.Title.eq("")).build().unique();
                                mPlayListDao.update(playListBean);
                            }
                            List<PlayListBean> playListBeans = mPlayListDao.queryBuilder().list();
                            Collections.sort(playListBeans);
                            return playListBeans;
                        })
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(newPlayList -> mAdapter.setNewData(newPlayList))
        );
    }

    private void initListener() {
        mAdapter.setItemListener((playListBean, isEditStatus) -> {
            if (isEditStatus) {
                if (playListBean.isSelected()) {
                    mSelectCount--;
                    playListBean.setSelected(false);
                } else {
                    mSelectCount++;
                    playListBean.setSelected(true);
                }
                LogUtil.d("===========选中  " + mSelectCount);
                mLlAddNewPlayList.setEnabled(false);
                mAdapter.notifyDataSetChanged();
            } else {
                mLlAddNewPlayList.setEnabled(true);
                PlayListFragment.this.switchShowDetailsView(playListBean.getTitle());
            }
        });
        // 长按删除
        mAdapter.setItemLongClickListener((musicInfo, currentPosition) -> {
            mDeletePosition = currentPosition;
            DeletePlayListDialog.newInstance(musicInfo, Constants.NUMBER_TWO).show(mActivity.getFragmentManager(), "deleteList");
        });
        // 编辑按钮
        mAdapter.setItemEditClickListener(currentPosition -> {
            mEditPosition = currentPosition;
            List<PlayListBean> listBeans = mPlayListDao.queryBuilder().list();
            Collections.sort(listBeans);
            if (listBeans.size() > 0) {
                String currentTitle = listBeans.get(currentPosition).getTitle();
                AddListDialog.newInstance(2, currentTitle).show(mActivity.getFragmentManager(), "addList");
            }
        });
        mLlAddNewPlayList.setOnLongClickListener(v -> {
            mAdapter.setItemSelectStatus(isItemSelectStatus);
            isItemSelectStatus = !isItemSelectStatus;
            return true;
        });
    }

    private void switchShowDetailsView(String title) {
        if (isShowDetailsView) {
            mLlAddNewPlayList.setVisibility(View.VISIBLE);
            mDetailsView.setVisibility(View.GONE);
            detailsViewTitle = null;
        } else {
            mLlAddNewPlayList.setVisibility(View.INVISIBLE);
            mDetailsView.setVisibility(View.VISIBLE);
            SpUtil.setDetailsFlag(mActivity, Constants.NUMBER_EIGHT);
            if (!mDetailsViewMap.containsKey(mClassName)) {
                mDetailsViewMap.put(mClassName, this);
            }

            if (mContext instanceof UpdataTitleListener) {
                detailsViewTitle = title;
                ((UpdataTitleListener) mContext).updataTitle(title, isShowDetailsView);
            }
        }
        isShowDetailsView = !isShowDetailsView;
    }


    @OnClick(R.id.ll_add_new_play_list)
    public void onClick(View v) {
        switch (v.getId()) {
            // 打开新建播放列表的Dialog
            case R.id.ll_add_new_play_list:
                String tvHint = getResources().getString(R.string.not_name_hint);
                AddListDialog.newInstance(1, tvHint).show(mActivity.getFragmentManager(), "addList");
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
            mDetailsViewMap.remove(mClassName);
            isShowDetailsView = !isShowDetailsView;
        }
    }

}
