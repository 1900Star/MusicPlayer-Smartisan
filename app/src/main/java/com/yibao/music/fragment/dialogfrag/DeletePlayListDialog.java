package com.yibao.music.fragment.dialogfrag;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.yibao.music.MusicApplication;
import com.yibao.music.R;
import com.yibao.music.model.AddAndDeleteListBean;
import com.yibao.music.model.PlayListBean;
import com.yibao.music.model.greendao.MusicBeanDao;
import com.yibao.music.util.Constant;
import com.yibao.music.util.MusicDaoUtil;
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
    private PlayListBean mPlayListBean;
    private RxBus mBus;
    private int mPageType;
    private static SwipeRefreshLayout.OnRefreshListener mListener;

    public static DeletePlayListDialog newInstance(PlayListBean musicInfo, int pageType, SwipeRefreshLayout.OnRefreshListener listener) {
        mListener = listener;
        Bundle bundle = new Bundle();
        bundle.putParcelable("musicInfo", musicInfo);
        bundle.putInt("pageType", pageType);
        DeletePlayListDialog deletePlayListDialog = new DeletePlayListDialog();
        deletePlayListDialog.setArguments(bundle);
        return deletePlayListDialog;
    }


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
        mBus = RxBus.getInstance();
        MusicBeanDao musicDao = MusicApplication.getInstance().getMusicDao();
        mPlayListBean = getArguments().getParcelable("musicInfo");
        mPageType = getArguments().getInt("pageType");
        if (mPlayListBean != null) {
            String deleteTitle = getString(R.string.confirm_delete) + mPlayListBean.getTitle() + getString(R.string.song_list);
//            tvDeleteTitle.setText(deleteTitle);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_delete_list_cancel) {
            dismiss();
        } else if (id == R.id.tv_delete_list_continue) {
            deletePlayList();
            dismiss();
        }
    }


    private void deletePlayList() {
        if (mPageType == Constant.NUMBER_TWO) {
            // 同步更新列表中，的歌曲的列表标识 (更新为“LSP_98”)
            MusicDaoUtil.setMusicListFlag(mPlayListBean);
        }
        mBus.post(new AddAndDeleteListBean(mPageType));
        mListener.onRefresh();
    }


}
