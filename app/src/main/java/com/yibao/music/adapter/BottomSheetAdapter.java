package com.yibao.music.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yibao.music.MusicApplication;
import com.yibao.music.base.bindings.BaseBindingAdapter;
import com.yibao.music.databinding.BottomSheetMusicItemBinding;
import com.yibao.music.model.AddAndDeleteListBean;
import com.yibao.music.model.MusicBean;
import com.yibao.music.util.Constants;
import com.yibao.music.util.RxBus;
import com.yibao.music.util.StringUtil;

import java.util.List;

/**
 * Des：${快速列表的Adapter}
 * Time:2017/8/22 14:31
 *
 * @author Stran
 */
public class BottomSheetAdapter
        extends BaseBindingAdapter<MusicBean> {


    public BottomSheetAdapter(List<MusicBean> list) {
        super(list);

    }

    @Override
    protected String getLastItemDes() {
        return " 首歌";
    }

    @Override
    public void bindView(RecyclerView.ViewHolder holder, MusicBean musicItem) {
        if (holder instanceof MusicHolder) {
            MusicHolder musicHolder = (MusicHolder) holder;
            musicHolder.mBinding.favoriteMusicName.setText(musicItem.getTitle());
            String unknownName = "<unknown>";
            musicHolder.mBinding.favoriteArtistName.setText(unknownName.equals(musicItem.getArtist()) ? "Smartisan" : musicItem.getArtist());
            musicHolder.mBinding.favoriteTime.setText(StringUtil.getFormatDate(Long.valueOf(musicItem.getTime())));
            int position = musicHolder.getAdapterPosition();
            // 侧滑删除收藏歌曲
            musicHolder.mBinding.deleteItem.setOnClickListener(v -> {
                musicItem.setFavorite(false);
                MusicApplication.getInstance().getMusicDao().update(musicItem);
                RxBus.getInstance().post(new AddAndDeleteListBean(Constants.NUMBER_FIVE, position, musicItem.getTitle()));
            });
            // MusicBottomSheetDialog页面接收,用于播放收藏列表中点击Position的音乐
            musicHolder.mBinding.rootFavoriteBottomSheet.setOnClickListener(view -> RxBus.getInstance().post(Constants.FAVORITE_POSITION, position));
        }
    }


    @Override
    public int getPositionForSection(int i) {
        return 0;
    }

    @Override
    public int getSectionForPosition(int i) {
        return 0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BottomSheetMusicItemBinding binding = BottomSheetMusicItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MusicHolder(binding);
    }

    static class MusicHolder
            extends RecyclerView.ViewHolder {

        BottomSheetMusicItemBinding mBinding;

        MusicHolder(BottomSheetMusicItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }


}
