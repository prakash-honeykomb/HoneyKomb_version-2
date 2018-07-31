package com.honeykomb.honeykomb.server_manager;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

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

public class HttpReq extends Thread {

    private static final String TAG = "HttpReq";
    int CONNECTION_TIMEOUT = 15000;
    int DATARETRIEVAL_TIMEOUT = 15000;
    Handler mHandle = null;
    String url = "";
    JSONObject jRequestObj = null;
    JSONObject jResponseObj = null;
    Context context = null;

    public HttpReq(Context mContext, String mUrl, JSONObject jObj, Handler mHandler) {
        mHandle = mHandler;
        context = mContext;
        url = mUrl;
        jRequestObj = jObj;
        /*try {
            jRequestObj.putOpt("requestId", UUID.randomUUID().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        //Log.i(TAG, "Client Request :"+AppData.serverRequestQueue+": " + jRequestObj.toString());
    }

    @Override
    public void run() {
        Log.v("HttpReq", "Run method called");
        super.run();
        Looper.prepare();

        Message msg = new Message();
        msg.what = 0;
        msg.arg1 = -1;

        HttpURLConnection urlConnection = null;
        try {
            URL urlToRequest = new URL(url);
            Log.v("HttpReq", " line 59:   " + url);
            urlConnection = (HttpURLConnection) urlToRequest.openConnection();
            urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
            urlConnection.setReadTimeout(DATARETRIEVAL_TIMEOUT);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty("Content-Type", "application/json");

            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
            out.write(jRequestObj.toString());
            out.close();
            Log.v("HttpReq", " line 71:   " + url);
            int statusCode = urlConnection.getResponseCode();
            Log.v("HttpReq", " line 73:   statusCode  " + statusCode);
            if (statusCode == HttpURLConnection.HTTP_OK) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                StringBuilder responseStrBuilder = new StringBuilder();

                String inputStr;
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);

                Log.i(TAG, "HTTPServer URL :" + urlToRequest.toString());
                Log.i(TAG, "HTTPServer Parameters :" + jRequestObj.toString());
                Log.i(TAG, "HTTPServer Response :" + responseStrBuilder.toString());
                msg.arg1 = 0;
                msg.obj = new JSONObject(responseStrBuilder.toString());
                if (mHandle != null) {
                    mHandle.sendMessage(msg);
                    Log.i(TAG, "mHandle.sendMessage..." + mHandle.getMessageName(msg));
                }//AppData.serverRequestQueue--;
            } else {
                Log.i(TAG, statusCode + " Error Url : " + url);
                if (mHandle != null)
                    mHandle.sendMessage(msg);
                if (context != null)
                    Toast.makeText(context, "Fail to connect to Server.\n Check Internet Connection. HttpReq line 94", Toast.LENGTH_LONG).show();
                //AppData.serverRequestQueue--;
            }
        } catch (JSONException e) {
            if (mHandle != null)
                mHandle.sendMessage(msg);
            if (context != null)
                Toast.makeText(context, "Fail to connect to Server.\n Check Internet Connection.HttpReq line 101", Toast.LENGTH_LONG).show();
            //AppData.serverRequestQueue--;
            e.printStackTrace();
        } catch (IOException e) {
            if (mHandle != null)
                mHandle.sendMessage(msg);
            if (context != null)
                //   Toast.makeText(context, "Fail to connect to Server.\n Check Internet Connection.HttpReq line 98", Toast.LENGTH_LONG).show();
                //AppData.serverRequestQueue--;
                e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        Looper.loop();
    }
}
