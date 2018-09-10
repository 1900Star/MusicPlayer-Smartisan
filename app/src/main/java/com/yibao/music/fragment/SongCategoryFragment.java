package com.yibao.music.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yibao.music.R;
import com.yibao.music.adapter.SongAdapter;
import com.yibao.music.base.BaseFragment;
import com.yibao.music.fragment.dialogfrag.MusicBottomSheetDialog;
import com.yibao.music.model.MusicBean;
import com.yibao.music.util.ColorUtil;
import com.yibao.music.util.Constants;
import com.yibao.music.util.MusicListUtil;
import com.yibao.music.util.SharePrefrencesUtil;
import com.yibao.music.view.music.MusicView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.artisanlist
 * @文件名: SongFragment
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mPosition = arguments.getInt("position");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.song_category_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        initData();
        initListener();
        return view;
    }

    private void initListener() {
        mSongAdapter.setOnItemMenuListener(() -> MusicBottomSheetDialog.newInstance().getBottomDialog(getActivity()));
    }

    private void initData() {
        switch (mPosition) {
            case 0:
            case 2:
                isShowSlidebar = true;
                mSongAdapter = new SongAdapter(mActivity, MusicListUtil.sortMusicAbc(mSongList), Constants.NUMBER_ZOER);
                break;
            case 1:
            case 3:
                isShowSlidebar = false;
                List<MusicBean> musicAddtimeList = MusicListUtil.sortMusicAddTime(mMusicBeanDao.queryBuilder().list(), Constants.NUMBER_ONE);
                mSongAdapter = new SongAdapter(mActivity, musicAddtimeList, Constants.NUMBER_ONE);
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
