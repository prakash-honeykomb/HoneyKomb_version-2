package com.honeykomb.honeykomb.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.honeykomb.honeykomb.R;
import com.honeykomb.honeykomb.network.HttpPostData;
import com.honeykomb.honeykomb.network.WebURLs;
import com.honeykomb.honeykomb.utils.Constants;
import com.honeykomb.honeykomb.utils.Util;
import com.honeykomb.honeykomb.utils.UtilityHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;

public class LogInActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = LogInActivity.class.getSimpleName();
    private ProgressDialog progressDialog = null;
    private String displayName = "";
    private String localPhoneNumber;
    private Button button;
    private String URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in_activity);
        initDB(this);
        button = findViewById(R.id.auth_button);
        button.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && data.getExtras() != null) {
            Bundle bundle1 = data.getExtras();
            progressDialog = new ProgressDialog(LogInActivity.this);
            progressDialog.setMessage("loading please wait...!!");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
            Log.i(TAG, "requestCode = " + requestCode + " ----resultCode = " + resultCode + " ----data = " + data.getExtras());
            Log.i(TAG, "bundle1 = " + bundle1.get("extra_idp_response"));
            IdpResponse idpResponse = (IdpResponse) bundle1.get("extra_idp_response");
            if (idpResponse != null) {
                localPhoneNumber = idpResponse.getPhoneNumber();
                SharedPreferences.Editor editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
                editor.putString(Constants.SHARED_PREFF_DIGITS_SESSION_ID, " ");
                editor.putString(Constants.SHARED_PREFF_PHONE_NUMBER, localPhoneNumber);
                editor.apply();
                URL = WebURLs.REST_ACTION_VERIFY_USER;
                doRequest(WebURLs.REST_ACTION_VERIFY_USER);
            } else {
                progressDialog.dismiss();
                button.setOnClickListener(this);
                Toast.makeText(LogInActivity.this, "Please try again later..!", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            button.setOnClickListener(this);
//            Toast.makeText(LogInActivity.this, "Please try again later..!", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    @Override
    public void onClick(View v) {
        if (v == button) {
            startActivityForResult(
                    AuthUI.getInstance().createSignInIntentBuilder().setTheme(R.style.FirebaseLoginTheme)
                            .setLogo(R.mipmap.default_icon).setProviders(Collections.singletonList(
                            new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build()
                    )).setIsSmartLockEnabled(false).build(), 0);
            button.setOnClickListener(null);
        }
    }

    @Override
    public void doRequest(String url) {
        if (url.equalsIgnoreCase(WebURLs.REST_ACTION_VERIFY_USER)) {
            HttpPostData httpPostData = new HttpPostData("Please wait...", url, getUserDetails(), this);
            httpPostData.execute();
        }
        if (url.equalsIgnoreCase(WebURLs.REST_ACTION_CREATE_USER)) {
            HttpPostData httpPostData = new HttpPostData("Please wait...", url, getCreateUserDetails(), this);
            httpPostData.execute();
        }
        if (url.equalsIgnoreCase(WebURLs.REST_ACTION_ACKNOWLEDGE_SERVER)) {
            HttpPostData httpPostData = new HttpPostData("Please wait...", url, getAcknowledgeDetails(), this);
            httpPostData.execute();
        }
    }

    private String getAcknowledgeDetails() {
        JSONObject jsonObject = new JSONObject();
        try {
            Log.i(TAG, "error on : -->> " + TAG);
            jsonObject.putOpt("authenticationKey", UtilityHelper.getStringPreferences(LogInActivity.this, Constants.AUTH_KEY));
            jsonObject.putOpt("hK_UUID", UtilityHelper.getStringPreferences(LogInActivity.this, Constants.HK_UUID));
            jsonObject.putOpt("serviceName", Constants.Service_Name_Verify_User);
        } catch (JSONException je) {
            Log.i(TAG, "getAcknowledgeDetails on catch block");
            je.printStackTrace();
            return jsonObject.toString();
        }
        return jsonObject.toString();
    }

    private String getCreateUserDetails() {
        JSONObject jObj = new JSONObject();
        Log.i(TAG, "CreateUser JOBJ VALUE = " + jObj.toString());
        try {
            jObj.putOpt("phone", localPhoneNumber);
            jObj.putOpt("photo", " ");
            jObj.putOpt("displayName", UtilityHelper.getStringPreferences(LogInActivity.this, Constants.DISPLAY_NAME));
            jObj.putOpt("digitsId", " ");
            jObj.putOpt("quickBloxID", " ");
            jObj.putOpt("deviceToken", UtilityHelper.getStringPreferences(LogInActivity.this, Constants.DEVICE_TOKEN));
            jObj.putOpt("oSType", Constants.OS_TYPE);
            jObj.putOpt("oSVersion", Constants.OS_VERSION);
            jObj.putOpt("deviceModel", Constants.DEVICE_MODEL);
        } catch (JSONException e) {
            e.printStackTrace();
            return jObj.toString();
        }
        Log.i(TAG, "JSON VALUE IN CREATE USER = " + jObj.toString());
        return jObj.toString();
    }

    @Override
    public void parseJsonResponse(String response, String requestType) {
        if (URL.equalsIgnoreCase(WebURLs.REST_ACTION_VERIFY_USER)) {
            try {
                JSONObject object = new JSONObject(response);
                Bundle bundle = new Bundle();
                if (object.has("messageCode")) {
                    SharedPreferences sp = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    // TODO put value in SharedPreferences as false for first time user login.
                    try {
                        if (object.has("authenticationKey"))
                            editor.putString(Constants.AUTH_KEY, object.getString("authenticationKey") == null ? "" : object.getString("authenticationKey"));
                        editor.apply();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // TODO new user
                    if (object.optInt("messageCode") == Constants.HANDLER_MESSAGE_CODE_USER_NOT_EXISTS) {
                        Intent intent = new Intent(LogInActivity.this, SignUpName.class);
                        bundle.putString("createUser", "createUser");
                        bundle.putString("phoneNumber", localPhoneNumber);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    } else {
                        // TODO existing user
                        Log.i(TAG, "EXISTING USER");
                        try {
                            displayName = object.getString("displayName");
                            bundle.putString("displayName", displayName);
                            Log.i(TAG, "WELCOMEUSER  = " + object.toString());
                            editor.putString(Constants.HK_UUID, object.getString("hK_UUID"));
                            editor.putString(Constants.DISPLAY_NAME, object.getString("displayName"));
                            editor.putString(Constants.HK_ID, object.getString("hK_ID"));
                            editor.putString(Constants.AUTH_KEY, object.getString("authenticationKey"));
                            editor.putString(Constants.SHARED_PREFF_PHONE_NUMBER, object.getString("phone"));
                            editor.putString(Constants.STATUS_MESSAGE, object.getString("message"));
                            String photoPath = " ";
                            if (object.has("photoPath")) {
                                photoPath = object.getString("photoPath");
                                editor.putString(Constants.PHOTO_PATH, photoPath);
                            }
                            editor.apply();

                            Util._db.createUser(object.getString("hK_UUID"), object.getString("displayName"), object.getString("message"),
                                    photoPath, " ", object.getString("hK_ID"), " ", object.getString("phone"));
                            URL = WebURLs.REST_ACTION_CREATE_USER;
                            doRequest(WebURLs.REST_ACTION_CREATE_USER);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                } else {
                    Log.i(TAG, "Login Failed WelcomeActivityNew line 176");
                    Toast.makeText(LogInActivity.this, "Message code not available \n" + object, Toast.LENGTH_SHORT).show();
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (URL.equalsIgnoreCase(WebURLs.REST_ACTION_CREATE_USER)) {
            try {
                JSONObject jObj = new JSONObject(response);
                Log.i(TAG, "create user handler responce = " + jObj.toString());
                if (jObj.has("messageCode")) {
                    SharedPreferences sp = getSharedPreferences("HONEY_PREFS", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean(Constants.LAUNCH_APP_FOR_FIRST_TIME, false);

                    editor.putString(Constants.HK_UUID, jObj.getString("hK_UUID"));
                    editor.putString(Constants.HK_ID, jObj.getString("hK_ID"));
                    editor.putString(Constants.DISPLAY_NAME, jObj.getString("displayName"));
                    editor.putString(Constants.AUTH_KEY, jObj.getString("authenticationKey"));
                    editor.putString(Constants.SHARED_PREFF_PHONE_NUMBER, jObj.getString("phone"));
                    editor.putString(Constants.STATUS_MESSAGE, jObj.getString("message"));
                    if (jObj.has("photoPath")) {
                        editor.putString(Constants.PHOTO_PATH, jObj.getString("photoPath"));
                    }
                    editor.apply();
                    URL = WebURLs.REST_ACTION_ACKNOWLEDGE_SERVER;
                    doRequest(WebURLs.REST_ACTION_ACKNOWLEDGE_SERVER);
                } else {
                    Log.i(TAG, "Login Failed");
                    Toast.makeText(LogInActivity.this, "Login Failed welcome line 207", Toast.LENGTH_SHORT).show();
                    UtilityHelper.setStringPreferences(getApplicationContext(), Constants.LAUNCH_APP_FOR_FIRST_TIME, true);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (URL.equalsIgnoreCase(WebURLs.REST_ACTION_ACKNOWLEDGE_SERVER)) {
            Bundle bundle = new Bundle();
            try {
                JSONObject jObj = new JSONObject(response);
                if (jObj.has("messageCode")) {
                    if (jObj.optInt("messageCode") == Constants.HANDLER_MESSAGE_CODE_OK) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Intent intent = new Intent(LogInActivity.this, HKAnimationNew.class);
                        bundle.putString("displayName", displayName);
                        bundle.putString("phoneNumber", localPhoneNumber);
                        intent.putExtras(bundle);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        overridePendingTransition(0, 0);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.i(TAG, "ACK Error in Verify");
                    }
                } else {
                    Log.i(TAG, "Login Failed");
                    Toast.makeText(LogInActivity.this, "Login Failed HandlerAck", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.i(TAG, "Login Failed");
                Toast.makeText(LogInActivity.this, "Login Failed HandlerAck", Toast.LENGTH_SHORT).show();
            }
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }

    @Override
    public String getValues() {
        return null;
    }

    @Override
    public void setContentLayout(int layout) {

    }

    public String getUserDetails() {

        JSONObject object = new JSONObject();
        try {

            String mobile = UtilityHelper.getUserPhoneNumber(LogInActivity.this);
            String deviceToken = UtilityHelper.getStringPreferences(LogInActivity.this, Constants.DEVICE_TOKEN).toString();
            Log.i(TAG, "PHONE_NUMBER = " + mobile + " && DEVICE_TOKEN  = " + deviceToken);
            object.putOpt("phone", mobile);
            object.putOpt("deviceModel", Constants.DEVICE_MODEL);
            if (deviceToken.equalsIgnoreCase("")) {
                object.putOpt("deviceToken", deviceToken);
            } else {
                object.putOpt("deviceToken", " ");
            }
            object.putOpt("oSType", Constants.OS_TYPE);
            object.putOpt("oSVersion", Constants.OS_VERSION);
            object.putOpt("photo", " ");
            object.putOpt("displayName", " ");
            object.putOpt("digitsId", 0L);
            object.putOpt("quickBloxID", 0L);

        } catch (JSONException e) {
            e.printStackTrace();
            return object.toString();
        }
        return object.toString();
    }
}