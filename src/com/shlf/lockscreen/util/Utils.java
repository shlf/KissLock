
package com.shlf.lockscreen.util;

import com.shlf.lockscreen.MainActivity;
import com.shlf.lockscreen.lock.LockActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

public class Utils {

    // 获取SDCard的目录路径功能
    public static String getSDCardPath() {
        String SDCardPath = null;
        // 判断SDCard是否存在
        boolean IsSDcardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
        if (IsSDcardExist) {
            SDCardPath = Environment.getExternalStorageDirectory().toString();
        }
        return SDCardPath;
    }

    // 压缩且保存图片到SDCard
    public static String compressAndSaveBitmapToSDCard(Bitmap rawBitmap, String path,
            String fileName, int quality) {
        String saveFilePaht = path + File.separator + fileName;
        File saveFile = new File(saveFilePaht);

        if (saveFile.exists()) saveFile.delete();

        try {
            saveFile.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(saveFile);
            if (fileOutputStream != null) {
                // imageBitmap.compress(format, quality, stream);
                // 把位图的压缩信息写入到一个指定的输出流中
                // 第一个参数format为压缩的格式
                // 第二个参数quality为图像压缩比的值,0-100.0 意味着小尺寸压缩,100意味着高质量压缩
                // 第三个参数stream为输出流
                rawBitmap.compress(Bitmap.CompressFormat.JPEG, quality, fileOutputStream);
            }
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return saveFile.getAbsolutePath();
    }

    /**
     * See PhoneApp.disableStatusBar(). import android.app.StatusBarManager;
     * StatusBarManager mStatusBarManager = (StatusBarManager)
     * getSystemService(Context.STATUS_BAR_SERVICE);
     * mStatusBarManager.disable(StatusBarManager.DISABLE_EXPAND); else
     * mStatusBarManager.disable(StatusBarManager.DISABLE_NONE);
     */
    public static void disableStatueBar(Context context) {
        Object service = context.getSystemService("statusbar");

        try {
            Class<?> statusBarManager = Class.forName("android.app.StatusBarManager");
            Method expand = statusBarManager.getMethod("disable", int.class);
            expand.invoke(service, 0x00000001);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // goto lock screen.
    public static void gotoLockScreen(Context ctx) {
        Intent newIntent = new Intent(ctx, LockActivity.class);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(newIntent);
    }

    // goto pick photo.
    public static void gotoMain(Context ctx) {
        Intent newIntent = new Intent(ctx, MainActivity.class);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(newIntent);
    }

    // goto launcher.
    public static void gotoLauncher(Context ctx) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory("android.intent.category.HOME");
        ctx.startActivity(intent);
    }
}
