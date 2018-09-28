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
import com.yibao.music.adapter.SearchDetailsAdapter;
import com.yibao.music.base.BaseMusicFragment;
import com.yibao.music.base.listener.MusicPagerListener;
import com.yibao.music.base.listener.UpdataTitleListener;
import com.yibao.music.model.AlbumInfo;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.greendao.MusicBeanDao;
import com.yibao.music.util.ColorUtil;
import com.yibao.music.util.Constants;
import com.yibao.music.util.SharePrefrencesUtil;
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
 * @文件名: AlbumMusicFragment
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/8 20:01
 * @描述： {TODO}
 */

public class AlbumMusicFragment extends BaseMusicFragment {
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
    private String mAlbumName;


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
        mDisposable.add(mBus.toObserverable(AlbumInfo.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::openDetailsView));
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

    private void openDetailsView(AlbumInfo bean) {
        if (isShowDetailsView) {
            mDetailsView.setVisibility(View.VISIBLE);
            List<MusicBean> list = mMusicBeanDao.queryBuilder().where(MusicBeanDao.Properties.Album.eq(bean.getAlbumName())).build().list();
            // DetailsView播放音乐需要的参数
            mDetailsView.setDataFlag(mFragmentManager, list.size(), bean.getAlbumName(), Constants.NUMBER_TWO);
            SearchDetailsAdapter adapter = new SearchDetailsAdapter(getActivity(), list, Constants.NUMBER_TWO);
            mDetailsView.setAdapter(getActivity(), Constants.NUMBER_TWO, bean, adapter);
            SharePrefrencesUtil.setDetailsFlag(mActivity, Constants.NUMBER_TEN);
            if (!mDetailsViewMap.containsKey(mClassName)) {
                mDetailsViewMap.put(mClassName, this);
            }
            if (mContext instanceof UpdataTitleListener) {
                mAlbumName = bean.getAlbumName();
                ((UpdataTitleListener) mContext).updataTitle(mAlbumName, isShowDetailsView);
            }


        } else {
            mDetailsView.setVisibility(View.GONE);

        }
        isShowDetailsView = !isShowDetailsView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isShowDetailsView) {
            if (mContext instanceof UpdataTitleListener) {
                ((UpdataTitleListener) mContext).updataTitle(mAlbumName, isShowDetailsView);
            }
        }

    }

    @Override
    protected void handleDetailsBack(int detailFlag) {
        super.handleDetailsBack(detailFlag);
        if (detailFlag == Constants.NUMBER_TEN) {
            mAlbumContentView.setVisibility(View.VISIBLE);
            mDetailsView.setVisibility(View.GONE);
            if (mDetailsViewMap.containsKey(mClassName)) {
                mDetailsViewMap.remove(mClassName);
            }
            isShowDetailsView = !isShowDetailsView;
        }


    }


    private void switchCategory(int showType) {
        mViewPager.setCurrentItem(showType,false);
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

    public static AlbumMusicFragment newInstance() {
        return new AlbumMusicFragment();
    }


}
