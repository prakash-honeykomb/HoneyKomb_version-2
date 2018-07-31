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
 * Created by Rajashekar on 21/06/2018.
 */

public class HttpPostData extends SafeAsyncTask<String> {

    String _url;
    String _message;
    BaseActivity _baseActivity;
    ProgressDialog _dialog;
    String body;
    BaseFragment _baseFragment;

    //String accessToken;
    public HttpPostData(String message, String url, String body, BaseActivity baseActivity) {
        this._baseActivity = baseActivity;
        this._url = url;
        this._message = message;

        this.body = body;
        //this.accessToken = accessToken;
        _dialog = new ProgressDialog(_baseActivity);
        _dialog.setMessage(message);
    }

    public HttpPostData(String message, String url, String body, BaseFragment baseFragment) {
        this._baseFragment = baseFragment;
        this._url = url;
        this._message = message;

        this.body = body;
        //this.accessToken = accessToken;
        _dialog = new ProgressDialog(_baseFragment.getActivity());
        _dialog.setMessage(message);
    }

    @Override
    protected void onPreExecute() throws Exception {
        super.onPreExecute();
        _dialog.setCancelable(false);
        _dialog.setCanceledOnTouchOutside(false);
        _dialog.show();
       /* _dialog.show();
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


        if (_baseActivity != null) {
            _baseActivity.parseJsonResponse(result, _url);
        }
        if (_baseFragment != null) {
            _baseFragment.parseJsonResponse(result, _url);
        }
        _dialog.dismiss();
        /*try {

            if (result == null) {
                Util.displayAlert(_baseActivity, "Server Connection failed, Please check your internet connection.");
            } else if (Util.isErrorResponse(result)) {
                if (_url.equalsIgnoreCase(WebServiceUrls.LOGIN)) {
                    Util.displayAlert(_baseActivity, _baseActivity.getResources().getString(R.string.user_login_error));
                }else {
                    RefreshToken token = new RefreshToken(_baseActivity,result);
                    token.setHttpPostData(this);
                    token.execute();
                   //Util.displayAlert(_baseActivity, new JSONObject(result).optString("error_description"));
                }
            } else {
                _baseActivity.parseJsonResponse(result, _url);
            }

        } catch (Exception e) {

            Util.displayAlert(_baseActivity, "Server Connection failed, Please check your internet connection.");
        }*/
    }

    private String executeRequest() {
        String response = null;
        try {
            Log.i(BaseActivity.TAG, "---------------Url-------------" + ":" + _url);
            //OAuth oauth = Util.db.getOAuth();
            URL url = new URL(_url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            Log.i(BaseActivity.TAG, "---------------Body-------------" + ":" + body);
            //  Log.i(BaseActivity.TAG+"---------------Is Valid Json-------------"+":" + Util.isJSONValid(body));

           /* if (_url.equalsIgnoreCase(WebServiceUrls.LOGIN)) {
                Log.i(BaseActivity.TAG+"---------------Static Access Token-------------"+":" + _baseActivity.getResources().getString(R.string.auth_code));
                connection.setRequestProperty("Authorization", "Basic " + _baseActivity.getResources().getString(R.string.auth_code));
            } else if (!oauth.access_token.isEmpty() && Util.isOAuthRequired(_url)) {
                connection.setRequestProperty("Authorization", oauth.token_type + " " + oauth.access_token);
                Log.i(BaseActivity.TAG+"---------------Access Token-------------", ":" + oauth.token_type + " " + oauth.access_token);
            }
            if (Util.isJSONValid(body)) {
                connection.setRequestProperty("Content-Type", "application/json");
            } else {
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
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
            Log.i(BaseActivity.TAG, "-------------HTTP Response----------" + ":" + _url + " :: " + response);
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(BaseActivity.TAG, "-------------Web Service Exception----------" + ":" + e.getMessage());
        }

        return response;
    }

}
