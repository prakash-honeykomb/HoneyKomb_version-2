package com.honeykomb.honeykomb.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.honeykomb.honeykomb.R;


public class ResponseReceiver extends BroadcastReceiver {
    public static final String ACTION_RESP = "com.honeykomb.honeykomb.activities.MainScreen";
    private String TAG = ResponseReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(ACTION_RESP)) {
            Log.e("ResponseReceiver", "onReceive = " + ACTION_RESP);
            if(intent.getExtras()!= null) {
                String notificationMessage = intent.getExtras().getString("notificationMessage");
                Log.i(TAG, " background APP = " + notificationMessage);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) (System.currentTimeMillis() & 0xfffffff),intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                notificationBuilder.setSmallIcon(R.mipmap.launch)
                        .setContentText(notificationMessage)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
            } else {
                notificationBuilder.setSmallIcon(R.mipmap.launch)
                        .setContentText(notificationMessage)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
            }
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                assert notificationManager != null;
                notificationManager.notify(0/*  ID of notification */, notificationBuilder.build());

            }
        }
    }
}
