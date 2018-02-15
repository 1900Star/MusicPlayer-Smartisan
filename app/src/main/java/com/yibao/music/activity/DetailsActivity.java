package com.yibao.music.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yibao.music.MyApplication;
import com.yibao.music.R;
import com.yibao.music.base.BaseActivity;
import com.yibao.music.model.ArtistInfo;
import com.yibao.music.playlist.PlayListAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.activity
 * @文件名: DetailsActivity
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/14 15:24
 * @描述： {TODO}
 */

public class DetailsActivity extends BaseActivity {


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
    RecyclerView mRecyclerVeiw;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        ArrayList<ArtistInfo> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            ArtistInfo artistInfo = new ArtistInfo();
            artistInfo.setName(i + "爱");
            artistInfo.setSongCount(i);
            list.add(artistInfo);
        }

        PlayListAdapter playListAdapter = new PlayListAdapter(list);
        LinearLayoutManager manager = new LinearLayoutManager(MyApplication.getIntstance());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerVeiw.setLayoutManager(manager);
        mRecyclerVeiw.setHasFixedSize(true);
        mRecyclerVeiw.setAdapter(playListAdapter);
        playListAdapter.notifyDataSetChanged();
    }


    @OnClick({R.id.iv_details_add_to_list,
            R.id.iv_details_add_to_play_list, R.id.ll_album_details_playall, R.id.ll_album_details_random_play})
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.iv_details_add_to_list:
                break;
            case R.id.iv_details_add_to_play_list:
                break;
            case R.id.ll_album_details_playall:
                startActivity(new Intent(this,DetailsListActivity.class));
                break;
            case R.id.ll_album_details_random_play:
                break;
        }
    }
}
