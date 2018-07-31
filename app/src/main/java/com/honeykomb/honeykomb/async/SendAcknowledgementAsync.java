package com.honeykomb.honeykomb.async;

import android.content.Context;
import android.os.AsyncTask;
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

public class SendAcknowledgementAsync extends AsyncTask<Void, Void, Message> {
    private static final String TAG = "HttpReq";
    int CONNECTION_TIMEOUT = 15000;
    int DATARETRIEVAL_TIMEOUT = 15000;
    Context context = null;
    private Context activity;
    private String authenticationKey;
    private String hK_uuid;
    private String service_name;
    private String SEND_ACKNOWLEDGEMENT = "acknowledgement";

    public SendAcknowledgementAsync(Context activity, String authenticationKey, String hK_uuid, String service_name) {
        this.activity = activity;
        this.authenticationKey = authenticationKey;
        this.hK_uuid = hK_uuid;
        this.service_name = service_name;
    }

    @Override
    protected Message doInBackground(Void... params) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArrayForACK = new JSONArray();
        try {
            jsonObject.putOpt("authenticationKey", authenticationKey);
            jsonObject.putOpt("hK_UUID", hK_uuid);
            jsonObject.putOpt("serviceName", service_name);
            jsonObject.putOpt("activityList", jsonArrayForACK);

        } catch (JSONException je) {
            je.printStackTrace();
        }
        String url = WebURLs.REST_ACTION_ACKNOWLEDGE_SERVER;
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

    @Override
    protected void onPostExecute(Message aVoid) {
        super.onPostExecute(aVoid);
        Log.i(TAG, "onPostExecute... SEND_ACKNOWLEDGEMENT = " + aVoid);
    }
}


