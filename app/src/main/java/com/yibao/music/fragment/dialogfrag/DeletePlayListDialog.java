package com.yibao.music.fragment.dialogfrag;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.yibao.music.MusicApplication;
import com.yibao.music.R;
import com.yibao.music.model.AddAndDeleteListBean;
import com.yibao.music.model.MusicInfo;
import com.yibao.music.model.greendao.MusicInfoDao;
import com.yibao.music.util.Constants;
import com.yibao.music.util.RxBus;

/**
 * Author：Sid
 * Des：${删除列表}
 * Time:2017/5/31 18:37
 *
 * @author Stran
 */
public class DeletePlayListDialog
        extends DialogFragment implements View.OnClickListener {


    private View mView;

    private TextView mTvDelete;
    private TextView mTvCancelDelete;
    private MusicInfoDao mMusicInfoDao;
    private MusicInfo mMusicInfo;
    private RxBus mBus;

    public static DeletePlayListDialog newInstance(MusicInfo musicInfo) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("musicInfo", musicInfo);
        DeletePlayListDialog deletePlayListDialog = new DeletePlayListDialog();
        deletePlayListDialog.setArguments(bundle);
        return deletePlayListDialog;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        mView = getActivity().getLayoutInflater().inflate(R.layout.delete_list_dialog, null);
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
        mTvCancelDelete.setOnClickListener(this);
        mTvDelete.setOnClickListener(this);

    }


    private void initView() {
        TextView tvDeleteTitle = mView.findViewById(R.id.tv_delete_list_title);
        mTvCancelDelete = mView.findViewById(R.id.tv_delete_list_cancel);
        mTvDelete = mView.findViewById(R.id.tv_delete_list_continue);
        mBus = MusicApplication.getIntstance().bus();
        mMusicInfoDao = MusicApplication.getIntstance().getMusicInfoDao();
        mMusicInfo = getArguments().getParcelable("musicInfo");
        if (mMusicInfo != null) {
            String deleteTitle = "确定要删除 “ " + mMusicInfo.getTitle() + " ” 歌曲列表吗？";
            tvDeleteTitle.setText(deleteTitle);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_delete_list_cancel:
                dismiss();
                break;
            case R.id.tv_delete_list_continue:
                deletePlayList();
                dismiss();
                break;
            default:
                break;
        }
    }


    private void deletePlayList() {
        mMusicInfoDao.delete(mMusicInfo);
        mBus.post(new AddAndDeleteListBean(Constants.NUMBER_TWO));
    }


}
