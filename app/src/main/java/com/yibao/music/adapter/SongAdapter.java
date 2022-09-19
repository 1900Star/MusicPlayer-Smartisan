package com.yibao.music.adapter;


import android.app.Activity;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yibao.music.R;
import com.yibao.music.base.bindings.BaseBindingAdapter;
import com.yibao.music.base.listener.OnMusicItemClickListener;
import com.yibao.music.databinding.ItemMusicListBinding;
import com.yibao.music.model.MusicBean;
import com.yibao.music.util.Constant;
import com.yibao.music.util.FileUtil;
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

public class SongAdapter
        extends BaseBindingAdapter<MusicBean> {
    private static final String TAG = "====" + SongAdapter.class.getSimpleName() + "    ";
    private Activity mContext;
    private final boolean mIsShowStickyView;
    private final int mScroeAndFrequnecyFlag;
    private final int mPageType;
    private final SparseBooleanArray mSparseBooleanArray;

    /**
     * @param context               c
     * @param list                  lx
     * @param sparseBooleanArray    s
     * @param isShowStickyView      控制列表的StickyView是否显示，0 显示 ，1 ：不显示
     *                              parm isArtistList     用来控制音乐列表和艺术家列表的显示
     * @param scoreAndFrequencyFlag 显示评分和播放次数 0 都不显示 ，1显示评分 ，2 显示播放次数
     * @param pageType  1 ABC 、2 评分 、3 播放次数 、 4 添加时间
     */
    public SongAdapter(Activity context, List<MusicBean> list, SparseBooleanArray sparseBooleanArray, boolean isShowStickyView, int scoreAndFrequencyFlag,int pageType) {
        super(list);
        this.mContext = context;
        this.mIsShowStickyView = isShowStickyView;
        this.mScroeAndFrequnecyFlag = scoreAndFrequencyFlag;
        this.mSparseBooleanArray = sparseBooleanArray;
        this.mPageType = pageType;
    }


    @Override
    protected String getLastItemDes() {
        return " 首歌";
    }

    @Override
    public void bindView(@NonNull RecyclerView.ViewHolder holder, MusicBean musicBean) {
        if (holder instanceof SongListViewHolder) {
            SongListViewHolder songListViewHolder = (SongListViewHolder) holder;
            int position = holder.getAdapterPosition();
            if (mScroeAndFrequnecyFlag == Constant.NUMBER_ONE) {
                songListViewHolder.mBinding.menuRatingBar.setVisibility(View.VISIBLE);
                songListViewHolder.mBinding.menuRatingBar.setRating(musicBean.getSongScore());
            } else if (mScroeAndFrequnecyFlag == Constant.NUMBER_TWO) {
                songListViewHolder.mBinding.tvFrequency.setVisibility(View.VISIBLE);
                songListViewHolder.mBinding.tvFrequency.setText(String.valueOf(musicBean.getPlayFrequency()));
            }
            songListViewHolder.mBinding.checkboxItem.setVisibility(isSelectStatus() ? View.VISIBLE : View.GONE);
            songListViewHolder.mBinding.ivSongItemMenu.setVisibility(isSelectStatus() ? View.INVISIBLE : View.VISIBLE);
//            songListViewHolder.mBinding.checkboxItem.setChecked(mSparseBooleanArray.get(position));
            ImageUitl.customLoadPic(mContext, FileUtil.getAlbumUrl(musicBean, 1), R.drawable.noalbumcover_220, songListViewHolder.mBinding.songAlbum);
            songListViewHolder.mBinding.songArtistName.setText(StringUtil.getArtist(musicBean));
            songListViewHolder.mBinding.songName.setText(StringUtil.getTitle(musicBean));
            if (mIsShowStickyView) {
                String firstTv = musicBean.getFirstChar();
                songListViewHolder.mBinding.itemStickyView.setText(firstTv);
                if (position == 0) {
                    songListViewHolder.mBinding.itemStickyView.setVisibility(View.VISIBLE);
                } else if (firstTv.equals(getDataList().get(position - 1)
                        .getFirstChar())) {
                    songListViewHolder.mBinding.itemStickyView.setVisibility(View.GONE);

                } else {
                    songListViewHolder.mBinding.itemStickyView.setVisibility(View.VISIBLE);
                }
            } else {
                songListViewHolder.mBinding.itemStickyView.setVisibility(View.GONE);
            }

            songListViewHolder.mBinding.ivSongItemMenu.setOnClickListener(view -> SongAdapter.this.openItemMenu(musicBean, position));
            songListViewHolder.mBinding.checkboxItem.setOnClickListener(v -> checkBoxClick(musicBean, position, songListViewHolder.mBinding.checkboxItem.isChecked()));
            //  Item点击监听
            songListViewHolder.mBinding.llMusicItem.setOnClickListener(view -> {
                if (isSelectStatus()) {
                    checkBoxClick(musicBean, position, songListViewHolder.mBinding.checkboxItem.isChecked());
                    openDetails(musicBean, position);
                } else {
                    if (mContext instanceof OnMusicItemClickListener) {
                        ((OnMusicItemClickListener) mContext).startMusicService(position,mPageType);
                    }
                }
            });

        }

    }


    @Override
    protected String getFirstChar(int i) {
        return getDataList().get(i).getFirstChar();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMusicListBinding binding = ItemMusicListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SongListViewHolder(binding);
    }

    static class SongListViewHolder extends RecyclerView.ViewHolder {

        ItemMusicListBinding mBinding;

        SongListViewHolder(ItemMusicListBinding binding) {
            super(binding.getRoot());
            mBinding = binding;

        }
    }


}
