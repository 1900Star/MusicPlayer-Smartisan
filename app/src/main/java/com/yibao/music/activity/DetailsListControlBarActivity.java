package com.yibao.music.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yibao.music.R;
import com.yibao.music.factory.RecyclerFactory;
import com.yibao.music.model.ArtistInfo;
import com.yibao.music.playlist.PlayListAdapter;
import com.yibao.music.util.Constants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.activity
 * @文件名: DetailsControlBarActivity
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/14 15:24
 * @描述： {TODO}
 */

public class DetailsListControlBarActivity extends AppCompatActivity {


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
    @BindView(R.id.artist_album_details_content)
    LinearLayout mContentView;
    private PlayListAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_list_activity);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        ArrayList<ArtistInfo> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            ArtistInfo artistInfo = new ArtistInfo();
            artistInfo.setArtist(i + "爱");
            artistInfo.setSongCount(i);
            list.add(artistInfo);
        }

        mAdapter = new PlayListAdapter(this,null);
        RecyclerView recyclerView = RecyclerFactory.creatRecyclerView(Constants.NUMBER_ONE, mAdapter);
        mContentView.addView(recyclerView);

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

                break;
            case R.id.ll_album_details_random_play:
                break;
        }
    }
}
