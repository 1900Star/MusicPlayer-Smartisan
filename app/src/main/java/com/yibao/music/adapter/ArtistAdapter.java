package com.yibao.music.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yibao.music.R;
import com.yibao.music.base.BaseRvAdapter;
import com.yibao.music.model.ArtistInfo;
import com.yibao.music.util.HanziToPinyins;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.artist
 * @文件名: ArtistAdapter
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/7 17:18
 * @描述： {TODO}
 */

public class ArtistAdapter extends BaseRvAdapter<ArtistInfo> {

    public ArtistAdapter(List<ArtistInfo> list) {
        super(list);

    }

    @Override
    protected String getLastItemDes() {
        return " 位艺术家";
    }

    @Override
    protected void bindView(RecyclerView.ViewHolder holder, ArtistInfo artistInfo) {
        if (holder instanceof ArtisHolder) {
            ArtisHolder artisHolder = (ArtisHolder) holder;
            int position = holder.getAdapterPosition();
            String songCount = artistInfo.getSongCount() + " 首歌曲,";
            String albumCount = artistInfo.getAlbumCount() + " 张专辑";
            artisHolder.mArtistName.setText(artistInfo.getArtist());
            artisHolder.mArtistAlbumCount.setText(albumCount);
            artisHolder.mArtistSongCount.setText(songCount);
            String firstChar = HanziToPinyins.stringToPinyinSpecial(artistInfo.getArtist()) + "";

            artisHolder.mStickyView.setText(firstChar);
            if (position == 0) {
                artisHolder.mStickyView.setVisibility(View.VISIBLE);
            } else if (firstChar.equals(HanziToPinyins
                    .stringToPinyinSpecial(mList.get(position - 1).getArtist()) + "")) {
                artisHolder.mStickyView.setVisibility(View.GONE);

            } else {
                artisHolder.mStickyView.setVisibility(View.VISIBLE);
            }


            artisHolder.mArtistItemContent.setOnClickListener(view -> openDetails(artistInfo));

        }


    }

    @Override
    protected RecyclerView.ViewHolder getViewHolder(View view) {
        return new ArtisHolder(view);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.artist_item;
    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }


    @Override
    public int getPositionForSection(int section) {
        for (int i = 0; i < getItemCount(); i++) {
            char firstChar = HanziToPinyins.stringToPinyinSpecial(mList.get(i).getArtist());
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的char ascii值
     */
    @Override
    public int getSectionForPosition(int position) {


        return HanziToPinyins.stringToPinyinSpecial(mList.get(position).getArtist());
    }


    class ArtisHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.artist_item_sticky_view)
        TextView mStickyView;
        @BindView(R.id.artist_item_name)
        TextView mArtistName;

        @BindView(R.id.artist_item_album_count)
        TextView mArtistAlbumCount;
        @BindView(R.id.artist_item_song_count)
        TextView mArtistSongCount;
        @BindView(R.id.artist_item_content)
        LinearLayout mArtistItemContent;

        ArtisHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}

