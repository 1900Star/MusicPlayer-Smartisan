package com.yibao.music.view.music;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yibao.music.R;
import com.yibao.music.adapter.DetailsListAdapter;
import com.yibao.music.model.AlbumInfo;
import com.yibao.music.model.ArtistInfo;
import com.yibao.music.util.Constants;
import com.yibao.music.util.StringUtil;

/**
 * Author：Sid
 * Des：${将详情页面封装到一个Viwe里面，方便多个页面使用}
 * Time:2017/9/10 00:43
 *
 * @author Stran
 */

public class DetailsView
        extends RelativeLayout implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private ImageView mIvArtistAlbummDetails;
    private TextView mTvArtistAlbummDetailsTitle;
    private TextView mTvArtistAlbummDetailsArtist;
    private TextView mTvArtistAlbummDetailsDate;
    private ImageView mIvDetailsAddToList;
    private ImageView mIvDetailsAddToPlayList;
    private LinearLayout mLlAlbumDetailsPlayall;
    private LinearLayout mLlAlbumDetailsRandomPlay;

    public DetailsView(Context context) {
        super(context);
        initView();
        initListener();
    }

    public DetailsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        initListener();
    }

    public DetailsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        initListener();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_details_add_to_list:

                break;
            case R.id.iv_details_add_to_play_list:

                break;
            case R.id.ll_album_details_playall:

                break;
            case R.id.ll_album_details_random_play:

                break;


            default:
                break;

        }
    }

    private void initListener() {
        mIvDetailsAddToList.setOnClickListener(this);
        mIvDetailsAddToPlayList.setOnClickListener(this);
        mLlAlbumDetailsPlayall.setOnClickListener(this);
        mLlAlbumDetailsRandomPlay.setOnClickListener(this);
    }


    private void initView() {
        LayoutInflater.from(getContext())
                .inflate(R.layout.details_fragment, this, true);
        mRecyclerView = findViewById(R.id.rv_artist_album_details);
        mIvArtistAlbummDetails = findViewById(R.id.iv_artist_albumm_details);
        mTvArtistAlbummDetailsTitle = findViewById(R.id.tv_artist_albumm_details_title);
        mTvArtistAlbummDetailsArtist = findViewById(R.id.tv_artist_albumm_details_artist);
        mTvArtistAlbummDetailsDate = findViewById(R.id.tv_artist_albumm_details_date);
        mIvDetailsAddToList = findViewById(R.id.iv_details_add_to_list);
        mIvDetailsAddToPlayList = findViewById(R.id.iv_details_add_to_play_list);
        mLlAlbumDetailsPlayall = findViewById(R.id.ll_album_details_playall);
        mLlAlbumDetailsRandomPlay = findViewById(R.id.ll_album_details_random_play);

    }


    /**
     * //设置列表的适配器
     *
     * @param context  d
     * @param dataType 将数据 强转成指定的类型  1 ： ArtistInfo   2 ： AlbumInfo
     * @param bean     d
     * @param adapter  d
     */
    public void setAdapter(Context context, int dataType, Object bean, DetailsListAdapter adapter) {
        initData(dataType, bean);
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    private void initData(int dataType, Object bean) {
        if (dataType == Constants.NUMBER_ONE) {
            ArtistInfo info = (ArtistInfo) bean;
            setMusicInfo(info.getAlbumName(), info.getArtist(), info.getAlbumId(), info.getYear());

        } else if (dataType == Constants.NUMBER_TWO) {
            AlbumInfo info = (AlbumInfo) bean;
            setMusicInfo(info.getAlbumName(), info.getArtist(), info.getAlbumId(), info.getYear());

        }

    }

    private void setMusicInfo(String albumName, String artist, long albumId, int issueYear) {
        mTvArtistAlbummDetailsTitle.setText(albumName);
        mTvArtistAlbummDetailsArtist.setText(artist);
        Glide.with(mIvArtistAlbummDetails.getContext())
                .load(StringUtil.getAlbulm(albumId))
                .placeholder(R.drawable.noalbumcover_220)
                .into(mIvArtistAlbummDetails);
        if (issueYear != Constants.NUMBER_ZOER) {
            String year = String.valueOf(issueYear);
            mTvArtistAlbummDetailsDate.setText(year);
        }

    }

}







