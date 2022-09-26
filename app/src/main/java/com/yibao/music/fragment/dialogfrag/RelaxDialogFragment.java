package com.yibao.music.fragment.dialogfrag;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;

import com.yibao.music.R;
import com.yibao.music.adapter.SplashPagerAdapter;
import com.yibao.music.util.Constant;
import com.yibao.music.util.DialogUtil;
import com.yibao.music.util.SpUtils;

/**
 * Author：Sid
 * Des：${TODO}
 * Time:2017/5/30 13:27
 *
 * @author Stran
 */
public class RelaxDialogFragment
        extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LinearLayout.inflate(getActivity(), R.layout.relax_dialog_fragment, null);
        initView(view);
        return DialogUtil.getDialogFag(getActivity(), view);
    }

    private void initView(View view) {
        ViewPager girlsViewPager = view.findViewById(R.id.vp_girls);
        SpUtils sp = new SpUtils(requireContext().getApplicationContext(), Constant.MUSIC_CONFIG);
        boolean urlFlag = sp.getBoolean(Constant.PIC_URL_FLAG,false);
        SplashPagerAdapter splashPagerAdapter = new SplashPagerAdapter(urlFlag);
        girlsViewPager.setAdapter(splashPagerAdapter);
//        splashPagerAdapter.setZoomViewClickListener(this::dismiss);
    }


    public static RelaxDialogFragment newInstance() {
        return new RelaxDialogFragment();
    }


}
