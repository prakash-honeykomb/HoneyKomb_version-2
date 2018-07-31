package com.honeykomb.honeykomb.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.honeykomb.honeykomb.async.GetAllActivitiesAsync;
import com.honeykomb.honeykomb.async.NotificationMoveAsync;
import com.honeykomb.honeykomb.network.WebURLs;
import com.honeykomb.honeykomb.utils.Constants;
import com.honeykomb.honeykomb.utils.Util;
import com.honeykomb.honeykomb.utils.UtilityHelper;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Rajashekar on 28/06/18.
 */

public class IntentServiceForNotification extends IntentService {

    private String authenticationKey;
    private String hK_UUID;
    private String notificationMessage = "";
    private String TAG = IntentServiceForNotification.class.getSimpleName();

    public IntentServiceForNotification() {
        super("IntentServiceForNotification");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        authenticationKey = UtilityHelper.getStringPreferences(getApplicationContext(), Constants.AUTH_KEY).toString();
        hK_UUID = UtilityHelper.getStringPreferences(getApplicationContext(), Constants.HK_UUID).toString();

        if (intent.getStringExtra("notificationMessage") != null) {
            notificationMessage = intent.getStringExtra("notificationMessage"); // any data that you want to send back to receivers
        }
        String response = executeRequest();
        pasrseResponse(response);
    }

    private String executeRequest() {
        String response = null;
        String method = "POST";
        String _url = WebURLs.REST_ACTION_USER_NOTIFICATIONS;
        try {
            Log.i(TAG, "---------------Url-------------:" + WebURLs.REST_ACTION_USER_NOTIFICATIONS);
            URL url = new URL(_url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setRequestMethod(method);
            String body = getBody();
            Log.i(TAG, "---------------Body-------------:" + body);
            Log.i(TAG, "---------------Is Valid Json-------------:" + Util.isJSONValid(body));
            if (Util.isJSONValid(body)) {
                connection.setRequestProperty("Content-Type", "application/json");
            } else {
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            }
            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(body);
            writer.flush();
            writer.close();
            os.close();

            connection.connect();
            Log.i(TAG, "-------------HTTP Status Code----------:" + connection.getResponseCode());
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream is = connection.getInputStream();
                response = IOUtils.toString(is);
            } else {
                InputStream is = connection.getErrorStream();
                response = IOUtils.toString(is);
            }
            Log.i(TAG, "-------------HTTP Response---------- :" + _url + " :: " + response);
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "--------ErrorLogService-----Web Service Exception----------:" + e.getMessage());
        }

        return response;

    }

    private String getBody() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.putOpt("authenticationKey", authenticationKey);
            jsonObject.putOpt("hK_UUID", hK_UUID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private void pasrseResponse(String response) {
            try {
                JSONObject jObj = new JSONObject(response);
                if (jObj.has("messageCode")) {
                    if (jObj.optInt("messageCode") == Constants.HANDLER_MESSAGE_CODE_OK) {
                        Log.i(TAG, "JSONRESPONSE for Notifications = " + jObj.toString());
                        try {
                            JSONArray jsonArray = jObj.getJSONArray("notificationsList");
                            if (jsonArray.length() != 0) {
                                new NotificationMoveAsync(getApplicationContext(), jObj.getJSONArray("notificationsList"), authenticationKey, hK_UUID).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                Bundle bundle = new Bundle();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String notificationId = jsonObject.getString("notificationId");
                                    Log.i(TAG, "notificationId = " + notificationId);

                                    bundle.putString("notificationId", jsonObject.getString("notificationId"));
                                    bundle.putString("notificaitonText", jsonObject.getString("notificaitonText"));
                                    bundle.putString("createdBy", jsonObject.getString("createdBy"));
                                    bundle.putString("actionType", jsonObject.getString("actionType"));
                                    bundle.putString("dateCreated", jsonObject.getString("dateCreated"));
                                    bundle.putString("objectId", jsonObject.getString("objectId"));
                                    bundle.putString("objectType", jsonObject.getString("objectType"));
                                    bundle.putString("pendingInvCount", String.valueOf(jsonObject.getInt("pendingInvCount")));
                                    bundle.putString("lastUpdated", jsonObject.getString("lastUpdated"));
                                    Util._db.createInvitation(bundle);
                                }
                            } else {
                                Log.i("Pankaj", "ELSE CURSOR");
                            }
                            new GetAllActivitiesAsync(IntentServiceForNotification.this, authenticationKey, hK_UUID, "No", notificationMessage).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Log.i(TAG, "msg = " + response);
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
    }
}
