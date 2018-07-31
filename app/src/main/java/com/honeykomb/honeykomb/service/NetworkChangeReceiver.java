package com.honeykomb.honeykomb.service;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.util.Log;

/**
 * Created by Rajashekar.Nimmala on 7/19/2017.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {
    private String TAG = NetworkChangeReceiver.class.getSimpleName();

    @Override
    public void onReceive(final Context context, final Intent intent) {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifi.isAvailable() || mobile.isAvailable()) {
            Log.i(TAG, "Network Available -------" + wifi.isAvailable() + " " + mobile.isConnected() + " " + wifi.isConnected());
            final int SPLASH_DISPLAY_LENGTH = 15000;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (wifi.isConnectedOrConnecting() || mobile.isConnectedOrConnecting()) {
                        Intent i = new Intent(Intent.ACTION_SYNC, null, context, SendAllActivityToServer.class);
                        context.startService(i);
                    }
                }
            }, SPLASH_DISPLAY_LENGTH);
        }
    }
}

