package com.yibao.music.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yibao.music.R;
import com.yibao.music.base.BaseRvAdapter;
import com.yibao.music.model.MusicInfo;
import com.yibao.music.model.PlayListBean;

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

public class PlayListAdapter extends BaseRvAdapter<PlayListBean> {

    public PlayListAdapter(List<PlayListBean> list) {
        super(list);
    }


    @Override
    protected void bindView(RecyclerView.ViewHolder holder, PlayListBean musicInfo) {

        if (holder instanceof PlayViewHolder) {
            PlayViewHolder playViewHolder = (PlayViewHolder) holder;
            playViewHolder.mTvPlayListName.setText(musicInfo.getTitle());
            String count = musicInfo.getSongCount() + " 首歌曲";
            playViewHolder.mTvPlayListCount.setText(count);
            playViewHolder.itemView.setOnClickListener(view -> PlayListAdapter.this.openDetails(musicInfo));
            playViewHolder.itemView.setOnLongClickListener(v -> { deletePlaylist(musicInfo, holder.getAdapterPosition());
                return true;
            });
        }
    }
    public void removeItem(int itemPosition) {
        mList.remove(itemPosition);
        notifyDataSetChanged();
    }
    @Override
    protected RecyclerView.ViewHolder getViewHolder(View view) {

        return new PlayViewHolder(view);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_play_list;
    }

    static class PlayViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_play_list_name)
        TextView mTvPlayListName;
        @BindView(R.id.tv_play_list_count)
        TextView mTvPlayListCount;
        @BindView(R.id.rl_play_list_item)
        RelativeLayout mRlPlayListItem;

        PlayViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    protected String getLastItemDes() {
        return " 个播放列表";
    }
}
