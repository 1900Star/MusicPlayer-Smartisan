package com.yibao.music.adapter;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yibao.music.R;
import com.yibao.music.base.bindings.BaseBindingAdapter;
import com.yibao.music.databinding.ItemAlbumListBinding;
import com.yibao.music.databinding.ItemAlbumTileBinding;
import com.yibao.music.model.AlbumInfo;
import com.yibao.music.util.Constants;
import com.yibao.music.util.ImageUitl;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.StringUtil;

import java.util.List;

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
        extends BaseBindingAdapter<AlbumInfo> {
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
        LogUtil.d("lsp", "专辑类型  " + isShowStickyView);
    }

    @Override
    protected String getLastItemDes() {
        return " 张专辑";
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAlbumTileBinding tileBinding = ItemAlbumTileBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        ItemAlbumListBinding listBinding = ItemAlbumListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return mIsShowStickyView == Constants.NUMBER_ZERO ? new AlbumListHolder(listBinding) : new AlbumTileHolder(tileBinding);
    }

    @Override
    public void bindView(RecyclerView.ViewHolder holder, AlbumInfo info) {
        //显示 StickyView  并且列表呈普通视图显示
        if (mIsShowStickyView == Constants.NUMBER_ZERO) {
            if (holder instanceof AlbumListHolder) {
                AlbumListHolder albumlistHolder = (AlbumListHolder) holder;
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

    private void setDataAlbumList(AlbumListHolder albumlistHolder, AlbumInfo info) {
        int position = albumlistHolder.getAdapterPosition();

        albumlistHolder.mBinding.tvAlbumListSongArtist.setText(info.getArtist());
        ImageUitl.customLoadPic(mContext, StringUtil.getAlbum(2, info.getAlbumId(), info.getAlbumName()), R.drawable.noalbumcover_220, albumlistHolder.mBinding.ivItemAlbumList);
        albumlistHolder.mBinding.tvAlbumListSongName.setText(info.getAlbumName());
        String songCount = info.getSongCount() + "首";
        albumlistHolder.mBinding.tvAlbumListSongCount.setText(songCount);
        String firstTv = info.getFirstChar();
        albumlistHolder.mBinding.ivAlbumListItemSelect.setVisibility(isSelectStatus() ? View.VISIBLE : View.GONE);
        albumlistHolder.mBinding.tvAlbumItemStickyView.setText(firstTv);
        if (position == 0) {
            albumlistHolder.mBinding.tvAlbumItemStickyView.setVisibility(View.VISIBLE);
        } else if (firstTv.equals(getDataList().get(position - 1).getFirstChar())) {
            albumlistHolder.mBinding.tvAlbumItemStickyView.setVisibility(View.GONE);

        } else {
            albumlistHolder.mBinding.tvAlbumItemStickyView.setVisibility(View.VISIBLE);
        }
        albumlistHolder.mBinding.ivAlbumListItemSelect.setOnClickListener(v -> selectStatus(info, position));
        //            Item点击监听
        albumlistHolder.mBinding.llAlbumListItem.setOnClickListener(view -> {
            LogUtil.d(getMTAG(), "AAAAAAAAAAAAAAAAA    " + isSelectStatus());
            if (isSelectStatus()) {
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


        ImageUitl.customLoadPic(mContext, StringUtil.getAlbum(2, albumInfo.getAlbumId(), albumInfo.getAlbumName()), R.drawable.noalbumcover_220, holder.mBinding.ivAlbumTileAlbum);
        holder.mBinding.tvAlbumTileName.setText(albumInfo.getAlbumName());

        holder.mBinding.ivAlbumTileAlbum.setOnClickListener(view1 -> AlbumAdapter.this.openDetails(albumInfo, holder.getAdapterPosition(), false));
    }

    private static class AlbumTileHolder extends RecyclerView.ViewHolder {

        ItemAlbumTileBinding mBinding;

        AlbumTileHolder(ItemAlbumTileBinding binding) {
            super(binding.getRoot());
            mBinding = binding;

        }
    }

    private static class AlbumListHolder extends RecyclerView.ViewHolder {

        ItemAlbumListBinding mBinding;

        AlbumListHolder(ItemAlbumListBinding binding) {
            super(binding.getRoot());
            mBinding = binding;

        }
    }


    @Override
    protected String getFirstChar(int i) {
        return getDataList().get(i).getFirstChar();
    }
}
