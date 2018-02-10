package com.yibao.music.artist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.yibao.music.R;
import com.yibao.music.base.BaseRvAdapter;
import com.yibao.music.model.ArtistInfo;
import com.yibao.music.util.HanziToPinyins;
import com.yibao.music.util.LogUtil;

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

public class ArtistAdapter extends BaseRvAdapter<ArtistInfo> implements SectionIndexer {

    public ArtistAdapter(Context context, List<ArtistInfo> list) {
        super(list);

    }

    @Override
    protected void bindView(RecyclerView.ViewHolder holder, ArtistInfo artistInfo) {
        if (holder instanceof ArtisHolder) {
            ArtisHolder artisHolder = (ArtisHolder) holder;
            int position = holder.getAdapterPosition();
            String songCount = artistInfo.getSongCount() + " 首歌曲,";
            String albumCount = artistInfo.getAlbumCount() + " 张专辑";
            artisHolder.mArtistName.setText(artistInfo.getName());
            artisHolder.mArtistAlbumCount.setText(albumCount);
            artisHolder.mArtistSongCount.setText(songCount);
            String firstChar = HanziToPinyins.stringToPinyinSpecial(artistInfo.getName()) + "";

            artisHolder.mStickyView.setText(firstChar);
            if (position == 0) {
                artisHolder.mStickyView.setVisibility(View.VISIBLE);
            } else if (position != 0 && firstChar.equals(HanziToPinyins
                    .stringToPinyinSpecial(mList.get(position - 1).getName()) + "")) {
                artisHolder.mStickyView.setVisibility(View.GONE);

            } else {
                artisHolder.mStickyView.setVisibility(View.VISIBLE);
            }


            artisHolder.mArtistItemContent.setOnClickListener(view ->

                    LogUtil.d("=======  打开艺术家详情  ==============         " + firstChar));

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
            char firstChar = HanziToPinyins.stringToPinyinSpecial(mList.get(i).getName());
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


        return HanziToPinyins.stringToPinyinSpecial(mList.get(position).getName());
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

