package com.yibao.music.view.music;

import android.app.FragmentManager;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yibao.music.R;
import com.yibao.music.adapter.SearchDetailsAdapter;
import com.yibao.music.base.listener.OnMusicItemClickListener;
import com.yibao.music.fragment.dialogfrag.RelaxDialogFragment;
import com.yibao.music.fragment.dialogfrag.PreviewBigPicDialogFragment;
import com.yibao.music.model.AlbumInfo;
import com.yibao.music.model.ArtistInfo;
import com.yibao.music.util.Constants;
import com.yibao.music.util.ImageUitl;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.RandomUtil;
import com.yibao.music.util.SharePrefrencesUtil;
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
    private int mDataFlag;
    private int mListSize;
    private String mQueryFlag;
    private LinearLayout mHeadView;
    private FragmentManager mFragmentManager;
    private Long mAlbumId;

    public void setDataFlag(FragmentManager fragmentManager, int listSize, String queryFlag, int dataFlag) {
        this.mFragmentManager = fragmentManager;
        this.mDataFlag = dataFlag;
        this.mQueryFlag = queryFlag;
        this.mListSize = listSize;
    }

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

    /**
     * //设置列表的适配器
     *
     * @param context  d
     * @param dataType 将数据 强转成指定的类型  1 ： ArtistInfo   2 ： AlbumInfo
     * @param bean     d
     * @param adapter  d
     */
    public void setAdapter(Context context, int dataType, Object bean, SearchDetailsAdapter adapter) {
        initData(dataType, bean);
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_artist_albumm_details:
                String albumUrl = StringUtil.getAlbulm(mAlbumId)
                        .toString();
                PreviewBigPicDialogFragment.newInstance(albumUrl)
                        .show(mFragmentManager, "album");
                break;
            case R.id.iv_details_add_to_list:
                LogUtil.d("=================添加到个人列表");
                break;
            case R.id.iv_details_add_to_play_list:
                LogUtil.d("=================添加到当前播放列表");
                break;
            case R.id.ll_album_details_playall:
                startMusic(Constants.NUMBER_ZOER);
                break;
            case R.id.ll_album_details_random_play:
                startMusic(RandomUtil.getRandomPostion(mListSize));
                break;
            default:
                break;

        }
    }

    private void startMusic(int startPosition) {
        if (getContext() instanceof OnMusicItemClickListener) {
            SharePrefrencesUtil.setMusicDataListFlag(getContext(), Constants.NUMBER_TEN);
            ((OnMusicItemClickListener) getContext()).startMusicServiceFlag(startPosition, mDataFlag, mQueryFlag);
        }
    }


    // 根据dataType将bean转换具体的数据类型
    private void initData(int dataType, Object bean) {
        if (dataType == Constants.NUMBER_ONE) {
            ArtistInfo info = (ArtistInfo) bean;
            mAlbumId = info.getAlbumId();
            setMusicInfo(info.getAlbumName(), info.getArtist(), mAlbumId, info.getYear());

        } else if (dataType == Constants.NUMBER_TWO) {
            AlbumInfo info = (AlbumInfo) bean;
            mAlbumId = info.getAlbumId();
            setMusicInfo(info.getAlbumName(), info.getArtist(), mAlbumId, info.getYear());

        }

    }


    private void setMusicInfo(String albumName, String artist, long albumId, int issueYear) {
        mTvArtistAlbummDetailsTitle.setText(albumName);
        mTvArtistAlbummDetailsArtist.setText(artist);
        ImageUitl.customLoadPic(getContext(), StringUtil.getAlbulm((albumId)), R.drawable.noalbumcover_220, mIvArtistAlbummDetails);
        if (issueYear != Constants.NUMBER_ZOER) {
            String year = String.valueOf(issueYear);
            mTvArtistAlbummDetailsDate.setText(year);
        }

    }

    private void initListener() {
        mIvDetailsAddToList.setOnClickListener(this);
        mIvDetailsAddToPlayList.setOnClickListener(this);
        mLlAlbumDetailsPlayall.setOnClickListener(this);
        mLlAlbumDetailsRandomPlay.setOnClickListener(this);
        mHeadView.setOnClickListener(this);
        mIvArtistAlbummDetails.setOnClickListener(this);
        mIvArtistAlbummDetails.setOnLongClickListener(v -> {
            RelaxDialogFragment.newInstance().show(mFragmentManager, "girlsDialog");
            return true;
        });
    }


    private void initView() {
        LayoutInflater.from(getContext())
                .inflate(R.layout.details_fragment, this, true);
        mHeadView = findViewById(R.id.details_head_view);
        mRecyclerView = findViewById(R.id.rv_artist_album_details);
        mIvArtistAlbummDetails = findViewById(R.id.iv_artist_albumm_details);
        mTvArtistAlbummDetailsTitle = findViewById(R.id.tv_artist_albumm_details_title);
        mTvArtistAlbummDetailsArtist = findViewById(R.id.tv_artist_albumm_details_artist);
        mTvArtistAlbummDetailsDate = findViewById(R.id.tv_artist_albumm_details_date);
        mIvDetailsAddToList = findViewById(R.id.iv_details_add_to_list);
        mIvDetailsAddToPlayList = findViewById(R.id.iv_details_add_to_play_list);
        mLlAlbumDetailsPlayall = findViewById(R.id.ll_album_details_playall);
        mLlAlbumDetailsRandomPlay = findViewById(R.id.ll_album_details_random_play);
        mRecyclerView.setNestedScrollingEnabled(false);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }
}







