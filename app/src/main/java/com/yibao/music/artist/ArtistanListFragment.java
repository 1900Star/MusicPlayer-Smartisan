package com.yibao.music.artist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yibao.music.R;
import com.yibao.music.base.BaseFragment;
import com.yibao.music.util.Constants;
import com.yibao.music.view.music.MusicView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.artisanlist
 * @文件名: ArtistanListFragment
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/4 23:49
 * @描述： {TODO}
 */

public class ArtistanListFragment extends BaseFragment {

    @BindView(R.id.artist_music_view)
    MusicView mMusicView;
    private Unbinder unbinder;
//    private ArrayList<ArtistInfo> mArtistList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.artisan_list_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);

        initData();
        initListener();
        return view;
    }


    private void initListener() {
//
    }

    private void initData() {
        ArtistAdapter adapter = new ArtistAdapter(getActivity(), mArtistList);
        mMusicView.setAdapter(getActivity(), Constants.NUMBER_TWO, adapter);


    }

    public static ArtistanListFragment newInstance() {

        return new ArtistanListFragment();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
