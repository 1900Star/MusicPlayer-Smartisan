package com.yibao.music.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
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
import com.yibao.music.view.music.DetailsView;
import com.yibao.music.view.music.MusicToolBar;

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
    @BindView(R.id.music_toolbar_list)
    MusicToolBar mMusicToolBar;
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
    @BindView(R.id.details_view)
    DetailsView mDetailsView;
    @BindView(R.id.album_content_view)
    LinearLayout mAlbumContentView;

    private DetailsViewAdapter mDetailsAdapter;
    private boolean isShowDetailsView = false;
    private boolean mDetailViewFlag = true;
    private List<MusicBean> mDetailList;
    private String detailsViewTitle;

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
        initListener();
    }


    @Override
    public void onResume() {
        super.onResume();
        mMusicToolBar.setToolbarTitle(isShowDetailsView ? detailsViewTitle : getString(R.string.music_album));
        initRxBusData();
    }

    @Override
    protected boolean getIsOpenDetail() {
        return isShowDetailsView;
    }

    private void initRxBusData() {
        disposeToolbar();
        if (mEditDisposable == null) {
            mEditDisposable = mBus.toObservableType(Constants.FRAGMENT_ALBUM, Object.class).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(editBean -> {
                        mMusicToolBar.setTvEditText(R.string.tv_edit);
                        mMusicToolBar.setTvDeleteVisibility(View.GONE);
                        isShowDetailsView = false;
                    });
        }
    }

    private void initListener() {
        mMusicToolBar.setClickListener(new MusicToolBar.OnToolbarClickListener() {
            @Override
            public void clickEdit() {
                if (mDetailViewFlag) {
                    mBus.post(Constants.ALBUM_FAG_EDIT, Constants.NUMBER_THRRE);
                }
                mMusicToolBar.setTvDeleteVisibility(isShowDetailsView ? View.GONE : View.VISIBLE);
                mMusicToolBar.setTvEditText(!isShowDetailsView ? R.string.tv_edit : R.string.complete);
                showDetailsView(null);
            }

            @Override
            public void switchMusicControlBar() {
                switchControlBar();
            }

            @Override
            public void clickDelete() {
                mBus.post(Constants.ALBUM_FAG_EDIT, Constants.NUMBER_FOUR);
            }
        });
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
                switchCategory(Constants.NUMBER_ZERO);
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
            mMusicToolBar.setToolbarTitle(getString(R.string.music_album));
            mDetailViewFlag = true;
        } else {
            if (albumInfo != null) {
                mDetailViewFlag = false;
                mDetailsView.setVisibility(View.VISIBLE);
                mDetailList = mMusicBeanDao.queryBuilder().where(MusicBeanDao.Properties.Album.eq(albumInfo.getAlbumName())).build().list();
                // DetailsView播放音乐需要的参数
                mDetailsView.setDataFlag(mFragmentManager, mDetailList.size(), albumInfo.getAlbumName(), Constants.NUMBER_TWO);
                mDetailsAdapter = new DetailsViewAdapter(mActivity, mDetailList, Constants.NUMBER_TWO);
                mDetailsView.setAdapter(Constants.NUMBER_TWO, albumInfo, mDetailsAdapter);
                mDetailsAdapter.setOnItemMenuListener((int position, MusicBean musicBean) ->
                        MoreMenuBottomDialog.newInstance(musicBean, position, false, false).getBottomDialog(mActivity));
                interceptBackEvent(Constants.NUMBER_TWELVE);
                detailsViewTitle = albumInfo.getAlbumName();
                mMusicToolBar.setToolbarTitle(detailsViewTitle);
            }
        }
        mMusicToolBar.setTvEditText(isShowDetailsView ? R.string.tv_edit : R.string.back);
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
        if (detailFlag == Constants.NUMBER_TWELVE) {
            showDetailsView(null);
        }
    }


    private void switchCategory(int showType) {
        mViewPager.setCurrentItem(showType, false);
        if (showType == Constants.NUMBER_ZERO) {

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


    public static AlbumFragment newInstance() {
        return new AlbumFragment();
    }


}
