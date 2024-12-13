package com.yibao.music.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yibao.music.base.bindings.BaseBindingAdapter;
import com.yibao.music.databinding.ArtistItemBinding;
import com.yibao.music.model.ArtistInfo;
import com.yibao.music.util.HanziToPinyins;

import java.util.List;


/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.artist
 * @文件名: ArtistAdapter
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/7 17:18
 * @描述： {TODO}
 */

public class ArtistAdapter extends BaseBindingAdapter<ArtistInfo> {

    public ArtistAdapter(List<ArtistInfo> list) {
        super(list);

    }

    @Override
    protected String getLastItemDes() {
        return " 位艺术家";
    }

    @Override
    public void bindView(RecyclerView.ViewHolder holder, ArtistInfo artistInfo) {
        if (holder instanceof ArtisHolder) {
            ArtisHolder artisHolder = (ArtisHolder) holder;
            int position = holder.getAdapterPosition();
            String songCount = artistInfo.getSongCount() + " 首歌曲";
            String albumCount = artistInfo.getAlbumCount() + " 张专辑";
            artisHolder.mBinding.artistItemName.setText(artistInfo.getArtist());
            artisHolder.mBinding.artistItemAlbumCount.setText(albumCount);
            artisHolder.mBinding.artistItemSongCount.setText(songCount);
            String firstChar = HanziToPinyins.stringToPinyinSpecial(artistInfo.getArtist()) + "";

            artisHolder.mBinding.artistItemStickyView.setText(firstChar);
            if (position == 0) {
                artisHolder.mBinding.artistItemStickyView.setVisibility(View.VISIBLE);
            } else if (firstChar.equals(HanziToPinyins
                    .stringToPinyinSpecial(getMList().get(position - 1).getArtist()) + "")) {
                artisHolder.mBinding.artistItemStickyView.setVisibility(View.GONE);

            } else {
                artisHolder.mBinding.artistItemStickyView.setVisibility(View.VISIBLE);
            }


            artisHolder.mBinding.artistItemContent.setOnClickListener(view -> openDetails(artistInfo, position));

        }


    }


    @Override
    protected String getFirstChar(int i) {
        return getMList().get(i).getFirstChar();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ArtistItemBinding binding = ArtistItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ArtisHolder(binding);
    }


    static class ArtisHolder extends RecyclerView.ViewHolder {

        ArtistItemBinding mBinding;

        ArtisHolder(ArtistItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;

        }
    }


}

