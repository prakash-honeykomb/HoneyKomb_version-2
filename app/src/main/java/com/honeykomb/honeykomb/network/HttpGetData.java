package com.honeykomb.honeykomb.network;

import android.app.ProgressDialog;
import android.util.Log;

import com.honeykomb.honeykomb.activity.BaseActivity;
import com.honeykomb.honeykomb.fragment.BaseFragment;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by laxmanamurthy on 10/3/2016.
 */

public class HttpGetData extends SafeAsyncTask<String> {


    String _url;
    String _message;
    BaseActivity _baseActivity;
    ProgressDialog _dialog;
    BaseFragment _baseFragment;
    //String accessToken;

    public HttpGetData(String message, String url, BaseActivity baseActivity) {
        this._baseActivity = baseActivity;
        this._url = url;
        this._message = message;
        //this.accessToken = accessToken;
        _dialog = new ProgressDialog(_baseActivity);
    }

    public HttpGetData(String message, String url, BaseFragment baseFragment) {
        this._baseFragment = baseFragment;
        this._url = url;
        this._message = message;
        //this.accessToken = accessToken;
        _dialog = new ProgressDialog(_baseFragment.getActivity());

    }

    @Override
    protected void onPreExecute() throws Exception {
        super.onPreExecute();
        _dialog.setMessage(_message);
        _dialog.setCancelable(false);
        _dialog.setCanceledOnTouchOutside(false);
        _dialog.show();

    }

    @Override
    public String call() throws Exception {

        return executeRequest();

    }

    @Override
    protected void onSuccess(String result) throws Exception {
        super.onSuccess(result);

        if (_baseActivity != null) {
            _baseActivity.parseJsonResponse(result, _url);
        }
        if (_baseFragment != null) {
            _baseFragment.parseJsonResponse(result, _url);
        }
        _dialog.dismiss();

    }

    private String executeRequest() {
        String response = null;
        try {
            //  OAuth oauth = Util.db.getOAuth();
            Log.i(BaseActivity.TAG, "---------------Url-------------" + ":" + _url);
            URL url = new URL(_url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            //connection.setDoOutput (true);
            // connection.setUseCaches (true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");//application/x-www-form-urlencoded
            connection.connect();
            Log.i(BaseActivity.TAG, "-------------HTTP Status Code----------" + ":" + connection.getResponseCode());
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream is = connection.getInputStream();
                response = IOUtils.toString(is);
            } else {
                InputStream is = connection.getErrorStream();
                response = IOUtils.toString(is);
            }
            Log.i(BaseActivity.TAG, "-------------HTTP Response----------" + ":" + " :: " + response);
            connection.disconnect();
        } catch (Exception e) {
            Log.i(BaseActivity.TAG, "-------------Web Service Exception----------" + ":" + e.getMessage());
        }

        return response;
    }

}
