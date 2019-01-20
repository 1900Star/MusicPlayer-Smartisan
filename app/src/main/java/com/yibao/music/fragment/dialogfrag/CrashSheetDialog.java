package com.yibao.music.fragment.dialogfrag;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.yibao.music.R;
import com.yibao.music.adapter.BottomSheetAdapter;
import com.yibao.music.adapter.CrashAdapter;
import com.yibao.music.base.factory.RecyclerFactory;
import com.yibao.music.util.Constants;

import java.io.File;

/**
 * Des：${TODO}
 * Time:2017/8/22 14:11
 *
 * @author Stran
 */
public class CrashSheetDialog {
    private Context mContext;
    private String mPackageName;

    public static CrashSheetDialog newInstance() {
        return new CrashSheetDialog();
    }

    public void getBottomDialog(Context context) {
        this.mContext = context;
        mPackageName = mContext.getPackageName();
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context)
                .inflate(R.layout.crash_bottom_sheet_dialog, null);
        init(dialog, view);
        dialog.show();
    }


    private void init(BottomSheetDialog dialog, View view) {
        dialog.setContentView(view);
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        if (window != null) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        dialog.setCanceledOnTouchOutside(true);
        LinearLayout rootView = view.findViewById(R.id.root_crash);
        // 读取CrashLog
        File files = new File(Constants.CRASH_LOG_PATH);
        File[] array = files.listFiles();
        CrashAdapter crashAdapter = new CrashAdapter(array);
        RecyclerView recyclerView = RecyclerFactory.creatRecyclerView(Constants.NUMBER_ONE, crashAdapter);
        crashAdapter.setItemClickListener(this::openCrashLog);
        rootView.addView(recyclerView);
        view.findViewById(R.id.tv_crash_title).setOnClickListener(v -> backTop(recyclerView));
    }

    private void openCrashLog(File crashFile) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString(WpsModel.OPEN_MODE, WpsModel.READ_MODE); // 打开模式
        bundle.putBoolean(WpsModel.SEND_CLOSE_BROAD, false); // 关闭时是否发送广播
        bundle.putString(WpsModel.THIRD_PACKAGE, mPackageName); // 第三方应用的包名，用于对改应用合法性的验证
        bundle.putBoolean(WpsModel.CLEAR_TRACE, true);// 清除打开记录
        // bundle.putBoolean(CLEAR_FILE, true); //关闭后删除打开文件
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setClassName(WpsModel.NORMAL_PACKAGE, WpsModel.NORMAL);
        Uri contentUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            contentUri = FileProvider.getUriForFile(mContext, mPackageName + ".fileprovider", crashFile);
        } else {
            contentUri = Uri.fromFile(crashFile);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    private void backTop(RecyclerView recyclerView) {
        CrashAdapter adapter = (CrashAdapter) recyclerView.getAdapter();
        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (adapter != null && manager != null) {
            int positionForSection = adapter.getPositionForSection(0);
            manager.scrollToPositionWithOffset(positionForSection, 0);
        }
    }

    public class WpsModel {
        static final String OPEN_MODE = "OpenMode";// 打开文件的模式。
        static final String SEND_CLOSE_BROAD = "SendCloseBroad";// 文件关闭时是否发送广播
        static final String THIRD_PACKAGE = "ThirdPackage";// 第三方的包名，关闭的广播会包含该项。
        static final String CLEAR_TRACE = "ClearTrace";// 关闭文件时是否删除使用记录。
        static final String READ_MODE = "ReadMode";// 打开直接进入阅读器模式
        static final String NORMAL = "cn.wps.moffice.documentmanager.PreStartActivity2";// 普通版
        static final String NORMAL_PACKAGE = "cn.wps.moffice_eng";// 普通版

    }
}


