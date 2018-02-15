package com.yibao.music.playlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.yibao.music.MyApplication;
import com.yibao.music.R;
import com.yibao.music.activity.DetailsActivity;
import com.yibao.music.base.BaseFragment;
import com.yibao.music.model.ArtistInfo;
import com.yibao.music.util.LogUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.playlist
 * @文件名: PlayListFragment
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/9 16:07
 * @描述： {TODO}
 */

public class PlayListFragment extends BaseFragment {
    @BindView(R.id.ll_add_new_play_list)
    LinearLayout mLlAddNewPlayList;
    @BindView(R.id.recycler_view_play_list)
    RecyclerView mRecyclerViewPlayList;
    private Unbinder unbinder;
    private PlayListAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.play_list_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        initData();
        initListener();

        return view;
    }

    private void initListener() {
        mAdapter.setItemListener(() -> startActivity(new Intent(getActivity(), DetailsActivity.class)));
    }

    private void initData() {
        ArrayList<ArtistInfo> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            ArtistInfo artistInfo = new ArtistInfo();
            artistInfo.setName(i + "爱");
            artistInfo.setSongCount(i);
            list.add(artistInfo);
        }

        mAdapter = new PlayListAdapter(list);
        LinearLayoutManager manager = new LinearLayoutManager(MyApplication.getIntstance());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerViewPlayList.setLayoutManager(manager);
        mRecyclerViewPlayList.setHasFixedSize(true);
        mRecyclerViewPlayList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.ll_add_new_play_list)
    public void onClick(View v) {
        switch (v.getId()) {
            // 打开新建播放列表的Dialog
            case R.id.ll_add_new_play_list:
                LogUtil.d("================新建播放列表=============");

                break;
            default:
                break;
        }
    }

    public static PlayListFragment newInstance() {
        return new PlayListFragment();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
