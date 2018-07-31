package com.honeykomb.honeykomb.async;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Message;
import android.util.Log;

import com.honeykomb.honeykomb.service.DeliverdAndReadStatusService;
import com.honeykomb.honeykomb.utils.Constants;
import com.honeykomb.honeykomb.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Rajashekar.Nimmala on 4/7/2017.
 */

public class DeliverdAndReadStatusAsync extends AsyncTask<Void, Void, Void> {
    private Context applicationContext;
    private String authenticationKey;
    private String hK_uuid;
    private String TAG = DeliverdAndReadStatusAsync.class.getSimpleName();
    private String activityId;

    public DeliverdAndReadStatusAsync(Context applicationContext, String authenticationKey, String hK_uuid, String activityId) {
        this.applicationContext = applicationContext;
        this.authenticationKey = authenticationKey;
        this.hK_uuid = hK_uuid;
        this.activityId = activityId;
    }

    @Override
    protected Void doInBackground(Void... params) {
        DeliverdAndReadStatusService readStatusService = new DeliverdAndReadStatusService(applicationContext);

        Message message = readStatusService.ServiceCall(authenticationKey, hK_uuid, activityId);
        Log.i(TAG, "DeliverdAndReadStatusAsync = " + message.toString());
        if (message.what == 0 && message.arg1 == 0) {
            JSONObject jObj = (JSONObject) message.obj;
            Log.i(TAG, "DeliverdAndReadStatusAsync jObj = " + jObj.toString());
            if (jObj.has("messageCode")) {
                if (jObj.optInt("messageCode") == Constants.HANDLER_MESSAGE_CODE_OK) {
                    Log.i(TAG, "DeliverdAndReadStatusAsync 200= " + jObj.toString());
                    try {
                        JSONArray jsonArray = jObj.getJSONArray("hkUsers");
                        if (jsonArray.length() != 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                udpateActivityUserDataToDB("deliveredTime", object.getString("deliveredTime"), object.getString("hK_UUID"));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    private void udpateActivityUserDataToDB(String deliveredTime, String deliveredTimeValue, String hK_uuid) {
//        final DataBaseHelper db = new DataBaseHelper(applicationContext);
//        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        /*SQLiteDatabase sqLiteDatabase = Util._db.getWritableDatabase();
        String query = "UPDATE ActivityUsers SET " + deliveredTime + " ='" + deliveredTimeValue + "' WHERE activityID = '" + activityId + "' and hK_UUID = '" + hK_uuid + "'";
        sqLiteDatabase.execSQL(query);*/
        Util._db.updateActivityUsersDataToDbNew(deliveredTime,deliveredTimeValue,activityId,hK_uuid);
//        sqLiteDatabase.close();
    }
}
