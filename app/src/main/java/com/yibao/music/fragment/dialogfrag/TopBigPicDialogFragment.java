package com.yibao.music.fragment.dialogfrag;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yibao.music.R;
import com.yibao.music.base.BaseDialogFragment;
import com.yibao.music.util.AnimationUtil;
import com.yibao.music.util.ImageUitl;
import com.yibao.music.util.LogUtil;
import com.yibao.music.view.ProgressBtn;
import com.yibao.music.view.ZoomImageView;

/**
 * Author：Sid
 * Des：${TODO}
 * Time:2017/5/30 13:27
 * @author Stran
 */
public class TopBigPicDialogFragment
        extends BaseDialogFragment {

    private View mView;

    @Override
    public View getViews() {
        mView = LinearLayout.inflate(getActivity(), R.layout.top_dialog_fragment, null);
        initView();
        return mView;
    }

    public static TopBigPicDialogFragment newInstance(String url) {
        TopBigPicDialogFragment fragment = new TopBigPicDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        fragment.setArguments(bundle);
        return fragment;
    }

    private void initView() {
        LinearLayout topPicContent = mView.findViewById(R.id.top_big_pic_content);
        TextView save = mView.findViewById(R.id.tv_save);
        ProgressBtn pb = mView.findViewById(R.id.pb_toppic);
        pb.setMax(MAX_DOWN_PREGRESS);
        ZoomImageView view = ImageUitl.creatZoomView(getActivity());
        String url = getArguments().getString("url");

        ImageUitl.loadPic(getActivity(), url, view);
        view.setOnClickListener(view1 -> TopBigPicDialogFragment.this.dismiss());
        save.setOnClickListener(view12 -> LogUtil.d(""));
        AnimationUtil.applyBobbleAnim(topPicContent);
        topPicContent.addView(view);


    }


}
