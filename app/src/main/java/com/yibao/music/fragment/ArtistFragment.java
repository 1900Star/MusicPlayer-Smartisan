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
import com.yibao.music.fragment.dialogfrag.MoreMenuBottomDialog;
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
 * @文件名: ArtistFragment
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/4 23:49
 * @描述： {TODO}
 */

public class ArtistFragment extends BaseMusicFragment {

    @BindView(R.id.artist_music_view)
    MusicView mMusicView;
    @BindView(R.id.details_view)
    DetailsView mDetailsView;
    private ArtistAdapter mAdapter;
    public static String detailsViewTitle;
    private List<ArtistInfo> mArtistList;
    public static boolean isShowDetailsView = false;
    private ArtistInfo mArtistInfo;
    private DetailsViewAdapter mDetailsAdapter;
    private List<MusicBean> mDetailList;

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
        mAdapter.setItemListener((bean, bean2) -> ArtistFragment.this.openDetailsView(bean));
    }


    private void initData() {
        mAdapter = new ArtistAdapter(mArtistList);
        mMusicView.setAdapter(getActivity(), Constants.NUMBER_TWO, true, mAdapter);

    }

    private void openDetailsView(ArtistInfo artistInfo) {
        mArtistInfo = artistInfo;
        if (isShowDetailsView) {
           removeFrag(mClassName);
            mDetailsView.setVisibility(View.GONE);
            mMusicView.setVisibility(View.VISIBLE);
            detailsViewTitle = null;
        } else {
            mDetailsView.setVisibility(View.VISIBLE);
            mDetailList = mMusicBeanDao.queryBuilder().where(MusicBeanDao.Properties.Artist.eq(artistInfo.getArtist())).build().list();
            // DetailsView播放音乐需要的参数
            mDetailsView.setDataFlag(mFragmentManager, mDetailList.size(), artistInfo.getArtist(), Constants.NUMBER_ONE);
            mDetailsAdapter = new DetailsViewAdapter(mActivity, mDetailList, Constants.NUMBER_ONE);
            mDetailsView.setAdapter( Constants.NUMBER_ONE, artistInfo, mDetailsAdapter);
            SpUtil.setDetailsFlag(mActivity, Constants.NUMBER_NINE);
            mDetailsAdapter.setOnItemMenuListener((int position, MusicBean musicBean) ->
                    MoreMenuBottomDialog.newInstance(musicBean, position,false).getBottomDialog(mActivity));
           putFragToMap(mClassName);
            detailsViewTitle = artistInfo.getAlbumName();
            changeToolBarTitle(artistInfo.getAlbumName(), isShowDetailsView);
        }
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
        super.handleDetailsBack(detailFlag);
        if (detailFlag == Constants.NUMBER_NINE) {
            openDetailsView(mArtistInfo);
        }
    }

    @Override
    protected void changeEditStatus(int currentIndex) {
    }

    public static ArtistFragment newInstance() {
        return new ArtistFragment();
    }
}
