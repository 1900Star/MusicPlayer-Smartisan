package com.yibao.music.fragment.dialogfrag;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.fragment.app.DialogFragment;

import com.yibao.music.MusicApplication;
import com.yibao.music.R;
import com.yibao.music.activity.SplashActivity;
import com.yibao.music.model.AddAndDeleteListBean;
import com.yibao.music.model.Message;
import com.yibao.music.model.PlayListBean;
import com.yibao.music.model.greendao.MusicBeanDao;
import com.yibao.music.util.Constants;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.MusicDaoUtil;
import com.yibao.music.util.RxBus;
import com.yibao.music.util.SharedPreferencesUtil;

/**
 * Author：Sid
 * Des：${删除列表}
 * Time:2017/5/31 18:37
 *
 * @author Stran
 */
public class ScannerConfigDialog
        extends DialogFragment implements View.OnClickListener {
    private static final String TAG = " ==== " + ScannerConfigDialog.class.getSimpleName() + "  ";

    private View mView;

    private AppCompatCheckBox mCheckBoxDuration;
    private AppCompatCheckBox mCheckBoxSize;
    private TextView mTvCancel;
    private TextView mTvContinue;
    private SharedPreferencesUtil mSp;
    private static boolean mLoadFlag;
    private RxBus mBus;

    /**
     * @param loadFlag true 自动扫描 、 false 手动扫描
     * @return ScannerConfigDialog
     */
    public static ScannerConfigDialog newInstance(boolean loadFlag) {
        mLoadFlag = loadFlag;
        return new ScannerConfigDialog();
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        mView = getActivity().getLayoutInflater().inflate(R.layout.scanner_config_dialog, null);
        builder.setView(mView);
        AlertDialog dialog = builder.create();
        Window window = dialog.getWindow();
        setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        if (window != null) {
            window.setGravity(Gravity.CENTER);
            window.setWindowAnimations(R.style.Theme_AppCompat_Dialog_Alert);
        }
        mBus = RxBus.getInstance();
        mSp = new SharedPreferencesUtil(getActivity().getApplicationContext(), Constants.MUSIC_SETTING);
        initView();
        initListener();
        return dialog;
    }


    private void initListener() {
        mTvCancel.setOnClickListener(this);
        mTvContinue.setOnClickListener(this);
        mCheckBoxSize.setOnClickListener(this);
        mCheckBoxDuration.setOnClickListener(this);
    }


    private void initView() {
        mTvCancel = mView.findViewById(R.id.tv_scanner_cancel);
        mTvContinue = mView.findViewById(R.id.tv_scanner_continue);
        mCheckBoxDuration = mView.findViewById(R.id.checkbox_config_scanner_duration);
        mCheckBoxSize = mView.findViewById(R.id.checkbox_config_scanner_file_size);
        boolean aBooleanFileSize = mSp.getBoolean(Constants.MUSIC_FILE_SIZE_FLAG, false);
        boolean aBooleanDuration = mSp.getBoolean(Constants.MUSIC_DURATION_FLAG, false);
        mCheckBoxSize.setChecked(aBooleanFileSize);
        mCheckBoxDuration.setChecked(aBooleanDuration);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_scanner_cancel:
                if (mLoadFlag) {
                    mBus.post("auto_load");
                }
                dismiss();
                break;
            case R.id.tv_scanner_continue:
                mSp.putValues(new SharedPreferencesUtil.ContentValue(Constants.MUSIC_FILE_SIZE_FLAG, mCheckBoxSize.isChecked()),
                        new SharedPreferencesUtil.ContentValue(Constants.MUSIC_DURATION_FLAG, mCheckBoxDuration.isChecked()));
                if (mLoadFlag) {
                    mBus.post("auto_load");
                } else {
                    Intent intent = new Intent(getActivity(), SplashActivity.class);
                    intent.putExtra(Constants.SCANNER_MEDIA, Constants.SCANNER_MEDIA);
                    startActivity(intent);

                }
                dismiss();
                break;
            default:
                break;
        }
    }


}
