package com.honeykomb.honeykomb.network;

import android.app.ProgressDialog;
import android.util.Log;

import com.honeykomb.honeykomb.activity.BaseActivity;
import com.honeykomb.honeykomb.fragment.BaseFragment;

import org.apache.commons.io.IOUtils;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by laxmanamurthy on 12/31/2016.
 */

public class HttpPutData extends SafeAsyncTask<String> {

    String _url;
    String _message;
    BaseActivity _baseActivity;
    ProgressDialog _dialog;
    String requestType;
    String body;
    BaseFragment _baseFragment;
    //String accessToken;

    public HttpPutData(String message, String url, String body, BaseActivity baseActivity) {
        this._baseActivity = baseActivity;
        this._url = url;
        this._message = message;
        this.requestType = requestType;
        this.body = body;
        //this.accessToken = accessToken;
        _dialog = new ProgressDialog(_baseActivity);

    }

    public HttpPutData(String message, String url, String body, BaseFragment baseFragment) {
        this._baseFragment = baseFragment;
        this._url = url;
        this._message = message;
        this.requestType = requestType;
        this.body = body;
        //this.accessToken = accessToken;
        _dialog = new ProgressDialog(_baseFragment.getActivity());

    }

    @Override
    protected void onPreExecute() throws Exception {
        super.onPreExecute();
        _dialog.setMessage(_message);
        _dialog.setCancelable(false);
        _dialog.show();
        /*_dialog.show();
        _dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        _dialog.setContentView(R.layout.progress);*/

    }

    @Override
    public String call() throws Exception {
        //if(RefreshTocken.isTokenRefresh()){
        return executeRequest();
        //}
        // return null;

    }

    @Override
    protected void onSuccess(String result) throws Exception {
        super.onSuccess(result);
        _dialog.dismiss();
        if (_baseActivity != null) {
            _baseActivity.parseJsonResponse(result, _url);
        }
        if (_baseFragment != null) {
            _baseFragment.parseJsonResponse(result, _url);
        }


    }

    private String executeRequest() {
        String response = null;

        try {
            Log.i(BaseActivity.TAG, "---------------Url-------------" + ":" + _url);
            //String url_string= URLEncoder.encode(_url);
            // OAuth oauth = Util.db.getOAuth();
            URL url = new URL(_url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(true);
            connection.setRequestMethod("PUT");
            //Log.i(BaseActivity.TAG+"---------------Body-------------",":"+body);
            //Log.i(BaseActivity.TAG+"---------------Is Valid Json-------------",":"+Util.isJSONValid(body));

           /* if(!Util.NullChecker(oauth.access_token).isEmpty() &&  Util.isOAuthRequired(_url)){
                connection.setRequestProperty("Authorization",oauth.token_type+" "+oauth.access_token);
                Log.i(BaseActivity.TAG+"---------------Access Token-------------",":"+oauth.access_token);
            }*/
           /* if(Util.isJSONValid(body)){
                connection.setRequestProperty("Content-Type","application/json");
            }else{
                connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            }*/
            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(body);
            writer.flush();
            writer.close();
            os.close();
            connection.connect();
            Log.i(BaseActivity.TAG, "-------------HTTP Status Code----------" + ":" + connection.getResponseCode());
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream is = connection.getInputStream();
                response = IOUtils.toString(is);
            } else {
                InputStream is = connection.getErrorStream();
                response = IOUtils.toString(is);
            }
            Log.i(BaseActivity.TAG, "-------------HTTP Response----------" + ":" + requestType + " :: " + response);
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(BaseActivity.TAG, "-------------Web Service Exception----------" + ":" + e.getMessage());
        }

        return response;
    }
}
