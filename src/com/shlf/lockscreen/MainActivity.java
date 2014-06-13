
package com.shlf.lockscreen;

import com.shlf.choosephoto.PickPhotoAction;
import com.shlf.lockscreen.util.SharedPreferencesManager;
import com.shlf.lockscreen.util.Utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
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
        showPhoto();
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

    private void showPhoto() {
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

            PickPhotoAction.createPickPhotoDialog(this).show();
        } else {
            Utils.gotoLockScreen(this);
            finish();
        }
    }

}
