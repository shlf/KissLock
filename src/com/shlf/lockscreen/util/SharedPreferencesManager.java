
package com.shlf.lockscreen.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {
    private static SharedPreferencesManager sInstance = new SharedPreferencesManager();
    private static Context sContext;

    public static final String FILE_PHOTO_PATH = "photo_path";
    public static final String KEY_PHOTO_PATH = "path";

    public static void init(Context ctx) {
        sContext = ctx;
    }

    public static SharedPreferencesManager getInstance() {
        return sInstance;
    }

    public void setString(String name, String key, String value) {
        SharedPreferences sp = sContext.getSharedPreferences(name, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).commit();
    }

    public String getStringValue(String name, String key, String defaultValue) {
        SharedPreferences sp = sContext.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sp.getString(key, defaultValue);
    }
}
