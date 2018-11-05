package com.yibao.music.fragment.dialogfrag;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.yibao.music.MusicApplication;
import com.yibao.music.R;
import com.yibao.music.model.AddAndDeleteListBean;
import com.yibao.music.model.MusicInfo;
import com.yibao.music.model.PlayListBean;
import com.yibao.music.util.Constants;
import com.yibao.music.util.SnakbarUtil;
import com.yibao.music.util.SoftKeybordUtil;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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
    private EditText mEditAddList;
    private TextView mTvAddListCancle;
    private TextView mTvAddListContinue;
    private InputMethodManager mInputMethodManager;
    private Disposable mSubscribe;
    private TextView mNoEdit;

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
        if (window != null) {
            window.setGravity(Gravity.CENTER);
            window.setWindowAnimations(R.style.Theme_AppCompat_Dialog_Alert);
        }
        initView();
        initData();
        initListener();
        return dialog;
    }

    private void initListener() {
        mTvAddListCancle.setOnClickListener(this);
        mEditAddList.setSelection(mEditAddList.length());
    }


    private void initView() {
        mEditAddList = mView.findViewById(R.id.edit_add_list);
        mTvAddListCancle = mView.findViewById(R.id.tv_add_list_cancle);
        mTvAddListContinue = mView.findViewById(R.id.tv_add_list_continue);
        mNoEdit = mView.findViewById(R.id.tv_add_list_cancel);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_add_list_cancle:
                dismiss();
                break;
            case R.id.tv_add_list_continue:
                addNewPlayList();
                break;
            default:
                break;
        }
    }

    private void addNewPlayList() {
        String listTitle = mEditAddList.getText().toString().trim();

        if (!listTitle.isEmpty()) {
            MusicApplication.getIntstance().getPlayListDao().insert(new PlayListBean(listTitle, System.currentTimeMillis()));
            dismiss();
            MusicApplication.getIntstance().bus().post(new AddAndDeleteListBean(Constants.NUMBER_ONE));
        }
    }


    private void initData() {
        // 主动弹出键盘
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mInputMethodManager = (InputMethodManager) getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            SoftKeybordUtil.showAndHintSoftInput(mInputMethodManager, 2, InputMethodManager.SHOW_FORCED);
        }
        mSubscribe = RxTextView.textChangeEvents(mEditAddList)
                .map(textViewTextChangeEvent -> {
                    if (textViewTextChangeEvent.text().length() == 21) {
                        SnakbarUtil.favoriteFailView(mView);
                    }
                    return TextUtils.isEmpty((textViewTextChangeEvent.text()));
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        mNoEdit.setVisibility(View.VISIBLE);
                        mTvAddListContinue.setVisibility(View.INVISIBLE);
                        mTvAddListContinue.setOnClickListener(null);
                    } else {
                        mNoEdit.setVisibility(View.INVISIBLE);
                        mTvAddListContinue.setVisibility(View.VISIBLE);
                        mTvAddListContinue.setOnClickListener(this);
                    }

                });
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        SoftKeybordUtil.showAndHintSoftInput(mInputMethodManager, 1, InputMethodManager.RESULT_UNCHANGED_SHOWN);

    }

    @Override
    public void onPause() {
        super.onPause();
        dismiss();
    }

    private void showAndHintSoftInput(int i, int resultUnchangedShown) {
        if (mInputMethodManager != null) {
            mInputMethodManager.toggleSoftInput(i,
                    resultUnchangedShown);

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSubscribe != null) {
            mSubscribe.dispose();
        }
    }
}
