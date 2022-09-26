package com.yibao.music.util;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;

import com.yibao.music.MusicApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;


/**
 * 作者：Stran on 2017/3/23 15:26
 * 描述：${ bitmap处理工具类}
 * 邮箱：strangermy@outlook.com
 * @author Luoshipeng
 */
public class BitmapUtil {

    public static android.graphics.Bitmap drawableToBitmap(Drawable drawable) {

        android.graphics.Bitmap bitmap = android.graphics.Bitmap.createBitmap(

                drawable.getIntrinsicHeight(),

                drawable.getIntrinsicWidth(),

                drawable.getOpacity() != PixelFormat.OPAQUE
                ? android.graphics.Bitmap.Config.ARGB_8888

                : android.graphics.Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(bitmap);


        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        drawable.draw(canvas);

        return bitmap;
    }

    public static boolean saveBitmap(Bitmap bitmap, String dir, String name, boolean isShowPhotos) {
        File path = new File(dir);
        if (!path.exists()) {
            path.mkdirs();
        }
        File file = new File(path + "/" + name);
        if (file.exists()) {
            file.delete();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return true;
        }
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 其次把文件插入到系统图库
        if (isShowPhotos) {
            try {
                MediaStore.Images.Media.insertImage(MusicApplication.getInstance()
                                                                 .getContentResolver(),
                                                    file.getAbsolutePath(),
                                                    name,
                                                    null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            // 最后通知图库更新
            MusicApplication.getInstance()
                         .sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                                                   Uri.parse("file://" + file)));
        }

        return true;
    }

}
