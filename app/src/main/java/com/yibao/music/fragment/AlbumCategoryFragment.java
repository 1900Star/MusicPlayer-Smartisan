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
import com.yibao.music.model.greendao.AlbumInfoDao;
import com.yibao.music.util.Constants;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.MusicListUtil;
import com.yibao.music.util.SpUtil;
import com.yibao.music.view.music.MusicView;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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
                if (bean.isSelected()) {
                    mSelectCount--;
                    bean.setSelected(false);
                } else {
                    mSelectCount++;
                    bean.setSelected(true);
                }
                LogUtil.d("=========== album list 选中  " + mSelectCount);
                mAlbumAdapter.notifyDataSetChanged();
            } else {
                mBus.post(bean);
            }
        });
    }

    public static AlbumCategoryFragment newInstance(int position) {
        Bundle args = new Bundle();
        AlbumCategoryFragment fragment = new AlbumCategoryFragment();
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected void handleDetailsBack(int detailFlag) {
        if (detailFlag == Constants.NUMBER_TEN) {
            SpUtil.setDetailsFlag(mContext, Constants.NUMBER_TEN);
            LogUtil.d("======= album category 编辑歌曲状态");
            mAlbumAdapter.setItemSelectStatus(false);
            isItemSelectStatus = !isItemSelectStatus;
        }
        super.handleDetailsBack(detailFlag);
    }

    @Override
    protected void changeEditStatus(int currentIndex) {
        if (currentIndex == Constants.NUMBER_THRRE) {
            LogUtil.d("=======  编辑专辑");
            closeEditStatus();
        }
    }

    private void closeEditStatus() {
        if (isItemSelectStatus) {
            putFragToMap(Constants.NUMBER_TEN, mClassName);
        } else {
            mDetailsViewMap.remove(mClassName);
            mAlbumAdapter.setItemSelectStatus(isItemSelectStatus);
        }
        changeTvEditText(getResources().getString(isItemSelectStatus ? R.string.complete : R.string.tv_edit));
        mAlbumAdapter.setItemSelectStatus(isItemSelectStatus);
        isItemSelectStatus = !isItemSelectStatus;
        if (!isItemSelectStatus && mSelectCount > 0) {
            LogUtil.d("========== status    " + mSelectCount);
            cancelAllSelected();
        }
    }

    // 取消所有已选
    private void cancelAllSelected() {
        List<AlbumInfo> albumInfoList = mAlbumDao.queryBuilder().where(AlbumInfoDao.Properties.Id.eq(true)).build().list();
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
}
