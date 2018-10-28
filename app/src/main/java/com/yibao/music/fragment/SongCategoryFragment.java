package com.yibao.music.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yibao.music.R;
import com.yibao.music.adapter.SongAdapter;
import com.yibao.music.base.BaseFragment;
import com.yibao.music.fragment.dialogfrag.FavoriteBottomSheetDialog;
import com.yibao.music.model.MusicBean;
import com.yibao.music.util.Constants;
import com.yibao.music.util.MusicListUtil;
import com.yibao.music.util.SpUtil;
import com.yibao.music.view.music.MusicView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.artisanlist
 * @文件名: SongMusicFragment
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/4 21:45
 * @描述： {显示当前音乐列表}
 */

public class SongCategoryFragment extends BaseFragment {

    @BindView(R.id.musci_view)
    MusicView mMusciView;
    private SongAdapter mSongAdapter;
    private int mPosition;
    private boolean isShowSlidebar;
    private List<MusicBean> mAbcList;
    private List<MusicBean> mAddTimeList;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mPosition = arguments.getInt("position");
        }
        mAbcList = MusicListUtil.sortMusicAbc(mMusicBeanDao.queryBuilder().list());
        mAddTimeList = MusicListUtil.sortMusicAddTime(mMusicBeanDao.queryBuilder().list(), Constants.NUMBER_ONE);

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
        mSongAdapter.setOnItemMenuListener(() -> FavoriteBottomSheetDialog.newInstance().getBottomDialog(getActivity()));
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


    public static SongCategoryFragment newInstance(int position) {
        Bundle args = new Bundle();
        SongCategoryFragment fragment = new SongCategoryFragment();
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

}
