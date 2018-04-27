package com.yibao.music.fragment.dialogfrag;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.MainThread;
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
import com.jakewharton.rxbinding2.widget.TextViewTextChangeEvent;
import com.yibao.music.MusicApplication;
import com.yibao.music.R;
import com.yibao.music.model.AddNewListBean;
import com.yibao.music.model.MusicInfo;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.SnakbarUtil;

import org.w3c.dom.Text;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
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
    private InputMethodManager mInputMethodManager;
    private Disposable mSubscribe;

    public static AddListDialog newInstance() {

        return new AddListDialog();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
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
                addNewPlayList();
                break;
            default:
                break;
        }
    }

    private void addNewPlayList() {
        String listTitle = mEditAddList.getText().toString().trim();
        if (!listTitle.isEmpty()) {
            MusicApplication.getIntstance().getMusicInfoDao().insert(new MusicInfo(listTitle));
            MusicApplication.getIntstance().bus().post(new AddNewListBean());
            dismiss();
        } else {
            SnakbarUtil.favoriteFailView(mEditAddList);
        }
    }

    private void initListener() {
        mTvAddListCancle.setOnClickListener(this);
        mEditAddList.setSelection(mEditAddList.length());

    }


    private void initView() {
        mEditAddList = mView.findViewById(R.id.edit_add_list);
        mTvAddListCancle = mView.findViewById(R.id.tv_add_list_cancle);
        mTvAddListContinue = mView.findViewById(R.id.tv_add_list_continue);
        TextView noEdit = mView.findViewById(R.id.tv_add_list_continue_no);

        handSoftInput(noEdit);
    }

    private void handSoftInput(TextView noEdit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mInputMethodManager = (InputMethodManager) getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            showAndHintSoftInput(2, InputMethodManager.SHOW_FORCED);
        }
        mSubscribe = RxTextView.textChangeEvents(mEditAddList)
                .map(textViewTextChangeEvent -> TextUtils.isEmpty((textViewTextChangeEvent.text()))).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(aBoolean -> {
                    if (aBoolean) {
                        noEdit.setVisibility(View.VISIBLE);
                        mTvAddListContinue.setVisibility(View.INVISIBLE);
                        mTvAddListContinue.setOnClickListener(null);
                    } else {
                        noEdit.setVisibility(View.INVISIBLE);
                        mTvAddListContinue.setVisibility(View.VISIBLE);
                        mTvAddListContinue.setOnClickListener(this);
                    }

                });
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        showAndHintSoftInput(1, InputMethodManager.RESULT_UNCHANGED_SHOWN);
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
