package com.yibao.music.adapter;


import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.yibao.music.R;
import com.yibao.music.base.BaseRvAdapter;
import com.yibao.music.base.listener.OnMusicItemClickListener;
import com.yibao.music.model.MusicBean;
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

public class SongAdapter
        extends BaseRvAdapter<MusicBean> {
    private Activity mContext;
    private int mIsShowStickyView;
    private int mScroeAndFrequnecyFlag;

    /**
     * @param context               c
     * @param list                  l
     * @param isShowStickyView      控制列表的StickyView是否显示，0 显示 ，1 ：不显示
     *                              parm isArtistList     用来控制音乐列表和艺术家列表的显示
     *
     * @param scroeAndFrequnecyFlag 显示评分和播放次数 0 都不显示 ，1显示评分 ，2 显示播放次数
     */
    public SongAdapter(Activity context, List<MusicBean> list, int isShowStickyView, int scroeAndFrequnecyFlag) {
        super(list);
        this.mContext = context;
        this.mIsShowStickyView = isShowStickyView;
        this.mScroeAndFrequnecyFlag = scroeAndFrequnecyFlag;
    }


    @NonNull
    @Override
    public SongListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_music_list, parent, false);
        return new SongListViewHolder(view);
    }

    @Override
    protected String getLastItemDes() {
        return " 首歌";
    }

    @Override
    protected void bindView(RecyclerView.ViewHolder holder, MusicBean musicBean) {
        if (holder instanceof SongListViewHolder) {
            SongListViewHolder songListViewHolder = (SongListViewHolder) holder;
            int position = holder.getAdapterPosition();
            if (mScroeAndFrequnecyFlag == Constants.NUMBER_ONE) {
                songListViewHolder.mRatingBar.setVisibility(View.VISIBLE);
                songListViewHolder.mRatingBar.setRating(musicBean.getSongScore());
            } else if (mScroeAndFrequnecyFlag == Constants.NUMBER_TWO) {
                songListViewHolder.mTvFrequency.setVisibility(View.VISIBLE);
                songListViewHolder.mTvFrequency.setText(String.valueOf(musicBean.getPlayFrequency()));
            }
            songListViewHolder.mItemSelect.setVisibility(isSelectStatus ? View.VISIBLE : View.GONE);
            ImageUitl.customLoadPic(mContext, StringUtil.getAlbulm(musicBean.getAlbumId()), R.drawable.noalbumcover_120, songListViewHolder.mSongAlbum);
            songListViewHolder.mSongArtistName.setText(StringUtil.getArtist(musicBean));
            songListViewHolder.mSongName.setText(StringUtil.getTitle(musicBean));
            if (mIsShowStickyView == Constants.NUMBER_ZERO) {
                String firstTv = musicBean.getFirstChar();
                songListViewHolder.mTvStickyView.setText(firstTv);
                if (position == 0) {
                    songListViewHolder.mTvStickyView.setVisibility(View.VISIBLE);
                } else if (firstTv.equals(mList.get(position - 1)
                        .getFirstChar())) {
                    songListViewHolder.mTvStickyView.setVisibility(View.GONE);

                } else {
                    songListViewHolder.mTvStickyView.setVisibility(View.VISIBLE);
                }
            } else {
                songListViewHolder.mTvStickyView.setVisibility(View.GONE);
            }

            songListViewHolder.mIvSongItemMenu.setOnClickListener(view -> openItemMenu(musicBean, position));
            songListViewHolder.mItemSelect.setOnClickListener(v -> selectStatus(musicBean, songListViewHolder));
            //  Item点击监听
            songListViewHolder.mLlMusicItem.setOnClickListener(view -> {
                if (isSelectStatus) {
                    selectStatus(musicBean, songListViewHolder);
                } else {
                    if (mContext instanceof OnMusicItemClickListener) {
                        ((OnMusicItemClickListener) mContext).startMusicService(position);
                    }
                }
            });

        }

    }


    private void selectStatus(MusicBean musicBean, SongAdapter.SongListViewHolder playViewHolder) {
        playViewHolder.mItemSelect.setImageResource(musicBean.isSelected() ? R.drawable.item_selected_normal : R.drawable.item_selected);
        openDetails(musicBean, true);
    }

    @Override
    protected RecyclerView.ViewHolder getViewHolder(View view) {

        return new SongListViewHolder(view);
    }

    @Override
    protected int getLayoutId() {

        return R.layout.item_music_list;
    }

    @Override
    protected String getFirstChar(int i) {
        return mList.get(i).getFirstChar();
    }


    static class SongListViewHolder
            extends RecyclerView.ViewHolder {
        @BindView(R.id.item_sticky_view)
        TextView mTvStickyView;
        @BindView(R.id.song_album)
        ImageView mSongAlbum;
        @BindView(R.id.iv_song_item_select)
        ImageView mItemSelect;
        @BindView(R.id.song_item_play_flag)
        ImageView mSongPlayFlag;
        @BindView(R.id.song_name)
        TextView mSongName;
        @BindView(R.id.menu_rating_bar)
        RatingBar mRatingBar;
        @BindView(R.id.tv_frequency)
        TextView mTvFrequency;
        @BindView(R.id.song_artist_name)
        TextView mSongArtistName;
        @BindView(R.id.ll_music_item)
        LinearLayout mLlMusicItem;
        @BindView(R.id.iv_song_item_menu)
        ImageView mIvSongItemMenu;

        SongListViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }

    }


}
