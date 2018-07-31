package com.honeykomb.honeykomb.service;

import android.content.Context;
import android.os.Message;
import android.util.Log;

import com.honeykomb.honeykomb.network.WebURLs;

import org.json.JSONArray;
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

public class AddActivityService {
    private static final String TAG = "AddActivityService";
    private Context context;

    public AddActivityService(Context context) {
        this.context = context;
    }

    public Message AddActivity(String authenticationKey, String hK_uuid, JSONArray allActivitysFromDB, Context activity) {
        this.context = activity;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.putOpt("authenticationKey", authenticationKey);
            jsonObject.putOpt("hK_UUID", hK_uuid);
            jsonObject.putOpt("userActivities", allActivitysFromDB);

            Log.i(TAG, "AddActivity to Server = " + jsonObject.toString());
        } catch (JSONException je) {
            je.printStackTrace();
        }
        String url = WebURLs.REST_ACTION_ADD_UPDATE_ACTIVITY;
        Message msg = new Message();
        msg.what = 0;
        msg.arg1 = -1;

        HttpURLConnection urlConnection = null;
        try {
            URL urlToRequest = new URL(url);
            urlConnection = (HttpURLConnection) urlToRequest.openConnection();
            int CONNECTION_TIMEOUT = 30000;
            urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
            int DATA_RETRIEVAL_TIMEOUT = 30000;
            urlConnection.setReadTimeout(DATA_RETRIEVAL_TIMEOUT);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty("Content-Type", "application/json");

            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
            out.write(jsonObject.toString());
            out.close();

            int statusCode = urlConnection.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                Log.i(TAG, jsonObject.toString());
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                StringBuilder responseStrBuilder = new StringBuilder();

                String inputStr;
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);
                Log.i(TAG, "AddActivity Server URL = " + urlToRequest.toString());
                Log.i(TAG, "AddActivity Server Response = " + responseStrBuilder.toString());
                msg.arg1 = 0;
                msg.obj = new JSONObject(responseStrBuilder.toString());
                Log.i(TAG,"mHandle.sendMessage..."+ msg.toString());
                //AppData.serverRequestQueue--;
                return msg;
            } else {
                Log.i(TAG, statusCode + " Error Url : " + url);
                if (context != null) {
//                    Toast.makeText(context, "Fail to connect to Server.\n Check Internet Connection.", Toast.LENGTH_LONG).show();
                    Log.i(TAG, "AddActivity to Server Response= " + url);
                }
                return msg;
                //AppData.serverRequestQueue--;
            }

        } catch (JSONException e) {
            if (context != null) {
                Log.i(TAG, "AddActivity to Server Response= " + e);
//                Toast.makeText(context, "Fail to connect to Server.\n Check Internet Connection.", Toast.LENGTH_LONG).show();
            }
            //AppData.serverRequestQueue--;
            e.printStackTrace();
            return msg;
        } catch (IOException e) {
            if (context != null) {
                Log.i(TAG, "AddActivity to Server Response= " + e);
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
