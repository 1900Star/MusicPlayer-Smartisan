package com.yibao.music.folder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yibao.music.R;
import com.yibao.music.base.BaseFragment;
import com.yibao.music.util.LogUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.folder
 * @文件名: FolderListFragment
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/9 20:51
 * @描述： {TODO}
 */

public class FolderListFragment extends BaseFragment {

    @BindView(R.id.iv_folder)
    ImageView mIvFolder;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.folder_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        initData();
        return view;
    }

    private void initData() {
        mIvFolder.setOnClickListener(view -> {
            LogUtil.d("  ========================------------------=-=-=-=-====   ", "");
            LogUtil.d("  ==============   ", "");
        });
    }

    public static FolderListFragment newInstance() {

        return new FolderListFragment();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
