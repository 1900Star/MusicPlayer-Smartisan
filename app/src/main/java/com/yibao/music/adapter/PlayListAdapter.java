package com.yibao.music.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yibao.music.MusicApplication;
import com.yibao.music.base.bindings.BaseBindingAdapter;
import com.yibao.music.databinding.ItemPlayListBinding;
import com.yibao.music.model.AddAndDeleteListBean;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.PlayListBean;
import com.yibao.music.model.greendao.MusicBeanDao;
import com.yibao.music.util.Constant;
import com.yibao.music.util.MusicDaoUtil;
import com.yibao.music.util.RxBus;

import java.util.List;


/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.playlist
 * @文件名: PlayListAdapter
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/9 16:22
 * @描述： {TODO}
 */

public class PlayListAdapter extends BaseBindingAdapter<PlayListBean> {


    public PlayListAdapter(List<PlayListBean> list) {
        super(list);

    }


    @Override
    public void bindView(@NonNull RecyclerView.ViewHolder holder, PlayListBean playListBean) {

        if (holder instanceof PlayViewHolder) {
            PlayViewHolder playViewHolder = (PlayViewHolder) holder;
            playViewHolder.mBinding.tvPlayListName.setText(playListBean.getTitle());
            List<MusicBean> musicBeans = MusicApplication.getInstance().getMusicDao().queryBuilder().where(MusicBeanDao.Properties.PlayListFlag.eq(playListBean.getTitle())).build().list();
            String count = musicBeans.size() + " 首歌曲";
            playViewHolder.mBinding.tvPlayListCount.setText(count);
            int adapterPosition = playViewHolder.getAdapterPosition();

            playViewHolder.mBinding.rlPlayListItem.setOnClickListener(view -> {
                PlayListAdapter.this.openDetails(playListBean, adapterPosition);
            });
            playViewHolder.mBinding.playListItemSlide.setOnClickListener(v -> {
                getMList().remove(adapterPosition);
                MusicDaoUtil.setMusicListFlag(playListBean);
                RxBus.getInstance().post(new AddAndDeleteListBean(Constant.NUMBER_TWO));
            });

            playViewHolder.mBinding.ivItemEdit.setOnClickListener(v -> editItemTitle(adapterPosition));
            playViewHolder.mBinding.rlPlayListItem.setOnLongClickListener(v -> {
                setLongClick(playListBean, adapterPosition);
                return true;
            });
        }
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemPlayListBinding binding = ItemPlayListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new PlayViewHolder(binding);
    }


    static class PlayViewHolder extends RecyclerView.ViewHolder {

        ItemPlayListBinding mBinding;

        PlayViewHolder(ItemPlayListBinding binding) {
            super(binding.getRoot());
            mBinding = binding;

        }
    }

    @Override
    protected String getLastItemDes() {
        return " 个播放列表";
    }
}
