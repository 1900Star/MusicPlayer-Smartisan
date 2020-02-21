package com.yibao.music.adapter;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yibao.music.MusicApplication;
import com.yibao.music.R;
import com.yibao.music.base.BaseRvAdapter;
import com.yibao.music.base.listener.OnMusicItemClickListener;
import com.yibao.music.model.AddAndDeleteListBean;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.SearchHistoryBean;
import com.yibao.music.model.greendao.SearchHistoryBeanDao;
import com.yibao.music.util.Constants;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.RxBus;
import com.yibao.music.util.SpUtil;
import com.yibao.music.util.StringUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


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
    /** 用来区分搜索的标识：1 Artist 、 2 Album 、  3 SongName(目前只按歌名搜索) 、   4 PlayList (播放列表) */
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
        return " 首歌";
    }

    @Override
    protected void bindView(RecyclerView.ViewHolder holder, MusicBean info) {
        if (holder instanceof DetailsHolder) {
            DetailsHolder detailsHolder = (DetailsHolder) holder;
            int adapterPosition = detailsHolder.getAdapterPosition();
            detailsHolder.mTvDetailsSongName.setText(info.getTitle());
            LogUtil.d(TAG," artist info     "+info.getTitle()+""+info.getAddTime());
            int duration = (int) info.getDuration();
            detailsHolder.mTvSongDuration.setText(StringUtil.parseDuration(duration));
            if (mDataFlag == Constants.NUMBER_FOUR) {
                // 播放列表的详情列表有侧滑删除
                detailsHolder.mSlideDeleteView.setOnClickListener(v -> {
                    info.setPlayListFlag(Constants.PLAY_LIST_BACK_FLAG);
                    MusicApplication.getIntstance().getMusicDao().update(info);
                    RxBus.getInstance().post(new AddAndDeleteListBean(Constants.NUMBER_SIX, adapterPosition, info.getTitle()));
                });
            }
            detailsHolder.mIvDetailsMenu.setOnClickListener(v -> openItemMenu(info, adapterPosition));
            detailsHolder.mDetailItem.setOnClickListener(view -> {
                if (mContext instanceof OnMusicItemClickListener) {
                    SpUtil.setSortFlag(mContext, Constants.NUMBER_TEN);
                    if (mDataFlag == Constants.NUMBER_THREE) {
                        insertSearchBean(info.getTitle());
                    }
                    ((OnMusicItemClickListener) mContext).startMusicServiceFlag(adapterPosition, Constants.NUMBER_TEN, mDataFlag, getQueryFlag(info));
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
        } else if (mDataFlag == Constants.NUMBER_THREE) {
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

    static class DetailsHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_details_song_name)
        TextView mTvDetailsSongName;
        @BindView(R.id.tv_song_duration)
        TextView mTvSongDuration;
        @BindView(R.id.iv_details_menu)
        ImageView mIvDetailsMenu;
        @BindView(R.id.iv_blueplay)
        ImageView mIvBluePlay;
        @BindView(R.id.delete_item_detail)
        LinearLayout mSlideDeleteView;
        @BindView(R.id.detail_item_view)
        RelativeLayout mDetailItem;

        DetailsHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
