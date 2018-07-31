package com.honeykomb.honeykomb.service;


import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Message;
import android.util.Log;

import com.honeykomb.honeykomb.database.DataBaseHelper;
import com.honeykomb.honeykomb.utils.Constants;
import com.honeykomb.honeykomb.utils.Util;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Rajashekar.Nimmala on 12/8/2017.
 */

public class GetContactsFromServer extends IntentService {
    private String TAG = GetContactsFromServer.class.getSimpleName();

    public GetContactsFromServer() {
        super("GetContactsFromServer");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        initDB(getApplicationContext());
        try {
            Log.i(TAG, "----onHandleIntent----");
            executeRequest();
        } catch (Exception e) {
            Log.i(TAG + "----Exception----", ": " + e.getMessage());
            this.stopSelf();
        }
        this.stopSelf();
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
            Log.i(TAG, "initDB Exception = " + e.getMessage());
        }
    }

    private void executeRequest() {
        getContactsFromServer();
    }

    private void getContactsFromServer() {

        SharedPreferences sp = getSharedPreferences(Constants.APP_PREFERENCE_NAME, Activity.MODE_PRIVATE);
        String authenticationKey = sp.getString("authenticationKey", "");
        String hK_UUID = sp.getString("hK_UUID", "");
        JSONArray DeviceUsers1 = new JSONArray();
        GetUserContactsService getUserContactsService = new GetUserContactsService(getApplicationContext());
        Message message = getUserContactsService.GetContacts(authenticationKey, hK_UUID, DeviceUsers1);
        if (message.what == 0 && message.arg1 == 0) {
            JSONObject jObj = (JSONObject) message.obj;
            if (jObj.has("messageCode")) {
                if (jObj.optInt("messageCode") == Constants.HANDLER_MESSAGE_CODE_OK) {
                    Log.i(TAG, "GetUserContacts ==: " + jObj.toString());
                    Util._db.createUserContacts(jObj);
                } else {
                    Log.i(TAG, "GetContacts Else");
                }
            }
        } else {
            Log.i(TAG, "USER Contacts Failed ");
        }
    }
}

