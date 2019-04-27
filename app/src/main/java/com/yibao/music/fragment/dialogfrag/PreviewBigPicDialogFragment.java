package com.yibao.music.fragment.dialogfrag;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yibao.music.R;
import com.yibao.music.base.BaseDialogFragment;
import com.yibao.music.util.AnimationUtil;
import com.yibao.music.util.ImageUitl;
import com.yibao.music.view.ZoomImageView;

/**
 * Author：Sid
 * Des：${TODO}
 * Time:2017/5/30 13:27
 * @author Stran
 */
public class PreviewBigPicDialogFragment
        extends BaseDialogFragment {

    private View mView;

    @Override
    public View getViews() {
        mView = LinearLayout.inflate(getActivity(), R.layout.top_dialog_fragment, null);
        initView();
        return mView;
    }

    public static PreviewBigPicDialogFragment newInstance(String url) {
        PreviewBigPicDialogFragment fragment = new PreviewBigPicDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        fragment.setArguments(bundle);
        return fragment;
    }

    private void initView() {
        ZoomImageView view = mView.findViewById(R.id.zoom_view);
        view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        String url = getArguments().getString("url");
        ImageUitl.loadPlaceholder(getActivity(), url, view);
        view.setOnClickListener(view1 -> PreviewBigPicDialogFragment.this.dismiss());
        AnimationUtil.applyBobbleAnim(view);


    }


}
