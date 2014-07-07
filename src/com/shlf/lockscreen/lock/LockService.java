
package com.shlf.lockscreen.lock;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class LockService extends Service {
    private static final String TAG = LockService.class.getSimpleName();
    private static final boolean DBG = true;

    private Intent mStartIntent = null;
    private LockLisnter mReceiver;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null == intent) return Service.START_STICKY;

        mStartIntent = intent;

        if (DBG) Log.d(TAG, "***registerIntentReceivers");
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        filter.addAction("android.intent.action.SCREEN_ON");

        mReceiver = new LockLisnter(); // 用于侦听
        registerReceiver(mReceiver, filter);

        if (DBG) Log.d(TAG, "service onStart and action is " + intent.getAction());
        if (DBG) Log.d(TAG, "service onStart and startId is " + startId);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (DBG) Log.d(TAG, "service onDestroy");
        unregisterReceiver(mReceiver);

        if (mStartIntent != null) {
            if (DBG) Log.d(TAG, "serviceIntent not null");
            startService(mStartIntent);
        }
    }

}
