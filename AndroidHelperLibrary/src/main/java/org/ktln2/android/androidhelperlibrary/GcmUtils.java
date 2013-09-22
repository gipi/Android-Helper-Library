package org.ktln2.android.androidhelperlibrary;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

/**
 * Code shamelessly copied from the getting started guide of Android
 * about Gcm services.
 *
 * You have to insert a <metadata> entry with the gcmSenderId attribute set.
 */
public class GcmUtils {
    private final static String TAG = "GcmUtils";

    private static GcmUtils mInstance = null;

    /**
     * This is the project number you got from the API Console, as described in "Getting Started."
     */
    String SENDER_ID = "";

    static public synchronized GcmUtils getInstance(Activity activity) {
        if (mInstance == null) {
            mInstance = new GcmUtils(activity);
        }

        return mInstance;
    }

    private GcmUtils(Activity activity) {
        mActivity = activity;

        try {
            ApplicationInfo app =
                    activity
                            .getPackageManager()
                            .getApplicationInfo(
                                    activity.getPackageName(),
                                    PackageManager.GET_META_DATA
                            );
            SENDER_ID = app.metaData.getString("gcmSenderID");
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("this is not possible");
        }
    }

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String PROPERTY_APP_VERSION = "appVersion";// WHAT IS THIS?
    public static final String PROPERTY_REG_ID = "registration_id";
    private static GoogleCloudMessaging gcmInstance = null;
    String regid;
    private Context mContext;
    private Activity mActivity;

    public void registerDeviceIfIsTheCase() {
        regid = getRegistrationId(mActivity);

        Log.d(TAG, "regid: " + regid);
        if (regid.isEmpty())
            registerInBackground();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    public boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mActivity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(
                        resultCode,
                        mActivity,
                        PLAY_SERVICES_RESOLUTION_REQUEST
                ).show();
            }
            return false;
        }
        return true;
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Activity activity) {
        final SharedPreferences prefs = getGCMPreferences(activity);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");

        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }

        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(activity);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }

        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Activity activity) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return activity.getSharedPreferences(
                activity.getClass().getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        AsyncTask a = new AsyncTask<Object, Long, String>() {
            @Override
            protected String doInBackground(Object... params) {
                String msg = "";
                try {
                    if (gcmInstance == null) {
                        gcmInstance = GoogleCloudMessaging.getInstance(mActivity);
                    }
                    regid = gcmInstance.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.d(TAG, "registered in background: " + msg);
            }
        }.execute(null, null, null);
    }

        /**
         * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
         * or CCS to send messages to your app. Not needed for this demo since the
         * device sends upstream messages to a server that echoes back the message
         * using the 'from' address in the message.
         */
    private void sendRegistrationIdToBackend() {
        // Your implementation here.
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param regId registration ID
     */
    private void storeRegistrationId(String regId) {
        final SharedPreferences prefs = getGCMPreferences(mActivity);
        int appVersion = getAppVersion(mActivity);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }
}
