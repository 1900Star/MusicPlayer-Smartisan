package com.yibao.music.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yibao.music.R;
import com.yibao.music.adapter.AlbumCategoryPagerAdapter;
import com.yibao.music.adapter.DetailsViewAdapter;
import com.yibao.music.base.BaseMusicFragment;
import com.yibao.music.base.listener.MusicPagerListener;
import com.yibao.music.fragment.dialogfrag.MoreMenuBottomDialog;
import com.yibao.music.model.AlbumInfo;
import com.yibao.music.aidl.MusicBean;
import com.yibao.music.model.greendao.MusicBeanDao;
import com.yibao.music.util.ColorUtil;
import com.yibao.music.util.Constants;
import com.yibao.music.util.SpUtil;
import com.yibao.music.view.music.DetailsView;
import com.yibao.music.view.music.MusicView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.album
 * @文件名: AlbumFragment
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/8 20:01
 * @描述： {TODO}
 */

public class AlbumFragment extends BaseMusicFragment {
    @BindView(R.id.iv_album_category_random_paly)
    ImageView mIvAlbumCategoryRandomPaly;
    @BindView(R.id.iv_album_category_list)
    ImageView mIvAlbumCategoryList;
    @BindView(R.id.tv_album_category_list)
    TextView mTvAlbumCategoryList;
    @BindView(R.id.album_category_list_ll)
    LinearLayout mAlbumCategoryListLl;
    @BindView(R.id.iv_album_category_tile)
    ImageView mIvAlbumCategoryTile;
    @BindView(R.id.tv_album_category_tile)
    TextView mTvAlbumCategoryTile;
    @BindView(R.id.album_category_tile_ll)
    LinearLayout mAlbumCategoryTileLl;
    @BindView(R.id.iv_album_category_paly)
    ImageView mIvAlbumCategoryPaly;

    @BindView(R.id.view_pager_album)
    ViewPager mViewPager;
    @BindView(R.id.album_music_view)
    MusicView mAlbumMusicView;
    @BindView(R.id.details_view)
    DetailsView mDetailsView;
    @BindView(R.id.album_content_view)
    LinearLayout mAlbumContentView;
    public static String detailsViewTitle;
    public static boolean isShowDetailsView = false;
    private DetailsViewAdapter mDetailsAdapter;
    private List<MusicBean> mDetailList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.album_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        initData();
        return view;
    }


    private void initData() {
        AlbumCategoryPagerAdapter pagerAdapter = new AlbumCategoryPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.addOnPageChangeListener(new MusicPagerListener() {
            @Override
            public void onPageSelected(int position) {
                switchCategory(position);
            }
        });
        initRxbusData();
    }

    private void initRxbusData() {
        mCompositeDisposable.add(mBus.toObserverable(AlbumInfo.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showDetailsView));
    }

    @OnClick({R.id.iv_album_category_random_paly,
            R.id.album_category_list_ll, R.id.album_category_tile_ll, R.id.iv_album_category_paly})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_album_category_random_paly:
            case R.id.iv_album_category_paly:
                randomPlayMusic();
                break;
            case R.id.album_category_list_ll:
                switchCategory(Constants.NUMBER_ZOER);
                break;
            case R.id.album_category_tile_ll:
                switchCategory(Constants.NUMBER_ONE);
                break;
            default:
                break;
        }
    }

    private void showDetailsView(AlbumInfo albumInfo) {
        if (isShowDetailsView) {
            mDetailsView.setVisibility(View.GONE);
            detailsViewTitle = null;
        } else {
            mDetailsView.setVisibility(View.VISIBLE);
            mDetailList = mMusicBeanDao.queryBuilder().where(MusicBeanDao.Properties.Album.eq(albumInfo.getAlbumName())).build().list();
            // DetailsView播放音乐需要的参数
            mDetailsView.setDataFlag(mFragmentManager, mDetailList.size(), albumInfo.getAlbumName(), Constants.NUMBER_TWO);
            mDetailsAdapter = new DetailsViewAdapter(mActivity, mDetailList, Constants.NUMBER_TWO);

            mDetailsView.setAdapter(Constants.NUMBER_TWO, albumInfo, mDetailsAdapter);
            mDetailsAdapter.setOnItemMenuListener((int position, MusicBean musicBean) ->
                    MoreMenuBottomDialog.newInstance(musicBean, position, false).getBottomDialog(mActivity));
            putFragToMap(mClassName);
            detailsViewTitle = albumInfo.getAlbumName();
            changeToolBarTitle(albumInfo.getAlbumName(), true);
        }
        SpUtil.setDetailsFlag(mActivity, Constants.NUMBER_TEN);
        changeTvEditText(getResources().getString(isShowDetailsView ? R.string.tv_edit : R.string.back));
        changeSearchVisibility(isShowDetailsView);
        isShowDetailsView = !isShowDetailsView;
    }

    @Override
    protected void deleteItem(int musicPosition) {
        super.deleteItem(musicPosition);
        if (mDetailList != null && mDetailsAdapter != null) {
            mDetailList.remove(musicPosition);
            mDetailsAdapter.setData(mDetailList);
        }
    }

    @Override
    protected void handleDetailsBack(int detailFlag) {
        if (detailFlag == Constants.NUMBER_TEN) {
            SpUtil.setDetailsFlag(mActivity, Constants.NUMBER_TEN);
            mAlbumContentView.setVisibility(View.VISIBLE);
            mDetailsView.setVisibility(View.GONE);
            removeFrag(mClassName);
            changeSearchVisibility(isShowDetailsView);
            isShowDetailsView = !isShowDetailsView;
        }
//        super.handleDetailsBack(detailFlag);
    }


    private void switchCategory(int showType) {
        mViewPager.setCurrentItem(showType, false);
        if (showType == Constants.NUMBER_ZOER) {

            mAlbumCategoryListLl.setBackgroundResource(R.drawable.btn_category_songname_down_selector);
            mIvAlbumCategoryList.setImageResource(R.drawable.album_category_list_down_selector);
            mTvAlbumCategoryList.setTextColor(ColorUtil.wihtle);


            mAlbumCategoryTileLl.setBackgroundResource(R.drawable.btn_category_views_selector);

            mIvAlbumCategoryTile.setImageResource(R.drawable.album_category_tile_selector);

            mTvAlbumCategoryTile.setTextColor(ColorUtil.textName);

        } else if (showType == Constants.NUMBER_ONE) {

            mAlbumCategoryTileLl.setBackgroundResource(R.drawable.btn_category_views_down_selector);
            mIvAlbumCategoryTile.setImageResource(R.drawable.album_category_tile_down_selector);
            mTvAlbumCategoryTile.setTextColor(ColorUtil.wihtle);

            mAlbumCategoryListLl.setBackgroundResource(R.drawable.btn_category_songname_selector);
            mIvAlbumCategoryList.setImageResource(R.drawable.album_category_list_selector);
            mTvAlbumCategoryList.setTextColor(ColorUtil.textName);


        }


    }

    @Override
    protected void changeEditStatus(int currentIndex) {


    }

    public static AlbumFragment newInstance() {
        return new AlbumFragment();
    }


}
