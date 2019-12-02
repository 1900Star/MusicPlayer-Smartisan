package com.yibao.music.fragment.dialogfrag;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yibao.music.R;
import com.yibao.music.base.listener.BottomSheetCallback;
import com.yibao.music.util.Constants;
import com.yibao.music.util.FileUtil;
import com.yibao.music.util.PermissionsUtil;

/**
 * Des：${TODO}
 * Time:2017/8/22 14:11
 *
 * @author Stran
 */
public class TakePhotoBottomSheetDialog {
    private Activity mContext;
    private View mTvCancel;
    private View mTvTakePhoto;
    private View mTvChoicePhoto;

    public static TakePhotoBottomSheetDialog newInstance() {
        return new TakePhotoBottomSheetDialog();
    }

    public void getBottomDialog(Activity context) {
        this.mContext = context;
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context)
                .inflate(R.layout.takephoto_dialog_fragment, null);
        initView(dialog, view);
        initListener(dialog);
        dialog.show();
    }


    private void initListener(BottomSheetDialog dialog) {
        mTvCancel.setOnClickListener(v -> dialog.dismiss());
        mTvTakePhoto.setOnClickListener(v -> {
            if (PermissionsUtil.cameraPermission()) {
                takeCameraPic();
                dialog.dismiss();
            } else {
                AndPermission.with(mContext)
                        .permission(Permission.Group.CAMERA)
                        .onGranted(permissions -> {
                            TakePhotoBottomSheetDialog.this.takeCameraPic();
                            dialog.dismiss();
                        })
                        .onDenied(permissions -> PermissionsDialog.newInstance().show(mContext.getFragmentManager(), "permissions"))
                        .start();
            }

        });
        mTvChoicePhoto.setOnClickListener(v -> {
            TakePhotoBottomSheetDialog.this.choicePhoto();
            dialog.dismiss();
        });
    }

    private void takeCameraPic() {

        String savePath = Environment.getExternalStorageDirectory().toString();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (FileUtil.hasSdcard()) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    FileUtil.getPicUri(mContext, savePath));
            mContext.startActivityForResult(intent, Constants.CODE_CAMERA_REQUEST);
        }
    }

    private void choicePhoto() {
        AndPermission.with(mContext)
                .permission(Permission.Group.STORAGE)
                .onGranted(permissions -> {
                    Intent intentFromGallery = new Intent(Intent.ACTION_PICK, null);
                    intentFromGallery.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    mContext.startActivityForResult(intentFromGallery, Constants.CODE_GALLERY_REQUEST);
                })
                .onDenied(permissions -> Log.d("lsp", "没有读取和写入的权限!"))
                .start();

    }

    private void initView(BottomSheetDialog dialog, View view) {
        mTvCancel = view.findViewById(R.id.tv_take_photo_cancel);
        mTvTakePhoto = view.findViewById(R.id.tv_take_photo);
        mTvChoicePhoto = view.findViewById(R.id.tv_choice_photo);
        dialog.setContentView(view);
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        if (window != null) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        BottomSheetBehavior<View> sheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        dialog.setCanceledOnTouchOutside(true);
        dialog.setOnDismissListener(dialog12 -> {
        });
        sheetBehavior.setBottomSheetCallback(new BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
    }


}


