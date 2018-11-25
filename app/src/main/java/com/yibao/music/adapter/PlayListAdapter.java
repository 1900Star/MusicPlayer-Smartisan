package com.yibao.music.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yibao.music.R;
import com.yibao.music.base.BaseRvAdapter;
import com.yibao.music.model.MusicInfo;
import com.yibao.music.model.PlayListBean;
import com.yibao.music.util.Constants;
import com.yibao.music.util.LogUtil;

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
    protected void bindView(RecyclerView.ViewHolder holder, PlayListBean playListBean) {

        if (holder instanceof PlayViewHolder) {
            PlayViewHolder playViewHolder = (PlayViewHolder) holder;
            playViewHolder.mIvItemSelect.setVisibility(isSelectStatus ? View.VISIBLE : View.GONE);
            playViewHolder.mIvItemEdit.setVisibility(isSelectStatus ? View.VISIBLE : View.GONE);
            playViewHolder.mIvItemArrow.setVisibility(isSelectStatus ? View.GONE : View.VISIBLE);
            playViewHolder.mTvPlayListName.setText(playListBean.getTitle());
            String count = playListBean.getSongCount() + " 首歌曲";
            playViewHolder.mTvPlayListCount.setText(count);
            playViewHolder.itemView.setOnClickListener(view -> {
                if (isSelectStatus) {
                    selectStatus(playListBean, playViewHolder);
                } else {
                    PlayListAdapter.this.openDetails(playListBean, false);
                }
            });
            playViewHolder.mIvItemSelect.setOnClickListener(v -> selectStatus(playListBean, playViewHolder));
            playViewHolder.mIvItemEdit.setOnClickListener(v -> editItmeTitle(holder.getAdapterPosition()));
            playViewHolder.itemView.setOnLongClickListener(v -> {
                deletePlaylist(playListBean, holder.getAdapterPosition());
                return true;
            });
        }
    }

    private void selectStatus(PlayListBean playListBean, PlayViewHolder playViewHolder) {
        playViewHolder.mIvItemSelect.setImageResource(playListBean.isSelected() ? R.drawable.item_selected_normal : R.drawable.item_selected);
        PlayListAdapter.this.openDetails(playListBean, true);
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
        @BindView(R.id.iv_item_arrow)
        ImageView mIvItemArrow;
        @BindView(R.id.iv_item_select)
        ImageView mIvItemSelect;
        @BindView(R.id.iv_item_edit)
        ImageView mIvItemEdit;
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
