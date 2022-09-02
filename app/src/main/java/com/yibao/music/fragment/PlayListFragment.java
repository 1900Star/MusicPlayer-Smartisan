package com.yibao.music.fragment;

import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.View;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.yibao.music.MusicApplication;
import com.yibao.music.R;
import com.yibao.music.activity.PlayListActivity;
import com.yibao.music.adapter.DetailsViewAdapter;
import com.yibao.music.adapter.PlayListAdapter;
import com.yibao.music.base.bindings.BaseMusicFragmentDev;
import com.yibao.music.base.factory.RecyclerFactory;
import com.yibao.music.base.listener.OnFinishActivityListener;
import com.yibao.music.databinding.PlayListFragmentBinding;
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
import com.yibao.music.util.ThreadPoolProxyFactory;
import com.yibao.music.util.ToastUtil;
import com.yibao.music.view.music.MusicToolBar;
import com.yibao.music.viewmodel.PlayListViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

public class PlayListFragment extends BaseMusicFragmentDev<PlayListFragmentBinding> implements SwipeRefreshLayout.OnRefreshListener {

    private PlayListAdapter mAdapter;
    private boolean isShowDetailsView = false;
    private int mDeletePosition;

    private int mEditPosition;
    private static String mSongName;
    private List<MusicBean> mDetailList;
    private DetailsViewAdapter mDetailsAdapter;
    private PlayListBean mPlayListBean;
    private Disposable mAddDeleteListDisposable;
    private static ArrayList<String> mArrayLisOpenDetail;
    private String mTempTitle;
    private static boolean isFromPlayListActivity;

    private PlayListBeanDao mPlayListDao;
    private PlayListViewModel mViewModel;

    @Override
    public void initView() {
        initRecyclerView(getMBinding().recyclerPlayList);
        getMBinding().swipePlayList.setColorSchemeColors(Color.BLUE, Color.RED, Color.YELLOW);
        getMBinding().swipePlayList.setOnRefreshListener(this);

        getMBinding().musicBar.musicToolbarList.setToolbarTitle(getString(R.string.play_list));
        getMBinding().musicBar.musicToolbarList.setVisibility(isFromPlayListActivity && SpUtil.getAddToPlayListFdlag(mContext) == Constants.NUMBER_ONE ? View.GONE : View.VISIBLE);
        mPlayListDao = MusicApplication.getInstance().getPlayListDao();
        mViewModel = new PlayListViewModel();
        mViewModel.getPlayList();

    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.getListModel().observe(this, playList -> {
            mAdapter = new PlayListAdapter(playList);
            getMBinding().recyclerPlayList.setAdapter(mAdapter);
            initListener();
            getMBinding().swipePlayList.setRefreshing(false);
        });
    }

    @Override
    public void initData() {

        mDetailsAdapter = new DetailsViewAdapter(mContext, new ArrayList<>(), Constants.NUMBER_FOUR);

    }




    private List<PlayListBean> getPlayList() {
        List<PlayListBean> playListBeans = mPlayListDao.queryBuilder().list();
        for (PlayListBean playListBean : playListBeans) {
            LogUtil.d(getMTag(), playListBean.getTitle());

        }
        Collections.sort(playListBeans);

        return playListBeans;
    }

    private void initListener() {


        getMBinding().llAddNewPlayList.setOnClickListener(v ->
                AddListDialog.newInstance(1, Constants.NULL_STRING, isFromPlayListActivity, this).show(getChildFragmentManager(), "addList"));
        // item 点击
        mAdapter.setItemListener((playListBean, position, isEditStatus) -> {
            mTempTitle = playListBean.getTitle();
            // 从PlayListActivity过来的
            if (isFromPlayListActivity) {
                addToList(playListBean);
            } else {
                if (!isEditStatus) {
                    PlayListFragment.this.showDetailsView(mTempTitle);
                }
            }
        });
        // 长按删除
        mAdapter.setItemLongClickListener((musicInfo, currentPosition) -> {
            if (!isFromPlayListActivity) {
                mDeletePosition = currentPosition;
                DeletePlayListDialog.newInstance(musicInfo, Constants.NUMBER_TWO).show(getChildFragmentManager(), "deleteList");
            }
        });
        // 编辑按钮
        mAdapter.setItemEditClickListener(currentPosition -> {
            mEditPosition = currentPosition;
            if (getPlayList().size() > 0) {
                String currentTitle = getPlayList().get(currentPosition).getTitle();
                AddListDialog.newInstance(2, currentTitle, false, this).show(getChildFragmentManager(), "addList");
            }
        });

    }





