package com.shlf.lockscreen.lock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)
                || intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Intent newIntent = new Intent(context, LockActivity.class);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 注意，必须添加这个标记，否则启动会失败
            context.startActivity(newIntent);

//            context.startService(new Intent(context, MyService.class));
        }
    }
}
