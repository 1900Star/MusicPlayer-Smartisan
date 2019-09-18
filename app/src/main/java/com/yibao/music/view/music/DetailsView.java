package com.yibao.music.view.music;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yibao.music.R;
import com.yibao.music.activity.PlayListActivity;
import com.yibao.music.adapter.DetailsViewAdapter;
import com.yibao.music.base.listener.OnMusicItemClickListener;
import com.yibao.music.fragment.dialogfrag.RelaxDialogFragment;
import com.yibao.music.fragment.dialogfrag.PreviewBigPicDialogFragment;
import com.yibao.music.model.AlbumInfo;
import com.yibao.music.model.ArtistInfo;
import com.yibao.music.model.MusicBean;
import com.yibao.music.util.Constants;
import com.yibao.music.util.ImageUitl;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.RandomUtil;
import com.yibao.music.util.SpUtil;
import com.yibao.music.util.StringUtil;
import com.yibao.music.view.MusicScrollView;
import com.yibao.music.view.SwipeItemLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
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
    private FragmentManager mFragmentManager;
    private Long mAlbumId;
    private List<MusicBean> mMusicList;
    private MusicScrollView mMusicScrollView;
    private LinearLayout mSuspensionLl;

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

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
//        LogUtil.d("==========++== 详情显示  " + visibility);

    }

    /**
     * //设置列表的适配器
     *
     * @param dataType 将数据 强转成指定的类型  1 ： ArtistInfo   2 ： AlbumInfo
     * @param bean     d
     * @param adapter  d
     */
    public void setAdapter(int dataType, Object bean, DetailsViewAdapter adapter) {
        initData(dataType, bean);
        mMusicList = adapter.getData();
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_artist_albumm_details:
                String albumUrl = StringUtil.getAlbulm(mAlbumId);
                PreviewBigPicDialogFragment.newInstance(albumUrl)
                        .show(mFragmentManager, "album");
                break;
            case R.id.iv_details_add_to_list:
                startPlayListActivity();
                break;
            case R.id.iv_details_add_to_play_list:
                LogUtil.d("=================添加到当前播放列表");
                break;
            case R.id.ll_album_details_playall:
                startMusic(Constants.NUMBER_ZERO);
                break;
            case R.id.ll_album_details_random_play:
                startMusic(RandomUtil.getRandomPostion(mListSize));
                break;
            default:
                break;

        }
    }

    protected void startPlayListActivity() {
        ArrayList<String> arrayList = new ArrayList<>();
        for (MusicBean musicBean : mMusicList) {
            arrayList.add(musicBean.getTitle());
        }
        Intent intent = new Intent(getContext(), PlayListActivity.class);
        intent.putStringArrayListExtra(Constants.ADD_TO_LIST, arrayList);
        getContext().startActivity(intent);
    }

    private void startMusic(int startPosition) {
        if (getContext() instanceof OnMusicItemClickListener) {
            SpUtil.setSortFlag(getContext(), Constants.NUMBER_TEN);
            ((OnMusicItemClickListener) getContext()).startMusicServiceFlag(startPosition, Constants.NUMBER_TEN, mDataFlag, mQueryFlag);
        }
    }


    /**
     * 根据dataType将bean转换具体的数据类型
     *
     * @param dataType d
     * @param bean     b
     */
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
        if (issueYear != Constants.NUMBER_ZERO) {
            String year = String.valueOf(issueYear);
            mTvArtistAlbummDetailsDate.setText(year);
        }

    }

    private void initListener() {
        mIvDetailsAddToList.setOnClickListener(this);
        mIvDetailsAddToPlayList.setOnClickListener(this);
        mLlAlbumDetailsPlayall.setOnClickListener(this);
        mLlAlbumDetailsRandomPlay.setOnClickListener(this);
        mIvArtistAlbummDetails.setOnClickListener(this);
        mIvArtistAlbummDetails.setOnLongClickListener(v -> {
            RelaxDialogFragment.newInstance().show(mFragmentManager, "girlsDialog");
            return true;
        });
    }


    private void initView() {
        LayoutInflater.from(getContext())
                .inflate(R.layout.details_fragment, this, true);
        mMusicScrollView = findViewById(R.id.detail_root);
        mSuspensionLl = findViewById(R.id.ll_play);
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
        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(getContext(), R.drawable.shape_item_decoration)));
        mRecyclerView.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(getContext()));
        mRecyclerView.addItemDecoration(divider);
    }

    public void setSuspension() {
        mMusicScrollView.resetHeight(mSuspensionLl, mRecyclerView);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }
}







