package com.honeykomb.honeykomb.service;

import android.content.Context;
import android.os.Message;
import android.util.Log;

import com.honeykomb.honeykomb.network.WebURLs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateActivityUsersService {
    private static final String GET_ACTIVITY = "users/getactivity";
    private static final String TAG = "HttpReq";
    Context context = null;
    int CONNECTION_TIMEOUT = 15000;
    int DATARETRIEVAL_TIMEOUT = 15000;

    public UpdateActivityUsersService(Context context) {
        this.context = context;
    }

    public Message GetServiceResponse(String authenticationKey, String hK_uuid, String activityID, String actionType, String rsvpCount, String needActivity) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.putOpt("authenticationKey", authenticationKey);
            jsonObject.putOpt("hK_UUID", hK_uuid);
            jsonObject.putOpt("accept", actionType);
            jsonObject.putOpt("activityId", activityID);
            jsonObject.putOpt("countRSVP", rsvpCount);
            jsonObject.putOpt("needActivity", needActivity);
            Log.i(TAG, "UpdateActivityUsersService Request" + jsonObject.toString());

        } catch (JSONException je) {
            je.printStackTrace();
        }
        String url = WebURLs.REST_ACTION_USER_GET_ACTIVITY;
        Message msg = new Message();
        msg.what = 0;
        msg.arg1 = -1;

        HttpURLConnection urlConnection = null;
        try {
            URL urlToRequest = new URL(url);
            urlConnection = (HttpURLConnection) urlToRequest.openConnection();
//            urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
//            urlConnection.setReadTimeout(DATARETRIEVAL_TIMEOUT);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty("Content-Type", "application/json");

            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
            out.write(jsonObject.toString());
            out.close();

            int statusCode = urlConnection.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {

                Log.i(TAG, "UpdateActivityUsersService Request" + jsonObject.toString());
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                StringBuilder responseStrBuilder = new StringBuilder();

                String inputStr;
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);

                Log.i(TAG, "Server Response :" + responseStrBuilder.toString());
                msg.arg1 = 0;
                msg.obj = new JSONObject(responseStrBuilder.toString());
                Log.i(TAG, "mHandle.sendMessage..." + msg.toString());
                //AppData.serverRequestQueue--;
                return msg;
            } else {
                Log.i(TAG, statusCode + " Error Url : " + url);
                if (context != null) {
//                    Toast.makeText(context, "Fail to connect to Server.\n Check Internet Connection.", Toast.LENGTH_LONG).show();
                }
                return msg;
                //AppData.serverRequestQueue--;
            }

        } catch (JSONException e) {
            if (context != null) {
//                Toast.makeText(context, "Fail to connect to Server.\n Check Internet Connection.", Toast.LENGTH_LONG).show();
            }
            //AppData.serverRequestQueue--;
            e.printStackTrace();
            return msg;
        } catch (IOException e) {
            if (context != null) {
//                Toast.makeText(context, "Fail to connect to Server.\n Check Internet Connection.", Toast.LENGTH_LONG).show();
            }
            //AppData.serverRequestQueue--;
            e.printStackTrace();
            return msg;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }
}

