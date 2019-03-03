package com.yibao.music.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yibao.music.R;
import com.yibao.music.adapter.SongAdapter;
import com.yibao.music.base.BaseMusicFragment;
import com.yibao.music.fragment.dialogfrag.MoreMenuBottomDialog;
import com.yibao.music.model.EditBean;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.greendao.MusicBeanDao;
import com.yibao.music.util.Constants;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.MusicListUtil;
import com.yibao.music.util.SnakbarUtil;
import com.yibao.music.view.music.MusicView;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.artisanlist
 * @文件名: SongFragment
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/4 21:45
 * @描述： {显示音乐分类列表}
 */

public class SongCategoryFragment extends BaseMusicFragment {

    @BindView(R.id.musci_view)
    MusicView mMusciView;
    private SongAdapter mSongAdapter;
    private int mPosition;
    private boolean isShowSlidebar = false;
    private List<MusicBean> mAbcList;
    private List<MusicBean> mAddTimeList;
    private boolean isItemSelectStatus = true;
    private int mSelectCount;
    private List<MusicBean> mPlayFrequencyList;
    private List<MusicBean> mScoreList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mPosition = arguments.getInt("position");
        }
        mAbcList = MusicListUtil.sortMusicAbc(mMusicBeanDao.queryBuilder().list());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPosition != Constants.NUMBER_ZERO) {
            mPlayFrequencyList = MusicListUtil.sortMusicList(mMusicBeanDao.queryBuilder().list(), Constants.NUMBER_THRRE);
            mAddTimeList = MusicListUtil.sortMusicList(mMusicBeanDao.queryBuilder().list(), Constants.NUMBER_ONE);
            mScoreList = MusicListUtil.sortMusicList(mMusicBeanDao.queryBuilder().list(), Constants.NUMBER_FOUR);
            // 新增歌曲刷新列表
            initData();
        }
        initListener();
        initRxBusData();
    }

    @Override
    protected boolean getIsOpenDetail() {
        return isItemSelectStatus;
    }

    private void initRxBusData() {
        disposeToolbar();
        if (mEditDisposable == null) {
            mEditDisposable = mBus.toObserverable(EditBean.class).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(editBean -> changeEditStatus(editBean.getCurrentIndex()));
        }

    }

    @Override
    protected void deleteItem(int musicPosition) {
        super.deleteItem(musicPosition);
        mSongAdapter.notifyItemRemoved(musicPosition);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.category_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        initData();
        return view;
    }

    private void initListener() {
        mSongAdapter.setOnItemMenuListener((int position, MusicBean musicBean) ->
                MoreMenuBottomDialog.newInstance(musicBean, position, false, false).getBottomDialog(mActivity));
        mSongAdapter.setItemListener((bean, isEditStatus) -> {
            if (isEditStatus) {
                mSelectCount = bean.isSelected() ? mSelectCount-- : mSelectCount++;
                bean.setIsSelected(!bean.isSelected());
                mMusicBeanDao.update(bean);
                mSongAdapter.notifyDataSetChanged();
                LogUtil.d("===========选中  " + mSelectCount);
            }
        });
    }

    private void initData() {
        switch (mPosition) {
            case 0:
                isShowSlidebar = true;
                mSongAdapter = new SongAdapter(mActivity, mAbcList, Constants.NUMBER_ZERO, Constants.NUMBER_ZERO);
                break;
            case 1:
                mSongAdapter = new SongAdapter(mActivity, mScoreList, Constants.NUMBER_ONE, Constants.NUMBER_ONE);
                break;
            case 2:
                mSongAdapter = new SongAdapter(mActivity, mPlayFrequencyList, Constants.NUMBER_ONE, Constants.NUMBER_TWO);
                break;
            case 3:
                mSongAdapter = new SongAdapter(mActivity, mAddTimeList, Constants.NUMBER_ONE, Constants.NUMBER_ZERO);
                break;
            default:
                break;
        }
        mMusciView.setAdapter(mActivity, Constants.NUMBER_ONE, isShowSlidebar, mSongAdapter);
    }

    protected void changeEditStatus(int currentIndex) {
        if (currentIndex == Constants.NUMBER_ONE) {
            closeEditStatus();
        } else if (currentIndex == Constants.NUMBER_TWO) {
            // 删除已选择的条目
            List<MusicBean> musicBeanList = mMusicBeanDao.queryBuilder().where(MusicBeanDao.Properties.IsSelected.eq(true)).build().list();
            if (musicBeanList.size() > Constants.NUMBER_ZERO) {
                LogUtil.d("======== Size    " + musicBeanList.size());
                for (MusicBean musicBean : musicBeanList) {
                    mMusicBeanDao.delete(musicBean);
                }
                mSongAdapter.setItemSelectStatus(false);
                mSongAdapter.setNewData(getSongList());
                mBus.post(Constants.NUMBER_TEN, new EditBean());
            } else {
                SnakbarUtil.favoriteSuccessView(mMusciView, "没有选中条目");
            }
        }
    }


    private void closeEditStatus() {
        interceptBackEvent(isItemSelectStatus ? Constants.NUMBER_ELEVEN : Constants.NUMBER_ZERO);
        mSongAdapter.setItemSelectStatus(isItemSelectStatus);
        isItemSelectStatus = !isItemSelectStatus;
        if (!isItemSelectStatus && mSelectCount > 0) {
            cancelAllSelected();
        }
    }

    @Override
    protected void handleDetailsBack(int detailFlag) {
        if (detailFlag == Constants.NUMBER_ELEVEN) {
            mSongAdapter.setItemSelectStatus(false);
            mBus.post(Constants.NUMBER_TEN, new EditBean());
            isItemSelectStatus = !isItemSelectStatus;
        }
    }

    /**
     * 取消所有已选
     */
    private void cancelAllSelected() {
        List<MusicBean> musicBeanList = mMusicBeanDao.queryBuilder().where(MusicBeanDao.Properties.IsSelected.eq(true)).build().list();
        Collections.sort(musicBeanList);
        for (MusicBean musicBean : musicBeanList) {
            musicBean.setSelected(false);
            mMusicBeanDao.update(musicBean);
        }
        mSelectCount = 0;
        mSongAdapter.setNewData(getSongList());
    }

    private List<MusicBean> getSongList() {
        List<MusicBean> musicBeanList = mMusicBeanDao.queryBuilder().list();
        return MusicListUtil.sortMusicAbc(musicBeanList);
    }

    public static SongFragment newInstance() {

        return new SongFragment();
    }

    public static SongCategoryFragment newInstance(int position) {
        Bundle args = new Bundle();
        SongCategoryFragment fragment = new SongCategoryFragment();
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

}
