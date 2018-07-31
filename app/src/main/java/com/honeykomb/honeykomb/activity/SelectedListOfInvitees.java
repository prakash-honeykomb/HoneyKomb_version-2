package com.honeykomb.honeykomb.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.honeykomb.honeykomb.R;
import com.honeykomb.honeykomb.adapters.SelectedInviteesAdapter;
import com.honeykomb.honeykomb.dao.ContactObject;
import com.honeykomb.honeykomb.dao.SelectedContactObject;
import com.honeykomb.honeykomb.database.DataBaseHelper;
import com.honeykomb.honeykomb.utils.Util;

import java.util.ArrayList;
import java.util.UUID;

public class SelectedListOfInvitees extends Activity implements View.OnClickListener {

    private static final int CONTACTS = 112;
    private ListView ll_selected_contacts;
    private SelectedInviteesAdapter selectedInviteesAdapter;
    private ArrayList<SelectedContactObject> selectedContactObjects = null;
    private String activityID = "";
    private String hK_UUID = "";
    private String authenticationKey;
    private boolean isOwner = false;
    private String isCompleted = "";
    private boolean dataChangedInvitee = false;
    private String TAG = SelectedListOfInvitees.class.getSimpleName();
    private String qbGroupID = "";
    private String disableAdd = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDB(this);

//        AnalyticsApplication application = (AnalyticsApplication) getApplication();
//        mTracker = application.getDefaultTracker();
        if (getIntent() != null) {
            if (getIntent().getExtras() != null) {

                activityID = getIntent().getExtras().getString("activityID");
                isOwner = getIntent().getExtras().getBoolean("isOwner");
                hK_UUID = getIntent().getExtras().getString("hK_UUID");
                if (getIntent().getExtras().getString("isCompleted") != null) {
                    isCompleted = getIntent().getExtras().getString("isCompleted");
                }
                if (getIntent().getExtras().getString("disableAdd") != null) {
                    disableAdd = getIntent().getExtras().getString("disableAdd");
                }


                SharedPreferences sp = getSharedPreferences("HONEY_PREFS", Activity.MODE_PRIVATE);
                authenticationKey = sp.getString("authenticationKey", "");
            }
        }
        setContentView(R.layout.selected_invitees);
        ImageView done_IMV = findViewById(R.id.done_IMV);
        ImageView iv_add_invbitees = findViewById(R.id.iv_select_invitees);
        ll_selected_contacts = findViewById(R.id.ll_selected_contacts);

        selectedContactObjects = Util._db.getActivityUsers(activityID);

        selectedInviteesAdapter = new SelectedInviteesAdapter(SelectedListOfInvitees.this/*getApplicationContext()*/, activityID, isOwner, hK_UUID, isCompleted, authenticationKey, disableAdd);
        ll_selected_contacts.setAdapter(selectedInviteesAdapter);
        ColorDrawable dividerColor = new ColorDrawable(getApplicationContext().getResources().getColor(R.color.color_username_rectangle));
        ll_selected_contacts.setDivider(dividerColor);
        ll_selected_contacts.setDividerHeight(1);

        qbGroupID = Util._db.getQBGroupIDBasedOnActivityID(activityID);
        Log.i(TAG, "qbGroupID = " + qbGroupID);

