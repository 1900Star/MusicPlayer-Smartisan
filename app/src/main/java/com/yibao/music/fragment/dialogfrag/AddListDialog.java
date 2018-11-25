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
import android.text.Selection;
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
import com.yibao.music.model.greendao.PlayListBeanDao;
import com.yibao.music.util.Constants;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.RxBus;
import com.yibao.music.util.SnakbarUtil;
import com.yibao.music.util.SoftKeybordUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
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


    private static int mOperationType = 1;
    private View mView;
    private EditText mEditAddList;
    private TextView mTvAddListCancle;
    private TextView mTvAddListContinue;
    private InputMethodManager mInputMethodManager;
    private TextView mNoEdit;
    private CompositeDisposable mCompositeDisposable;
    private static String mEditHint;

    public static AddListDialog newInstance(int operationType, String editHint) {
        mOperationType = operationType;
        mEditHint = editHint;
        return new AddListDialog();
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mCompositeDisposable = new CompositeDisposable();
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
        TextView title = mView.findViewById(R.id.tv_title);
        mEditAddList = mView.findViewById(R.id.edit_add_list);
        mTvAddListCancle = mView.findViewById(R.id.tv_add_list_cancle);
        mTvAddListContinue = mView.findViewById(R.id.tv_add_list_continue);
        mNoEdit = mView.findViewById(R.id.tv_add_list_cancel);
        title.setText(mOperationType == Constants.NUMBER_ONE ? R.string.add_new_play_list : R.string.rename_tile);
        mEditAddList.setHint(mEditHint);
//        Selection.selectAll(mEditAddList.getText());
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
        PlayListBeanDao dao = MusicApplication.getIntstance().getPlayListDao();
        if (!listTitle.isEmpty()) {
            List<PlayListBean> beanList = dao.queryBuilder().where(PlayListBeanDao.Properties.Title.eq(listTitle)).list();
            if (beanList.size() > 0) {
                SnakbarUtil.favoriteSuccessView(mEditAddList, "播放列表已存在");
            } else {
                if (mOperationType == Constants.NUMBER_ONE) {
                    dao.insert(new PlayListBean(listTitle, System.currentTimeMillis()));
                    dismiss();
                    RxBus.getInstance().post(new AddAndDeleteListBean(Constants.NUMBER_ONE));
                } else {
                    dismiss();
                    RxBus.getInstance().post(new AddAndDeleteListBean(Constants.NUMBER_FOUR, listTitle));
                }
            }

        }
    }


    private void initData() {

        // 主动弹出键盘
        mCompositeDisposable.add(Observable.timer(50, TimeUnit.MILLISECONDS)
                .subscribe(aLong -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        mInputMethodManager = (InputMethodManager) getContext()
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        SoftKeybordUtil.showAndHintSoftInput(mInputMethodManager, 2, InputMethodManager.SHOW_FORCED);
                    }
                })
        );
        mCompositeDisposable.add(RxTextView.textChangeEvents(mEditAddList)
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

                }));
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
            mCompositeDisposable.dispose();
            mCompositeDisposable = null;
        }
    }
}
