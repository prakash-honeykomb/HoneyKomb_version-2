package com.honeykomb.honeykomb.service;


import android.content.Context;
import android.os.Message;
import android.util.Log;

import com.honeykomb.honeykomb.dao.ServiceContactObject;
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
import java.util.ArrayList;

/**
 * Created by Rajashekar.Nimmala on 8/8/2017.
 */

public class VerifyUserService {
    private static final String TAG = VerifyUserService.class.getSimpleName();
    private Context context;

    public VerifyUserService(Context context) {
        this.context = context;
    }

    public Message verifyUser(ArrayList<ServiceContactObject> serviceContactObject, String authenticationKey, String hK_uuid) {
        JSONObject jObjectActivity = new JSONObject();
        JSONArray arrayOfContacts = new JSONArray();
        try {
            for (ServiceContactObject contactObject : serviceContactObject) {
                JSONObject jsonContactObject = new JSONObject();
                jsonContactObject.putOpt("displayName", contactObject.contactName);
                jsonContactObject.putOpt("phone", contactObject.contactNo);
                arrayOfContacts.put(jsonContactObject);
            }
            jObjectActivity.putOpt("authenticationKey", authenticationKey);
            jObjectActivity.putOpt("hK_UUID", hK_uuid);
            jObjectActivity.putOpt("profiles", arrayOfContacts);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = WebURLs.REST_ACTION_NEW_USER_REG;
        Message msg = new Message();
        msg.what = 0;
        msg.arg1 = -1;

        HttpURLConnection urlConnection = null;
        try {
            URL urlToRequest = new URL(url);
            urlConnection = (HttpURLConnection) urlToRequest.openConnection();
            urlConnection.setConnectTimeout(15000);
            urlConnection.setReadTimeout(15000);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty("Content-Type", "application/json");

            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
            out.write(jObjectActivity.toString());
            out.close();

            int statusCode = urlConnection.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                Log.i(TAG, "jRequestObj VALUE... " + jObjectActivity.toString());
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
                return msg;
            } else {
                Log.i(TAG, statusCode + " Error Url : " + url);

                if (context != null)
                    Log.i(TAG, "ContactServiceUpdate Fail to connect to Server.\n Check Internet Connection.");
                return msg;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return msg;
        } catch (IOException e) {
            e.printStackTrace();
            return msg;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }
}


