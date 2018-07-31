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

/**
 * Created by Rajashekar.Nimmala on 4/7/2017.
 */

public class DeliverdAndReadStatusService {
    int CONNECTION_TIMEOUT = 15000;
    int DATARETRIEVAL_TIMEOUT = 15000;
    private String TAG = DeliverdAndReadStatusService.class.getName();
    private Context applicationContext;

    public DeliverdAndReadStatusService(Context applicationContext) {
        this.applicationContext = applicationContext;

    }

    public Message ServiceCall(String authenticationKey, String hK_uuid, /*String[] hk_uuid_list*/ String activityId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.putOpt("authenticationKey", authenticationKey);
            jsonObject.putOpt("hK_UUID", hK_uuid);
            jsonObject.putOpt("activityId", activityId);


        } catch (JSONException je) {
            je.printStackTrace();
        }
        Log.i(TAG, "request " + jsonObject.toString());
        String url = WebURLs.REST_ACTION_READ_N_DELIVER;
        Message msg = new Message();
        msg.what = 0;
        msg.arg1 = -1;

        HttpURLConnection urlConnection = null;
        try {
            URL urlToRequest = new URL(url);
            urlConnection = (HttpURLConnection) urlToRequest.openConnection();
            urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
            urlConnection.setReadTimeout(DATARETRIEVAL_TIMEOUT);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty("Content-Type", "application/json");

            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
            out.write(jsonObject.toString());
            out.close();

            int statusCode = urlConnection.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                Log.i(TAG, "readNDeliverdURL server response... " + jsonObject.toString());
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                StringBuilder responseStrBuilder = new StringBuilder();

                String inputStr;
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);

                Log.i(TAG, "readNDeliverdURL Server Response :" + responseStrBuilder.toString());
                msg.arg1 = 0;
                msg.obj = new JSONObject(responseStrBuilder.toString());
                Log.i(TAG, "readNDeliverdURL mHandle.sendMessage... " + msg.toString());
                //AppData.serverRequestQueue--;
                return msg;
            } else {
                Log.i(TAG, statusCode + " Error Url : " + url);
                if (applicationContext != null) {
                }
                return msg;
                //AppData.serverRequestQueue--;
            }

        } catch (JSONException e) {
            if (applicationContext != null) {
            }
            //AppData.serverRequestQueue--;
            e.printStackTrace();
            return msg;
        } catch (IOException e) {
            if (applicationContext != null) {
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


