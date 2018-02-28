package com.yibao.music.view.music;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yibao.music.R;
import com.yibao.music.album.DetailsListAdapter;
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
        extends RelativeLayout {

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

    private void initListener() {

    }


    private void initView() {
        //自定义组合控件将第三个参数设置为true  解析之后直接添加到当前view中
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
     * @param dataType 将数据 强转成指定的类型
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
            mTvArtistAlbummDetailsTitle.setText(info.getAlbumName());
            mTvArtistAlbummDetailsArtist.setText(info.getArtist());
            Glide.with(mIvArtistAlbummDetails.getContext())
                    .load(StringUtil.getAlbulm(info.getAlbumId()))
                    .placeholder(R.drawable.noalbumcover_220)
                    .into(mIvArtistAlbummDetails);
            setYear(info.getYear());

        } else if (dataType == Constants.NUMBER_TWO) {
            AlbumInfo info = (AlbumInfo) bean;
            mTvArtistAlbummDetailsTitle.setText(info.getAlbumName());
            mTvArtistAlbummDetailsArtist.setText(info.getArtist());
            Glide.with(mIvArtistAlbummDetails.getContext())
                    .load(StringUtil.getAlbulm(info.getAlbumId()))
                    .placeholder(R.drawable.noalbumcover_220)
                    .into(mIvArtistAlbummDetails);
            setYear(info.getYear());


        }

    }

    private void setYear(int year) {
        if (year != Constants.NUMBER_ZOER) {
            String s = year + "";
            mTvArtistAlbummDetailsDate.setText(s);
        }
    }


}







