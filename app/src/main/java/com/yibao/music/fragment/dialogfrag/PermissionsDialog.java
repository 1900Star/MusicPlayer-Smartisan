package com.yibao.music.fragment.dialogfrag;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.yibao.music.R;

/**
 * Author：Sid
 * Des：${TODO}
 * Time:2017/5/31 18:37
 *
 * @author Stran
 */
public class PermissionsDialog
        extends DialogFragment implements View.OnClickListener {


    private View mView;
    private TextView mTvAddListCancle;
    private TextView mTvAddListContinue;

    public static PermissionsDialog newInstance() {
        return new PermissionsDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        mView = getActivity().getLayoutInflater().inflate(R.layout.add_list_dialog, null);

        builder.setView(mView);
        AlertDialog dialog = builder.create();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.CENTER);
            window.setWindowAnimations(R.style.Theme_AppCompat_Dialog_Alert);
        }
        initView();
        initListener();
        return dialog;
    }

    private void initListener() {
        mTvAddListCancle.setOnClickListener(this);
        mTvAddListContinue.setOnClickListener(this);
    }


    private void initView() {
        TextView title = mView.findViewById(R.id.tv_title);
        mView.findViewById(R.id.edit_add_list).setVisibility(View.GONE);
        mTvAddListCancle = mView.findViewById(R.id.tv_add_list_cancle);
        mTvAddListContinue = mView.findViewById(R.id.tv_add_list_continue);
        mTvAddListContinue.setText(R.string.go_open);
        mTvAddListContinue.setVisibility(View.VISIBLE);
        title.setText(R.string.camera_not_open);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_add_list_cancle:
                dismiss();
                break;
            case R.id.tv_add_list_continue:
                toSelfSetting(getActivity());
                dismiss();
                break;
            default:
                break;
        }
    }

    private static void toSelfSetting(Context context) {
        Intent mIntent = new Intent();
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        mIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        context.startActivity(mIntent);
    }

    @Override
    public void onPause() {
        super.onPause();
        dismiss();
    }

}
