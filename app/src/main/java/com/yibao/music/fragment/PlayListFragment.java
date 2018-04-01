package com.yibao.music.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.yibao.music.R;
import com.yibao.music.adapter.PlayListAdapter;
import com.yibao.music.base.BaseFragment;
import com.yibao.music.base.factory.RecyclerFactory;
import com.yibao.music.fragment.dialogfrag.AddListDialog;
import com.yibao.music.model.AddNewListBean;
import com.yibao.music.model.MusicInfo;
import com.yibao.music.util.Constants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;


/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.playlist
 * @文件名: PlayListFragment
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/9 16:07
 * @描述： {TODO}
 */

public class PlayListFragment extends BaseFragment {
    @BindView(R.id.ll_add_new_play_list)
    LinearLayout mLlAddNewPlayList;
    @BindView(R.id.play_list_content)
    LinearLayout mPlayListContent;
    @BindView(R.id.album_details_head_content)
    LinearLayout mAlbumDetailsHeadContent;
    private Unbinder unbinder;
    private com.yibao.music.playlist.PlayListAdapter mAdapter;
    private CompositeDisposable mDisposable;
    private int addListFlag = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDisposable = new CompositeDisposable();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.play_list_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        initData();
        receiveRxbuData();
        initListener();

        return view;
    }

    private void initData() {
        List<MusicInfo> playList = mMusicInfoDao.queryBuilder().list();
        mAdapter = new com.yibao.music.playlist.PlayListAdapter(playList);
        RecyclerView recyclerView = RecyclerFactory.creatRecyclerView(Constants.NUMBER_ONE, mAdapter);
        mPlayListContent.addView(recyclerView);

    }

    private void receiveRxbuData() {
        mDisposable.add(mBus.toObserverable(AddNewListBean.class)
                .subscribeOn(Schedulers.io()).map(addNewListBean -> mMusicInfoDao.queryBuilder().list())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(newPlayList -> mAdapter.addData(newPlayList))
        );


    }

    private void initListener() {

        mAdapter.setItemListener(str -> switchShowDetailsView());

    }

    private void switchShowDetailsView() {
        if (isShowDetailsView) {
            mLlAddNewPlayList.setVisibility(View.VISIBLE);
            mAlbumDetailsHeadContent.setVisibility(View.GONE);

        } else {
            mLlAddNewPlayList.setVisibility(View.GONE);
            mAlbumDetailsHeadContent.setVisibility(View.VISIBLE);
        }
        isShowDetailsView = !isShowDetailsView;
    }


    @OnClick(R.id.ll_add_new_play_list)
    public void onClick(View v) {
        switch (v.getId()) {
            // 打开新建播放列表的Dialog
            case R.id.ll_add_new_play_list:
                AddListDialog.newInstance().show(getFragmentManager(), "addList");
                break;
            default:
                break;
        }
    }

    public static PlayListFragment newInstance() {
        return new PlayListFragment();
    }

    private boolean isHandlePressed;

    @Override
    public boolean backPressed() {
//        switchShowDetailsView();
//        if (isHandlePressed) {
//            return false;
//        } else {
//            LogUtil.d("================Click MyFragment");
//            isHandlePressed = true;
//            return true;
//        }
        return mLlAddNewPlayList.getVisibility() == View.VISIBLE;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


}
