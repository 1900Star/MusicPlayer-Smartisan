package com.yibao.music.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yibao.music.R;
import com.yibao.music.adapter.AlbumAdapter;
import com.yibao.music.base.BaseMusicFragment;
import com.yibao.music.model.AlbumInfo;
import com.yibao.music.model.EditBean;
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
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @ Author: Luoshipeng
 * @ Name:   AlbumCategoryFragment
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/9/11/ 23:47
 * @ Des:    TODO
 */
public class AlbumCategoryFragment extends BaseMusicFragment {
    @BindView(R.id.musci_view)
    MusicView mMusicView;
    private int mPosition;
    private List<AlbumInfo> mAlbumList;
    private boolean isItemSelectStatus = true;
    private AlbumAdapter mAlbumAdapter;
    private int mSelectCount;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mPosition = arguments.getInt("position");
        }
        mAlbumList = MusicListUtil.getAlbumList(mSongList);
    }

    @Override
    protected boolean getIsOpenDetail() {
        return !isItemSelectStatus;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.category_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        initData();
        return view;
    }

    private void initData() {
        mAlbumAdapter = new AlbumAdapter(mActivity, mAlbumList, mPosition);
        mMusicView.setAdapter(mActivity, mPosition == 0 ? 3 : 4, mPosition == 0, mAlbumAdapter);
        mAlbumAdapter.setItemListener((bean, isEditStatus) -> {
            if (isEditStatus) {
                mSelectCount = bean.isSelected() ? mSelectCount-- : mSelectCount++;
                bean.setSelected(!bean.isSelected());
                LogUtil.d("=========== album list 选中  " + mSelectCount);
                mAlbumAdapter.notifyDataSetChanged();
            } else {
                mBus.post(bean);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        initRxBusData();
    }

    private void initRxBusData() {
        disposeToolbar();
        if (mEditDisposable == null) {
            mEditDisposable = mBus.toObserverable(EditBean.class).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(editBean -> changeEditStatus(editBean.getCurrentIndex()));
        }

    }

    protected void changeEditStatus(int currentIndex) {
        if (currentIndex == Constants.NUMBER_THRRE) {
            closeEditStatus();
        } else if (currentIndex == Constants.NUMBER_FOUR) {
            // 删除已选择的条目
            List<MusicBean> musicBeanList = mMusicBeanDao.queryBuilder().where(MusicBeanDao.Properties.IsSelected.eq(true)).build().list();
            if (musicBeanList.size() > Constants.NUMBER_ZERO) {
                LogUtil.d("======== Size    " + musicBeanList.size());
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
            mBus.post(Constants.NUMBER_NINE, new EditBean());
            isItemSelectStatus = true;
        }
    }


    // 取消所有已选
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
