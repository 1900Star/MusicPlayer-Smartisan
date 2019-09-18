package com.yibao.music.adapter;

import androidx.recyclerview.widget.RecyclerView;
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


            artisHolder.mArtistItemContent.setOnClickListener(view -> openDetails(artistInfo, position, false));

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
    protected String getFirstChar(int i) {
        return mList.get(i).getFirstChar();
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

