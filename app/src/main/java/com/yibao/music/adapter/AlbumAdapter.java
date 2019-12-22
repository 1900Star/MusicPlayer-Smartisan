package com.yibao.music.adapter;


import android.app.Activity;

import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yibao.music.R;
import com.yibao.music.base.BaseRvAdapter;
import com.yibao.music.model.AlbumInfo;
import com.yibao.music.util.Constants;
import com.yibao.music.util.ImageUitl;
import com.yibao.music.util.StringUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @项目名： BigGirl
 * @包名： ${PACKAGE_NAME}
 * @文件名: ${NAME}
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2016/11/5 15:53
 * @描述： {TODO}
 */

public class AlbumAdapter
        extends BaseRvAdapter<AlbumInfo> {
    private Activity mContext;
    private int mIsShowStickyView;

    /**
     * @param context          c
     * @param list             l
     * @param isShowStickyView 控制列表的StickyView是否显示，0 显示 ，1 ：不显示
     *                         parm isArtistList     用来控制音乐列表和艺术家列表的显示
     */
    public AlbumAdapter(Activity context, List<AlbumInfo> list, int isShowStickyView) {
        super(list);
        this.mContext = context;
        this.mIsShowStickyView = isShowStickyView;
    }

    @Override
    protected String getLastItemDes() {
        return " 张专辑";
    }

    @Override
    protected void bindView(RecyclerView.ViewHolder holder, AlbumInfo info) {
        //显示 StickyView  并且列表呈普通视图显示
        if (mIsShowStickyView == Constants.NUMBER_ZERO) {
            if (holder instanceof AlbumlistHolder) {
                AlbumlistHolder albumlistHolder = (AlbumlistHolder) holder;
                setDataAlbumList(albumlistHolder, info);
            }

            //显示 隐藏StickyView  并且列表平呈平铺视图显示
        } else if (mIsShowStickyView == Constants.NUMBER_ONE) {
            if (holder instanceof AlbumTileHolder) {
                AlbumTileHolder albumTileHolder = (AlbumTileHolder) holder;
                setDataAlbumTile(albumTileHolder, info);
            }

        }


    }

    private void setDataAlbumList(AlbumlistHolder albumlistHolder, AlbumInfo info) {
        int position = albumlistHolder.getAdapterPosition();

        albumlistHolder.mTvAlbumListSongArtist.setText(info.getArtist());
        ImageUitl.customLoadPic(mContext, StringUtil.getAlbum(2,info.getAlbumId(), info.getArtist()), R.drawable.noalbumcover_220, albumlistHolder.mIvItemAlbumList);
        albumlistHolder.mTvAlbumListSongName.setText(info.getAlbumName());
        String songCount = info.getSongCount() + "首";
        albumlistHolder.mTvAlbumListSongCount.setText(songCount);
        String firstTv = info.getFirstChar();
        albumlistHolder.mIvListSelect.setVisibility(isSelectStatus ? View.VISIBLE : View.GONE);
        albumlistHolder.mTvAlbumItemStickyView.setText(firstTv);
        if (position == 0) {
            albumlistHolder.mTvAlbumItemStickyView.setVisibility(View.VISIBLE);
        } else if (firstTv.equals(mList.get(position - 1).getFirstChar())) {
            albumlistHolder.mTvAlbumItemStickyView.setVisibility(View.GONE);

        } else {
            albumlistHolder.mTvAlbumItemStickyView.setVisibility(View.VISIBLE);
        }
        albumlistHolder.mIvListSelect.setOnClickListener(v -> selectStatus(info, position));
        //            Item点击监听
        albumlistHolder.mLlAlbumListItem.setOnClickListener(view -> {
            if (isSelectStatus) {
                selectStatus(info, position);
            } else {
                AlbumAdapter.this.openDetails(info, position, false);
            }
        });
    }

    private void selectStatus(AlbumInfo musicBean, int adapterPosition) {
        openDetails(musicBean, adapterPosition, true);
    }

    private void setDataAlbumTile(AlbumTileHolder holder, AlbumInfo albumInfo) {


        ImageUitl.customLoadPic(mContext, StringUtil.getAlbum(2,albumInfo.getAlbumId(), albumInfo.getArtist()), R.drawable.noalbumcover_120, holder.mIvAlbumTileAlbum);
        holder.mTvAlbumTileName.setText(albumInfo.getAlbumName());

        holder.mIvAlbumTileAlbum.setOnClickListener(view1 -> AlbumAdapter.this.openDetails(albumInfo, holder.getAdapterPosition(), false));

    }

    static class AlbumTileHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_album_tile_album)
        ImageView mIvAlbumTileAlbum;
        @BindView(R.id.tv_album_tile_name)
        TextView mTvAlbumTileName;

        AlbumTileHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }


    }

    static class AlbumlistHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_album_item_sticky_view)
        TextView mTvAlbumItemStickyView;
        @BindView(R.id.iv_item_album_list)
        ImageView mIvItemAlbumList;
        @BindView(R.id.iv_album_list_item_select)
        ImageView mIvListSelect;
        @BindView(R.id.tv_album_list_song_name)
        TextView mTvAlbumListSongName;
        @BindView(R.id.tv_album_list_song_artist)
        TextView mTvAlbumListSongArtist;
        @BindView(R.id.tv_album_list_song_count)
        TextView mTvAlbumListSongCount;
        @BindView(R.id.ll_album_list_item)
        LinearLayout mLlAlbumListItem;
        @BindView(R.id.root_list)
        RelativeLayout mRootList;

        AlbumlistHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);


        }
    }

    @Override
    protected RecyclerView.ViewHolder getViewHolder(View view) {

        return mIsShowStickyView == Constants.NUMBER_ZERO ? new AlbumlistHolder(view) : new AlbumTileHolder(view);
    }

    @Override
    protected int getLayoutId() {

        return mIsShowStickyView == Constants.NUMBER_ZERO ? R.layout.item_album_list : R.layout.item_album_tile;
    }


    @Override
    protected String getFirstChar(int i) {
        return mList.get(i).getFirstChar();
    }
}