        iv_add_invbitees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedContactObjects = Util._db.getActivityUsers(activityID);
                Intent intentInvitee = new Intent(SelectedListOfInvitees.this, Contact.class);
//                Intent intentInvitee = new Intent(SelectedListOfInvitees.this, ContactNew.class);
                if (selectedContactObjects != null && selectedContactObjects.size() > 0) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("selectedContactObjects", selectedContactObjects);
                    bundle.putString("from", "EventDetials");
                    intentInvitee.putExtras(bundle);
                    startActivityForResult(intentInvitee, CONTACTS);
                } else {
                    startActivityForResult(intentInvitee, CONTACTS);
                }
            }
        });


        done_IMV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (!isOwner || isCompleted.equalsIgnoreCase("Completed")) {
            iv_add_invbitees.setClickable(false);
            done_IMV.setClickable(false);
        }
        if (disableAdd.equalsIgnoreCase("Yes")) {
            iv_add_invbitees.setClickable(false);
            done_IMV.setClickable(false);
        }
        if (Util._db.getStatusByActivityID(activityID).equalsIgnoreCase("inActive")) {
            iv_add_invbitees.setClickable(false);
            done_IMV.setClickable(false);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {

    }


    @Override
    public void finish() {
        selectedInviteesAdapter = (SelectedInviteesAdapter) ll_selected_contacts.getAdapter();
        if (dataChangedInvitee || selectedInviteesAdapter.dataChangedInviteeAdapter) {
            Util._db.updateActivitySelectedListOfInvitees(activityID);
            Intent intent = new Intent(SelectedListOfInvitees.this, EventDetails.class);
            setResult(Activity.RESULT_OK, intent);
        }
        super.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case (CONTACTS): {
                if (resultCode == Activity.RESULT_OK) {

                    ArrayList<String> contactNo = data.getStringArrayListExtra("contactNo");
                    ArrayList<String> contactHkUUID = data.getStringArrayListExtra("contacthkUUID");
                    Log.i(TAG, "contactHkUUIDVALUE Invitee :  " + contactHkUUID.toString());
                    Log.i(TAG, "SELECTEDCONTACT Invitee :" + contactNo.toString());
                    Log.i(TAG, "contactNo Size Invitee :" + contactNo.size());

                    for (int i = 0; i < contactNo.size(); i++) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("QuickBloxID", "");
                        ContactObject cb = Util._db.getUserFromContactNumber(/*db,*/ contactNo.get(i));
                        if (cb != null) {
                            contentValues.put("hK_UUID", cb.getHkUUID());
                            contentValues.put("QuickBloxID", cb.getQuickBlockID());
                            contentValues.put("QBUserID", cb.getQBUserID());
                        }
                        contentValues.put("createdBy", hK_UUID);
                        contentValues.put("modifiedBy", hK_UUID);
                        contentValues.put("activityUserID", UUID.randomUUID().toString());
                        contentValues.put("activityID", activityID);
                        contentValues.put("phoneNumber", contactNo.get(i));
                        contentValues.put("countRSVP", "0");
                        contentValues.put("invitationStatus", "Pending");
                        contentValues.put("userActivityStatus", "Active");
                        contentValues.put("ActionType", "ADD");

                        Util._db.insertOrUpdateSLOfINV(contentValues);
                        dataChangedInvitee = true;
                        if (Util._db.getActionTypeBasedOnActivityID(activityID)) {
                            Util._db.updateActivitySelectedListOfInvitees(activityID);

                        }
                        qbGroupID = Util._db.getQBGroupIDBasedOnActivityID(activityID);
                        if (qbGroupID != null && qbGroupID.trim().length() > 2 && cb != null && cb.getHkID() != null && !cb.getHkID().equalsIgnoreCase("")) {
                            String UniqueID = UUID.randomUUID().toString();
                            Util._db.saveUserWithDBasParams(UniqueID, Integer.parseInt(qbGroupID), cb.getHkID(), "ADD", "NO");
//                            UtilityHelper.addUserToGroup(UniqueID, SelectedListOfInvitees.this, Integer.parseInt(qbGroupID), cb.getHkID());
                        }
                    }
                    selectedInviteesAdapter = new SelectedInviteesAdapter(SelectedListOfInvitees.this/*getApplicationContext()*/, activityID, isOwner, hK_UUID, isCompleted, authenticationKey, disableAdd);
                    ll_selected_contacts.setAdapter(selectedInviteesAdapter);

                }
                break;
            }
            default:
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "Setting screen name: " + TAG);
//        mTracker.setScreenName("Image~" + TAG);
//        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
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
            Log.i(TAG, "initDB Exception = " + e.getMessage());
        }
    }
}
