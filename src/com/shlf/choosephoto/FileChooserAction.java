
package com.shlf.choosephoto;

import com.orleonsoft.android.simplefilechooser.Constants;
import com.orleonsoft.android.simplefilechooser.ui.FileChooserActivity;

import android.app.Activity;
import android.content.Intent;

public class FileChooserAction {
    public static final int FILE_CHOOSE_PHOTO = 3;

    public static void runDirectoryChooser(Activity activity) {
        Intent intent = new Intent(activity, FileChooserActivity.class);
        activity.startActivityForResult(intent, FILE_CHOOSE_PHOTO);
    }

    public static String pathFromData(Intent data) {
        return data.getStringExtra(Constants.KEY_FILE_SELECTED);
    }

}
