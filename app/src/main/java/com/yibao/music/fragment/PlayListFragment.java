package com.yibao.music.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
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
import com.yibao.music.util.SnakbarUtil;
import com.yibao.music.util.SpUtil;
import com.yibao.music.util.ToastUtil;
import com.yibao.music.view.music.MusicToolBar;
import com.yibao.music.view.music.PlayListDetailView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
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
    @BindView(R.id.appbar_playlist)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.music_toolbar_list)
    MusicToolBar mMusicToolBar;
    @BindView(R.id.ll_add_new_play_list)
    LinearLayout mLlAddNewPlayList;
    @BindView(R.id.play_list_detail_view)
    PlayListDetailView mDetailView;
    @BindView(R.id.play_list_content)
    LinearLayout mPlayListContent;
    private PlayListAdapter mAdapter;
    private boolean isShowDetailsView = false;
    private int mDeletePosition;
    private boolean isItemSelectStatus = true;
    private int mEditPosition;
    private int mSelectCount;
    private static String mSongName;
    private List<MusicBean> mDetailList;
    private DetailsViewAdapter mDetailsAdapter;
    private PlayListBean mPlayListBean;
    private Disposable mAddDeleteListDisposable;
    private static ArrayList<String> mArrayList;
    private String mTempTitle;
    private static boolean isFormPlayListActivity;

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
    protected boolean getIsOpenDetail() {
        LogUtil.d("HSHS==========   " + isShowDetailsView + " ==  " + !isItemSelectStatus);
        return isShowDetailsView || !isItemSelectStatus;
    }

    @Override
    public void onResume() {
        super.onResume();

        mMusicToolBar.setToolbarTitle(isShowDetailsView ? mTempTitle : getString(R.string.play_list));
        LogUtil.d(" ZAZA=========   " + isFormPlayListActivity + " == " + SpUtil.getAddToPlayListFdlag(mActivity));
        mAppBarLayout.setVisibility(isFormPlayListActivity && SpUtil.getAddToPlayListFdlag(mActivity) == Constants.NUMBER_ONE ? View.GONE : View.VISIBLE);
        mAdapter.setNewData(getPlayList());
        receiveRxbuData();
    }

    private void initData() {
        mAdapter = new PlayListAdapter(getPlayList());
        RecyclerView recyclerView = RecyclerFactory.creatRecyclerView(Constants.NUMBER_ONE, mAdapter);
        mPlayListContent.addView(recyclerView);
        mDetailsAdapter = new DetailsViewAdapter(mActivity, null, Constants.NUMBER_FOUR);
    }

    /**
     * 新增和删除列表
     */
    private void receiveRxbuData() {
        if (mAddDeleteListDisposable == null) {
            mAddDeleteListDisposable = mBus.toObserverable(AddAndDeleteListBean.class)
                    .subscribeOn(Schedulers.io()).map(bean -> {
                        int operationType = bean.getOperationType();
                        // 删除列表
                        if (operationType == Constants.NUMBER_TWO) {
                            mAdapter.notifyItemRemoved(mDeletePosition);
                        } else if (operationType == Constants.NUMBER_FOUR) {
                            // 更新列表名,同步更新列表中的歌曲的列表标识
                            mPlayListBean = getPlayList().get(mEditPosition);
                            List<MusicBean> beanList = mMusicBeanDao.queryBuilder()
                                    .where(MusicBeanDao.Properties.PlayListFlag.eq(mPlayListBean.getTitle())).build().list();
                            for (MusicBean musicBean : beanList) {
                                musicBean.setPlayListFlag(bean.getListTitle());
                                mMusicBeanDao.update(musicBean);
                            }
                            // 更新列表名
                            mPlayListBean.setTitle(bean.getListTitle());
                            mPlayListDao.update(mPlayListBean);

                        } else if (operationType == Constants.NUMBER_SIX) {
                            mDetailsAdapter.notifyDataSetChanged();
                            mDetailList.remove(bean.getPosition());
                            if (mDetailList.size() == Constants.NUMBER_ZERO) {
                                String str = getResources().getString(R.string.play_list);
                                showDetailsView(str);
                            }
                        }
                        return getPlayList();
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(newPlayList -> {
                                mAdapter.setNewData(newPlayList);
                                if (isFormPlayListActivity) {
                                    addToList(getPlayList().get(mEditPosition));
                                }
                            }
                    );
        }
    }


    private List<PlayListBean> getPlayList() {
        List<PlayListBean> playListBeans = mPlayListDao.queryBuilder().list();
        Collections.sort(playListBeans);
        return playListBeans;
    }

    private void initListener() {
        mMusicToolBar.setClickListener(new MusicToolBar.OnToolbarClickListener() {
            @Override
            public void clickEdit() {
                if (getPlayList().size() > Constants.NUMBER_ZERO) {
                    if (isShowDetailsView) {
                        showDetailsView(getString(R.string.play_list));
                        mMusicToolBar.setTvEditText(R.string.tv_edit);
                    } else {
                        closeEditStatus();
                    }
                } else {
                    SnakbarUtil.favoriteFailView(mMusicToolBar, "当前没有播放列表");
                }

            }

            @Override
            public void switchMusicControlBar() {
                switchControlBar();
            }

            @Override
            public void clickDelete() {
                deleteListItem();
            }
        });

        mLlAddNewPlayList.setOnClickListener(v -> AddListDialog.newInstance(1, Constants.NULL_STRING, isFormPlayListActivity).show(mActivity.getFragmentManager(), "addList"));
        mAdapter.setItemListener((playListBean, isEditStatus) -> {
            LogUtil.d("TUTU======  " + isFormPlayListActivity +" ==  "+ isEditStatus);
            mTempTitle = playListBean.getTitle();
            // 从PlayListActivity过来的
            if (isFormPlayListActivity) {
                addToList(playListBean);
            } else {
                if (isEditStatus) {
                    mSelectCount = playListBean.isSelected() ? mSelectCount-- : mSelectCount++;
                    playListBean.setIsSelected(!playListBean.isSelected());
                    mPlayListDao.update(playListBean);
                    mAdapter.notifyDataSetChanged();
                } else {
                    PlayListFragment.this.showDetailsView(mTempTitle);
                }
            }
        });
        // 长按删除
        mAdapter.setItemLongClickListener((musicInfo, currentPosition) -> {
            if (isFormPlayListActivity) {
                mDeletePosition = currentPosition;
                DeletePlayListDialog.newInstance(musicInfo, Constants.NUMBER_TWO).show(mActivity.getFragmentManager(), "deleteList");
            }
        });
        // 编辑按钮
        mAdapter.setItemEditClickListener(currentPosition -> {
            mEditPosition = currentPosition;
            if (getPlayList().size() > 0) {
                String currentTitle = getPlayList().get(currentPosition).getTitle();
                AddListDialog.newInstance(2, currentTitle, false).show(mActivity.getFragmentManager(), "addList");
            }
        });
    }

    private void deleteListItem() {
        List<PlayListBean> beanList = mPlayListDao.queryBuilder().where(PlayListBeanDao.Properties.IsSelected.eq(true)).build().list();
        if (beanList.size() > Constants.NUMBER_ZERO) {
            for (PlayListBean playListBean : beanList) {
                mPlayListDao.delete(playListBean);
            }
            mAdapter.setItemSelectStatus(false);
            mAdapter.setNewData(getPlayList());
            mLlAddNewPlayList.setEnabled(true);
            mMusicToolBar.setTvEditText(R.string.tv_edit);
            mMusicToolBar.setTvDeleteVisibility(View.GONE);
        } else {
            SnakbarUtil.favoriteSuccessView(mMusicToolBar, "没有选中条目");
        }
    }


    private void showDetailsView(String title) {
        mMusicToolBar.setToolbarTitle(title);
        mLlAddNewPlayList.setVisibility(isShowDetailsView ? View.VISIBLE : View.GONE);
        mDetailView.setVisibility(isShowDetailsView ? View.GONE : View.VISIBLE);
        if (isShowDetailsView) {
            mAdapter.setNewData(getPlayList());
        } else {
            mDetailList = mMusicBeanDao.queryBuilder().where(MusicBeanDao.Properties.PlayListFlag.eq(title)).build().list();
            mDetailView.setQureyFlag(title, mDetailList.size());
            mDetailsAdapter.setNewData(mDetailList);
            RecyclerView recyclerView = RecyclerFactory.creatRecyclerView(Constants.NUMBER_ONE, mDetailsAdapter);
            mDetailsAdapter.setOnItemMenuListener((position, musicBean) -> {
                MoreMenuBottomDialog.newInstance(musicBean, position, false, false).getBottomDialog(mActivity);
            });
            mDetailView.setAdapter(recyclerView);
            interceptBackEvent(Constants.NUMBER_EIGHT);
        }
        isShowDetailsView = !isShowDetailsView;
        mMusicToolBar.setTvEditText(isShowDetailsView ? R.string.back : R.string.tv_edit);
    }

    /**
     * @param playListBean 通过PlayListActivity将选中的歌曲添加到列表中，有批量添加和单曲添加，
     */
    private void addToList(PlayListBean playListBean) {
        if (mContext instanceof PlayListActivity) {
            // 批量添加
            if (mArrayList != null && mArrayList.size() > 0) {
                new Thread(() -> {
                    for (String songTitle : mArrayList) {
                        List<MusicBean> musicBeanList = mMusicBeanDao.queryBuilder().where(MusicBeanDao.Properties.PlayListFlag.eq(playListBean.getTitle()), MusicBeanDao.Properties.Title.eq(songTitle)).build().list();
                        if (musicBeanList.size() == 0) {
                            insertSongToList(playListBean, songTitle);
                        }
                    }
                }).start();

            } else {
                // 单曲添加
                List<MusicBean> beanList = mMusicBeanDao.queryBuilder().where(MusicBeanDao.Properties.PlayListFlag.eq(playListBean.getTitle()), MusicBeanDao.Properties.Title.eq(mSongName)).build().list();
                if (beanList.size() > 0) {
                    ToastUtil.songalreadyExist(mActivity);
                } else {
                    insertSongToList(playListBean, mSongName);
                }
            }
            ((OnFinishActivityListener) mContext).finishActivity();
        }
    }

    private void insertSongToList(PlayListBean playListBean, String songName) {
        List<MusicBean> musicBeans = mMusicBeanDao.queryBuilder().where(MusicBeanDao.Properties.Title.eq(songName)).build().list();
        if (musicBeans.size() > 0) {
            MusicBean musicBean = musicBeans.get(0);
            musicBean.setPlayListFlag(playListBean.getTitle());
            musicBean.setAddListTime(System.currentTimeMillis());
            mMusicBeanDao.update(musicBean);
            // 更新列表的歌曲数量
            playListBean.setSongCount(playListBean.getSongCount() + 1);
            mPlayListDao.update(playListBean);
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


    @Override
    protected void deleteItem(int musicPosition) {
        super.deleteItem(musicPosition);
        if (mDetailList != null && mDetailsAdapter != null) {
            mDetailList.remove(musicPosition);
            if (mDetailList.size() == Constants.NUMBER_ZERO) {
                showDetailsView(mPlayListBean.getTitle());
                mPlayListBean.setSongCount(Constants.NUMBER_ZERO);
                mPlayListDao.update(mPlayListBean);
                mAdapter.setNewData(getPlayList());
            } else {
                mDetailsAdapter.setData(mDetailList);
            }
        }
    }

    public static PlayListFragment newInstance(String songName, ArrayList<String> arrayList, boolean formPlayListActivity) {
        isFormPlayListActivity = formPlayListActivity;
        mSongName = songName;
        mArrayList = arrayList;
        return new PlayListFragment();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (SpUtil.getAddToPlayListFdlag(mActivity) == Constants.NUMBER_ZERO) {
            isFormPlayListActivity = false;
        }
        if (mAddDeleteListDisposable != null) {
            mAddDeleteListDisposable.dispose();
            mAddDeleteListDisposable = null;
        }
    }

    @Override
    protected void handleDetailsBack(int detailFlag) {
        if (detailFlag == Constants.NUMBER_EIGHT) {
            if (!isItemSelectStatus) {
                if (!isShowDetailsView) {
                    closeEditStatus();
                }
            } else {
                showDetailsView(getString(R.string.play_list));
            }
        }
    }

    private void closeEditStatus() {
        interceptBackEvent(isItemSelectStatus ? Constants.NUMBER_EIGHT : Constants.NUMBER_ZERO);
        mMusicToolBar.setTvEditText(isItemSelectStatus ? R.string.complete : R.string.tv_edit);
        mMusicToolBar.setTvDeleteVisibility(isItemSelectStatus ? View.VISIBLE : View.GONE);
        mAdapter.setItemSelectStatus(isItemSelectStatus);
        isItemSelectStatus = !isItemSelectStatus;
        if (!isItemSelectStatus && mSelectCount > 0) {
            cancelAllSelected();
        }
        mLlAddNewPlayList.setEnabled(isItemSelectStatus);
    }

}
