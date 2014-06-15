
package com.shlf.lockscreen;

import com.shlf.choosephoto.PickPhotoAction;
import com.shlf.lockscreen.util.SharedPreferencesManager;
import com.shlf.lockscreen.util.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends Activity {

    private SharedPreferencesManager mSpm;

    private TextView mShowPhoto;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mSpm = SharedPreferencesManager.getInstance();

        mShowPhoto = (TextView) findViewById(R.id.photo);

        String photoPath = mSpm.getStringValue(SharedPreferencesManager.FILE_PHOTO_PATH,
                SharedPreferencesManager.KEY_PHOTO_PATH, "");

        if (TextUtils.isEmpty(photoPath)) {
            InputStream is = null;
            try {
                is = getAssets().open("default/mycar.jpg");
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (null != is) {
                mShowPhoto.setBackgroundDrawable(Drawable.createFromStream(is, "default.jpg"));
            }
        } else {
            mShowPhoto.setBackgroundDrawable(Drawable.createFromPath(photoPath));
        }

        boolean repickTip = getIntent().getBooleanExtra("repick-tip", false);
        if (repickTip) {
            createTipDialog().show();
        } else {
            if (TextUtils.isEmpty(photoPath)) {
                PickPhotoAction.createPickPhotoDialog(this).show();
            } else {
                Utils.gotoLockScreen(this);
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            String photoPath = PickPhotoAction.getPhotoPath(requestCode, data);

            mShowPhoto.setBackgroundDrawable(Drawable.createFromPath(photoPath));
            mSpm.setString(SharedPreferencesManager.FILE_PHOTO_PATH,
                    SharedPreferencesManager.KEY_PHOTO_PATH, photoPath);

            Utils.gotoLockScreen(this);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * repick picture
     */
    private Dialog createTipDialog() {
        // Wrap our context to inflate list items using correct theme
        final Context dialogContext = new ContextThemeWrapper(this, android.R.style.Theme_Light);

        String[] choices = new String[2];
        choices[0] = getString(R.string.repick_photo);
        choices[1] = getString(R.string.goto_launcher);
        final ListAdapter adapter = new ArrayAdapter<String>(dialogContext,
                android.R.layout.simple_list_item_1, choices);

        final AlertDialog.Builder builder = new AlertDialog.Builder(dialogContext);
        builder.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                switch (which) {
                    case 0:
                        PickPhotoAction.createPickPhotoDialog(MainActivity.this).show();
                        break;
                    case 1:
                        Utils.gotoLauncher(MainActivity.this);
                        finish();
                        break;
                }
            }
        });
        return builder.create();
    }
}