    private void showDetailsView(String title) {
        getMBinding().musicBar.musicToolbarList.setToolbarTitle(title);
        getMBinding().llAddNewPlayList.setVisibility(isShowDetailsView ? View.VISIBLE : View.GONE);
        getMBinding().playListDetailView.setVisibility(isShowDetailsView ? View.GONE : View.VISIBLE);
        if (isShowDetailsView) {
            mAdapter.setNewData(getPlayList());
        } else {
            mDetailList = mMusicBeanDao.queryBuilder().where(MusicBeanDao.Properties.PlayListFlag.eq(title)).build().list();
            getMBinding().playListDetailView.setQureyFlag(title, mDetailList.size());
            mDetailsAdapter.setNewData(mDetailList);
            RecyclerView recyclerView = RecyclerFactory.createRecyclerView(Constants.NUMBER_ONE, mDetailsAdapter);
            mDetailsAdapter.setOnItemMenuListener((position, musicBean) -> MoreMenuBottomDialog.newInstance(musicBean, position, false, false).getBottomDialog(mContext));
            getMBinding().playListDetailView.setAdapter(recyclerView);
            interceptBackEvent(Constants.NUMBER_EIGHT);
        }
        isShowDetailsView = !isShowDetailsView;
        getMBinding().musicBar.musicToolbarList.setTvEditText(isShowDetailsView ? R.string.back : R.string.tv_edit);
    }

    /**
     * @param playListBean 通过PlayListActivity将选中的歌曲添加到列表中，有批量添加和单曲添加，
     */
    private void addToList(PlayListBean playListBean) {
        if (mContext instanceof PlayListActivity) {
            // 批量添加
            if (mArrayLisOpenDetail != null && mArrayLisOpenDetail.size() > 0) {
                ThreadPoolProxyFactory.newInstance().execute(() -> {
                    for (String songTitle : mArrayLisOpenDetail) {
                        List<MusicBean> musicBeanList = mMusicBeanDao.queryBuilder().where(MusicBeanDao.Properties.PlayListFlag.eq(playListBean.getTitle()), MusicBeanDao.Properties.Title.eq(songTitle)).build().list();
                        if (musicBeanList.size() == 0) {
                            insertSongToList(playListBean, songTitle);
                        }
                    }
                });
            } else {
                // 单曲添加
                List<MusicBean> beanList = mMusicBeanDao.queryBuilder().where(MusicBeanDao.Properties.PlayListFlag.eq(playListBean.getTitle()), MusicBeanDao.Properties.Title.eq(mSongName)).build().list();
                if (beanList.size() > 0) {
                    ToastUtil.songalreadyExist(mContext);
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
        isFromPlayListActivity = formPlayListActivity;
        mSongName = songName;
        mArrayLisOpenDetail = arrayList;
        return new PlayListFragment();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (SpUtil.getAddToPlayListFdlag(mContext) == Constants.NUMBER_ZERO) {
            isFromPlayListActivity = false;
        }
        if (mAddDeleteListDisposable != null) {
            mAddDeleteListDisposable.dispose();
            mAddDeleteListDisposable = null;
        }
    }

    @Override
    protected void handleDetailsBack(int detailFlag) {
        if (detailFlag == Constants.NUMBER_EIGHT) {

            LogUtil.d(getMTag(), "播放列表  AAAAA ");

            showDetailsView(getString(R.string.play_list));
        }
    }



    @Override
    protected boolean isOpenDetail() {
        return isShowDetailsView;
    }

    @Override
    public void onRefresh() {
        LogUtil.d(getMTag(), "添加成功");
        mViewModel.getPlayList();

    }
}
