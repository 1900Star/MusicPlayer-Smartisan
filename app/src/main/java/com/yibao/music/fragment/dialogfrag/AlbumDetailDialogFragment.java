package com.yibao.music.fragment.dialogfrag;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.yibao.music.R;
import com.yibao.music.base.listener.OnAlbumDetailListener;
import com.yibao.music.model.qq.AlbumSong;
import com.yibao.music.network.QqMusicRemote;
import com.yibao.music.network.RetrofitHelper;
import com.yibao.music.util.Constants;
import com.yibao.music.util.DialogUtil;

import java.util.Objects;

/**
 * Author：Sid
 * Des：${TODO}
 * Time:2017/5/30 13:27
 *
 * @author Stran
 */
public class AlbumDetailDialogFragment
        extends DialogFragment {

    private NestedScrollView mVpGirls;
    private ImageView mIvAlbumDetail;
    private TextView mTvAlbumDetailName;
    private TextView mTvAlbumDetailLanguage;
    private TextView mTvAlbumDetailDate;
    private TextView mTvAlbumDetailCompany;
    private TextView mTvAlbumDetailGenre;
    private TextView mTvAlbumDetailDesc;
    private static final String ALBUM_URL = "album_url";
    private static final String ALBUM_NAME = "album_name";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LinearLayout.inflate(getActivity(), R.layout.album_detail_dialog_fragment, null);
        initView(view);
        initData();
        return DialogUtil.getDialogFag(getActivity(), view);
    }

    private void initData() {
        if (getArguments() != null) {
            String albumUrl = getArguments().getString(ALBUM_URL);
            Glide.with(Objects.requireNonNull(getActivity())).load(albumUrl).into(mIvAlbumDetail);
            String albumName = getArguments().getString(ALBUM_NAME);
            QqMusicRemote.getAlbumDetail(albumName, dataBean -> {
                if (dataBean != null) {
                    mTvAlbumDetailName.setText(dataBean.getName());
                    mTvAlbumDetailLanguage.setText(dataBean.getLan());
                    mTvAlbumDetailDate.setText(dataBean.getADate());
                    mTvAlbumDetailCompany.setText(dataBean.getCompany());
                    mTvAlbumDetailGenre.setText(dataBean.getGenre());
                    mTvAlbumDetailDesc.setText(dataBean.getDesc());
                }

            });
            mIvAlbumDetail.setOnClickListener(v -> PreviewBigPicDialogFragment.newInstance(albumUrl)
                    .show(getChildFragmentManager(), "album"));
        }
    }

    private void initView(View view) {
        mVpGirls = view.findViewById(R.id.vp_girls);
        mIvAlbumDetail = view.findViewById(R.id.iv_album_detail);
        mTvAlbumDetailName = view.findViewById(R.id.tv_album_detail_name);
        mTvAlbumDetailLanguage = view.findViewById(R.id.tv_album_detail_language);
        mTvAlbumDetailDate = view.findViewById(R.id.tv_album_detail_date);
        mTvAlbumDetailCompany = view.findViewById(R.id.tv_album_detail_company);
        mTvAlbumDetailGenre = view.findViewById(R.id.tv_album_detail_genre);
        mTvAlbumDetailDesc = view.findViewById(R.id.tv_album_detail_desc);
    }


    public static AlbumDetailDialogFragment newInstance(String albumUrl, String albumName) {
        Bundle bundle = new Bundle();
        bundle.putString(ALBUM_URL, albumUrl);
        bundle.putString(ALBUM_NAME, albumName);
        AlbumDetailDialogFragment fragment = new AlbumDetailDialogFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


}
