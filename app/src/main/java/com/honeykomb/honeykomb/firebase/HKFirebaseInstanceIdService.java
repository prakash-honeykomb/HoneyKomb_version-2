package com.honeykomb.honeykomb.firebase;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.honeykomb.honeykomb.utils.Constants;
import com.honeykomb.honeykomb.utils.UtilityHelper;

/**
 * Created by pankaj on 24/11/16.
 */
public class HKFirebaseInstanceIdService extends com.google.firebase.iid.FirebaseInstanceIdService {
    private static final String TAG = HKFirebaseInstanceIdService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        Log.i(TAG, "in onTokenRefresh");
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.i(TAG, "Refreshed token: " + refreshedToken);

        // Saving reg id to shared preferences
        storeRegIdInPref(refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Constants.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        Log.i(TAG, "sendRegistrationToServer: " + token);
    }

    private void storeRegIdInPref(String token) {
        Log.i("DEVICETOKEN", " = " + token);
        UtilityHelper.setStringPreferences(getApplicationContext(), Constants.DEVICE_TOKEN, token);
        UtilityHelper.setStringPreferences(getApplicationContext(), Constants.REG_ID, token);
    }
}