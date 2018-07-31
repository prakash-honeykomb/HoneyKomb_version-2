package com.honeykomb.honeykomb.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.honeykomb.honeykomb.R;
import com.honeykomb.honeykomb.async.NewUserRegisteredAsync;
import com.honeykomb.honeykomb.dao.ActivityOwnerDetails;
import com.honeykomb.honeykomb.dao.HKUsers;
import com.honeykomb.honeykomb.listeners.AppListeners;
import com.honeykomb.honeykomb.network.HttpPostDataNew;
import com.honeykomb.honeykomb.network.WebURLs;
import com.honeykomb.honeykomb.utils.Constants;
import com.honeykomb.honeykomb.utils.GIFView;
import com.honeykomb.honeykomb.utils.Util;
import com.honeykomb.honeykomb.utils.UtilityHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


public class HKAnimationNew extends BaseActivity implements AppListeners.SingleDialogListener {
    private static final String TAG = HKAnimationNew.class.getSimpleName();
    private AppListeners.SingleDialogListener mCallback;
    private String authenticationKey;
    private String hK_UUID;
    private boolean newUser = false;
    private String serviceName;
    private JSONArray jsonArrayForACK;

    @Override
    protected void
    onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hk_animation);
        initDB(this);
        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if (bd != null && bd.get("createUser") != null)
            newUser = true;
        try {
            mCallback = HKAnimationNew.this;
        } catch (ClassCastException e) {
            throw new ClassCastException(HKAnimationNew.this.toString() + " must implement AppListener.SingleDialogListener");
        }

        GIFView gifView = findViewById(R.id.viewGIF);
        gifView.setImageResource(R.mipmap.animation);

        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        gifView.setLayoutParams(params);
        if (!UtilityHelper.haveNetworkConnection(HKAnimationNew.this)) {
            UtilityHelper.showDialogTwoButton(HKAnimationNew.this, HKAnimationNew.this.getString(R.string.error),
                    HKAnimationNew.this.getString(R.string.common_net_error), HKAnimationNew.this.getString(R.string.settings),
                    HKAnimationNew.this.getString(R.string.try_again), UtilityHelper.ButtonNavigation.SETTINGS, UtilityHelper.ButtonNavigation.TRY_AGAIN, mCallback);
        } else {

            SharedPreferences sp = getSharedPreferences(Constants.APP_PREFERENCE_NAME, Activity.MODE_PRIVATE);
            authenticationKey = sp.getString("authenticationKey", "");
            hK_UUID = sp.getString("hK_UUID", "");
            Log.i(TAG, "authenticationKey VALUE " + authenticationKey);
            Log.i(TAG, "hK_UUID VALUE " + hK_UUID);

            getUserContacts();
        }
    }

    private void proceedToTheNextActivity() {
        Intent intent = new Intent(HKAnimationNew.this, DemoActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public void setContentLayout(int layout) {

    }

    @Override
    public void onSingleButton(UtilityHelper.ButtonNavigation value) {
        Log.i(TAG, "onSingleButton ============================" + value);
        switch (value) {
            case SAME_SCREEN:
                // retains the user in same screen.
                break;
            case LOGIN_SCREEN:
                // takes the user out of the application.
                Intent intent = new Intent(HKAnimationNew.this, SplashScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                this.finish();
                break;
            case EXIT_APP:
                // Exits the application
                this.finish();
                break;
            case HOME_SCREEN:
                // Takes the user in home screen.
                break;
            case TRY_AGAIN:
                if (!UtilityHelper.haveNetworkConnection(HKAnimationNew.this)) {
                    UtilityHelper.showDialogTwoButton(HKAnimationNew.this, HKAnimationNew.this.getString(R.string.error),
                            HKAnimationNew.this.getString(R.string.common_net_error), HKAnimationNew.this.getString(R.string.settings),
                            HKAnimationNew.this.getString(R.string.try_again), UtilityHelper.ButtonNavigation.SETTINGS, UtilityHelper.ButtonNavigation.TRY_AGAIN, mCallback);
                } else {
                    getUserContacts();
                }
                break;
            case SETTINGS:
                startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                break;

        }
    }

    public void getUserContacts() {
        doRequest(WebURLs.REST_ACTION_CONTACT_LIST);
    }

    private void getUserActivities() {
        doRequest(WebURLs.REST_ACTION_USER_ACTIVITY);
    }

    private void getUserNotifications() {
        doRequest(WebURLs.REST_ACTION_USER_NOTIFICATIONS);
    }

    @Override
    public void doRequest(String url) {
        if (url.equalsIgnoreCase(WebURLs.REST_ACTION_CONTACT_LIST)) {
            HttpPostDataNew httpPostData = new HttpPostDataNew("Please wait...", url, getContactDetails(), this);
            httpPostData.execute();
        }
        if (url.equalsIgnoreCase(WebURLs.REST_ACTION_ACKNOWLEDGE_SERVER) && serviceName.equalsIgnoreCase(Constants.Service_Name_Contact_List)) {
            HttpPostDataNew httpPostData = new HttpPostDataNew("Please wait...", url, getAcknowledgeDetails(Constants.Service_Name_Contact_List), this);
            httpPostData.execute();
        }
        if (url.equalsIgnoreCase(WebURLs.REST_ACTION_ACKNOWLEDGE_SERVER) && serviceName.equalsIgnoreCase(Constants.Service_Name_GET_ALL_Activity)) {
            HttpPostDataNew httpPostData = new HttpPostDataNew("Please wait...", url, getActivityAcknowledgeDetails(Constants.Service_Name_GET_ALL_Activity), this);
            httpPostData.execute();
        }
        if (url.equalsIgnoreCase(WebURLs.REST_ACTION_ACKNOWLEDGE_SERVER) && serviceName.equalsIgnoreCase(Constants.Service_Name_Notifications)) {
            HttpPostDataNew httpPostData = new HttpPostDataNew("Please wait...", url, getAcknowledgeDetails(Constants.Service_Name_Notifications), this);
            httpPostData.execute();
        }
        if (url.equalsIgnoreCase(WebURLs.REST_ACTION_USER_ACTIVITY)) {
            HttpPostDataNew httpPostData = new HttpPostDataNew("Please wait...", url, getUserActivityDetails(), this);
            httpPostData.execute();
        }
        if (url.equalsIgnoreCase(WebURLs.REST_ACTION_USER_NOTIFICATIONS)) {
            HttpPostDataNew httpPostData = new HttpPostDataNew("Please wait...", url, getUserNotificationDetails(), this);
            httpPostData.execute();
        }
    }

    private String getActivityAcknowledgeDetails(String serviceName) {
        JSONObject jsonObject = new JSONObject();
        try {
            Log.i(TAG, "error on : -->> " + TAG);
            jsonObject.putOpt("authenticationKey", UtilityHelper.getStringPreferences(HKAnimationNew.this, Constants.AUTH_KEY));
            jsonObject.putOpt("hK_UUID", UtilityHelper.getStringPreferences(HKAnimationNew.this, Constants.HK_UUID));
            jsonObject.putOpt("serviceName", serviceName);
            if (jsonArrayForACK != null && jsonArrayForACK.length() > 0) {
                jsonObject.putOpt("activityList", jsonArrayForACK);
                Log.v(TAG, "activityList->> " + jsonArrayForACK);
            }
        } catch (JSONException je) {
            Log.i(TAG, "getAcknowledgeDetails on catch block");
            je.printStackTrace();
            return jsonObject.toString();
        }
        return jsonObject.toString();
    }

    private String getUserNotificationDetails() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.putOpt("authenticationKey", authenticationKey);
            jsonObject.putOpt("hK_UUID", hK_UUID);

        } catch (JSONException je) {
            Log.i(TAG, "getUserNotificationDetails JSONException = " + je.getMessage());
            return jsonObject.toString();
        }
        return jsonObject.toString();
    }

    private String getUserActivityDetails() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.putOpt("authenticationKey", authenticationKey);
            jsonObject.putOpt("hK_UUID", hK_UUID);
            jsonObject.putOpt("login", "yes");

        } catch (JSONException je) {
            je.printStackTrace();
            Log.i(TAG, "getUserActivityDetails JSONException = " + je.getMessage());
            return jsonObject.toString();
        }
        return jsonObject.toString();
    }

    private String getContactDetails() {
        JSONObject jsonObject = new JSONObject();
        try {

            ArrayList<String> phone = new ArrayList<>();// TODO sending empty array, need to send all contacts.
            jsonObject.putOpt("authenticationKey", authenticationKey);
            jsonObject.putOpt("hK_UUID", hK_UUID);
            jsonObject.putOpt("phoneIds", phone);

        } catch (JSONException je) {
            je.printStackTrace();
            return jsonObject.toString();
        }
        return jsonObject.toString();
    }

    private String getAcknowledgeDetails(String serviceName) {
        JSONObject jsonObject = new JSONObject();
        try {
            Log.i(TAG, "error on : -->> " + TAG);
            jsonObject.putOpt("authenticationKey", UtilityHelper.getStringPreferences(HKAnimationNew.this, Constants.AUTH_KEY));
            jsonObject.putOpt("hK_UUID", UtilityHelper.getStringPreferences(HKAnimationNew.this, Constants.HK_UUID));
            jsonObject.putOpt("serviceName", serviceName);
        } catch (JSONException je) {
            Log.i(TAG, "getAcknowledgeDetails on catch block");
            je.printStackTrace();
            return jsonObject.toString();
        }
        return jsonObject.toString();
    }

    @Override
    public void parseJsonResponse(String response, String requestType) {

        if (requestType.equalsIgnoreCase(WebURLs.REST_ACTION_CONTACT_LIST)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.has("messageCode")) {
                    if (jsonObject.optInt("messageCode") == Constants.HANDLER_MESSAGE_CODE_OK) {

                        Log.i(TAG, "USERCONTACRESPONSE : " + jsonObject.toString());
                        Util._db.createUserContacts(jsonObject);
                        JSONArray jsonArray = jsonObject.optJSONArray("contactList");
                        if (newUser && jsonArray != null) {

                            ArrayList<String> hkUUID_Array = new ArrayList<>();
                            JSONArray hkUUID_ArrayJA = new JSONArray();
                            ContentResolver cr = getContentResolver();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                                if (jsonObject1 != null) {

                                    String HK_UUID = jsonObject1.optString("hK_UUID", "");
                                    String phoneNumber = jsonObject1.optString("phone", "");

                                    if (phoneNumber.length() > 0 && HK_UUID.length() > 0) {
                                        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
                                        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
                                        if (cursor != null) {
                                            cursor.moveToFirst();
                                            if (cursor.getCount() > 0)
                                                hkUUID_Array.add(HK_UUID);
                                            cursor.close();
                                        }
                                    }
                                }

                            }
                            // add elements to al, including duplicates
                            Set<String> hs = new HashSet<>(hkUUID_Array);
                            hkUUID_Array.clear();
                            hkUUID_Array.addAll(hs);
                            hkUUID_Array.remove(hK_UUID);

                            if (hkUUID_Array.size() > 0) {
                                for (int i = 0; i < hkUUID_Array.size(); i++) {
                                    hkUUID_ArrayJA.put(hkUUID_Array.get(i));
                                }
                            }
                            new NewUserRegisteredAsync(getApplicationContext(), hkUUID_ArrayJA, authenticationKey, hK_UUID).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            newUser = false;
                        }
                        Log.i(TAG, "before sendAcknowledgement hkanimation line 456"

                        );
                        serviceName = "";
                        serviceName = Constants.Service_Name_Contact_List;
                        doRequest(WebURLs.REST_ACTION_ACKNOWLEDGE_SERVER);
                    } else {
                        Log.i(TAG, "GetContacts Else");
                    }
                } else {
                    Log.i(TAG, "USER Contacts Failed ");
                    Toast.makeText(HKAnimationNew.this, "USER Contacts Failed ", Toast.LENGTH_SHORT).show();
                    UtilityHelper.setStringPreferences(getApplicationContext(), "appLaunchedFirstTime", true);
                    this.finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.i(TAG, "parseJsonResponse JSONException = " + e.getMessage());
                Toast.makeText(HKAnimationNew.this, "USER Contacts Failed ", Toast.LENGTH_SHORT).show();
                UtilityHelper.setStringPreferences(getApplicationContext(), "appLaunchedFirstTime", true);
            }
        }
        if (requestType.equalsIgnoreCase(WebURLs.REST_ACTION_USER_ACTIVITY)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.has("messageCode")) {
                    if (jsonObject.optInt("messageCode") == Constants.HANDLER_MESSAGE_CODE_OK) {
                        Log.i(TAG, "JSONRESPONSE for ACTIVITY = " + jsonObject.toString());
                        try {
                            jsonArrayForACK = new JSONArray();
                            JSONArray jsonArray = jsonObject.getJSONArray("userActivities");
                            Log.i(TAG, "jsonArray.length() = " + jsonArray.length());

                            for (int i = 0; i < jsonArray.length(); i++) {

                                Bundle bundle = new Bundle();

                                JSONObject object = jsonArray.getJSONObject(i);

                                JSONObject jsonObjectForACK = new JSONObject();
                                jsonObjectForACK.put("activityId", object.getString("activityId"));
                                jsonObjectForACK.put("activityOwner", object.getString("activityOwner"));
                                jsonArrayForACK.put(jsonObjectForACK);

                                Log.i(TAG, "object = " + object.toString());
                                bundle.putString("active", object.getString("active"));
                                bundle.putString("activityID", object.getString("activityId"));
                                bundle.putString("activityNotes", object.getString("activityNotes"));
                                bundle.putString("activityStatus", object.getString("activityStatus"));
                                bundle.putString("activityTitle", object.getString("activityTitle"));
                                bundle.putString("allowOtherToModify", object.getString("allowOtherToModify"));
                                bundle.putString("blockCalendar", object.getString("blockCalendar"));
                                bundle.putString("createdBy", object.getString("createdBy"));
                                bundle.putString("dateCreated", object.getString("dateCreated"));
                                bundle.putString("dateModified", object.getString("dateModified"));
                                bundle.putString("activityOwner", object.getString("activityOwner"));
                                bundle.putString("reminder", object.getString("reminder"));
                                bundle.putString("hK_UUID", object.getString("hK_UUID"));
                                bundle.putString("userActivityStatus", object.getString("activityStatus"));
                                bundle.putString("invitationStatus", object.getString("invitationStatus"));
                                bundle.putString("actionType", " ");

                                ArrayList<String> ownerDetails = Util._db.getOwnerDetails(object.getString("activityOwner"));
                                ActivityOwnerDetails activityOwnerDetails = new ActivityOwnerDetails();

                                activityOwnerDetails.sethK_UUID(object.getString("activityOwner"));
                                activityOwnerDetails.sethKID(ownerDetails.get(0));
                                activityOwnerDetails.setPhoto(object.getString("imagePath"));
                                activityOwnerDetails.setDisplayName(ownerDetails.get(2));
                                activityOwnerDetails.setQuickBloxID(ownerDetails.get(3));
                                activityOwnerDetails.setPhone(ownerDetails.get(4));
                                activityOwnerDetails.setActive(object.getString("active"));

                                bundle.putParcelable("activityOwnerDetails", activityOwnerDetails);

                                bundle.putString("imagePath", object.getString("imagePath"));
                                bundle.putString("location", object.getString("location"));
                                bundle.putString("address", object.getString("address"));

                                JSONArray hkUsersArray = object.getJSONArray("hkUsers");
                                if (hkUsersArray.length() != 0) {
                                    Log.i(TAG, "hkUsersArray size = " + hkUsersArray.length());
                                    ArrayList<HKUsers> hkUsersList = new ArrayList<>();
                                    for (int j = 0; j < hkUsersArray.length(); j++) {

                                        JSONObject hkUserObject = hkUsersArray.getJSONObject(j);
                                        Log.i(TAG, "hkUserObject  = " + hkUserObject.toString());
                                        HKUsers hkUser = new HKUsers();
                                        if (hkUserObject.has("phoneNumber")) {
                                            hkUser.setPhoneNumber(hkUserObject.getString("phoneNumber"));
                                        } else {
                                            hkUser.setPhoneNumber(" ");
                                        }
                                        hkUser.setInvitationStatus(hkUserObject.getString("invitationStatus"));
                                        if (hkUserObject.has("userActivityStatus")) {
                                            hkUser.setUserActivityStatus(hkUserObject.getString("userActivityStatus"));
                                        }
                                        hkUser.sethK_UUID(hkUserObject.getString("hK_UUID"));

                                        if (hkUserObject.getString("hK_UUID").equalsIgnoreCase(hK_UUID))
                                            bundle.putString("userActivityStatus", hkUserObject.getString("userActivityStatus"));

                                        hkUser.setCountRSVP(hkUserObject.getString("countRSVP"));
                                        if (hkUserObject.has("quickBloxID")) {
                                            hkUser.setQuickBloxID(hkUserObject.getString("quickBloxID"));
                                        } else {
                                            hkUser.setQuickBloxID(" ");
                                        }
                                        hkUser.setDeliveredTime(hkUserObject.getString("deliveredTime"));

                                        hkUsersList.add(hkUser);
                                    }
                                    bundle.putParcelableArrayList("hkUsersList", hkUsersList);
                                }

                                JSONArray phoneIdsArray = object.getJSONArray("phoneIds");
                                if (phoneIdsArray.length() != 0) {
                                    Log.i(TAG, "phoneIdsArray size = " + phoneIdsArray.length());
                                }

                                bundle.putString("QuickbloxGroupID", object.getString("quickbloxGroupID"));
                                bundle.putString("QuickbloxRoomJID", "0");
                                if (object.has("activityRSVP")) {
                                    bundle.putString("activityRSVP", object.getString("activityRSVP"));
                                }
                                if (object.has("countRSVP")) {
                                    bundle.putString("countRSVP", object.getString("countRSVP"));
                                }
                                bundle.putString("lastAccessUpdate", object.getString("lastAccessUpdate"));
                                bundle.putString("activityDetailID", " ");
                                bundle.putString("columnName", " ");
                                bundle.putString("columnValue", " ");
                                Util._db.getInviteesCount(object.getString("activityId"));
                                int cout = Util._db.getInviteesCount(object.getString("activityId"));
                                Log.i(TAG, "invitees count: " + Util._db.getInviteesCount(object.getString("activityId")));
                                Util._db.createActivity(bundle, cout);
                                Util._db.createActivityUsers(bundle);
                                Util._db.createActivityDetails(bundle);

                                // added by raj for creating activity dates at sign up.

                                HashMap<String, String> activityDates = new HashMap<>();
                                activityDates.put("activityID", object.getString("activityId"));
                                activityDates.put("actionType", "");
                                activityDates.put("createdBy", object.getString("createdBy"));
                                activityDates.put("dateModified", object.getString("dateModified"));
                                activityDates.put("modifiedBy", "");
                                activityDates.put("ActivityDateID", UUID.randomUUID().toString());
                                JSONArray activityDatesArray = object.getJSONArray("activityDates");
                                if (activityDatesArray.length() != 0) {
                                    for (int j = 0; j < activityDatesArray.length(); j++) {

                                        JSONObject activityDatesObject = activityDatesArray.getJSONObject(j);
                                        String aString = activityDatesObject.getString("startDate");
                                        String cutStartDate = aString.substring(0, 10);

                                        String aString1 = activityDatesObject.getString("endDate");
                                        String cutEndDate = aString1.substring(0, 10);
//
                                        String startDate = UtilityHelper.convertToDateTime(cutStartDate, activityDatesObject.getString("startTime"));
                                        String endDate = UtilityHelper.convertToDateTime(cutEndDate, activityDatesObject.getString("endTime"));
                                        activityDates.put("startDate", startDate);
                                        activityDates.put("endDate", endDate);

                                        Util._db.createActivityDates(activityDates);
                                    }
                                }
                            }
                            if (jsonArray.length() > 0) {
                                serviceName = "";
                                serviceName = Constants.Service_Name_GET_ALL_Activity;
                                doRequest(WebURLs.REST_ACTION_ACKNOWLEDGE_SERVER);
                            } else {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            UtilityHelper.setStringPreferences(getApplicationContext(), "appLaunchedFirstTime", true);
                        }
                    }
                } else {
                    Log.i(TAG, "msg = " + response);
                    UtilityHelper.setStringPreferences(getApplicationContext(), "appLaunchedFirstTime", true);
                    this.finish();
                }
            } catch (JSONException e) {
                Log.i(TAG, "URL - REST_ACTION_USER_ACTIVITY JSONException = " + e.getMessage());
            }
        }
        if (requestType.equalsIgnoreCase(WebURLs.REST_ACTION_ACKNOWLEDGE_SERVER) && serviceName.equalsIgnoreCase(Constants.Service_Name_Contact_List)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.has("messageCode")) {
                    if (jsonObject.optInt("messageCode") == Constants.HANDLER_MESSAGE_CODE_OK) {
                        Log.i(TAG, "JSONRESPONSE from Contact ACK = " + jsonObject.toString());
                        getUserActivities();
                    } else {
                        Log.i(TAG, "Login Failed 1 ");
                        UtilityHelper.setStringPreferences(getApplicationContext(), "appLaunchedFirstTime", true);
                        Toast.makeText(HKAnimationNew.this, "Login Failed Service_Name_Contact_List", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.i(TAG, "Login Failed 2 ");
                    UtilityHelper.setStringPreferences(getApplicationContext(), "appLaunchedFirstTime", true);
                    Toast.makeText(HKAnimationNew.this, "Login Failed Service_Name_Contact_List", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Log.i(TAG, "URL - REST_ACTION_ACKNOWLEDGE_SERVER + 1 = JSONException = " + e.getMessage());
                UtilityHelper.setStringPreferences(getApplicationContext(), "appLaunchedFirstTime", true);
                Toast.makeText(HKAnimationNew.this, "Login Failed HKanimation line 65", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestType.equalsIgnoreCase(WebURLs.REST_ACTION_ACKNOWLEDGE_SERVER) && serviceName.equalsIgnoreCase(Constants.Service_Name_GET_ALL_Activity)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.has("messageCode")) {
                    if (jsonObject.optInt("messageCode") == Constants.HANDLER_MESSAGE_CODE_OK) {
                        Log.i(TAG, "JSONRESPONSE from Activity ACK = " + jsonObject.toString());
//                        getUserNotifications();
                    } else {
                        Log.i(TAG, "Login Failed activity 1 ");
//                        UtilityHelper.setStringPreferences(getApplicationContext(), "appLaunchedFirstTime", true);
//                        Toast.makeText(HKAnimationNew.this, "Login Failed Service_Name_GET_ALL_Activity", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.i(TAG, "Login Failed activity 2 ");
//                    UtilityHelper.setStringPreferences(getApplicationContext(), "appLaunchedFirstTime", true);
//                    Toast.makeText(HKAnimationNew.this, "Login Failed Service_Name_GET_ALL_Activity", Toast.LENGTH_SHORT).show();
                }
                UtilityHelper.setStringPreferences(getApplicationContext(), "appLaunchedFirstTime", false);
                getUserNotifications();
            } catch (JSONException e) {
                Log.i(TAG, "URL - REST_ACTION_ACKNOWLEDGE_SERVER + 1 = JSONException = " + e.getMessage());
//                UtilityHelper.setStringPreferences(getApplicationContext(), "appLaunchedFirstTime", true);
//                Toast.makeText(HKAnimationNew.this, "Login Failed HKanimation line 65", Toast.LENGTH_SHORT).show();
                UtilityHelper.setStringPreferences(getApplicationContext(), "appLaunchedFirstTime", false);
                getUserNotifications();
            }
        }
        if (requestType.equalsIgnoreCase(WebURLs.REST_ACTION_USER_NOTIFICATIONS)) {
            try {
                JSONObject obj = new JSONObject(response);
                if (obj.has("messageCode")) {
                    if (obj.optInt("messageCode") == Constants.HANDLER_MESSAGE_CODE_OK) {
                        Log.i(TAG, "JSONRESPONSE for Notifications = " + obj.toString());
                        try {
                            int pendingInvitationCount = obj.optInt("pendingInvCount");
                            Log.i(TAG, "pendingInvitationCount = " + pendingInvitationCount);
                            JSONArray jsonArray = obj.getJSONArray("notificationsList");
                            if (jsonArray.length() > 0) {
                                Bundle bundel = new Bundle();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    bundel.putString("notificationId", jsonObject.getString("notificationId"));
                                    bundel.putString("notificaitonText", jsonObject.getString("notificaitonText"));
                                    bundel.putString("createdBy", jsonObject.getString("createdBy"));
                                    bundel.putString("actionType", jsonObject.getString("actionType"));
                                    bundel.putString("objectType", jsonObject.getString("objectType"));
                                    bundel.putString("objectId", jsonObject.getString("objectId"));
                                    bundel.putString("dateCreated", jsonObject.getString("dateCreated"));
                                    bundel.putString("pendingInvCount", String.valueOf(jsonObject.getInt("pendingInvCount")));
                                    bundel.putString("lastUpdated", jsonObject.getString("lastUpdated"));

                                    Util._db.createInvitation(bundel);
                                }
                                Log.i(TAG, "pendingInvitationCount   parameters = " + " pendingInvitationCount= " + pendingInvitationCount
                                        + " authenticationKey =" + authenticationKey
                                        + " hK_UUID =" + hK_UUID
                                        + " Constants.Service_Name_Notifications =" + Constants.Service_Name_Notifications);
                                serviceName = "";
                                serviceName = Constants.Service_Name_Notifications;
                                doRequest(WebURLs.REST_ACTION_ACKNOWLEDGE_SERVER);
                                Log.i(TAG, "pendingInvitationCount    after sendAcknowledgement on Hkanimation line 720");
                            }else{
                                proceedToTheNextActivity();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            UtilityHelper.setStringPreferences(getApplicationContext(), "appLaunchedFirstTime", true);
                            this.finish();
                        }
                    }
                } else {
                    Log.i(TAG, "msg = " + response);
                    UtilityHelper.setStringPreferences(getApplicationContext(), "appLaunchedFirstTime", true);
                    Toast.makeText(HKAnimationNew.this, "Please check your network connection, to help setting up the application.", Toast.LENGTH_LONG).show();
                    this.finish();
                }
            } catch (JSONException e) {
                Log.i(TAG, "URL = REST_ACTION_USER_NOTIFICATIONS -- JSONException" + e.getMessage());
                UtilityHelper.setStringPreferences(getApplicationContext(), "appLaunchedFirstTime", true);
                Toast.makeText(HKAnimationNew.this, "Please check your network connection, to help setting up the application.", Toast.LENGTH_LONG).show();
                this.finish();
            }
        }
        if (requestType.equalsIgnoreCase(WebURLs.REST_ACTION_ACKNOWLEDGE_SERVER) && serviceName.equalsIgnoreCase(Constants.Service_Name_Notifications)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.has("messageCode")) {
                    if (jsonObject.optInt("messageCode") == Constants.HANDLER_MESSAGE_CODE_OK) {
                        Log.i(TAG, "JSONRESPONSE from Contact ACK = " + jsonObject.toString());
                        proceedToTheNextActivity();
                        this.finish();
                    } else {
                        Log.i(TAG, "Login Failed 1 ");
                        UtilityHelper.setStringPreferences(getApplicationContext(), "appLaunchedFirstTime", true);
                        Toast.makeText(HKAnimationNew.this, "Login Failed Service_Name_Notifications", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.i(TAG, "Login Failed 2 ");
                    UtilityHelper.setStringPreferences(getApplicationContext(), "appLaunchedFirstTime", true);
                    Toast.makeText(HKAnimationNew.this, "Login Failed Service_Name_Notifications", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Log.i(TAG, "URL - REST_ACTION_ACKNOWLEDGE_SERVER + 1 = JSONException = " + e.getMessage());
                UtilityHelper.setStringPreferences(getApplicationContext(), "appLaunchedFirstTime", true);
                Toast.makeText(HKAnimationNew.this, "Login Failed Service_Name_Notifications", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public String getValues() {
        return null;
    }

}