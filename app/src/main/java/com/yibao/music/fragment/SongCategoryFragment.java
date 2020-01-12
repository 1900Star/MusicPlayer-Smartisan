package com.yibao.music.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yibao.music.R;
import com.yibao.music.adapter.SongAdapter;
import com.yibao.music.base.BaseLazyFragment;
import com.yibao.music.fragment.dialogfrag.MoreMenuBottomDialog;
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
import io.reactivex.functions.Consumer;
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

public class SongCategoryFragment extends BaseLazyFragment {

    @BindView(R.id.musci_view)
    MusicView mMusicView;
    private SongAdapter mSongAdapter;
    private int mPosition;
    private boolean isShowSlidBar = false;
    private boolean isItemSelectStatus = true;
    private int mSelectCount;
    private static final String MUSIC_POSITION = "position";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mPosition = arguments.getInt(MUSIC_POSITION);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPosition != Constants.NUMBER_ZERO && getUserVisibleHint()) {
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
            mEditDisposable = mBus.toObservableType(Constants.SONG_FAG_EDIT, Object.class).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(o -> SongCategoryFragment.this.changeEditStatus((Integer) o));
        }
//        mCompositeDisposable.add();
        mBus.toObservableType(Constants.DELETE_SONG, Object.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(integer -> {
                    int position = (int) integer;
                    LogUtil.d(TAG, "收到删除    " + position);
                    mSongAdapter.deleteSong(position);
                });
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

    @Override
    protected void initView(Bundle savedInstanceState) {

    }

    private void initListener() {
        mSongAdapter.setOnItemMenuListener((int position, MusicBean musicBean) ->
                MoreMenuBottomDialog.newInstance(musicBean, position, false, false).getBottomDialog(mActivity));
        mSongAdapter.setItemListener((bean, position, isEditStatus) -> {
            if (isEditStatus) {
                mSelectCount = bean.isSelected() ? mSelectCount-- : mSelectCount++;
                bean.setIsSelected(!bean.isSelected());
                mMusicBeanDao.update(bean);
                mSongAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initData() {
        switch (mPosition) {
            case 0:
                List<MusicBean> abcList = MusicListUtil.sortMusicAbc(mSongList);
                isShowSlidBar = true;
                mSongAdapter = new SongAdapter(mActivity, abcList, Constants.NUMBER_ZERO, Constants.NUMBER_ZERO);
                break;
            case 1:
                List<MusicBean> scoreList = MusicListUtil.sortMusicList(mSongList, Constants.SORT_SCORE);
                mSongAdapter = new SongAdapter(mActivity, scoreList, Constants.NUMBER_ONE, Constants.NUMBER_ONE);
                break;
            case 2:
                List<MusicBean> playFrequencyList = MusicListUtil.sortMusicList(mSongList, Constants.SORT_FREQUENCY);
                mSongAdapter = new SongAdapter(mActivity, playFrequencyList, Constants.NUMBER_ONE, Constants.NUMBER_TWO);
                break;
            case 3:
                List<MusicBean> addTimeList = MusicListUtil.sortMusicList(mSongList, Constants.SORT_DOWN_TIME);
                mSongAdapter = new SongAdapter(mActivity, addTimeList, Constants.NUMBER_ONE, Constants.NUMBER_ZERO);
                break;
            default:
                break;
        }
        mMusicView.setAdapter(mActivity, Constants.NUMBER_ONE, isShowSlidBar, mSongAdapter);
    }

    private void changeEditStatus(int currentIndex) {
        if (currentIndex == Constants.NUMBER_ONE) {
            closeEditStatus();
        } else if (currentIndex == Constants.NUMBER_TWO) {
            // 删除已选择的条目
            List<MusicBean> musicBeanList = mMusicBeanDao.queryBuilder().where(MusicBeanDao.Properties.IsSelected.eq(true)).build().list();
            if (musicBeanList.size() > Constants.NUMBER_ZERO) {
                LogUtil.d(TAG, "======== Size    " + musicBeanList.size());
                for (MusicBean musicBean : musicBeanList) {
                    mMusicBeanDao.delete(musicBean);
                }
                mSongAdapter.setItemSelectStatus(false);
                mSongAdapter.setNewData(getSongList());
                mBus.post(Constants.FRAGMENT_SONG, Constants.NUMBER_ZERO);
            } else {
                SnakbarUtil.favoriteSuccessView(mMusicView, "没有选中条目");
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
            mBus.post(Constants.FRAGMENT_SONG, Constants.NUMBER_ZERO);
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

    public static SongCategoryFragment newInstance(int position) {
        Bundle args = new Bundle();
        SongCategoryFragment fragment = new SongCategoryFragment();
        args.putInt(MUSIC_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

}
