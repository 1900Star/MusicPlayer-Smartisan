package com.yibao.music.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yibao.music.R;
import com.yibao.music.adapter.ArtistAdapter;
import com.yibao.music.adapter.SearchDetailsAdapter;
import com.yibao.music.base.BaseMusicFragment;
import com.yibao.music.base.listener.UpdataTitleListener;
import com.yibao.music.model.ArtistInfo;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.greendao.MusicBeanDao;
import com.yibao.music.util.Constants;
import com.yibao.music.util.MusicListUtil;
import com.yibao.music.util.SpUtil;
import com.yibao.music.view.music.DetailsView;
import com.yibao.music.view.music.MusicView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.artisanlist
 * @文件名: ArtistanListMusicFragment
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/4 23:49
 * @描述： {TODO}
 */

public class ArtistanListMusicFragment extends BaseMusicFragment {

    @BindView(R.id.artist_music_view)
    MusicView mMusicView;
    @BindView(R.id.details_view)
    DetailsView mDetailsView;
    private ArtistAdapter mAdapter;
    private String mAlbumName;
    private List<ArtistInfo> mArtistList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArtistList = MusicListUtil.getArtistList(mSongList);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.artisan_list_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        initData();
        initListener();
        return view;
    }

    private void initListener() {
        mAdapter.setItemListener(ArtistanListMusicFragment.this::openDetailsView);
    }


    private void initData() {
        mAdapter = new ArtistAdapter(mArtistList);
        mMusicView.setAdapter(getActivity(), Constants.NUMBER_TWO, true, mAdapter);

    }

    private void openDetailsView(ArtistInfo bean) {
        if (isShowDetailsView) {
            mDetailsView.setVisibility(View.GONE);
        } else {
            mDetailsView.setVisibility(View.VISIBLE);
            List<MusicBean> list = mMusicBeanDao.queryBuilder().where(MusicBeanDao.Properties.Artist.eq(bean.getArtist())).build().list();
            // DetailsView播放音乐需要的参数
            mDetailsView.setDataFlag(mFragmentManager, list.size(), bean.getArtist(), Constants.NUMBER_ONE);
            SearchDetailsAdapter adapter = new SearchDetailsAdapter(getActivity(), list, Constants.NUMBER_ONE);
            mDetailsView.setAdapter(getActivity(), Constants.NUMBER_ONE, bean, adapter);
            SpUtil.setDetailsFlag(mActivity, Constants.NUMBER_NINE);
            if (!mDetailsViewMap.containsKey(mClassName)) {
                mDetailsViewMap.put(mClassName, this);
            }
            if (mContext instanceof UpdataTitleListener) {
                mAlbumName = bean.getAlbumName();
                ((UpdataTitleListener) mContext).updataTitle(mAlbumName, isShowDetailsView);
            }
        }
        isShowDetailsView = !isShowDetailsView;
    }
    @Override
    protected void handleDetailsBack(int detailFlag) {
        super.handleDetailsBack(detailFlag);
        if (detailFlag == Constants.NUMBER_NINE) {
            mMusicView.setVisibility(View.VISIBLE);
            mDetailsView.setVisibility(View.GONE);
            mDetailsViewMap.remove(mClassName);
            isShowDetailsView = !isShowDetailsView;
        }
    }


    public static ArtistanListMusicFragment newInstance() {
        return new ArtistanListMusicFragment();
    }
}
