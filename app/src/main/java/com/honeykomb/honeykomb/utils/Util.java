package com.honeykomb.honeykomb.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.honeykomb.honeykomb.R;
import com.honeykomb.honeykomb.customised.BadgeDrawerArrowDrawable;
import com.honeykomb.honeykomb.database.DataBaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class Util {

    private static int REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private static int REQUEST_CAMERA = 2;
    private static int REQUEST_READ_CONTACTS = 3;
    private static int REQUEST_READ_SMS = 4;
    public static DataBaseHelper _db;
    private static String TAG = Util.class.getSimpleName();

    // State
    public static final int STATE_EXPANDED = 0;
    public static final int STATE_COLLAPSED = 1;
    public static final int STATE_PROCESSING = 2;

    private int mInitHeight = 0;


    // geo location
    boolean gps_enabled = false;
    boolean network_enabled = false;

    public static void requestCameraPermission(final Context context) {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.CAMERA)) {
            new AlertDialog.Builder(context)
                    .setMessage(context.getResources().getString(R.string.app_name) + " need Camera access")
                    .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                        }
                    }).show();
        } else {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
        }
    }

    public static void requestPermission(final Context context) {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(context)
                    .setMessage(context.getResources().getString(R.string.request_external_storage_permision))
                    .setPositiveButton(context.getResources().getString(R.string.allow), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    REQUEST_WRITE_EXTERNAL_STORAGE);
                        }
                    }).show();

        } else {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }

    public static void requestReadSMSPermission(final Context context) {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_SMS)) {
            new AlertDialog.Builder(context)
                    .setMessage(context.getResources().getString(R.string.app_name) + " need read SMS permission")
                    .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_SMS}, REQUEST_READ_SMS);
                        }
                    }).show();
        } else {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.READ_SMS}, REQUEST_READ_SMS);
        }
    }

    public static void requestReadContactsPermission(final Context context) {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_CONTACTS)) {
            new AlertDialog.Builder(context)
                    .setMessage(context.getResources().getString(R.string.app_name) + " need read Contacts permission")
                    .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    }).show();
        } else {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
    }

    public static void BackupDatabase() {
        try {
            boolean success;
            File file;
            file = new File(Environment.getExternalStorageDirectory() + "");
            if (file.exists()) {
                success = true;
                Log.i(TAG, "----------is File exits-----");
            } else {
                success = file.mkdir();
                Log.i(TAG, "----------is File Create-----");
            }
            if (success) {
                //String inFileName = "/data/data/com.sygic.sdk.demo/databases/MOLS_DB.s3db";
                File dbFile = new File(DataBaseHelper.DB_PATH + DataBaseHelper.DATABASE_NAME);
                FileInputStream fis = new FileInputStream(dbFile);


                String outFileName = Environment.getExternalStorageDirectory() + "/HoneyKomb_DB.db";
                Log.i(TAG, "outFileName = " + outFileName);
                // Open the empty db as the output stream
                OutputStream output = new FileOutputStream(outFileName);

                // Transfer bytes from the inputfile to the outputfile
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    output.write(buffer, 0, length);
                }

                output.flush();
                output.close();
                fis.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Typeface setTextViewTypeFace(Context context, String fontType) {
        Typeface font = Typeface.createFromAsset(context.getAssets(), fontType);
        return font;
    }

    public static boolean isJSONValid(String body) {
        try {
            new JSONObject(body);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(body);
            } catch (JSONException ex1) {

                return false;
            }
        }
        return true;
    }

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            assert am != null;
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            if (runningProcesses != null) {
                for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        for (String activeProcess : processInfo.pkgList) {
                            if (activeProcess.equals(context.getPackageName())) {
                                isInBackground = false;
                            }
                        }
                    }
                }
            }
        } else {
            assert am != null;
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    public static void playNotificationSound(Context activity) {
        // Playing notification sound
        try {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(activity, alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateLaunchMode(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(Constants.LAUNCH_APP_FOR_FIRST_TIME, true);
        editor.apply();
    }

    public static Drawable setBadgeCount(Context context, int res, int badgeCount){
        LayerDrawable icon = (LayerDrawable) ContextCompat.getDrawable(context, R.drawable.ic_badge_drawable);
        Drawable mainIcon = ContextCompat.getDrawable(context, res);
        BadgeDrawerArrowDrawable badge = new BadgeDrawerArrowDrawable(context);
        badge.setCount(String.valueOf(badgeCount));
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
        icon.setDrawableByLayerId(R.id.ic_main_icon, mainIcon);

        return icon;
    }

    public static void hide_keyboard_from(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
