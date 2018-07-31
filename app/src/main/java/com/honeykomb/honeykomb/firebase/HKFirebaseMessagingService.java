package com.honeykomb.honeykomb.firebase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.honeykomb.honeykomb.database.DataBaseHelper;
import com.honeykomb.honeykomb.service.IntentServiceForNotification;
import com.honeykomb.honeykomb.utils.Constants;
import com.honeykomb.honeykomb.utils.Util;

/**
 * Created by pankaj on 24/11/16.
 */
public class HKFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = HKFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        initDB(this);

        Log.i(TAG, "From: " + remoteMessage.getFrom());

        SharedPreferences sp = getSharedPreferences(Constants.APP_PREFERENCE_NAME, Activity.MODE_PRIVATE);
        String authenticationKey = sp.getString(Constants.AUTH_KEY, "");
        String hK_UUID = sp.getString(Constants.HK_UUID, "");

        Log.i(TAG, "RemoteMessage : " + remoteMessage.toString());
        Log.i(TAG, "RemoteMessage Notification  : " + remoteMessage.getNotification());
        Log.i(TAG, "RemoteMessage Data  : " + remoteMessage.getData());
        Log.i(TAG, "RemoteMessage Collapse Key  : " + remoteMessage.getCollapseKey());
        Log.i(TAG, "RemoteMessage Message Id Key  : " + remoteMessage.getMessageId());
        Log.i(TAG, "RemoteMessage Sent Time  : " + remoteMessage.getSentTime());
        Log.i(TAG, "RemoteMessage Ttl : " + remoteMessage.getTtl());
        Log.i(TAG, "RemoteMessage To : " + remoteMessage.getTo());
        Log.i(TAG, "RemoteMessage MessageType : " + remoteMessage.getMessageType());

        if (remoteMessage.getData() != null) {
            Log.i(TAG, "Notification Body 2 : " + remoteMessage.getData().get("message"));
//            Log.i(TAG, "Notification Body 3 : " + remoteMessage.getNotification().getBody());
//            handleNotification(remoteMessage.getNotification().getBody());
        }
        if (remoteMessage.getData() != null && !remoteMessage.getData().isEmpty()) {
            if (remoteMessage.getData().get("message")!= null && remoteMessage.getData().get("message").length() > 0) {
                String message = remoteMessage.getData().get("message");
                Log.i(TAG, "Server Message without PENDINGINVITATIONS = " + message);
                Intent addActivityService = new Intent(HKFirebaseMessagingService.this, IntentServiceForNotification.class);
                addActivityService.putExtra("authenticationKey", authenticationKey);
                addActivityService.putExtra("hK_UUID", hK_UUID);
                addActivityService.putExtra("notificationMessage", message);
                startService(addActivityService);
            }
        } else {
            Intent addActivityService = new Intent(HKFirebaseMessagingService.this, IntentServiceForNotification.class);
            addActivityService.putExtra("authenticationKey", authenticationKey);
            addActivityService.putExtra("hK_UUID", hK_UUID);
            addActivityService.putExtra("notificationMessage", "No");
            startService(addActivityService);
        }
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}