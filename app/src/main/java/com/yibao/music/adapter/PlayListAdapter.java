package com.yibao.music.adapter;

import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

import com.yibao.music.MusicApplication;
import com.yibao.music.R;
import com.yibao.music.aidl.MusicBean;
import com.yibao.music.base.BaseRvAdapter;
import com.yibao.music.model.AddAndDeleteListBean;
import com.yibao.music.model.PlayListBean;
import com.yibao.music.model.greendao.MusicBeanDao;
import com.yibao.music.util.Constants;
import com.yibao.music.util.MusicDaoUtil;
import com.yibao.music.util.RxBus;

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
    private SparseBooleanArray mCheckedBoxMap;

    public PlayListAdapter(List<PlayListBean> list, SparseBooleanArray checkedBoxMap) {
        super(list);
        mCheckedBoxMap = checkedBoxMap;
    }


    @Override
    protected void bindView(RecyclerView.ViewHolder holder, PlayListBean playListBean) {

        if (holder instanceof PlayViewHolder) {
            PlayViewHolder playViewHolder = (PlayViewHolder) holder;
            playViewHolder.mCheckBox.setVisibility(isSelectStatus ? View.VISIBLE : View.GONE);
            playViewHolder.mIvItemEdit.setVisibility(isSelectStatus ? View.VISIBLE : View.GONE);
            playViewHolder.mIvItemArrow.setVisibility(isSelectStatus ? View.GONE : View.VISIBLE);
            playViewHolder.mTvPlayListName.setText(playListBean.getTitle());
            List<MusicBean> musicBeans = MusicApplication.getIntstance().getMusicDao().queryBuilder().where(MusicBeanDao.Properties.PlayListFlag.eq(playListBean.getTitle())).build().list();
            String count = musicBeans.size() + " 首歌曲";
            playViewHolder.mTvPlayListCount.setText(count);
            int adapterPosition = playViewHolder.getAdapterPosition();
            playViewHolder.mCheckBox.setChecked(mCheckedBoxMap.get(adapterPosition));
            playViewHolder.mRlPlayListItem.setOnClickListener(view -> {
                if (isSelectStatus) {
                    checkBoxClick(playListBean, adapterPosition, playViewHolder.mCheckBox.isChecked());
                    PlayListAdapter.this.openDetails(playListBean, adapterPosition, true);
                } else {
                    PlayListAdapter.this.openDetails(playListBean, adapterPosition, false);
                }
            });
            playViewHolder.mDeleteView.setOnClickListener(v -> {
                mList.remove(adapterPosition);
                MusicDaoUtil.setMusicListFlag(playListBean);
                RxBus.getInstance().post(new AddAndDeleteListBean(Constants.NUMBER_TWO));
            });
            playViewHolder.mCheckBox.setOnClickListener(v -> {
                checkBoxClick(playListBean, adapterPosition, playViewHolder.mCheckBox.isChecked());
//                    PlayListAdapter.this.openDetails(playListBean, true);
            });

            playViewHolder.mIvItemEdit.setOnClickListener(v -> editItmeTitle(adapterPosition));
            playViewHolder.mRlPlayListItem.setOnLongClickListener(v -> {
                deletePlaylist(playListBean, adapterPosition);
                return true;
            });
        }
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
        @BindView(R.id.checkbox_item)
        AppCompatCheckBox mCheckBox;
        @BindView(R.id.iv_item_edit)
        ImageView mIvItemEdit;
        @BindView(R.id.tv_play_list_name)
        TextView mTvPlayListName;
        @BindView(R.id.tv_play_list_count)
        TextView mTvPlayListCount;
        @BindView(R.id.rl_play_list_item)
        RelativeLayout mRlPlayListItem;
        @BindView(R.id.play_list_item_slide)
        LinearLayout mDeleteView;

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
