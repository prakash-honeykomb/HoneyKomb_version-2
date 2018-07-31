package com.honeykomb.honeykomb.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.honeykomb.honeykomb.R;
import com.honeykomb.honeykomb.database.DataBaseHelper;
import com.honeykomb.honeykomb.service.ContactsService;
import com.honeykomb.honeykomb.service.GetContactsFromServer;
import com.honeykomb.honeykomb.service.SendAllActivityToServer;
import com.honeykomb.honeykomb.service.SendOverDueActivities;
import com.honeykomb.honeykomb.utils.Constants;
import com.honeykomb.honeykomb.utils.Util;

import java.io.File;

import static java.lang.System.out;

public class SplashScreen extends Activity {
    private String TAG = SplashScreen.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        initDB(this);
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, getResources().getString(R.string.permission_granted));
                Util.requestCameraPermission(this);
            } else {
                Log.i(TAG, "Permission is revoked");
                Util.requestPermission(this);
            }
        } else {
            final int SPLASH_DISPLAY_LENGTH = 3000;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    SharedPreferences prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
                    Boolean isFirstStart = prefs.getBoolean(Constants.LAUNCH_APP_FOR_FIRST_TIME, true);
                    String userStatus = prefs.getString(Constants.USER_STATUS, "");
                    Log.e(TAG, "SplashActivity------ userStatus = " + userStatus);
                    if (isFirstStart) {
                        File myDir = new File(getCacheDir(), getResources().getString(R.string.app_name));
                        if (!myDir.exists())
                            myDir.mkdirs();
                        Log.e("appDirectory", "HoneyKomb folder = " + out.toString());
                        Intent i = new Intent(Intent.ACTION_SYNC, null, SplashScreen.this, ContactsService.class);
                        startService(i);
                        registerUser();
                    } else {
                        Intent i = new Intent(Intent.ACTION_SYNC, null, getApplicationContext(), GetContactsFromServer.class);
                        getApplicationContext().startService(i);
                        goToMainScreen();
                    }
                }
            }, SPLASH_DISPLAY_LENGTH);
        }
    }

    private void registerUser() {
        Intent intent = new Intent(SplashScreen.this, LogInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    private void goToMainScreen() {

        Intent service = new Intent(SplashScreen.this, SendOverDueActivities.class);
        startService(service);

        Intent i = new Intent(SplashScreen.this, SendAllActivityToServer.class);
        startService(i);

        SharedPreferences prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        boolean isDemoShown = prefs.getBoolean(Constants.DEMO_SHOWN, false);

        if (isDemoShown) {
            Intent intent = new Intent(SplashScreen.this, MainScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        } else {
            Intent intent = new Intent(SplashScreen.this, DemoActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("requestCode", " = read and write permission given..!");
                Util.requestCameraPermission(this);
            } else {
                Toast.makeText(SplashScreen.this, getResources().getString(R.string.need_permission), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("requestCode", " = CAMERA permission given..!");
                Util.requestReadContactsPermission(this);
            } else {
                Toast.makeText(SplashScreen.this, getResources().getString(R.string.need_permission), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        if (requestCode == 3) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("requestCode", " = CAMERA permission given..!");
                Util.requestReadSMSPermission(this);
            } else {
                Toast.makeText(SplashScreen.this, getResources().getString(R.string.need_permission), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        if (requestCode == 4) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("requestCode", " = Read SMS logs permission given..!");
                SharedPreferences prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
                Boolean isFirstStart = prefs.getBoolean(Constants.LAUNCH_APP_FOR_FIRST_TIME, true);
                String userStatus = prefs.getString(Constants.USER_STATUS, "");
                Log.e(TAG, "SplashActivity------ userStatus = " + userStatus);
                if (isFirstStart) {
                    File myDir = new File(getCacheDir(), getResources().getString(R.string.app_name));
                    if (!myDir.exists())
                        myDir.mkdirs();
                    Log.e("appDirectory", "HoneyKomb folder = " + out.toString());
                    Intent i = new Intent(Intent.ACTION_SYNC, null, SplashScreen.this, ContactsService.class);
                    startService(i);
                    registerUser();
                } else {
                    Intent i = new Intent(Intent.ACTION_SYNC, null, getApplicationContext(), GetContactsFromServer.class);
                    getApplicationContext().startService(i);
                    goToMainScreen();
                }
            } else {
                Toast.makeText(SplashScreen.this, getResources().getString(R.string.need_permission), Toast.LENGTH_SHORT).show();
                finish();
            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    public void initDB(Context ctx) {
        try {
            if (Util._db == null) {
                Util._db = new DataBaseHelper(ctx);
                Util._db.open();
            } else if (!Util._db.isOpen()) {
                Util._db.open();
            }
            Util.BackupDatabase();
//            Util.CreateCMSAppFolder(getApplicationContext());
        } catch (Exception e) {
            Log.e(TAG, " Exception = " + e.getMessage());
        }
    }
}
