package org.ktln2.android.androidhelperlibrary;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;


public class AndroidHelper {
    public static void logInfo(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();

        Log.d(AndroidHelper.class.getName(),
                String.format(
                        "screen: %dx%d, density: %.2f, density DPI: %d",
                        dm.widthPixels,
                        dm.heightPixels,
                        dm.density,
                        dm.densityDpi));
    }

    public static void log(String message) {
        //Log.d();
    }
}
