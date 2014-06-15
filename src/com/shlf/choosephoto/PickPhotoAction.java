
package com.shlf.choosephoto;

import com.shlf.lockscreen.R;
import com.shlf.lockscreen.util.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Toast;

public class PickPhotoAction {
    private static final String TAG = PickPhotoAction.class.getSimpleName();
    private static Activity sActivity;

    // 使用照相机拍照获取图片
    public static final int SELECT_PIC_BY_TACK_PHOTO = 1;

    // 使用相册中的图片
    public static final int SELECT_PIC_BY_PICK_PHOTO = 2;

    /**
     * Creates a dialog offering two options: take a photo or pick a photo from
     * the gallery.
     */
    public static Dialog createPickPhotoDialog(Activity activity) {
        sActivity = activity;

        // Wrap our context to inflate list items using correct theme
        final Context dialogContext = new ContextThemeWrapper(sActivity,
                android.R.style.Theme_Light);

        String[] choices = new String[3];
        choices[0] = sActivity.getString(R.string.take_photo);
        choices[1] = sActivity.getString(R.string.pick_photo);
        choices[2] = sActivity.getString(android.R.string.cancel);
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
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// "android.media.action.IMAGE_CAPTURE"

            /***
             * 需要说明一下，以下操作使用照相机拍照，拍照后的图片会存放在相册中的 这里使用的这种方式有一个好处就是获取的图片是拍照后的原图
             * 如果不实用ContentValues存放照片路径的话，拍照后获取的图片为缩略图不清晰
             */
            ContentValues values = new ContentValues();
            Uri photoUri = sActivity.getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
            sActivity.startActivityForResult(intent, SELECT_PIC_BY_TACK_PHOTO);
        } else {
            Toast.makeText(sActivity, sActivity.getString(R.string.error_sdcard), Toast.LENGTH_LONG)
                    .show();
        }
    }

    /***
     * 从相册中取图片
     */
    private static void doPickPhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
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
}
