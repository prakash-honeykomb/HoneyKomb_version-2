package com.honeykomb.honeykomb.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.honeykomb.honeykomb.R;
import com.honeykomb.honeykomb.database.DataBaseHelper;
import com.honeykomb.honeykomb.server_manager.ConnectServer;
import com.honeykomb.honeykomb.utils.Constants;
import com.honeykomb.honeykomb.utils.GIFView;
import com.honeykomb.honeykomb.utils.Util;
import com.honeykomb.honeykomb.utils.UtilityHelper;

import org.json.JSONException;
import org.json.JSONObject;


public class SignUpName extends BaseActivity {
    private static final String TAG = "SignUpName";
    public static String userName;
    public TextView tv_displayname;
    public EditText et_displayName;
    private String phoneNumber;
    private Bundle bundle;
    private LinearLayout animation_LL;
    private ScrollView scrollView;
    public Button bt_continue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDB(this);
        setContentView(R.layout.fragment_signupname);
        animation_LL = findViewById(R.id.animation_LL);
        scrollView = findViewById(R.id.scrollView);
        bundle = getIntent().getExtras();

        SharedPreferences prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        if (prefs.getBoolean(Constants.LAUNCH_APP_FOR_FIRST_TIME, true)) {
            firstTimeLoginScreen();
        }
        et_displayName = (EditText) findViewById(R.id.et_displayname);
        et_displayName.setImeOptions(EditorInfo.IME_ACTION_DONE);
        if (bundle != null) {
            if (bundle.getString("displayName") != null) {
                tv_displayname = (TextView) findViewById(R.id.tv_displayname);
                tv_displayname.setText("Welcome  to HoneyKomb !!! ");

                et_displayName.setText(bundle.getString("displayName"));
                et_displayName.setEnabled(false);

                userName = et_displayName.getText().toString();
                if (userName.trim().length() > 0) {
                    SignUpName.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                }
                SharedPreferences sp = getSharedPreferences(Constants.APP_PREFERENCE_NAME, Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("userName", userName);
                editor.apply();

                if (!bundle.getString("phoneNumber").equalsIgnoreCase("") || bundle.getString("phoneNumber") != null) {
                    phoneNumber = bundle.getString("phoneNumber");
                }

            }
            if (!bundle.getString("phoneNumber").equalsIgnoreCase("") || bundle.getString("phoneNumber") != null) {
                phoneNumber = bundle.getString("phoneNumber");
            }
        } else {
            et_displayName.setEnabled(true);
        }

        bt_continue = (Button) findViewById(R.id.btn_continue);
        bt_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hide_keyboard_from(SignUpName.this, et_displayName);
                bt_continue.setClickable(false);
                if (et_displayName.getText().toString().trim().length() > 2 && et_displayName.getText().toString().trim().length() < 30) {

                    animation_LL.setVisibility(View.VISIBLE);
                    scrollView.setVisibility(View.GONE);
                    GIFView gifView = findViewById(R.id.viewGIF);
                    gifView.setImageResource(R.mipmap.animation);
                    int width = ViewGroup.LayoutParams.MATCH_PARENT;
                    int height = ViewGroup.LayoutParams.MATCH_PARENT;
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
                    gifView.setLayoutParams(params);

                    userName = et_displayName.getText().toString().trim();
                    phoneNumber = bundle.getString("phoneNumber");

                    SharedPreferences.Editor editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
                    editor.putString("phoneNumber", phoneNumber);
                    editor.apply();

                    ConnectServer.getInstance().CreateUser(SignUpName.this, userName, phoneNumber, handler);
                } else {
                    Toast.makeText(SignUpName.this, "Please enter your display name,minimum 3 characters and maximum 30, to register.", Toast.LENGTH_SHORT).show();
                    bt_continue.setClickable(true);
                    UtilityHelper.setStringPreferences(getApplicationContext(), "appLaunchedFirstTime", true);
                    animation_LL.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 0 && msg.arg1 == 0) {
                JSONObject jObj = (JSONObject) msg.obj;
                Log.i(TAG, "create user handler responce = " + jObj.toString());
                if (jObj.has("messageCode")) {
                    try {
                        SharedPreferences sp = getSharedPreferences(Constants.APP_PREFERENCE_NAME, Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putBoolean(Constants.LAUNCH_APP_FOR_FIRST_TIME, false);
                        editor.apply();
                        String hK_UUID = " ", hK_ID = " ", displayName = " ", authenticationKey = " ", statusMessage = " ", PhoneNumber = " ";
                        if (jObj.has("hK_UUID")) {
                            hK_UUID = jObj.getString("hK_UUID");
                        }
                        if (jObj.has("hK_ID")) {
                            hK_ID = jObj.getString("hK_ID");
                        }
                        if (jObj.has("displayName")) {
                            displayName = jObj.getString("displayName");
                        }
                        if (jObj.has("authenticationKey")) {
                            authenticationKey = jObj.getString("authenticationKey");
                        }
                        if (jObj.has("message")) {
                            statusMessage = jObj.getString("message");
                        } else {
                            statusMessage = " ";
                        }
                        if (jObj.has("phone")) {
                            PhoneNumber = jObj.getString("phone");
                        } else {
                            PhoneNumber = " ";
                        }

                        editor.putString("authenticationKey", jObj.getString("authenticationKey"));
                        editor.putString("hK_UUID", jObj.getString("hK_UUID"));
                        editor.apply();

                        Util._db.createUser(hK_UUID, displayName, statusMessage,
                                " ", " ", hK_ID, " ", PhoneNumber);

                        ConnectServer.getInstance().sendAcknowledgement(getApplicationContext(), authenticationKey, hK_UUID, Constants.Service_Name_Verify_User, null, handlerAck, "welcomeActivity");
                        Log.i(TAG, "From Handler2");
                        Intent intent = new Intent(SignUpName.this, HKAnimationNew.class);
                        bundle.putString("displayName", jObj.getString("displayName"));
                        bundle.putString("phoneNumber", jObj.getString("phone"));
                        intent.putExtras(bundle);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        overridePendingTransition(0, 0);
                        startActivity(intent);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Log.i(TAG, "Login Failed");
                Toast.makeText(SignUpName.this, "Login Failed welcome line 225", Toast.LENGTH_SHORT).show();
                UtilityHelper.setStringPreferences(getApplicationContext(), "appLaunchedFirstTime", true);
            }
            return true;
        }
    });


    void firstTimeLoginScreen() {
        UtilityHelper.setStringPreferences(getApplicationContext(), "appLaunchedFirstTime", false);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        UtilityHelper.setStringPreferences(getApplicationContext(), "appLaunchedFirstTime", true);
    }


    private Handler handlerAck = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 0 && msg.arg1 == 0) {
                JSONObject jObj = (JSONObject) msg.obj;
                if (jObj.has("messageCode")) {
                    if (jObj.optInt("messageCode") == Constants.HANDLER_MESSAGE_CODE_OK) {

                    } else {
                        Log.i(TAG, "ACK Error in Verify");
                    }
                }
            } else {
                Log.i(TAG, "Login Failed");
                Toast.makeText(SignUpName.this, "Login Failed HandlerAck", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    });

    public static void hide_keyboard_from(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void initDB(Context ctx) {
        try {
            if (Util._db == null) {
                Util._db = new DataBaseHelper(ctx);
                Util._db.open();
            } else if (!Util._db.isOpen()) {
                Util._db.open();
            }
            Util.BackupDatabase();
        } catch (Exception e) {
            Log.e(TAG, "Exception = " + e);
        }
    }


    @Override
    public void setContentLayout(int layout) {

    }

    @Override
    public void doRequest(String url) {

    }

    @Override
    public void parseJsonResponse(String response, String requestType) {

    }

    @Override
    public String getValues() {
        return null;
    }
}
