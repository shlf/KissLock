package com.shlf.lockscreen.lock;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class LockLayer implements View.OnSystemUiVisibilityChangeListener {
    private Activity mActivty;
    private WindowManager mWindowManager;
    private View mLockView;
    private LayoutParams mLockViewLayoutParams;
    private static LockLayer mLockLayer;
    private boolean isLocked;
    private final static int FLAG_APKTOOL_VALUE = 1280;

    public static synchronized LockLayer getInstance(Activity act){
        if(mLockLayer == null){
            mLockLayer = new LockLayer(act);
        }
        return mLockLayer;
    }

    private LockLayer(Activity act) {
        mActivty = act;
        init();
    }

    private void init(){
        isLocked = false;
        mWindowManager = mActivty.getWindowManager();
        mLockViewLayoutParams = new LayoutParams();
        mLockViewLayoutParams.width = LayoutParams.MATCH_PARENT;
        mLockViewLayoutParams.height = LayoutParams.MATCH_PARENT;
        mLockViewLayoutParams.type = LayoutParams.TYPE_SYSTEM_ALERT;
        mLockViewLayoutParams.flags = FLAG_APKTOOL_VALUE;
        mLockViewLayoutParams.flags |= LayoutParams.FLAG_DISMISS_KEYGUARD;
        mLockViewLayoutParams.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        mLockViewLayoutParams.softInputMode = LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN;
    }

    public synchronized void lock() {
        if(mLockView!=null && !isLocked){
            mWindowManager.addView(mLockView, mLockViewLayoutParams);
        }
        isLocked = true;
    }

    public synchronized void unlock() {
        if(mWindowManager!=null&&isLocked){
            mWindowManager.removeView(mLockView);
        }
        isLocked = false;
    }

    public synchronized void setLockView(View v){
        mLockView = v;
    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {
        Log.d("DDDDD", "onSystemUiVisibilityChange() ....");
        if (visibility != View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) {
            mLockView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    }
}
