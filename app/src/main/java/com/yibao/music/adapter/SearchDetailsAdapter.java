package com.yibao.music.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yibao.music.R;
import com.yibao.music.base.BaseRvAdapter;
import com.yibao.music.base.listener.OnMusicItemClickListener;
import com.yibao.music.model.MusicBean;
import com.yibao.music.util.Constants;
import com.yibao.music.util.SpUtil;
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

public class SearchDetailsAdapter extends BaseRvAdapter<MusicBean> {
    private Context mContext;
    // 用来区分搜索的标识：1 Artist 、 2 Album 、  3 SongName
    private int mDataFlag;

    public SearchDetailsAdapter(Context context, List<MusicBean> list, int dataFlag) {
        super(list);
        this.mContext = context;
        this.mDataFlag = dataFlag;
    }

    public void setDataFlag(int flag) {
        this.mDataFlag = flag;
    }

    @Override
    protected String getLastItemDes() {
        return " 首歌";
    }

    @Override
    protected void bindView(RecyclerView.ViewHolder holder, MusicBean info) {
        if (holder instanceof DetailsHolder) {
            DetailsHolder detailsHolder = (DetailsHolder) holder;
            detailsHolder.mTvDetailsSongName.setText(info.getTitle());
            int duration = (int) info.getDuration();
            detailsHolder.mTvSongDuration.setText(StringUtil.parseDuration(duration));
            detailsHolder.itemView.setOnClickListener(view -> {
                if (mContext instanceof OnMusicItemClickListener) {
                    SpUtil.setMusicDataListFlag(mContext, Constants.NUMBER_TEN);
                    ((OnMusicItemClickListener) mContext).startMusicServiceFlag(detailsHolder.getAdapterPosition(), mDataFlag, getQueryFlag(info));
                }

            });

        }

    }

    @Nullable
    private String getQueryFlag(MusicBean info) {
        String queryFlag = null;
        if (mDataFlag == Constants.NUMBER_ONE) {
            queryFlag = info.getArtist();
        } else if (mDataFlag == Constants.NUMBER_TWO) {
            queryFlag = info.getAlbum();
        } else if (mDataFlag == Constants.NUMBER_THRRE) {
            queryFlag = info.getTitle();
        }
        return queryFlag;
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
