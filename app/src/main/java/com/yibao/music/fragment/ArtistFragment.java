package com.yibao.music.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yibao.music.R;
import com.yibao.music.adapter.ArtistAdapter;
import com.yibao.music.adapter.DetailsViewAdapter;
import com.yibao.music.base.BaseMusicFragment;
import com.yibao.music.base.BaseObserver;
import com.yibao.music.fragment.dialogfrag.MoreMenuBottomDialog;
import com.yibao.music.model.ArtistInfo;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.greendao.MusicBeanDao;
import com.yibao.music.util.Constants;
import com.yibao.music.util.MusicListUtil;
import com.yibao.music.util.ThreadPoolProxyFactory;
import com.yibao.music.view.music.DetailsView;
import com.yibao.music.view.music.MusicToolBar;
import com.yibao.music.view.music.MusicView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.artisanlist
 * @文件名: ArtistFragment
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/4 23:49
 * @描述： {TODO}
 */

public class ArtistFragment extends BaseMusicFragment {

    @BindView(R.id.music_toolbar_list)
    MusicToolBar mMusicToolBar;
    @BindView(R.id.artist_music_view)
    MusicView mMusicView;
    @BindView(R.id.details_view)
    DetailsView mDetailsView;
    private ArtistAdapter mAdapter;
    public boolean isShowDetailsView = false;
    private DetailsViewAdapter mDetailsAdapter;
    private List<MusicBean> mDetailList;
    private String mTempTitle;


    @Override
    protected boolean getIsOpenDetail() {
        return isShowDetailsView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initListener();
        mMusicToolBar.setToolbarTitle(isShowDetailsView ? mTempTitle : getString(R.string.music_artisan));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.artisan_list_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        mMusicToolBar.setTvEditVisibility(isShowDetailsView);
        initData();
        return view;
    }

    private void initListener() {
        mAdapter.setItemListener((bean, bean2) -> ArtistFragment.this.openDetailsView(bean));
        mMusicToolBar.setClickListener(new MusicToolBar.OnToolbarClickListener() {
            @Override
            public void clickEdit() {
                if (isShowDetailsView) {
                    openDetailsView(null);
                }
            }

            @Override
            public void switchMusicControlBar() {
                switchControlBar();
            }

            @Override
            public void clickDelete() {
            }
        });
    }


    private void initData() {
        List<ArtistInfo> artistList = MusicListUtil.getArtistList(mSongList);
        mAdapter = new ArtistAdapter(artistList);
        mMusicView.setAdapter(getActivity(), Constants.NUMBER_TWO, true, mAdapter);

    }

    private void openDetailsView(ArtistInfo artistInfo) {
        mDetailsView.setVisibility(isShowDetailsView ? View.GONE : View.VISIBLE);
        mMusicToolBar.setTvEditVisibility(isShowDetailsView);
        if (!isShowDetailsView) {
            if (artistInfo != null) {
                mTempTitle = artistInfo.getAlbumName();
                mDetailList = mMusicBeanDao.queryBuilder().where(MusicBeanDao.Properties.Artist.eq(artistInfo.getArtist())).build().list();
                // DetailsView播放音乐需要的参数
                mDetailsView.setDataFlag(mFragmentManager, mDetailList.size(), artistInfo.getArtist(), Constants.NUMBER_ONE);
                mDetailsAdapter = new DetailsViewAdapter(mActivity, mDetailList, Constants.NUMBER_ONE);
                mDetailsView.setAdapter(Constants.NUMBER_ONE, artistInfo, mDetailsAdapter);
                mDetailsAdapter.setOnItemMenuListener((int position, MusicBean musicBean) ->
                        MoreMenuBottomDialog.newInstance(musicBean, position, false, false).getBottomDialog(mActivity));
                interceptBackEvent(Constants.NUMBER_NINE);
                mMusicToolBar.setTvEditText(R.string.music_artisan);
            }
        }
        mMusicToolBar.setToolbarTitle(isShowDetailsView ? getString(R.string.music_artisan) : mTempTitle);
        isShowDetailsView = !isShowDetailsView;
        mMusicToolBar.setTvEditVisibility(isShowDetailsView);
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
        if (detailFlag == Constants.NUMBER_NINE) {
            openDetailsView(null);
        }
    }

    public static ArtistFragment newInstance() {
        return new ArtistFragment();
    }
}
