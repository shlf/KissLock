
package com.shlf.lockscreen;

import com.shlf.lockscreen.util.SharedPreferencesManager;

import android.app.Application;

public class KissApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferencesManager.init(getApplicationContext());
    }
}
