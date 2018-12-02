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
import com.yibao.music.base.listener.UpdataTitleListener;
import com.yibao.music.fragment.dialogfrag.MoreMenuBottomDialog;
import com.yibao.music.model.EditBean;
import com.yibao.music.model.MoreMenuStatus;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.greendao.MusicBeanDao;
import com.yibao.music.util.Constants;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.MusicListUtil;
import com.yibao.music.util.SpUtil;
import com.yibao.music.view.music.MusicView;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.artisanlist
 * @文件名: SongFragment
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/4 21:45
 * @描述： {显示当前音乐列表}
 */

public class SongCategoryFragment extends BaseMusicFragment {

    @BindView(R.id.musci_view)
    MusicView mMusciView;
    private SongAdapter mSongAdapter;
    private int mPosition;
    private boolean isShowSlidebar;
    private List<MusicBean> mAbcList;
    private List<MusicBean> mAddTimeList;
    private boolean isItemSelectStatus = true;
    private int mSelectCount;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mPosition = arguments.getInt("position");
        }
        mAbcList = MusicListUtil.sortMusicAbc(mMusicBeanDao.queryBuilder().list());
        mAddTimeList = MusicListUtil.sortTime(mMusicBeanDao.queryBuilder().list(), Constants.NUMBER_ONE);
        SpUtil.setDetailsFlag(mContext, Constants.NUMBER_ELEVEN);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPosition == 1 || mPosition == 3) {
            int newMusicFlag = SpUtil.getNewMusicFlag(mActivity);
            if (newMusicFlag == 1) {
                initData();
                SpUtil.setNewMusicFlag(mActivity, 0);
            }
        }

    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.category_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        initData();
        initListener();
        return view;
    }

    private void initListener() {
        mSongAdapter.setOnItemMenuListener((int position, MusicBean musicBean) -> MoreMenuBottomDialog.newInstance(musicBean).getBottomDialog(mActivity));
        mSongAdapter.setItemListener((bean, isEditStatus) -> {
            if (isEditStatus) {
                if (bean.isSelected()) {
                    mSelectCount--;
                    bean.setSelected(false);
                    mMusicBeanDao.update(bean);
                } else {
                    mSelectCount++;
                    bean.setSelected(true);
                    mMusicBeanDao.update(bean);
                }
                LogUtil.d("===========选中  " + mSelectCount);
                mSongAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initData() {
        switch (mPosition) {
            case 0:
            case 2:
                isShowSlidebar = true;
                mSongAdapter = new SongAdapter(mActivity, mAbcList, Constants.NUMBER_ZOER);
                break;
            case 1:
            case 3:
                isShowSlidebar = false;
                mSongAdapter = new SongAdapter(mActivity, mAddTimeList, Constants.NUMBER_ONE);
                break;
            default:
                break;
        }
        mMusciView.setAdapter(mActivity, Constants.NUMBER_ONE, isShowSlidebar, mSongAdapter);

    }

    @Override
    protected void changeEditStatus(int currentIndex) {
        if (currentIndex == Constants.NUMBER_TWO) {
            closeEditStatus();
        } else if (currentIndex == 12) {
            // 删除已选择的条目
            List<MusicBean> musicBeanList = mMusicBeanDao.queryBuilder().where(MusicBeanDao.Properties.IsSelected.eq(true)).build().list();
            for (MusicBean musicBean : musicBeanList) {
                mMusicBeanDao.delete(musicBean);
            }
            mSongAdapter.setItemSelectStatus(false);
            mSongAdapter.setNewData(getSongList());
            changeTvEditVisibility();
        }
    }


    private void closeEditStatus() {
        if (isItemSelectStatus) {
            putFragToMap(Constants.NUMBER_ELEVEN, mClassName);
        } else {
            mDetailsViewMap.remove(mClassName);
            mSongAdapter.setItemSelectStatus(isItemSelectStatus);
        }
        changeTvEditText(getResources().getString(isItemSelectStatus ? R.string.complete : R.string.tv_edit));
        mSongAdapter.setItemSelectStatus(isItemSelectStatus);
        isItemSelectStatus = !isItemSelectStatus;
        if (!isItemSelectStatus && mSelectCount > 0) {
            cancelAllSelected();
        }
    }

    private void changeTvEditVisibility() {
        if (mContext instanceof UpdataTitleListener) {
            ((UpdataTitleListener) mContext).updataTitle(getResources().getString(R.string.tv_edit), false);
        }

    }

    @Override
    protected void handleDetailsBack(int detailFlag) {
        if (detailFlag == 11) {
            SpUtil.setDetailsFlag(mContext, Constants.NUMBER_ELEVEN);
            mSongAdapter.setItemSelectStatus(false);
            isItemSelectStatus = !isItemSelectStatus;
        }
        super.handleDetailsBack(detailFlag);
    }

    // 取消所有已选
    private void cancelAllSelected() {
        List<MusicBean> musicBeanList = mMusicBeanDao.queryBuilder().where(MusicBeanDao.Properties.IsSelected.eq(true)).build().list();
        Collections.sort(musicBeanList);
        for (MusicBean musicBean : musicBeanList) {
            mMusicBeanDao.delete(musicBean);
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
