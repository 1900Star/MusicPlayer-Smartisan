package com.yibao.music.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yibao.music.R;
import com.yibao.music.adapter.AlbumAdapter;
import com.yibao.music.adapter.AlbumCategoryPagerAdapter;
import com.yibao.music.base.BaseFragment;
import com.yibao.music.base.BaseRvAdapter;
import com.yibao.music.model.AlbumInfo;
import com.yibao.music.util.Constants;
import com.yibao.music.util.MusicListUtil;
import com.yibao.music.view.music.MusicView;

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
public class AlbumCategoryFragment extends BaseFragment {
    @BindView(R.id.musci_view)
    MusicView mMusicView;
    private int mPosition;
    private List<AlbumInfo> mAlbumList;

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
        AlbumAdapter adapter = new AlbumAdapter(mActivity, mAlbumList, mPosition);
        mMusicView.setAdapter(mActivity, mPosition == 0 ? 3 : 4, mPosition==0, adapter);
        adapter.setItemListener(new BaseRvAdapter.OnItemListener<AlbumInfo>() {
            @Override
            public void showDetailsView(AlbumInfo bean, boolean isEditStatus) {
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


}
