package com.yibao.music.fragment.dialogfrag;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.yibao.music.R;
import com.yibao.music.util.ApkVersionUtil;


public class VersionDialog extends DialogFragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LinearLayout.inflate(getActivity(), R.layout.version_dialog, null);
        initView(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {

            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.shape_dialog_bg);
        }
    }

    private void initView(View view) {
        TextView versionNumber = view.findViewById(R.id.tv_version_number);
        TextView versionName = view.findViewById(R.id.tv_version_name);
        int versionCode = ApkVersionUtil.getVersionCode(requireActivity().getApplicationContext());
        versionNumber.setText(String.valueOf(versionCode));
        versionName.setText(ApkVersionUtil.getVersionName(requireActivity().getApplicationContext()));
    }


    public static VersionDialog newInstance() {
        return new VersionDialog();
    }
}