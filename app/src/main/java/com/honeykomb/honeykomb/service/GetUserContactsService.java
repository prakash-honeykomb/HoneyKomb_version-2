package com.honeykomb.honeykomb.service;

import android.content.Context;
import android.os.Message;
import android.util.Log;

import com.honeykomb.honeykomb.network.WebURLs;
import com.honeykomb.honeykomb.utils.Util;

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

/**
 * Created by Hyd-Shekar on 1/17/2017.
 */
public class GetUserContactsService {
    public static final String USER_CONTACTS_LIST = "users/contactlist";
    private static final String TAG = "HttpReq";
    int CONNECTION_TIMEOUT = 15000;
    int DATARETRIEVAL_TIMEOUT = 15000;
    Context context = null;

    public GetUserContactsService(Context context) {
        this.context = context;
    }

    // public Message GetContacts(String authenticationKey, String hK_uuid, ArrayList<String> arrayPhone)  prakash 07 july 2017
    public Message GetContacts(String authenticationKey, String hK_uuid, JSONArray arrayPhone) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.putOpt("authenticationKey", authenticationKey);
            jsonObject.putOpt("hK_UUID", hK_uuid);
            jsonObject.putOpt("phoneIds", arrayPhone);

            Log.i(TAG, "json objectCreateUpdateActivity to Server = " + jsonObject.toString());

        } catch (JSONException je) {
            je.printStackTrace();
        }
        String url = WebURLs.REST_ACTION_CONTACT_LIST ;
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
                Log.i(TAG, "jRequestObj VALUE... " + jsonObject.toString());
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                StringBuilder responseStrBuilder = new StringBuilder();

                String inputStr;
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);

                Log.i(TAG, "Server Response :" + responseStrBuilder.toString());
                msg.arg1 = 0;
                msg.obj = new JSONObject(responseStrBuilder.toString());
                Log.i(TAG, "mHandle.sendMessage... " + msg.toString());
                //AppData.serverRequestQueue--;
                return msg;
            } else {
                Log.i(TAG, statusCode + " Error Url : " + url);

                if (context != null)
                    Log.i(TAG, "ContactServiceUpdate Fail to connect to Server.\n Check Internet Connection.");
                return msg;
                //AppData.serverRequestQueue--;
            }

        } catch (JSONException e) {
            if (context != null) {
                //          Toast.makeText(context, "Fail to connect to Server.\n Check Internet Connection.", Toast.LENGTH_LONG).show();
            }
            //AppData.serverRequestQueue--;
            e.printStackTrace();
            return msg;
        } catch (IOException e) {
            if (context != null) {
                //            Toast.makeText(context, "Fail to connect to Server.\n Check Internet Connection.", Toast.LENGTH_LONG).show();
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


