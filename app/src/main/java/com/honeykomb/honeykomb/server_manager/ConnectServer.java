package com.honeykomb.honeykomb.server_manager;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.honeykomb.honeykomb.network.WebURLs;
import com.honeykomb.honeykomb.utils.Constants;
import com.honeykomb.honeykomb.utils.UtilityHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ConnectServer {
    private static final String TAG = "ConnectServer";
    private static ConnectServer instance;

    private static void initInstance() {
        if (instance == null) {
            instance = new ConnectServer();

        }
    }

    public static ConnectServer getInstance() {
        if (instance == null)
            initInstance();
        return instance;
    }

    public static void VerifyUser(Context context, String mobile, Long digitsID, Handler handler) {
        UtilityHelper.setStringPreferences(context, "FirstTimeLogin", false);
        JSONObject jObj = new JSONObject();
        try {
            jObj.putOpt("phone", mobile);
            jObj.putOpt("deviceModel", Constants.DEVICE_MODEL);
            if (!UtilityHelper.getStringPreferences(context, "devicetoken").toString().equalsIgnoreCase("")) {
                jObj.putOpt("deviceToken", UtilityHelper.getStringPreferences(context, "devicetoken"));
                Log.i(TAG, "DEVICETOKEN1  = " + UtilityHelper.getStringPreferences(context, "devicetoken"));
            } else {
                jObj.putOpt("deviceToken", " "/*(String) UtilityHelper.getStringPreferences(context, "devicetoken")*/);
                Log.i(TAG, "DEVICETOKEN2 = " + UtilityHelper.getStringPreferences(context, "devicetoken"));
                Log.i(TAG, "DEVICETOKEN2 = " + UtilityHelper.getStringPreferences(context, "devicetoken"));
            }
            jObj.putOpt("oSType", Constants.OS_TYPE);
            jObj.putOpt("oSVersion", Constants.OS_VERSION);
            jObj.putOpt("photo", " ");
            jObj.putOpt("displayName", " ");
            jObj.putOpt("digitsId", digitsID);
            jObj.putOpt("quickBloxID", digitsID);

        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        Log.i(TAG, "jObj = " + jObj.toString());
        HttpReq hr = new HttpReq(context, WebURLs.REST_ACTION_VERIFY_USER, jObj, handler);
        hr.start();
    }

    public static void CreateUser(Context context, String userName, String mobile, Handler handler) {
        JSONObject jObj = new JSONObject();
        Log.i(TAG, "CreateUser JOBJ VALUE = " + jObj.toString());
        try {
            Log.i(TAG, "CreateUser () displayName VALUE = " + userName);
            jObj.putOpt("phone", mobile);
            jObj.putOpt("photo", " ");
            jObj.putOpt("displayName", userName);
            jObj.putOpt("digitsId", " ");
            jObj.putOpt("quickBloxID", " ");
            jObj.putOpt("deviceToken", UtilityHelper.getStringPreferences(context.getApplicationContext(), "devicetoken"));
            jObj.putOpt("oSType", Constants.OS_TYPE);
            jObj.putOpt("oSVersion", Constants.OS_VERSION);
            jObj.putOpt("deviceModel", Constants.DEVICE_MODEL);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        Log.i(TAG, "JSONVALUEINCREATEUSER = " + jObj.toString());
        HttpReq hr = new HttpReq(context, WebURLs.REST_ACTION_CREATE_USER, jObj, handler);
        hr.start();
    }

    public static void getUserActivity(Context context, String authenticationKey, String hK_UUID, String login, Handler handler) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.putOpt("authenticationKey", authenticationKey);
            jsonObject.putOpt("hK_UUID", hK_UUID);
            jsonObject.putOpt("login", login);

            HttpReq hr = new HttpReq(context, WebURLs.REST_ACTION_USER_ACTIVITY, jsonObject, handler);
            hr.start();

        } catch (JSONException je) {
            je.printStackTrace();
        }

    }

    public static void getContacts(Context context, String authenticationKey, String hK_UUID, ArrayList<String> phone, Handler handler) {

        JSONObject jsonObject = new JSONObject();
        try {


            jsonObject.putOpt("authenticationKey", authenticationKey);
            jsonObject.putOpt("hK_UUID", hK_UUID);
//            JSONObject jsonObject1=new JSONObject(String.valueOf(phone));
            jsonObject.putOpt("phoneIds", phone);


            HttpReq hr = new HttpReq(context, WebURLs.REST_ACTION_CONTACT_LIST, jsonObject, handler);
            hr.start();

        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    public static void getUserNotifications(Context context, String authenticationKey, String hK_UUID, Handler handler) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.putOpt("authenticationKey", authenticationKey);
            jsonObject.putOpt("hK_UUID", hK_UUID);

            HttpReq hr = new HttpReq(context, WebURLs.REST_ACTION_USER_NOTIFICATIONS, jsonObject, handler);
            hr.start();

        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    public static void sendAcknowledgement(Context context, String authenticationKey, String hK_UUID, String serviceName, JSONArray jsonArrayForACK, Handler handler, String activityname) {
        JSONObject jsonObject = new JSONObject();
        try {

            Log.i(TAG, "error on : -->> " + activityname);
            jsonObject.putOpt("authenticationKey", authenticationKey);
            jsonObject.putOpt("hK_UUID", hK_UUID);
            jsonObject.putOpt("serviceName", serviceName);
            if (jsonArrayForACK != null && jsonArrayForACK.length() > 0) {
                jsonObject.putOpt("activityList", jsonArrayForACK);
                Log.v(TAG, "activityList->> " + jsonArrayForACK);
            }

            HttpReq hr = new HttpReq(context, WebURLs.REST_ACTION_ACKNOWLEDGE_SERVER, jsonObject, handler);
            hr.start();

        } catch (JSONException je) {
            Log.i(TAG, "sendAcknowledgement on catch block"

            );
            je.printStackTrace();
        }
    }

    public void sendUserRSVPCount(Activity context, String authenticationKey, String hK_uuid, String activityID, String actionType, String countRSVP, Handler handler) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.putOpt("authenticationKey", authenticationKey);
            jsonObject.putOpt("hK_UUID", hK_uuid);
            jsonObject.putOpt("accept", actionType);
            jsonObject.putOpt("activityId", activityID);
            jsonObject.putOpt("countRSVP", countRSVP);

            Log.i(TAG, "json objectCreateUpdateActivity to Server = " + jsonObject.toString());
            HttpReq hr = new HttpReq(context, WebURLs.REST_ACTION_USERS_RSVP, jsonObject, handler);
            hr.start();

        } catch (JSONException je) {
            je.printStackTrace();
        }
    }
}