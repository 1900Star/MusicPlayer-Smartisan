package com.yibao.music.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yibao.music.MusicApplication;
import com.yibao.music.R;
import com.yibao.music.adapter.PlayListAdapter;
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
 * @包名： com.yibao.music.album
 * @文件名: AlbumListDetailsFragment
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/9 15:36
 * @描述： {TODO}
 */

public class AlbumListDetailsFragment extends BaseFragment {


    @BindView(R.id.iv_artist_albumm_details)
    ImageView mIvArtistAlbummDetails;
    @BindView(R.id.tv_artist_albumm_details_title)
    TextView mTvArtistAlbummDetailsTitle;
    @BindView(R.id.tv_artist_albumm_details_artist)
    TextView mTvArtistAlbummDetailsArtist;
    @BindView(R.id.tv_artist_albumm_details_date)
    TextView mTvArtistAlbummDetailsDate;
    @BindView(R.id.iv_details_add_to_list)
    ImageView mIvDetailsAddToList;
    @BindView(R.id.iv_details_add_to_play_list)
    ImageView mIvDetailsAddToPlayList;
    @BindView(R.id.ll_album_details_playall)
    LinearLayout mLlAlbumDetailsPlayall;
    @BindView(R.id.ll_album_details_random_play)
    LinearLayout mLlAlbumDetailsRandomPlay;
    @BindView(R.id.rv_artist_album_details)
    RecyclerView mRecyclerView;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.details_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        initData();

        return view;
    }

    private void initData() {
        ArrayList<ArtistInfo> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            ArtistInfo artistInfo = new ArtistInfo();
            artistInfo.setArtist(i + "爱");
            artistInfo.setSongCount(i);
            list.add(artistInfo);
        }

        PlayListAdapter playListAdapter = new PlayListAdapter(null);
        LinearLayoutManager manager = new LinearLayoutManager(MusicApplication.getIntstance());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(playListAdapter);
        playListAdapter.notifyDataSetChanged();
    }


    public static AlbumListDetailsFragment newInstance() {
        return new AlbumListDetailsFragment();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @OnClick({R.id.iv_details_add_to_list,
            R.id.iv_details_add_to_play_list,
            R.id.ll_album_details_playall,
            R.id.ll_album_details_random_play})
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.iv_details_add_to_list:
                break;
            case R.id.iv_details_add_to_play_list:
                break;
            case R.id.ll_album_details_playall:
                LogUtil.d("==================详情点击测试");
                break;
            case R.id.ll_album_details_random_play:
                LogUtil.d("==================详情点击测试");
                break;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public boolean backPressed() {
        return false;
    }
}
