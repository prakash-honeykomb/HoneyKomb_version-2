package com.honeykomb.honeykomb.async;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.honeykomb.honeykomb.dao.ActivityDetails;
import com.honeykomb.honeykomb.dao.NonHKContact;
import com.honeykomb.honeykomb.dao.ServiceContactObject;
import com.honeykomb.honeykomb.service.VerifyUserService;
import com.honeykomb.honeykomb.utils.Constants;
import com.honeykomb.honeykomb.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;


/**
 * Created by Rajashekar.Nimmala on 8/8/2017.
 */

public class VerifyNonHKUsers extends AsyncTask<String, Void, String> {

    private ArrayList<ServiceContactObject> serviceContactObject;
    private Context context;
    private String TAG = VerifyNonHKUsers.class.getSimpleName();
    private String authenticationKey;
    private String hK_UUID;
    private String activityID;
    private List<NonHKContact> contactList;

    public VerifyNonHKUsers(ArrayList<ServiceContactObject> serviceContactObject, Context context, String authenticationKey, String hK_UUID, String activityID) {
        this.serviceContactObject = serviceContactObject;
        this.context = context;
        this.authenticationKey = authenticationKey;
        this.hK_UUID = hK_UUID;
        this.activityID = activityID;
    }

    @Override
    protected String doInBackground(String... params) {
        VerifyUserService getUserContactsService = new VerifyUserService(context);
        Message message = getUserContactsService.verifyUser(serviceContactObject, authenticationKey, hK_UUID);
        if (message.what == 0 && message.arg1 == 0) {
            JSONObject jObj = (JSONObject) message.obj;
            if (jObj.has("messageCode")) {
                if (jObj.optInt("messageCode") == Constants.HANDLER_MESSAGE_CODE_OK) {
                    Log.i(TAG, "JSONRESPONSE = " + jObj.toString());
                    if (jObj.has("contactList")) {
                        Type collectionType = new TypeToken<Collection<NonHKContact>>() {
                        }.getType();
                        try {
                            this.contactList = new Gson().fromJson(String.valueOf(jObj.getJSONArray("contactList")), collectionType);
                            if (contactList.size() > 0) {
                                Util._db.saveNewHkUsers(contactList, activityID);
                                return "success";
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (jObj.optInt("messageCode") == Constants.HANDLER_MESSAGE_UNAUTHORIZED) {
                    Util.updateLaunchMode(context);
                    return null;
                }
            }
        } else {
            Log.i(TAG, "VerifyNonHKUsers Failed ");
        }
        return null;
    }

    @Override
    protected void onPostExecute(String requestResponceObject) {
        super.onPostExecute(requestResponceObject);
        Log.i(TAG, "onPostExecute = ");
        if (requestResponceObject != null && requestResponceObject.equalsIgnoreCase("success")) {
            ActivityDetails activityDetails = Util._db.getActivityDetailsBasedOnActvityID(activityID);
            for (NonHKContact hkContact : contactList) {
                String UniqueID = UUID.randomUUID().toString();
                Util._db.saveUserWithDBasParams(UniqueID,/* db,*/ Integer.parseInt(activityDetails.getQuickbloxGroupID()), hkContact.hKID, "ADD", "NO");
            }
            /*Bundle bundle = new Bundle();
            bundle.putSerializable("createUserList", (ArrayList<NonHKContact>) contactList);
            bundle.putString("activityID", activityID);
            Intent intent = new Intent(context, AddNewUsersToChatGroup.class);
            intent.putExtra("bundle", bundle);
            context.startService(intent);*/
        }
    }

}

