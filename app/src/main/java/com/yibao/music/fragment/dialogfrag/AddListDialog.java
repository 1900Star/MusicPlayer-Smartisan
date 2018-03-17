package com.yibao.music.dialogfrag;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.yibao.music.MusicApplication;
import com.yibao.music.R;
import com.yibao.music.model.AddNewListBean;
import com.yibao.music.model.MusicInfo;
import com.yibao.music.util.SnakbarUtil;

/**
 * Author：Sid
 * Des：${TODO}
 * Time:2017/5/31 18:37
 *
 * @author Stran
 */
public class AddListDialog
        extends DialogFragment implements View.OnClickListener {


    private View mView;
    /**
     * 未命名播放列表 1
     */
    private EditText mEditAddList;
    /**
     * 取消
     */
    private TextView mTvAddListCancle;
    /**
     * 继续
     */
    private TextView mTvAddListContinue;

    public static AddListDialog newInstance() {

        return new AddListDialog();
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        mView = getActivity().getLayoutInflater().inflate(R.layout.add_lit_dialog, null);
        builder.setView(mView);
        AlertDialog dialog = builder.create();
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.setWindowAnimations(R.style.Theme_AppCompat_Dialog_Alert);
        initView();
        initListener();
        return dialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_add_list_cancle:
                dismiss();
                break;
            case R.id.tv_add_list_continue:
                String listTitle = mEditAddList.getText().toString().trim();
                if (!listTitle.isEmpty()) {
                    MusicApplication.getIntstance().getMusicInfoDao().insert(new MusicInfo(listTitle));
                    MusicApplication.getIntstance().bus().post(new AddNewListBean());
                    dismiss();
                } else {
                    SnakbarUtil.favoriteFailView(mEditAddList);
                }
                break;
            default:
                break;
        }
    }

    private void initListener() {
        mTvAddListCancle.setOnClickListener(this);
        mTvAddListContinue.setOnClickListener(this);
        mEditAddList.setSelection(mEditAddList.length());

    }


    private void initView() {
        mEditAddList = mView.findViewById(R.id.edit_add_list);
        mTvAddListCancle = mView.findViewById(R.id.tv_add_list_cancle);
        mTvAddListContinue = mView.findViewById(R.id.tv_add_list_continue);
//        mEditAddList.setFocusable(true);
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//            InputMethodManager imm = (InputMethodManager) getContext()
//                    .getSystemService(Context.INPUT_METHOD_SERVICE);
//
//            if (imm != null) {
//                imm.showSoftInput(mEditAddList, InputMethodManager.RESULT_SHOWN);
//                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
//                        InputMethodManager.HIDE_IMPLICIT_ONLY);
//            }
//        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();


    }


}
