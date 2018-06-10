package com.yibao.music.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yibao.music.R;
import com.yibao.music.adapter.AlbumAdapter;
import com.yibao.music.adapter.DetailsListAdapter;
import com.yibao.music.base.BaseFragment;
import com.yibao.music.model.AlbumInfo;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.greendao.MusicBeanDao;
import com.yibao.music.util.ColorUtil;
import com.yibao.music.util.Constants;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.MusicListUtil;
import com.yibao.music.util.SharePrefrencesUtil;
import com.yibao.music.view.music.DetailsView;
import com.yibao.music.view.music.MusicView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.album
 * @文件名: AlbumFragment
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/8 20:01
 * @描述： {TODO}
 */

public class AlbumFragment extends BaseFragment {
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
    @BindView(R.id.album_music_view)
    MusicView mAlbumMusicView;
    @BindView(R.id.details_view)
    DetailsView mDetailsView;
    @BindView(R.id.album_content_view)
    LinearLayout mAlbumContentView;


    private Unbinder unbinder;
    private AlbumAdapter mAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.album_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        initData(Constants.NUMBER_ZOER, true, Constants.NUMBER_THRRE);
        initListener();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initListener() {
        mAdapter.setItemListener(AlbumFragment.this::openDetailsView);

    }


    /**
     * 加载列表
     *
     * @param adapterShowType       普通视图显示  和 GridView视图显示  ( 3 列 )
     * @param isShowSlideBar        是否显示SlideBar
     * @param adapterAndManagerType RecyclerView的Manager ，3 == LinearLayoutManager
     *                              4 == GridLayoutManager
     */
    private void initData(int adapterShowType, boolean isShowSlideBar, int adapterAndManagerType) {
        mAdapter = new AlbumAdapter(mActivity, mAlbumList, adapterShowType);
        mAlbumMusicView.setAdapter(mActivity, adapterAndManagerType, isShowSlideBar, mAdapter);
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
                initData(Constants.NUMBER_ZOER, true, Constants.NUMBER_THRRE);
                break;
            case R.id.album_category_tile_ll:
                switchCategory(Constants.NUMBER_ONE);
                initData(Constants.NUMBER_ONE, false, Constants.NUMBER_FOUR);
                break;
            default:
                break;
        }
    }

    private void openDetailsView(AlbumInfo bean) {
        if (isShowDetailsView) {
            LogUtil.d("===============显示 ");
            mDetailsView.setVisibility(View.VISIBLE);
            List<MusicBean> list = mMusicBeanDao.queryBuilder().where(MusicBeanDao.Properties.Album.eq(bean.getAlbumName())).build().list();
            // DetailsView播放音乐需要的参数
            mDetailsView.setDataFlag(list.size(), bean.getAlbumName(), Constants.NUMBER_TWO);
            DetailsListAdapter adapter = new DetailsListAdapter(getActivity(), list, Constants.NUMBER_TWO);
            mDetailsView.setAdapter(getActivity(), Constants.NUMBER_TWO, bean, adapter);
            SharePrefrencesUtil.setDetailsFlag(mActivity, Constants.NUMBER_TEN);
            if (!mDetailsViewMap.containsKey(mClassName)) {
                mDetailsViewMap.put(mClassName, this);
            }

        } else {
            mDetailsView.setVisibility(View.GONE);
        }
        isShowDetailsView = !isShowDetailsView;
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

    public static AlbumFragment newInstance() {
        return new AlbumFragment();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();

    }

}
