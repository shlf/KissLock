
package com.shlf.lockscreen.lock;

import com.shlf.lockscreen.util.Utils;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

public class LockLisnter extends BroadcastReceiver {
    private static final String TAG = LockLisnter.class.getSimpleName();
    private static final boolean DBG = true;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent != null) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action)) {
                if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                    if (DBG) Log.d(TAG, "***" + action);
                    if (DBG) Log.d(TAG, "***Equal with SCREEN_OFF");

                    // 如果是Screen_Off,启动自己的画面
                    Utils.gotoLockScreen(context);

                    KeyguardManager mKeyguardManager = (KeyguardManager) context
                            .getSystemService(Context.KEYGUARD_SERVICE);
                    KeyguardLock mKeyguardLock = mKeyguardManager
                            .newKeyguardLock(LockActivity.class.getSimpleName());
                    mKeyguardLock.disableKeyguard();
                }
            }
        }
    }

}
