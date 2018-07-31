package com.honeykomb.honeykomb.service;


import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Message;
import android.util.Log;

import com.honeykomb.honeykomb.utils.Constants;
import com.honeykomb.honeykomb.utils.Util;

import org.json.JSONObject;

import java.util.ArrayList;


public class SendOverDueActivities extends IntentService {

    private String TAG = SendOverDueActivities.class.getSimpleName();

    public SendOverDueActivities() {
        super("SendOverDueActivities");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Log.i(TAG, "----onHandleIntent----");
            executeRequest();
        } catch (Exception e) {
            Log.i(TAG + "----Exception----", ": " + e.getMessage());
            this.stopSelf();
        }
        this.stopSelf();
    }

    private void executeRequest() {

        ArrayList<String> overdueActivitiesList ;
        ArrayList<String> overdueActivitiesAfter48List ;

        overdueActivitiesList = Util._db.getOverDueActivityIDS();
        overdueActivitiesAfter48List = Util._db.getOverDueActivityIDSAfter48Hrs();

        if (overdueActivitiesList.size() > 0 || overdueActivitiesAfter48List.size() > 0) {
            String authenticationKey, hK_uuid;

            SharedPreferences sp = getSharedPreferences(Constants.APP_PREFERENCE_NAME, Activity.MODE_PRIVATE);
            authenticationKey = sp.getString("authenticationKey", "");
            hK_uuid = sp.getString("hK_UUID", "");

            SendOverDueActivitiesService sendOverDueActivitiesService = new SendOverDueActivitiesService(getApplicationContext());
            Message message = sendOverDueActivitiesService.SendOverDueActivity(authenticationKey, hK_uuid, overdueActivitiesList, overdueActivitiesAfter48List);

            if (message.what == 0 && message.arg1 == 0) {
                JSONObject jObj = (JSONObject) message.obj;
                if (jObj.has("messageCode")) {
                    if (jObj.optInt("messageCode") == Constants.HANDLER_MESSAGE_CODE_OK) {
                        Log.i(TAG, "JSONRESPONSE for SendOverDueActivities = " + jObj.toString());
                    }
                }
            } else {
                Log.i(TAG, "msg = " + message);
            }

        }

    }
}


