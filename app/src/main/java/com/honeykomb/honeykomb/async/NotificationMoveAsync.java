package com.honeykomb.honeykomb.async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.honeykomb.honeykomb.network.WebURLs;
import com.honeykomb.honeykomb.utils.Constants;
import com.honeykomb.honeykomb.utils.Util;

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
 * Created by Hyd-Shekar on 2/1/2017.
 */

public class NotificationMoveAsync extends AsyncTask<Void, Void, Void> {
    private String authenticationKey;
    private String hK_uuid;
    private JSONArray jsonArray;
    private Context applicationContext;
    private String TAG = NotificationMoveAsync.class.getSimpleName();

    public NotificationMoveAsync(Context applicationContext, JSONArray notificationsList, String authenticationKey, String hK_uuid) {
        this.authenticationKey = authenticationKey;
        this.hK_uuid = hK_uuid;
        this.jsonArray = notificationsList;
        this.applicationContext = applicationContext;
    }

    @Override
    protected Void doInBackground(Void... params) {
        String response = executeRequest(WebURLs.REST_ACTION_NOTIFICATION_MOVE, getBody());
        parseResponse(response);
        return null;
    }

    private String getBody() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.putOpt("authenticationKey", authenticationKey);
            jsonObject.putOpt("hKUUID", hK_uuid);
            jsonObject.putOpt("notificationsList", jsonArray);

        } catch (JSONException je) {
            je.printStackTrace();
            return jsonObject.toString();
        }
        return jsonObject.toString();
    }

    private void parseResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            Log.i(TAG, " jObj = " + jsonObject.toString());
            if (jsonObject.has("messageCode")) {
                if (jsonObject.optInt("messageCode") == Constants.HANDLER_MESSAGE_CODE_OK) {
                    Log.i(TAG, "JSONRESPONSE for NotificationMoveAsync = " + jsonObject.toString());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i(TAG, "JSONException for NotificationMoveAsync = " + response);
        }

    }

    private String executeRequest(String _url, String body) {

        String response = null;
        String method = "POST";
        try {
            Log.i(TAG, "---------------Url-------------:" + WebURLs.REST_ACTION_USER_NOTIFICATIONS);
            URL url = new URL(_url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setRequestMethod(method);
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
}
