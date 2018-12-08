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
import com.yibao.music.adapter.DetailsViewAdapter;
import com.yibao.music.adapter.PlayListAdapter;
import com.yibao.music.base.BaseMusicFragment;
import com.yibao.music.base.factory.RecyclerFactory;
import com.yibao.music.base.listener.OnFinishActivityListener;
import com.yibao.music.base.listener.UpdataTitleListener;
import com.yibao.music.fragment.dialogfrag.AddListDialog;
import com.yibao.music.fragment.dialogfrag.DeletePlayListDialog;
import com.yibao.music.fragment.dialogfrag.MoreMenuBottomDialog;
import com.yibao.music.model.AddAndDeleteListBean;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.PlayListBean;
import com.yibao.music.model.greendao.MusicBeanDao;
import com.yibao.music.model.greendao.PlayListBeanDao;
import com.yibao.music.util.Constants;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.SpUtil;
import com.yibao.music.util.ToastUtil;
import com.yibao.music.view.music.PlayListDetailView;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
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
    @BindView(R.id.play_list_detail_view)
    PlayListDetailView mDetailView;
    @BindView(R.id.play_list_content)
    LinearLayout mPlayListContent;
    private PlayListAdapter mAdapter;
    public static boolean isShowDetailsView = false;
    private int mDeletePosition;
    public static String detailsViewTitle;
    private boolean isItemSelectStatus = true;
    private int mEditPosition;
    private int mSelectCount;
    private static int mFlag;
    private static String mSongName;
    private List<MusicBean> mDetailList;
    private DetailsViewAdapter mDetailsAdapter;
    private PlayListBean mPlayListBean;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.play_list_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        initData();
        initListener();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.setNewData(getPlayList());
        receiveRxbuData();
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
                                // 更新列表名,同步更新列表中的歌曲的列表标识
                                PlayListBean updataTitleBean = getPlayList().get(mEditPosition);
//                        PlayListBean newPlayListBean = getPlayList().get(mEditPosition);
                                List<MusicBean> beanList = mMusicBeanDao.queryBuilder().where(MusicBeanDao.Properties.PlayListFlag.eq(updataTitleBean.getTitle())).build().list();
                                for (MusicBean musicBean : beanList) {
                                    musicBean.setPlayListFlag(bean.getListTitle());
                                    mMusicBeanDao.update(musicBean);
                                }
                                // 更新列表名
                                updataTitleBean.setTitle(bean.getListTitle());
                                mPlayListDao.update(updataTitleBean);

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
        mLlAddNewPlayList.setOnClickListener(v -> AddListDialog.newInstance(1, "").show(mActivity.getFragmentManager(), "addList"));
        mAdapter.setItemListener((playListBean, isEditStatus) -> {
            if (SpUtil.getAddToPlayListFlag(mActivity) == Constants.NUMBER_ONE) {
                if (mContext instanceof PlayListActivity) {
                    List<MusicBean> beanList = mMusicBeanDao.queryBuilder().where(MusicBeanDao.Properties.PlayListFlag.eq(playListBean.getTitle()), MusicBeanDao.Properties.Title.eq(mSongName)).build().list();
                    if (beanList.size() > 0) {
                        ToastUtil.songalreadyExist(mActivity);
                    } else {
                        List<MusicBean> musicBeans = mMusicBeanDao.queryBuilder().where(MusicBeanDao.Properties.Title.eq(mSongName)).build().list();
                        if (musicBeans.size() > 0) {
                            MusicBean musicBean = musicBeans.get(0);
                            musicBean.setPlayListFlag(playListBean.getTitle());
                            mMusicBeanDao.update(musicBean);
                            playListBean.setSongCount(playListBean.getSongCount() + 1);
                            mPlayListDao.update(playListBean);
                        }
                    }
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
            SpUtil.setAddToPlayListFlag(mContext, Constants.NUMBER_ZOER);
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
            mDetailView.setVisibility(View.GONE);
            mAdapter.setNewData(getPlayList());
            detailsViewTitle = null;
        } else {
            mLlAddNewPlayList.setVisibility(View.INVISIBLE);
            mDetailView.setVisibility(View.VISIBLE);
            mDetailList = mMusicBeanDao.queryBuilder().where(MusicBeanDao.Properties.PlayListFlag.eq(title)).build().list();
            mDetailView.setQureyFlag(title, mDetailList.size());
            mDetailsAdapter = new DetailsViewAdapter(mActivity, mDetailList, Constants.NUMBER_FOUR);
            mDetailView.setAdapter(mDetailsAdapter);
            mDetailsAdapter.setOnItemMenuListener((position, musicBean) -> MoreMenuBottomDialog.newInstance(musicBean, position, false).getBottomDialog(mActivity));
            putFragToMap(Constants.NUMBER_EIGHT, mClassName);
            detailsViewTitle = title;
            changeToolBarTitle(title, isShowDetailsView);
        }
        changeTvEditText(getResources().getString(isShowDetailsView ? R.string.tv_edit : R.string.back_play_list));
        isShowDetailsView = !isShowDetailsView;
    }

    @Override
    protected void deleteItem(int musicPosition) {
        super.deleteItem(musicPosition);
        if (mDetailList != null && mDetailsAdapter != null) {
            mDetailList.remove(musicPosition);
            if (mDetailList.size() == Constants.NUMBER_ZOER) {
                showDetailsView(mPlayListBean.getTitle());
                mPlayListBean.setSongCount(Constants.NUMBER_ZOER);
                mPlayListDao.update(mPlayListBean);
                mAdapter.setNewData(getPlayList());
            } else {
                mDetailsAdapter.setData(mDetailList);
            }
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
