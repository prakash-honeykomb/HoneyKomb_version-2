package com.honeykomb.honeykomb.async;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.honeykomb.honeykomb.server_manager.ConnectServer;
import com.honeykomb.honeykomb.service.AddActivityService;
import com.honeykomb.honeykomb.utils.Constants;
import com.honeykomb.honeykomb.utils.Util;
import com.honeykomb.honeykomb.utils.UtilityHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executor;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Hyd-Shekar on 1/17/2017.
 */

public class AddActivityAsync extends AsyncTask<String, Void, String> {
    private static final String TAG = AddActivityAsync.class.getSimpleName();

    private Context context;
    private String authenticationKey;
    private String hK_uuid;
    private String auth = "";

    public AddActivityAsync(Context context) {
        this.context = context;
        SharedPreferences sp = context.getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        this.authenticationKey = sp.getString("authenticationKey", "");
        this.hK_uuid = sp.getString("hK_UUID", "");
    }

    @Override
    protected String doInBackground(String... params) {
        if (Looper.myLooper() == null) {
            Looper.prepare();
        }
        Handler mHandle = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {

                JSONArray allActivitysFromDB = Util._db.getAllActivitysFromDBToServer();
                if (allActivitysFromDB.length() != 0) {
                    AddActivityService addActivityService = new AddActivityService(context);
                    Message message = addActivityService.AddActivity(authenticationKey, hK_uuid, allActivitysFromDB, context);
                    if (message.what == 0 && message.arg1 == 0) {
                        JSONObject jObj = (JSONObject) message.obj;
                        if (jObj.has("messageCode")) {
                            if (jObj.optInt("messageCode") == Constants.HANDLER_MESSAGE_CODE_OK) {
                                Log.i(TAG, "messageCode  = " + jObj.optInt("messageCode"));
                                ContentValues contentValues = new ContentValues();
                                contentValues.put("actionType", " ");
                                contentValues.put("PendingRoomJID", "0");
                                Util._db.updateFromAddActivityAsync(contentValues);
                            }
                            if (jObj.optInt("messageCode") == Constants.HANDLER_MESSAGE_CODE_OK) {
                                JSONArray jsonArrayForACK = new JSONArray();
                                for (int i = 0; i < allActivitysFromDB.length(); i++) {
                                    try {
                                        JSONObject jsonObject = allActivitysFromDB.getJSONObject(i);
                                        JSONObject jsonObjectForACK = new JSONObject();
                                        jsonObjectForACK.put("activityId", jsonObject.getString("activityId"));
                                        jsonObjectForACK.put("activityOwner", hK_uuid);
                                        jsonArrayForACK.put(jsonObjectForACK);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                ConnectServer.sendAcknowledgement(context, authenticationKey, hK_uuid, Constants.Service_Name_Activity, jsonArrayForACK, null, "ADDActivityASYNC");
                            } else if (jObj.optInt("messageCode") == Constants.HANDLER_MESSAGE_CODE_INSUFFICIENT) {
                                Log.i(TAG, "insufficient data = " + jObj.toString());
                            } else if (jObj.optInt("messageCode") == Constants.HANDLER_MESSAGE_UNAUTHORIZED) {
                                Util.updateLaunchMode(context);
                            }
                        }
                    }
                } else {
                    Log.i(TAG, "ActivityService handlerAcvtivity Failed  No activity to send");
                }

                Intent broadcastInt = new Intent();
                broadcastInt.setAction(Constants.REFRESH_VIEW);
                LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastInt);
                Executor executor = UtilityHelper.getExecutor();

                new GetAllActivitiesAsync(context, authenticationKey, hK_uuid, "No", "No").executeOnExecutor(executor);
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                    new GetAllActivitiesAsync(context, authenticationKey, hK_uuid, "No", "No").executeOnExecutor(executor);
                else
                    new GetAllActivitiesAsync(context, authenticationKey, hK_uuid, "No", "No").execute("");*/

                return true;
            }
        });
        mHandle.dispatchMessage(new Message());
        Looper.loop();
        return auth;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

    }

}