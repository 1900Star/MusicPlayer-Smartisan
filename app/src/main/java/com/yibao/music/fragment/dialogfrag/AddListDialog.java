package com.yibao.music.fragment.dialogfrag;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.yibao.music.MusicApplication;
import com.yibao.music.R;
import com.yibao.music.base.listener.TextChangedListener;
import com.yibao.music.model.PlayListBean;
import com.yibao.music.model.greendao.PlayListBeanDao;
import com.yibao.music.util.Constant;
import com.yibao.music.util.SnakbarUtil;
import com.yibao.music.util.SoftKeybordUtil;
import com.yibao.music.util.ToastUtil;
import com.yibao.music.viewmodel.PlayListViewModel;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Author：Sid
 * Des：${TODO}
 * Time:2017/5/31 18:37
 *
 * @author Stran
 */
public class AddListDialog
        extends DialogFragment implements View.OnClickListener {


    private static int mOperationType = 1;
    private View mView;
    private EditText mEditAddList;
    private TextView mTvAddListCancle;
    private TextView mTvAddListContinue;
    private InputMethodManager mInputMethodManager;
    private TextView mNoInputTv;

    private static String mEditHint;
    private static boolean isFormPlayListActivity;
    private static final int MAX_LENGTH = 21;
    private static SwipeRefreshLayout.OnRefreshListener mListener;
    private final PlayListViewModel mViewModel = new PlayListViewModel();

    /**
     * @param operationType 1 新建列表  2 列表改名
     * @param editHint      e
     * @param b             PlayListActivity弹出新建AddListDialog  时赋值为 true 。
     * @return c
     */
    public static AddListDialog newInstance(int operationType, String editHint, boolean b, SwipeRefreshLayout.OnRefreshListener listener) {
        mOperationType = operationType;
        mEditHint = editHint;
        isFormPlayListActivity = b;
        mListener = listener;
        return new AddListDialog();
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        mView = requireActivity().getLayoutInflater().inflate(R.layout.add_list_dialog, null);

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
        mEditAddList.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                if (str.length() == MAX_LENGTH) {
                    SnakbarUtil.favoriteFailView(mView, "列表名的长度不能超过21个字符");
                } else if (str.isEmpty()) {
                    mNoInputTv.setVisibility(View.VISIBLE);
                    mTvAddListContinue.setVisibility(View.INVISIBLE);
                    mTvAddListContinue.setOnClickListener(null);
                } else {
                    mNoInputTv.setVisibility(View.INVISIBLE);
                    mTvAddListContinue.setVisibility(View.VISIBLE);
                    mTvAddListContinue.setOnClickListener(v -> addNewPlayList());
                }


            }
        });

    }


    private void initView() {
        TextView title = mView.findViewById(R.id.tv_title);
        mEditAddList = mView.findViewById(R.id.edit_add_list);
        mTvAddListCancle = mView.findViewById(R.id.tv_add_list_cancle);
        mTvAddListContinue = mView.findViewById(R.id.tv_add_list_continue);
        mNoInputTv = mView.findViewById(R.id.tv_no_input_cancel);
        mNoInputTv.setVisibility(View.VISIBLE);
        title.setText(mOperationType == Constant.NUMBER_ONE ? R.string.add_new_play_list : R.string.rename_tile);
        mTvAddListContinue.setText(getResources().getString(isFormPlayListActivity ? R.string.save_and_add : R.string.continues));
        mEditAddList.setHint(mEditHint);
    }


    @Override
    public void onResume() {
        super.onResume();
        mViewModel.getResultModel().observe(this, message -> {
            if (message.getCode() == 0) {
                mListener.onRefresh();
                dismiss();
            } else if (message.getCode() == 100) {
                ToastUtil.show(getActivity(), message.getMsgKey());
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_add_list_cancle) {
            dismiss();
        }
    }

    // 添加列表
    private void addNewPlayList() {
        String listTitle = mEditAddList.getText().toString().trim();
        if (listTitle.isEmpty()) {
            ToastUtil.show(getActivity(), "输入名称");
        } else {
            mViewModel.addPlayList(listTitle, mOperationType);
        }

    }


    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private void initData() {

        // 主动弹出键盘
        mHandler.postDelayed(() -> {
            mInputMethodManager = (InputMethodManager) getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            SoftKeybordUtil.showAndHintSoftInput(mInputMethodManager, 2, InputMethodManager.SHOW_FORCED);

        }, 50);

    }


    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        SoftKeybordUtil.showAndHintSoftInput(mInputMethodManager, 1, InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    @Override
    public void onPause() {
        super.onPause();
        dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
