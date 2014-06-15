
package com.shlf.lockscreen.lock;

import com.shlf.lockscreen.MainActivity;
import com.shlf.lockscreen.R;
import com.shlf.lockscreen.util.SharedPreferencesManager;
import com.shlf.lockscreen.util.Utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

public class LockActivity extends Activity {
    private static final String TAG = LockActivity.class.getSimpleName();
    private static final boolean DBG = true;

    private LockLayer mLockLayer;
    private View mLock;
    private TextView mShowPhoto;

    private Intent mServiceIntent;
    private SharedPreferencesManager mSpm;

    private Handler mHandler = new Handler();
    private Runnable mDisableHomeKey = new Runnable() {
        @Override
        public void run() {
            disableHomeKey();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSpm = SharedPreferencesManager.getInstance();

        // getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        // requestWindowFeature(Window.FEATURE_NO_TITLE);

        // setContentView(R.layout.display);
        mLock = View.inflate(this, R.layout.main, null);
        // mLock.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        mShowPhoto = (TextView) mLock.findViewById(R.id.photo);
        mShowPhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(LockActivity.this, MainActivity.class);
                newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                newIntent.putExtra("repick-tip", true);
                startActivity(newIntent);
                finish();
            }
        });

        if (mServiceIntent == null) {
            mServiceIntent = new Intent(this, LockService.class);
        }
        startService(mServiceIntent);

        // mHandler.postDelayed(mDisableHomeKey, 200);
        // disableStatueBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLockLayer = LockLayer.getInstance(this);
        mLockLayer.setLockView(mLock);
        mLockLayer.lock();

        showPhoto();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLockLayer.unlock();
        if (DBG) Log.d(TAG, "onPause()");
    }

    private void disableHomeKey() {
        if (DBG) Log.d(TAG, "------------->> disableHomeKey()");
        getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
    }

    private void showPhoto() {
        String photoPath = mSpm.getStringValue(SharedPreferencesManager.FILE_PHOTO_PATH,
                SharedPreferencesManager.KEY_PHOTO_PATH, "");

        if (TextUtils.isEmpty(photoPath)) {
            displayDefault();

            // goto pick photo.
            Utils.gotoMain(this);
            finish();
        } else {
            if ("default".equals(photoPath)) {
                displayDefault();
            } else {
                mShowPhoto.setBackgroundDrawable(Drawable.createFromPath(photoPath));
            }
        }
    }

    private void displayDefault() {
        InputStream is = null;
        try {
            is = getAssets().open("default/mycar.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (null != is) {
            mShowPhoto.setBackgroundDrawable(Drawable.createFromStream(is, "default.jpg"));
        }
    }
}
