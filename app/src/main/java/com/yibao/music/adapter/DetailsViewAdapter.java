package com.yibao.music.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yibao.music.MusicApplication;
import com.yibao.music.R;
import com.yibao.music.base.BaseRvAdapter;
import com.yibao.music.base.listener.OnMusicItemClickListener;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.SearchHistoryBean;
import com.yibao.music.model.greendao.SearchHistoryBeanDao;
import com.yibao.music.util.Constants;
import com.yibao.music.util.SpUtil;
import com.yibao.music.util.StringUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.ObservableEmitter;


/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.playlist
 * @文件名: PlayListAdapter
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/9 16:22
 * @描述： { 1 ArtistFragment 、 2 AlbumFragment、 3 SearchActivity 、4 PlayListDetailView 会使用这个Adapter，dataFlag ( 1 、2、3、4)作为使用页面的标识}
 */

public class DetailsViewAdapter extends BaseRvAdapter<MusicBean> {
    private Context mContext;
    // 用来区分搜索的标识：1 Artist 、 2 Album 、  3 SongName(目前只按歌名搜索) 、   4 PlayList (播放列表)
    private int mDataFlag;

    public DetailsViewAdapter(Context context, List<MusicBean> list, int dataFlag) {
        super(list);
        this.mContext = context;
        this.mDataFlag = dataFlag;
    }

    public void setDataFlag(int flag) {
        this.mDataFlag = flag;
    }

    @Override
    protected String getLastItemDes() {
//        return mDataFlag == 1 ? " 名歌手" : mDataFlag == 2 ? " 张专辑" : " 首歌";
        return " 首歌";
    }

    @Override
    protected void bindView(RecyclerView.ViewHolder holder, MusicBean info) {
        if (holder instanceof DetailsHolder) {
            DetailsHolder detailsHolder = (DetailsHolder) holder;
            detailsHolder.mTvDetailsSongName.setText(info.getTitle());
            int duration = (int) info.getDuration();
            detailsHolder.mTvSongDuration.setText(StringUtil.parseDuration(duration));
            detailsHolder.mIvDetailsMenu.setOnClickListener(v -> openItemMenu(info, detailsHolder.getAdapterPosition()));
            detailsHolder.itemView.setOnClickListener(view -> {
                if (mContext instanceof OnMusicItemClickListener) {
                    SpUtil.setMusicDataListFlag(mContext, Constants.NUMBER_TEN);
                    if (mDataFlag == Constants.NUMBER_THRRE) {
                        insertSearchBean(info.getTitle());
                    }
                    ((OnMusicItemClickListener) mContext).startMusicServiceFlag(detailsHolder.getAdapterPosition(), mDataFlag, getQueryFlag(info));
                }

            });

        }

    }

    /**
     * 搜索并播放过的歌曲
     *
     * @param queryConditions 搜索的歌名
     */
    private static void insertSearchBean(String queryConditions) {
        SearchHistoryBeanDao searchDao = MusicApplication.getIntstance().getSearchDao();
        List<SearchHistoryBean> historyList = searchDao.queryBuilder().where(SearchHistoryBeanDao.Properties.SearchContent.eq(queryConditions)).build().list();
        if (historyList.size() < 1) {
            searchDao.insert(new SearchHistoryBean(queryConditions, Long.toString(System.currentTimeMillis())));
        } else {
            SearchHistoryBean searchHistoryBean = historyList.get(0);
            searchHistoryBean.setSearchTime(Long.toString(System.currentTimeMillis()));
            searchDao.update(searchHistoryBean);
        }
    }

    @Nullable
    private String getQueryFlag(MusicBean info) {
        String queryFlag = null;
        if (mDataFlag == Constants.NUMBER_ONE) {
            queryFlag = info.getArtist();
        } else if (mDataFlag == Constants.NUMBER_TWO) {
            queryFlag = info.getAlbum();
        } else if (mDataFlag == Constants.NUMBER_THRRE) {
            queryFlag = info.getTitle();
        } else if (mDataFlag == Constants.NUMBER_FOUR) {
            queryFlag = info.getPlayListFlag();
        }
        return queryFlag;
    }


    @Override
    protected RecyclerView.ViewHolder getViewHolder(View view) {

        return new DetailsHolder(view);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_details_adapter;
    }

    public void setData(List<MusicBean> detailList) {
        mList = detailList;
        notifyDataSetChanged();
    }

    static class DetailsHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_details_song_name)
        TextView mTvDetailsSongName;
        @BindView(R.id.tv_song_duration)
        TextView mTvSongDuration;
        @BindView(R.id.iv_details_menu)
        ImageView mIvDetailsMenu;
        @BindView(R.id.iv_blueplay)
        ImageView mIvBluePlay;
        @BindView(R.id.rl_details_item)
        RelativeLayout mRlDetailsItem;

        DetailsHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
