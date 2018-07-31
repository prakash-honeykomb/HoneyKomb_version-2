package com.honeykomb.honeykomb.async;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.honeykomb.honeykomb.R;
import com.honeykomb.honeykomb.activity.MainScreen;
import com.honeykomb.honeykomb.dao.ActivityOwnerDetails;
import com.honeykomb.honeykomb.dao.HKUsers;
import com.honeykomb.honeykomb.network.WebURLs;
import com.honeykomb.honeykomb.receiver.ResponseReceiver;
import com.honeykomb.honeykomb.utils.Constants;
import com.honeykomb.honeykomb.utils.Util;
import com.honeykomb.honeykomb.utils.UtilityHelper;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Hyd-Shekar on 1/17/2017.
 */
public class GetAllActivitiesAsync extends AsyncTask<String, Void, ArrayList<ArrayList<String>>> {
    private static final String TAG = "GetAllActivitiesAsync";
    private Context activity;
    private String authenticationKey;
    private String hK_uuid;
    private String needActivity;
    private String ifNotificationMessage;
    private ArrayList<ArrayList<String>> qbChatIDs = new ArrayList<>();
    private JSONArray jsonArrayForACK;

    public GetAllActivitiesAsync(Context activity, String authenticationKey, String hK_uuid, String needActivity, String ifNotificationMessage) {
        this.activity = activity;
        this.authenticationKey = authenticationKey;
        this.hK_uuid = hK_uuid;
        this.needActivity = needActivity;
        this.ifNotificationMessage = ifNotificationMessage;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ArrayList<ArrayList<String>> doInBackground(String... params) {

        String response = executeRequest(WebURLs.REST_ACTION_USER_ACTIVITY, getBody());
        parseResponse(response);

        return qbChatIDs;
    }

    @SuppressLint("NewApi")
    private void parseResponse(String response) {
        if(response!= null) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.has("messageCode")) {
                    if (jsonObject.optInt("messageCode") == Constants.HANDLER_MESSAGE_CODE_OK) {
                        Log.i(TAG, "JSONRESPONSE for ACTIVITY = " + jsonObject.toString());

                        jsonArrayForACK = new JSONArray();
                        JSONArray jsonArray = jsonObject.getJSONArray("userActivities");
                        if (jsonArray.length() != 0) {
                            ArrayList<String> activityIds = Util._db.getAllActivityIDs();
                            Log.i(TAG, "activityIds = " + activityIds.toString());
                            Log.i(TAG, "jsonArray.length() = " + jsonArray.length());
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Bundle bundle = new Bundle();

                                JSONObject object = jsonArray.getJSONObject(i);

                                Log.i(TAG, "object ================ " + object.toString());
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
                                bundle.putString("modifiedBy", "");
                                bundle.putString("reminder", object.getString("reminder"));
                                bundle.putString("hK_UUID", object.getString("hK_UUID"));
                                bundle.putString("invitationStatus", object.getString("invitationStatus"));

                                bundle.putString("actionType", " ");

                                ArrayList<String> ownerDetails = Util._db.getOwnerDetails(object.getString("activityOwner"));
                                ActivityOwnerDetails activityOwnerDetails = new ActivityOwnerDetails();
                                JSONObject activityOwnerOBJ = object.getJSONObject("actOwnerDetails");
                                if (ownerDetails.size() > 1) {
                                    activityOwnerDetails.sethKID(ownerDetails.get(0));
                                    activityOwnerDetails.setDisplayName(ownerDetails.get(2));
                                    activityOwnerDetails.setQuickBloxID(ownerDetails.get(3));
                                    activityOwnerDetails.setPhone(ownerDetails.get(4));
                                } else {
                                    activityOwnerDetails.sethKID(activityOwnerOBJ.optString("hKID"));
                                    activityOwnerDetails.setDisplayName(activityOwnerOBJ.optString("displayName"));
                                    activityOwnerDetails.setQuickBloxID(activityOwnerOBJ.optString("quickBloxID"));
                                    activityOwnerDetails.setPhone(activityOwnerOBJ.optString("phone"));
                                }
                                activityOwnerDetails.sethK_UUID(object.getString("activityOwner"));
                                activityOwnerDetails.setPhoto(object.getString("imagePath"));
                                activityOwnerDetails.setActive(object.getString("active"));
                                bundle.putParcelable("activityOwnerDetails", activityOwnerDetails);


                                bundle.putString("imagePath", object.getString("imagePath"));
                                bundle.putString("location", object.getString("location"));
                                bundle.putString("address", object.getString("address"));

                                JSONArray hkUsersArray = object.getJSONArray("hkUsers");
                                ArrayList<HKUsers> hkUsersList = new ArrayList<>();
                                if (hkUsersArray.length() != 0) {
                                    for (int j = 0; j < hkUsersArray.length(); j++) {

                                        Log.i(TAG, "hkUsersArray size = " + hkUsersArray.length() + " Title: " + object.getString("activityTitle"));
                                        JSONObject hkUserObject = hkUsersArray.getJSONObject(j);
                                        // TODO dated 12-07-2017 8:40 pm from if condition
                                        if (!hkUserObject.getString("phoneNumber").equalsIgnoreCase(" ") || !hkUserObject.getString("quickBloxID").equalsIgnoreCase("OK.")) {
                                            HKUsers hkUser = new HKUsers();

                                            hkUser.setPhoneNumber(hkUserObject.getString("phoneNumber"));
                                            hkUser.setInvitationStatus(hkUserObject.getString("invitationStatus"));
                                            hkUser.setUserActivityStatus(hkUserObject.optString("userActivityStatus"));
                                            if (hK_uuid.equals(hkUserObject.getString("hK_UUID"))) {
                                                bundle.putString("userActivityStatus", hkUserObject.optString("userActivityStatus"));
                                                bundle.putString("countRSVP", hkUserObject.getString("countRSVP"));
                                                bundle.putString("QuickbloxGroupID", object.getString("quickbloxGroupID"));
                                                bundle.putString("lastUpdated", object.optString("lastUpdated"));
                                                if (object.has("displayName")) {
                                                    bundle.putString("activityOwnerName", hkUserObject.optString("displayName"));
                                                } else {
                                                    bundle.putString("activityOwnerName", " ");
                                                }

                                            }
                                            hkUser.sethK_UUID(hkUserObject.getString("hK_UUID"));
                                            hkUser.setCountRSVP(hkUserObject.getString("countRSVP"));
                                            hkUser.setQuickBloxID(hkUserObject.getString("quickBloxID"));
                                            hkUser.setDeliveredTime(hkUserObject.getString("deliveredTime"));

                                            hkUsersList.add(hkUser);
                                        }
                                        // TODO dated 12-07-2017 8:40 pm till here if condition
                                    }
                                }
                                Log.i(TAG, "hkUsersList with phone numbers included " + hkUsersList.size());
                                bundle.putParcelableArrayList("hkUsersList", hkUsersList);


                                bundle.putString("QuickbloxGroupID", object.getString("quickbloxGroupID"));
                                bundle.putString("QuickbloxRoomJID", "0"/*object.getString("quickbloxRoomJID")*/);
                                bundle.putString("activityRSVP", object.getString("activityRSVP"));

                                bundle.putString("lastAccessUpdate", object.getString("lastAccessUpdate"));
                                bundle.putString("activityDetailID", " ");
                                bundle.putString("columnName", " ");
                                bundle.putString("columnValue", " ");

                                int cout = Util._db.getInviteesCount(object.getString("activityId"));
                                Log.i(TAG, "invitees count: " + Util._db.getInviteesCount(object.getString("activityId")));
                                Util._db.createActivity(bundle, cout);
                                Util._db.createActivityUsers(bundle);
                                Util._db.createActivityDetails(bundle);
                                // TODO inserting activity owner in activity user table as we are getting wrong values from server.
                                // TODO
                                Bundle bundle1 = new Bundle();
                                bundle1.putParcelable("activityOwnerDetails", activityOwnerDetails);
                                bundle1.putString("activityID", object.getString("activityId"));
                                bundle1.putString("actionType", " ");
                                bundle1.putString("dateModified", object.getString("dateModified"));
                                bundle1.putString("createdBy", activityOwnerDetails.gethK_UUID());
                                if (object.has("modifiedBy")) {
                                    bundle1.putString("modifiedBy", object.getString("modifiedBy"));
                                } else {
                                    bundle1.putString("modifiedBy", " ");
                                }
                                Util._db.createActivityOwner(bundle1);
                                Util._db.deleteActivityDates(object.getString("activityId"));
                                HashMap<String, String> activityDates = new HashMap<>();

                                try {
                                    activityDates.put("ActivityDateID", UUID.randomUUID().toString());
                                    activityDates.put("activityID", object.getString("activityId"));
                                    activityDates.put("actionType", "");
                                    activityDates.put("createdBy", object.getString("createdBy"));
                                    activityDates.put("dateModified", object.getString("dateModified"));
                                    if (object.has("modifiedBy")) {
                                        activityDates.put("modifiedBy", object.getString("modifiedBy"));
                                    } else {
                                        activityDates.put("modifiedBy", " ");
                                    }

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
                                            if (!activityIds.contains(object.getString("activityId")))
                                                Util._db.setReminder(object.getString("activityId"), startDate, object.getString("reminder"));
                                        }
                                    }

                                } catch (Exception e) {
                                    Log.i(TAG, "datetime : " + e);
                                }
                            }
                            executeRequest(WebURLs.REST_ACTION_ACKNOWLEDGE_SERVER, getAckBody());
                        }
                    }
                } else {
                    Log.i(TAG, "msg = " + response);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.i(TAG, "msg = " + response);
            }
        }
    }

    private String getAckBody() {

        JSONObject jsonObject = new JSONObject();
        try {

            Log.i(TAG, "error on : -->> " + TAG);
            jsonObject.putOpt("authenticationKey", authenticationKey);
            jsonObject.putOpt("hK_UUID", hK_uuid);
            jsonObject.putOpt("serviceName", Constants.Service_Name_GET_ALL_Activity);
            if (jsonArrayForACK != null && jsonArrayForACK.length() > 0) {
                jsonObject.putOpt("activityList", jsonArrayForACK);
                Log.v(TAG, "activityList->> " + jsonArrayForACK);
            }
        } catch (JSONException je) {
            Log.i(TAG, "sendAcknowledgement on catch block");
            je.printStackTrace();
            return jsonObject.toString();
        }

        return jsonObject.toString();
    }

    private String executeRequest(String _url, String body) {
        String response = null;
        String method = "POST";
        try {
            Log.i(TAG, "---------------Url-------------:" + WebURLs.REST_ACTION_USER_ACTIVITY);
            URL url = new URL(_url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setRequestMethod(method);
            Log.i(TAG, "---------------Body-------------:" + body);
            Log.i(TAG, "---------------Is Valid Json-------------:" + Util.isJSONValid(body));
            if (Util.isJSONValid(body)) {
                connection.setRequestProperty("Content-Type", "application/json");
            } else {
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            }
            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(body);
            writer.flush();
            writer.close();
            os.close();

            connection.connect();
            Log.i(TAG, "-------------HTTP Status Code----------:" + connection.getResponseCode());
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream is = connection.getInputStream();
                response = IOUtils.toString(is);
            } else {
                InputStream is = connection.getErrorStream();
                response = IOUtils.toString(is);
            }
            Log.i(TAG, "-------------HTTP Response---------- :" + _url + " :: " + response);
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "--------ErrorLogService-----Web Service Exception----------:" + e.getMessage());
        }

        return response;

    }

    private String getBody() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.putOpt("authenticationKey", authenticationKey);
            jsonObject.putOpt("hK_UUID", hK_uuid);
            jsonObject.putOpt("login", needActivity);

        } catch (JSONException je) {
            je.printStackTrace();
        }
        return jsonObject.toString();
    }

    @Override
    protected void onPostExecute(ArrayList<ArrayList<String>> QBChatIDs) {
        super.onPostExecute(QBChatIDs);
        // TODO this is when the applciation is in foreground.
        if (!Util.isAppIsInBackground(activity)) {
            Intent broadcastInt = new Intent();
            broadcastInt.setAction(Constants.REFRESH_VIEW);
            Log.i(TAG, "app in son foregrond  == = = = = " + ifNotificationMessage);
            LocalBroadcastManager.getInstance(activity).sendBroadcast(broadcastInt);
        } else {
            //TODO this is when the application is in background and if there is any message from server.
            if (ifNotificationMessage != null) {
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(ResponseReceiver.ACTION_RESP);
                broadcastIntent.putExtra("notificationMessage", ifNotificationMessage);
                Log.i(TAG, "app is in bakground  == = = = = " + ifNotificationMessage);
                if (!ifNotificationMessage.equalsIgnoreCase("No")) {
                    Util.playNotificationSound(activity);
                    Intent intent = new Intent(activity, MainScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Bundle bundle = new Bundle();
                    bundle.putString("notification", ifNotificationMessage);
                    intent.putExtras(bundle);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PendingIntent pendingIntent = PendingIntent.getActivity(activity, 0, intent,
                            PendingIntent.FLAG_ONE_SHOT);

                    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(activity)
                            .setSmallIcon(R.mipmap.launch)
                            .setLargeIcon(BitmapFactory.decodeResource(activity.getResources(),R.mipmap.launch))
                            .setContentTitle(activity.getResources().getString(R.string.app_name))
                            .setContentText(ifNotificationMessage)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent);
                    NotificationManager notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
                    assert notificationManager != null;
                    notificationManager.notify(0, notificationBuilder.build());
                }
            }
        }
    }
}
