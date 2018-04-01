package com.yibao.music.fragment.dialogfrag;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import com.yibao.music.R;
import com.yibao.music.adapter.SplashPagerAdapter;
import com.yibao.music.util.DialogUtil;

/**
 * Author：Sid
 * Des：${TODO}
 * Time:2017/5/30 13:27
 *
 * @author Stran
 */
public class RelaxDialogFragment
        extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LinearLayout.inflate(getActivity(), R.layout.relax_dialog_fragment, null);
        initView(view);
        return DialogUtil.getDialogFag(getActivity(), view);
    }

    private void initView(View view) {
        ViewPager girlsViewPager = view.findViewById(R.id.vp_girls);
        SplashPagerAdapter splashPagerAdapter = new SplashPagerAdapter();
        girlsViewPager.setAdapter(splashPagerAdapter);
        splashPagerAdapter.setZoomViewClickListener(this::dismiss);
    }


    public static RelaxDialogFragment newInstance() {
        return new RelaxDialogFragment();
    }


}
