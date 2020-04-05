package com.yibao.music.fragment;

import android.os.Bundle;
import android.view.View;

import com.yibao.music.R;
import com.yibao.music.adapter.AlbumAdapter;
import com.yibao.music.base.BaseLazyFragment;
import com.yibao.music.model.AlbumInfo;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.greendao.AlbumInfoDao;
import com.yibao.music.model.greendao.MusicBeanDao;
import com.yibao.music.util.Constants;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.MusicListUtil;
import com.yibao.music.util.SnakbarUtil;
import com.yibao.music.view.music.MusicView;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Luoshipeng
 * @ author: Luoshipeng
 * @ Name:   AlbumCategoryFragment
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/9/11/ 23:47
 * @ Des:    TODO
 */
public class AlbumCategoryFragment extends BaseLazyFragment {

    @BindView(R.id.musci_view)
    MusicView mMusicView;
    private int mPosition;
    private List<AlbumInfo> mAlbumList;
    private boolean isItemSelectStatus = true;
    private AlbumAdapter mAlbumAdapter;
    private int mSelectCount;

    @Override
    protected boolean getIsOpenDetail() {
        return !isItemSelectStatus;
    }

    @Override
    protected void initView(View view) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mPosition = arguments.getInt("position");
        }
        List<MusicBean> musicBeanList = mMusicBeanDao.queryBuilder().list();
        mAlbumList = MusicListUtil.getAlbumList(musicBeanList);
        initData();
    }

    protected void initData() {
        mAlbumAdapter = new AlbumAdapter(mActivity, mAlbumList, mPosition);
        mMusicView.setAdapter(mActivity, mPosition == 0 ? 3 : 4, mPosition == 0, mAlbumAdapter);
        mAlbumAdapter.setItemListener((bean, position, isEditStatus) -> {
            if (isEditStatus) {
                mSelectCount = bean.isSelected() ? mSelectCount-- : mSelectCount++;
                bean.setSelected(!bean.isSelected());
                LogUtil.d(TAG, "=========== album list 选中  " + mSelectCount);
                mAlbumAdapter.notifyDataSetChanged();
            } else {
                mBus.post(bean);
            }
        });
    }


    @Override
    protected int getContentViewId() {
        return R.layout.category_fragment;
    }

    protected void initRxBusData() {
        disposeToolbar();
        if (mEditDisposable == null) {
            mEditDisposable = mBus.toObservableType(Constants.ALBUM_FAG_EDIT, Object.class).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(o -> AlbumCategoryFragment.this.changeEditStatus((Integer) o));
        }

    }

    private void changeEditStatus(int currentIndex) {
        if (currentIndex == Constants.NUMBER_THREE) {
            closeEditStatus();
        } else if (currentIndex == Constants.NUMBER_FOUR) {
            // 删除已选择的条目
            List<MusicBean> musicBeanList = mMusicBeanDao.queryBuilder().where(MusicBeanDao.Properties.IsSelected.eq(true)).build().list();
            if (musicBeanList.size() > Constants.NUMBER_ZERO) {
                LogUtil.d(TAG, "======== Size    " + musicBeanList.size());
                for (MusicBean musicBean : musicBeanList) {
                    mMusicBeanDao.delete(musicBean);
                }
                mAlbumAdapter.setItemSelectStatus(false);
                mAlbumAdapter.setNewData(getAlbumList());
                interceptBackEvent(Constants.NUMBER_TEN);
            } else {
                SnakbarUtil.favoriteSuccessView(mMusicView, "没有选中条目");
            }
        }
    }

    private void closeEditStatus() {
        mAlbumAdapter.setItemSelectStatus(isItemSelectStatus);
        isItemSelectStatus = !isItemSelectStatus;
        interceptBackEvent(Constants.NUMBER_TEN);
        if (!isItemSelectStatus && mSelectCount > 0) {
            cancelAllSelected();
        }
    }

    @Override
    protected void handleDetailsBack(int detailFlag) {
        if (detailFlag == Constants.NUMBER_TEN) {
            mAlbumAdapter.setItemSelectStatus(false);
            mBus.post(Constants.FRAGMENT_ALBUM, Constants.NUMBER_ZERO);
            isItemSelectStatus = true;
        }
    }


    /**
     * 取消所有已选
     */
    private void cancelAllSelected() {
        List<AlbumInfo> albumInfoList = mAlbumDao.queryBuilder().where(AlbumInfoDao.Properties.MSelected.eq(true)).build().list();
        Collections.sort(albumInfoList);
        for (AlbumInfo albumInfo : albumInfoList) {
            mAlbumDao.delete(albumInfo);
        }
        mSelectCount = 0;
        mAlbumAdapter.setNewData(getAlbumList());
    }

    private List<AlbumInfo> getAlbumList() {
        return mAlbumDao.queryBuilder().list();

    }

    public static AlbumCategoryFragment newInstance(int position) {
        Bundle args = new Bundle();
        AlbumCategoryFragment fragment = new AlbumCategoryFragment();
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

}
