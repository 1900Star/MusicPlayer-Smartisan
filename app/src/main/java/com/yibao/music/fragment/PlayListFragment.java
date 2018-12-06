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
import com.yibao.music.activity.PlayListActivity;
import com.yibao.music.adapter.PlayListAdapter;
import com.yibao.music.base.BaseMusicFragment;
import com.yibao.music.base.factory.RecyclerFactory;
import com.yibao.music.base.listener.OnFinishActivityListener;
import com.yibao.music.base.listener.UpdataTitleListener;
import com.yibao.music.fragment.dialogfrag.AddListDialog;
import com.yibao.music.fragment.dialogfrag.DeletePlayListDialog;
import com.yibao.music.model.AddAndDeleteListBean;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.PlayListBean;
import com.yibao.music.model.greendao.MusicBeanDao;
import com.yibao.music.model.greendao.PlayListBeanDao;
import com.yibao.music.util.Constants;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.SpUtil;

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
    private int mEditPosition;
    private int mSelectCount;
    private static int mFlag;
    private static String mSongName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.play_list_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        receiveRxbuData();
        initListener();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        SpUtil.setAddToPlayListFlag(mActivity, mFlag);
        mAdapter = new PlayListAdapter(getPlayList());
        RecyclerView recyclerView = RecyclerFactory.creatRecyclerView(Constants.NUMBER_ONE, mAdapter);
        mPlayListContent.addView(recyclerView);
    }

    /**
     * 新增和删除列表
     */
    private void receiveRxbuData() {
        mCompositeDisposable.add(mBus.toObserverable(AddAndDeleteListBean.class)
                .subscribeOn(Schedulers.io()).map(bean -> {
                    int operationType = bean.getOperationType();
                    // 删除列表
                    if (operationType == Constants.NUMBER_TWO) {
                        mAdapter.notifyItemRemoved(mDeletePosition);
                        changeTvEditVisibility();
                    } else if (operationType == Constants.NUMBER_FOUR) {
                        // 更新列表名
                        PlayListBean playListBean = getPlayList().get(mEditPosition);
                        playListBean.setTitle(bean.getListTitle());
                        mPlayListDao.update(playListBean);
                    }
                    return getPlayList();
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(newPlayList -> {
                    mAdapter.setNewData(newPlayList);
                    changeTvEditVisibility();
                })
        );
    }

    private void changeTvEditVisibility() {
        if (mContext instanceof UpdataTitleListener) {
            ((UpdataTitleListener) mContext).setEditVisibility(getPlayList().size() > 0 ? View.VISIBLE :
                    View.GONE);
        }

    }

    private List<PlayListBean> getPlayList() {
        List<PlayListBean> playListBeans = mPlayListDao.queryBuilder().list();
        Collections.sort(playListBeans);
        return playListBeans;
    }

    private void initListener() {
        mAdapter.setItemListener((playListBean, isEditStatus) -> {
            if (SpUtil.getAddToPlayListFlag(mActivity) == Constants.NUMBER_ONE) {
                if (mContext instanceof PlayListActivity) {
                    MusicBean musicBean = mMusicBeanDao.queryBuilder().where(MusicBeanDao.Properties.Title.eq(mSongName)).build().unique();
                    musicBean.setPlayListFlag(playListBean.getTitle());
                    mMusicBeanDao.update(musicBean);
                    playListBean.setSongCount(playListBean.getSongCount() + 1);
                    mPlayListDao.update(playListBean);
                    ((OnFinishActivityListener) mContext).finishActivity();
                }
            } else {
                if (isEditStatus) {
                    mSelectCount = playListBean.isSelected() ? mSelectCount-- : mSelectCount++;
                    playListBean.setIsSelected(!playListBean.isSelected());
                    mPlayListDao.update(playListBean);
                    mAdapter.notifyDataSetChanged();
                } else {
                    PlayListFragment.this.showDetailsView(playListBean.getTitle());
                }
            }
        });
        // 长按删除
        mAdapter.setItemLongClickListener((musicInfo, currentPosition) -> {
            if (SpUtil.getAddToPlayListFlag(mActivity) != Constants.NUMBER_ONE) {
                mDeletePosition = currentPosition;
                DeletePlayListDialog.newInstance(musicInfo, Constants.NUMBER_TWO).show(mActivity.getFragmentManager(), "deleteList");
            }
        });
        // 编辑按钮
        mAdapter.setItemEditClickListener(currentPosition -> {
            mEditPosition = currentPosition;
            if (getPlayList().size() > 0) {
                String currentTitle = getPlayList().get(currentPosition).getTitle();
                AddListDialog.newInstance(2, currentTitle).show(mActivity.getFragmentManager(), "addList");
            }
        });
    }

    @Override
    protected void changeEditStatus(int currentIndex) {
        if (currentIndex == Constants.NUMBER_ZOER) {
            closeEditStatus();
        } else if (currentIndex == 10) {
            // 删除已选择的条目
            List<PlayListBean> beanList = mPlayListDao.queryBuilder().where(PlayListBeanDao.Properties.IsSelected.eq(true)).build().list();
            for (PlayListBean playListBean : beanList) {
                mPlayListDao.delete(playListBean);
            }
            mAdapter.setItemSelectStatus(false);
            mAdapter.setNewData(getPlayList());
            mLlAddNewPlayList.setEnabled(true);
            changeTvEditVisibility();

        }
    }

    // 取消所有已选
    private void cancelAllSelected() {
        List<PlayListBean> playListBeanList = mPlayListDao.queryBuilder().where(PlayListBeanDao.Properties.IsSelected.eq(true)).build().list();
        Collections.sort(playListBeanList);
        for (PlayListBean playListBean : playListBeanList) {
            playListBean.setSelected(false);
            mPlayListDao.update(playListBean);
        }
        mSelectCount = 0;
        mAdapter.setNewData(getPlayList());
    }

    private void showDetailsView(String title) {
        if (isShowDetailsView) {
            removeFrag(mClassName);
            mLlAddNewPlayList.setVisibility(View.VISIBLE);
            mDetailsView.setVisibility(View.GONE);
            detailsViewTitle = null;
        } else {
            mLlAddNewPlayList.setVisibility(View.INVISIBLE);
            mDetailsView.setVisibility(View.VISIBLE);
            List<MusicBean> list = mMusicBeanDao.queryBuilder().where(MusicBeanDao.Properties.PlayListFlag.eq(title)).build().list();
            LogUtil.d("==========  播放列表     " + list.size());
            putFragToMap(Constants.NUMBER_EIGHT, mClassName);
            detailsViewTitle = title;
            changeToolBarTitle(title, isShowDetailsView);
        }
        changeTvEditText(getResources().getString(isShowDetailsView ? R.string.tv_edit : R.string.back_play_list));
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

    public static PlayListFragment newInstance(int flag, String songName) {
        mFlag = flag;
        mSongName = songName;
        return new PlayListFragment();
    }

    @Override
    protected void handleDetailsBack(int detailFlag) {
        changeTvEditText(getResources().getString(R.string.tv_edit));
        if (detailFlag == Constants.NUMBER_EIGHT) {
            if (!isItemSelectStatus) {
                if (!isShowDetailsView) {
                    closeEditStatus();
                }
            } else {
                showDetailsView(detailsViewTitle);
            }

        }
        super.handleDetailsBack(detailFlag);
    }

    private void closeEditStatus() {
        if (isItemSelectStatus) {
            putFragToMap(Constants.NUMBER_EIGHT, mClassName);
        } else {
            removeFrag(mClassName);
            mAdapter.setItemSelectStatus(isItemSelectStatus);
        }
        changeTvEditText(getResources().getString(isItemSelectStatus ? R.string.complete : R.string.tv_edit));
        mAdapter.setItemSelectStatus(isItemSelectStatus);
        isItemSelectStatus = !isItemSelectStatus;
        if (!isItemSelectStatus && mSelectCount > 0) {
            LogUtil.d("========== status    " + mSelectCount);
            cancelAllSelected();
        }
        mLlAddNewPlayList.setEnabled(isItemSelectStatus);
    }

}
