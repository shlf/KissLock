
package com.shlf.choosephoto;

import com.shlf.lockscreen.R;
import com.shlf.lockscreen.util.SharedPreferencesManager;
import com.shlf.lockscreen.util.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PickPhotoAction {
    private static final String TAG = PickPhotoAction.class.getSimpleName();

    private static final File PHOTO_DIR = new File(Environment.getExternalStorageDirectory()
            + "/DCIM/Camera");

    // 使用照相机拍照获取图片
    public static final int SELECT_PIC_BY_TACK_PHOTO = 1;

    // 使用相册中的图片
    public static final int SELECT_PIC_BY_PICK_PHOTO = 2;

    private static Activity sActivity;
    private static File mCurrentPhotoFile;

    /**
     * Creates a dialog offering two options: take a photo or pick a photo from
     * the gallery.
     */
    public static Dialog createPickPhotoDialog(Activity activity) {
        sActivity = activity;

        // Wrap our context to inflate list items using correct theme
        final Context dialogContext = new ContextThemeWrapper(sActivity,
                android.R.style.Theme_Light);

        String[] choices = new String[5];
        choices[0] = sActivity.getString(R.string.take_photo);
        choices[1] = sActivity.getString(R.string.pick_photo);
        choices[2] = sActivity.getString(R.string.file_photo);
        choices[3] = sActivity.getString(R.string.default_photo);
        choices[4] = sActivity.getString(android.R.string.cancel);
        final ListAdapter adapter = new ArrayAdapter<String>(dialogContext,
                android.R.layout.simple_list_item_1, choices);

        final AlertDialog.Builder builder = new AlertDialog.Builder(dialogContext);
        builder.setTitle(R.string.attachToContact);
        builder.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                switch (which) {
                    case 0:
                        doTakePhoto();
                        break;
                    case 1:
                        doPickPhoto();
                        break;
                    case 2:
                        FileChooserAction.runDirectoryChooser(sActivity);
                        break;
                    case 3:
                        SharedPreferencesManager mSpm = SharedPreferencesManager.getInstance();
                        mSpm.setString(SharedPreferencesManager.FILE_PHOTO_PATH,
                                SharedPreferencesManager.KEY_PHOTO_PATH, "default");

                        Utils.gotoLockScreen(sActivity);
                        sActivity.finish();
                        break;
                    case 4:
                        Utils.gotoLockScreen(sActivity);
                        sActivity.finish();
                        break;
                }
            }
        });
        return builder.create();
    }

    /**
     * 拍照获取图片
     */
    private static void doTakePhoto() {
        // 执行拍照前，应该先判断SD卡是否存在
        String SDState = Environment.getExternalStorageState();
        if (SDState.equals(Environment.MEDIA_MOUNTED)) {
            if (!PHOTO_DIR.exists()) PHOTO_DIR.mkdirs();
            try {
                // Launch camera to take photo for selected contact
                PHOTO_DIR.mkdirs();
                mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName());
                final Intent intent = getTakePickIntent(mCurrentPhotoFile);
                sActivity.startActivityForResult(intent, SELECT_PIC_BY_TACK_PHOTO);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(sActivity, "R.string.photoPickerNotFoundText", Toast.LENGTH_LONG)
                        .show();
            }
        } else {
            Toast.makeText(sActivity, sActivity.getString(R.string.error_sdcard), Toast.LENGTH_LONG)
                    .show();
        }
    }

    /**
     * Create a file name for the icon photo using current time.
     */
    private static String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }

    /**
     * Constructs an intent for capturing a photo and storing it in a temporary
     * file.
     */
    public static Intent getTakePickIntent(File f) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        return intent;
    }

    /***
     * 从相册中取图片
     */
    private static void doPickPhoto() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        sActivity.startActivityForResult(intent, SELECT_PIC_BY_PICK_PHOTO);
    }

    /**
     * 选择图片后，获取图片的路径
     * 
     * @param requestCode
     * @param data
     * @return
     */
    public static String getPhotoPath(int requestCode, Intent data) {
        Uri photoUri = null;
        if (requestCode == SELECT_PIC_BY_PICK_PHOTO) {
            // 从相册取图片，有些手机有异常情况，请注意
            if (data == null) {
                Toast.makeText(sActivity, sActivity.getString(R.string.error_select),
                        Toast.LENGTH_LONG).show();
                return null;
            }

            photoUri = data.getData();
            if (photoUri == null) {
                Toast.makeText(sActivity, sActivity.getString(R.string.error_select),
                        Toast.LENGTH_LONG).show();
                return null;
            }
        }

        String[] pojo = {
            MediaStore.Images.Media.DATA
        };

        String picPath = "";
        Cursor cursor = sActivity.getContentResolver().query(photoUri, pojo, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);
            cursor.moveToFirst();
            picPath = cursor.getString(columnIndex);
            cursor.close();
        }

        Log.i(TAG, "imagePath = " + picPath);
        if (!TextUtils.isEmpty(picPath)
                && (picPath.endsWith(".png") || picPath.endsWith(".PNG")
                        || picPath.endsWith(".jpg") || picPath.endsWith(".JPG"))) {
            return picPath;
        } else {
            Toast.makeText(sActivity, sActivity.getString(R.string.error_file_formart),
                    Toast.LENGTH_LONG).show();
        }

        return null;
    }

    public static String handResult(int requestCode, Intent data) {
        switch (requestCode) {
            case SELECT_PIC_BY_TACK_PHOTO: {
                // Add the image to the media store
                MediaScannerConnection.scanFile(sActivity, new String[] {
                    mCurrentPhotoFile.getAbsolutePath()
                }, new String[] {
                    null
                }, null);
                return mCurrentPhotoFile.getAbsolutePath();
            }

            case SELECT_PIC_BY_PICK_PHOTO: {
                return getPhotoPath(requestCode, data);
            }

            case FileChooserAction.FILE_CHOOSE_PHOTO: {
                return FileChooserAction.pathFromData(data);
            }

            default:
                return null;
        }
    }
}
