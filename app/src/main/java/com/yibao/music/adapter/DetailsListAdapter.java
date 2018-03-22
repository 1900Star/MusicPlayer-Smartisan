package com.yibao.music.adapter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yibao.music.R;
import com.yibao.music.base.BaseRvAdapter;
import com.yibao.music.base.listener.OnMusicItemClickListener;
import com.yibao.music.model.MusicBean;
import com.yibao.music.service.AudioPlayService;
import com.yibao.music.service.AudioServiceConnection;
import com.yibao.music.util.Constants;
import com.yibao.music.util.SharePrefrencesUtil;
import com.yibao.music.util.StringUtil;

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

public class DetailsListAdapter extends BaseRvAdapter<MusicBean> {
    private Context mContext;
    private int mDataFlag;

    public DetailsListAdapter(Context context, List<MusicBean> list, int dataFlag) {
        super(list);
        this.mContext = context;
        this.mDataFlag = dataFlag;
    }

    @Override
    protected String getLastItemDes() {
        return " 首歌";
    }

    @Override
    protected void bindView(RecyclerView.ViewHolder holder, MusicBean info) {
        String queryFlag = mDataFlag == Constants.NUMBER_ONE ? info.getArtist() : info.getAlbum();
        if (holder instanceof DetailsHolder) {
            DetailsHolder detailsHolder = (DetailsHolder) holder;
            detailsHolder.mTvDetailsSongName.setText(info.getTitle());
            int duration = (int) info.getDuration();
            detailsHolder.mTvSongDuration.setText(StringUtil.parseDuration(duration));
            detailsHolder.itemView.setOnClickListener(view -> {
                if (mContext instanceof OnMusicItemClickListener) {
                    SharePrefrencesUtil.setMusicDataListFlag(mContext, Constants.NUMBER_TEN);
                    ((OnMusicItemClickListener) mContext).startMusicServiceFlag(detailsHolder.getAdapterPosition(), mDataFlag, queryFlag);
                }

            });

        }

    }

    private void playMusic(int position) {
        Intent intent = new Intent();
        intent.setClass(mContext, AudioPlayService.class);
        intent.putExtra("sortFlag", Constants.NUMBER_EIGHT);
        intent.putExtra("position", position);
        AudioServiceConnection connection = new AudioServiceConnection();
        mContext.bindService(intent, connection, Service.BIND_AUTO_CREATE);
        mContext.startService(intent);
        SharePrefrencesUtil.setMusicDataListFlag(mContext, Constants.NUMBER_EIGHT);
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
