package com.yibao.music.playlist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.yibao.music.R;
import com.yibao.music.base.BaseFragment;
import com.yibao.music.dialogfrag.AddListDialog;
import com.yibao.music.factory.RecyclerFactory;
import com.yibao.music.model.AddNewListBean;
import com.yibao.music.model.MusicInfo;
import com.yibao.music.util.Constants;
import com.yibao.music.util.LogUtil;

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
    private PlayListAdapter mAdapter;
    private List<MusicInfo> mList;
    private CompositeDisposable mDisposable;
    private int addListFlag = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mList = mMusicInfoDao.queryBuilder().build().list();
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
        mDisposable = new CompositeDisposable();
        mAdapter = new PlayListAdapter(getActivity(), mList);
        RecyclerView recyclerView = RecyclerFactory.creatRecyclerView(Constants.NUMBER_ONE, mAdapter);
        mPlayListContent.addView(recyclerView);
    }

    private void receiveRxbuData() {

        mDisposable.add(mBus.toObserverable(AddNewListBean.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(addNewListBean -> {
                    MusicInfo info = new MusicInfo();
                    info.setPlayStatus(1);
                    info.setTitle(addNewListBean.getNewListTitle());
                    mList.add(info);
                    mAdapter.addData(mList);
                    mAdapter.notifyDataSetChanged();
                    if (addListFlag == Constants.NUMBER_ONE) {
                        mMusicInfoDao.insert(info);
                        addListFlag++;
                    }

                }));


    }

    private void initListener() {

        mAdapter.setItemListener(str -> {
            if (isShowDetailsView) {
                mPlayListContent.setVisibility(View.VISIBLE);
                mAlbumDetailsHeadContent.setVisibility(View.GONE);

            } else {
                mPlayListContent.setVisibility(View.GONE);
                mAlbumDetailsHeadContent.setVisibility(View.VISIBLE);
            }
            isShowDetailsView = !isShowDetailsView;
        });

    }


    @OnClick(R.id.ll_add_new_play_list)
    public void onClick(View v) {
        switch (v.getId()) {
            // 打开新建播放列表的Dialog
            case R.id.ll_add_new_play_list:

                AddListDialog.newInstance().show(getFragmentManager(), "addList");

                LogUtil.d("================新建播放列表====  " + mList.size());

                break;
            default:
                break;
        }
    }

    public static PlayListFragment newInstance() {
        return new PlayListFragment();
    }

    @Override
    protected int getFlag() {
        return Constants.NUMBER_ONE;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
