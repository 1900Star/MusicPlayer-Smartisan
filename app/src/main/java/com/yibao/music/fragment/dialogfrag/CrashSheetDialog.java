package com.yibao.music.fragment.dialogfrag;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yibao.music.R;
import com.yibao.music.adapter.CrashAdapter;
import com.yibao.music.base.factory.RecyclerFactory;
import com.yibao.music.util.Constants;
import com.yibao.music.util.FileUtil;

import java.io.File;
import java.util.List;

/**
 * Des：${TODO}
 * Time:2017/8/22 14:11
 *
 * @author Stran
 */
public class CrashSheetDialog {
    private Context mContext;
    private String mPackageName;
    private File mFiles;

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
        mFiles = new File(Constants.CRASH_LOG_PATH);
        if (mFiles.exists()) {
            File[] array = mFiles.listFiles();
            CrashAdapter crashAdapter = new CrashAdapter(array);
            RecyclerView recyclerView = RecyclerFactory.creatRecyclerView(Constants.NUMBER_ONE, crashAdapter);
            crashAdapter.setItemClickListener(this::openCrashLog);
            rootView.addView(recyclerView);
            TextView tvTitle = view.findViewById(R.id.tv_crash_title);
            tvTitle.setOnClickListener(v -> backTop(recyclerView));
            tvTitle.setOnLongClickListener(v -> {
                FileUtil.deleteFile(mFiles);
                dialog.dismiss();
                return true;
            });
        }

    }

    private void openCrashLog(File crashFile) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri contentUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            contentUri = FileProvider.getUriForFile(mContext, mPackageName, crashFile);
        } else {
            contentUri = Uri.fromFile(crashFile);
        }
        // 默认用WPS打开日志
        if (isAvilible()) {
            Bundle bundle = new Bundle();
            bundle.putString(WpsModel.OPEN_MODE, WpsModel.READ_MODE);
            bundle.putBoolean(WpsModel.SEND_CLOSE_BROAD, false);
            bundle.putString(WpsModel.THIRD_PACKAGE, mPackageName);
            bundle.putBoolean(WpsModel.CLEAR_TRACE, true);
            // bundle.putBoolean(CLEAR_FILE, true); //关闭后删除打开文件
            intent.setClassName(WpsModel.NORMAL_PACKAGE, WpsModel.NORMAL);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            intent.putExtras(bundle);
        } else {
            intent.setDataAndType(contentUri, Constants.DATA_TYPE_TXT);
        }
        mContext.startActivity(intent);


    }

    private boolean isAvilible() {
        String wpsPackageName = "cn.wps.moffice_eng";
        final PackageManager packageManager = mContext.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            if (pinfo.get(i).packageName.equalsIgnoreCase(wpsPackageName)) {
                return true;
            }
        }
        return false;
    }

    private void backTop(RecyclerView recyclerView) {
        CrashAdapter adapter = (CrashAdapter) recyclerView.getAdapter();
        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (adapter != null && manager != null) {
            int positionForSection = adapter.getPositionForSection(0);
            manager.scrollToPositionWithOffset(positionForSection, 0);
        }
    }

    private class WpsModel {
        static final String OPEN_MODE = "OpenMode";
        static final String SEND_CLOSE_BROAD = "SendCloseBroad";
        static final String THIRD_PACKAGE = "ThirdPackage";
        static final String CLEAR_TRACE = "ClearTrace";
        static final String READ_MODE = "ReadMode";
        static final String NORMAL = "cn.wps.moffice.documentmanager.PreStartActivity2";
        static final String NORMAL_PACKAGE = "cn.wps.moffice_eng";

    }
}


