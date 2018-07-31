package com.honeykomb.honeykomb.async;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Message;
import android.util.Log;

import com.honeykomb.honeykomb.service.UpdateActivityUsersService;
import com.honeykomb.honeykomb.utils.Constants;
import com.honeykomb.honeykomb.utils.Util;

import org.json.JSONObject;

public class UpdateActivityUsersAsync extends AsyncTask<Void, Void, Void> {
    private Context activity;
    private String authenticationKey;
    private String hK_uuid;
    private String activityID;
    private String actionType;
    private String RSVPCount;
    private String needActivity;
    private String accept;
    private String TAG = UpdateActivityUsersAsync.class.getSimpleName();

    public UpdateActivityUsersAsync(Context activity, String authenticationKey, String hK_uuid, String activityID, String actionType, String RSVPCount, String needActivity, String accept) {
        this.activity = activity;
        this.authenticationKey = authenticationKey;
        this.hK_uuid = hK_uuid;
        this.activityID = activityID;
        this.actionType = actionType;
        this.RSVPCount = RSVPCount;
        this.needActivity = needActivity;
        this.accept = accept;
    }

    @Override
    protected Void doInBackground(Void... params) {

        UpdateActivityUsersService updateActivityUsersService = new UpdateActivityUsersService(activity);
        Message message = updateActivityUsersService.GetServiceResponse(authenticationKey, hK_uuid, activityID, actionType, RSVPCount, needActivity);
        if (message.what == 0 && message.arg1 == 0) {
            JSONObject jObj = (JSONObject) message.obj;
            if (jObj.has("messageCode")) {
                if (jObj.optInt("messageCode") == Constants.HANDLER_MESSAGE_CODE_OK) {
                    Log.i(TAG, "doInBackground UpdateActivityUsersService = " + jObj.toString());
                } else if (jObj.optInt("messageCode") == Constants.HANDLER_MESSAGE_UNAUTHORIZED) {
                    Util.updateLaunchMode(activity);
                }
            }
        } else {
            Log.i(TAG, "msg = " + message);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        new SendAcknowledgementAsync(activity, authenticationKey, hK_uuid, Constants.Service_Name_GET_ALL_Activity).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
