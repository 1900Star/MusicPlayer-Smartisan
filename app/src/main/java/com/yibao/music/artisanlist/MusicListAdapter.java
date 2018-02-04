package com.yibao.music.artisanlist;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yibao.music.R;
import com.yibao.music.base.BaseRvAdapter;
import com.yibao.music.base.listener.OnMusicListItemClickListener;
import com.yibao.music.model.MusicBean;
import com.yibao.music.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.yibao.music.util.StringUtil.getPinYin;

/**
 *  @项目名： BigGirl
 *  @包名： ${PACKAGE_NAME}
 *  @文件名: ${NAME}
 *  @author: Stran
 *  @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 *  @创建时间: 2016/11/5 15:53
 *  @描述：    {TODO}
 */

public class MusicListAdapter
        extends BaseRvAdapter<MusicBean>
        implements SectionIndexer


{
    private String TAG = "MusicListAdapter";
    private Context mContext;
    private SparseIntArray intArrayA = new SparseIntArray();
    private SparseIntArray intArrayB = new SparseIntArray();


    public MusicListAdapter(Context context, List<MusicBean> list) {
        super(list);
        this.mContext = context;
    }


    @Override
    public MusicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_music_list, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    protected void bindView(RecyclerView.ViewHolder holder, MusicBean info) {
        if (holder instanceof MusicViewHolder) {
            MusicViewHolder viewHolder = (MusicViewHolder) holder;
            viewHolder.mSongArtistName.setText(info.getArtist());
            Glide.with(mContext)
                    .load(StringUtil.getAlbulm(info.getAlbumId()))
                    .placeholder(R.mipmap.playing_cover_lp)
                    .into(viewHolder.mSongAlbum);
            String name = StringUtil.getSongName(info.getTitle());
            viewHolder.mSongName.setText(name);
            String firstTv = getPinYin(name);
            viewHolder.mTvStickyView.setText(firstTv);
            int position = holder.getAdapterPosition();
            if (position == 0) {
                viewHolder.mTvStickyView.setVisibility(View.VISIBLE);
            } else if (position != 0 && firstTv.equals(getPinYin(mList.get(position - 1)
                    .getTitle()))) {
                viewHolder.mTvStickyView.setVisibility(View.GONE);

            } else {
                viewHolder.mTvStickyView.setVisibility(View.VISIBLE);
            }

            //            Item点击监听
            viewHolder.mLlMusicItem.setOnClickListener(view -> {
                if (mContext instanceof OnMusicListItemClickListener) {
                    ((OnMusicListItemClickListener) mContext).startMusicService(position);
                }
            });

        }

    }

    @Override
    protected RecyclerView.ViewHolder getViewHolder(View view) {

        return new MusicViewHolder(view);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_music_list;
    }


    @Override
    public String[] getSections() {
        //清空intArray
        intArrayA.clear();
        intArrayB.clear();
        //定义集合存放sections
        ArrayList<String> sections = new ArrayList<>();
        //遍历联系人集合添加section
        for (int i = 0; i < mList.size(); i++) {
            //获取联系人首字母
            String firstC = getPinYin(mList.get(i)
                    .getTitle());
            if (!sections.contains(firstC)) {
                sections.add(firstC);
                //添加section和position对应的值
                intArrayA.put(sections.size() - 1, i);
            }
            intArrayB.put(i, sections.size() - 1);
        }
        return sections.toArray(new String[sections.size()]);

    }

    @Override
    public int getPositionForSection(int position) {

        return intArrayA.get(position);
    }

    @Override
    public int getSectionForPosition(int i) {

        return intArrayB.get(i);
    }


    static class MusicViewHolder
            extends RecyclerView.ViewHolder {
        @BindView(R.id.item_sticky_view)
        TextView mTvStickyView;
        @BindView(R.id.song_album)
        ImageView mSongAlbum;
        @BindView(R.id.music_play_flag)
        ImageView mMusicPlayFlag;
        @BindView(R.id.song_name)
        TextView mSongName;
        @BindView(R.id.song_artist_name)
        TextView mSongArtistName;
        @BindView(R.id.ll_music_item)
        LinearLayout mLlMusicItem;

        MusicViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }
}
