package com.yibao.music.album;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yibao.music.R;
import com.yibao.music.base.BaseRvAdapter;
import com.yibao.music.model.ArtistInfo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.playlist
 * @文件名: PlayListAdapter
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/9 16:22
 * @描述： {TODO}
 */

public class DetailsListAdapter extends BaseRvAdapter<ArtistInfo> {
    public DetailsListAdapter(List<ArtistInfo> list) {
        super(list);
    }

    @Override
    protected void bindView(RecyclerView.ViewHolder holder, ArtistInfo artistInfo) {


    }

    @Override
    protected RecyclerView.ViewHolder getViewHolder(View view) {

        return new DetailsHolder(view);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_details_adapter;
    }

    static class DetailsHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_details_song_name)
        TextView mTvDetailsSongName;
        @BindView(R.id.tv_song_duration)
        TextView mTvSongDuration;
        @BindView(R.id.iv_details_menu)
        ImageView mIvDetailsMenu;
        @BindView(R.id.iv_blueplay)
        ImageView mIvBluePlay;
        @BindView(R.id.rl_details_item)
        RelativeLayout mRlDetailsItem;

        DetailsHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
