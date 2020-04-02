package com.yibao.music.fragment;

import android.os.Bundle;
import android.util.SparseBooleanArray;
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
import com.yibao.music.util.Constants;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.MusicListUtil;
import com.yibao.music.util.SnakbarUtil;
import com.yibao.music.view.music.MusicView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
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
    private static final String MUSIC_POSITION = "position";
    private SparseBooleanArray mSparseBooleanArray = new SparseBooleanArray();
    private List<MusicBean> mSelectList = new ArrayList<>();
    private Disposable mDeleteSongDisposable;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mPosition = arguments.getInt(MUSIC_POSITION);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.category_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        initData();
        LogUtil.d(TAG, " song size   " + mSongList.size());
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
                mSparseBooleanArray.put(position, true);
                updateSelected(bean);
                mSongAdapter.notifyDataSetChanged();
            }
        });
        mSongAdapter.setCheckBoxClickListener((bean, isChecked, position) -> {
            LogUtil.d(TAG, bean.getTitle() + " == " + isChecked);
            mSparseBooleanArray.put(position, isChecked);
            updateSelected(bean);
            mSongAdapter.notifyDataSetChanged();
        });
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
        if (mDeleteSongDisposable == null) {
            mDeleteSongDisposable = mBus.toObservableType(Constants.DELETE_SONG, Object.class)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(integer -> {
                        int position = (int) integer;
                        LogUtil.d(TAG, "收到删除    " + position);
                        mSongAdapter.deleteSong(position);
                    });

        }
    }

    @Override
    protected void deleteItem(int musicPosition) {
        super.deleteItem(musicPosition);
        mSongAdapter.notifyItemRemoved(musicPosition);
    }


    private void updateSelected(MusicBean bean) {
        if (mSelectList.contains(bean)) {
            mSelectList.remove(bean);
        } else {
            mSelectList.add(bean);
        }
    }

    private void initData() {
        switch (mPosition) {
            case 0:
                List<MusicBean> abcList = MusicListUtil.sortMusicAbc(mSongList);
                setNotAllSelected(abcList);
                isShowSlidBar = true;
                mSongAdapter = new SongAdapter(mActivity, abcList, mSparseBooleanArray, Constants.NUMBER_ZERO, Constants.NUMBER_ZERO);
                break;
            case 1:
                List<MusicBean> scoreList = MusicListUtil.sortMusicList(mSongList, Constants.SORT_SCORE);
                setNotAllSelected(scoreList);
                mSongAdapter = new SongAdapter(mActivity, scoreList, mSparseBooleanArray, Constants.NUMBER_ONE, Constants.NUMBER_ONE);
                break;
            case 2:
                List<MusicBean> playFrequencyList = MusicListUtil.sortMusicList(mSongList, Constants.SORT_FREQUENCY);
                setNotAllSelected(playFrequencyList);
                mSongAdapter = new SongAdapter(mActivity, playFrequencyList, mSparseBooleanArray, Constants.NUMBER_ONE, Constants.NUMBER_TWO);
                break;
            case 3:
                List<MusicBean> addTimeList = MusicListUtil.sortMusicList(mSongList, Constants.SORT_DOWN_TIME);
                setNotAllSelected(addTimeList);
                mSongAdapter = new SongAdapter(mActivity, addTimeList, mSparseBooleanArray, Constants.NUMBER_ONE, Constants.NUMBER_ZERO);
                break;
            default:
                break;
        }
        mMusicView.setAdapter(mActivity, Constants.NUMBER_ONE, isShowSlidBar, mSongAdapter);
    }

    private void setNotAllSelected(List<MusicBean> listBeanList) {
        for (int i = 0; i < listBeanList.size(); i++) {
            mSparseBooleanArray.put(i, false);
        }
    }

    private void changeEditStatus(int currentIndex) {
        if (currentIndex == Constants.NUMBER_ONE) {
            closeEditStatus();
        } else if (currentIndex == Constants.NUMBER_TWO) {
            // 删除已选择的条目
            deleteListItem();
        }
    }

    private void deleteListItem() {
        LogUtil.d(TAG, "Size " + mSelectList.size());
        if (mSelectList.size() > Constants.NUMBER_ZERO) {
            for (MusicBean musicBean : mSelectList) {
                LogUtil.d(TAG, musicBean.getTitle());
//                FileUtil.deleteFile(new File(musicBean.getSongUrl()));
//                mMusicBeanDao.delete(musicBean);
            }
            mSongAdapter.setItemSelectStatus(false);
            mSongAdapter.setNewData(getSongList());
//            mBus.post(Constants.FRAGMENT_SONG, Constants.NUMBER_ZERO);
        } else {
            SnakbarUtil.favoriteSuccessView(mMusicView, "没有选中条目");
        }
    }

    private List<MusicBean> getSongList() {

        List<MusicBean> musicBeans = mMusicBeanDao.queryBuilder().list();
        return MusicListUtil.sortMusicAbc(musicBeans);
    }


    private void closeEditStatus() {
        interceptBackEvent(isItemSelectStatus ? Constants.NUMBER_ELEVEN : Constants.NUMBER_ZERO);
        mSongAdapter.setItemSelectStatus(isItemSelectStatus);
        isItemSelectStatus = !isItemSelectStatus;
        mSparseBooleanArray.clear();
        if (mSelectList.size() > 0) {
            mSelectList.clear();
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

    @Override
    public void onPause() {
        super.onPause();
        if (mDeleteSongDisposable != null) {
            mDeleteSongDisposable.dispose();
            mDeleteSongDisposable = null;

        }
    }

    public static SongCategoryFragment newInstance(int position) {
        Bundle args = new Bundle();
        SongCategoryFragment fragment = new SongCategoryFragment();
        args.putInt(MUSIC_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

}
