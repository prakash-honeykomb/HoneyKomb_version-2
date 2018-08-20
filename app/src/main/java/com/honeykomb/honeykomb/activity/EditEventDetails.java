package com.honeykomb.honeykomb.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.honeykomb.honeykomb.R;
import com.honeykomb.honeykomb.async.AddActivityAsync;
import com.honeykomb.honeykomb.async.VerifyNonHKUsers;
import com.honeykomb.honeykomb.dao.RSVPCount;
import com.honeykomb.honeykomb.dao.SelectedContactObject;
import com.honeykomb.honeykomb.dao.ServiceContactObject;
import com.honeykomb.honeykomb.listeners.LocationInterface;
import com.honeykomb.honeykomb.utils.Constants;
import com.honeykomb.honeykomb.utils.Util;
import com.honeykomb.honeykomb.utils.UtilityHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.Executor;

public class EditEventDetails extends BaseActivity implements LocationInterface {
    private String activityID;
    private String activityOwner;
    private String hK_UUID;
    private EditText eventTitleET, eventDescriptionET, eventLocationET, startDateET, endDateET;
    private ImageView eventLocationIMV;
    private String TAG = EditEventDetails.class.getSimpleName();
    boolean gps_enabled = false;
    boolean network_enabled = false;
    private SimpleDateFormat mFormatter = new SimpleDateFormat("EEE, MMM dd, yyyy | hh:mm aaa", Locale.getDefault());
    private TextView noOfInviteesTV, inviteesTV;
    // constants
    private static final int PLACE_PICKER_REQUEST = 1;
    static final long ONE_MINUTE_IN_MILLIS = 60000;
    private ArrayList<String> oldDetails = null;
    private String updatedMsg = "";
    private boolean dataChanged = false;
    private boolean locationSelected = false;
    private static final int CONTACTS_FROM_LIST = 1121;
    private String authKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.edit_event_details);
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        activityID = (String) bundle.get("activityID");
//        String activityDateID = (String) bundle.get("activityDateID");
        activityOwner = (String) bundle.get("activityOwner");
        oldDetails = bundle.getStringArrayList("eventDetails");

        SharedPreferences sp = getSharedPreferences(Constants.APP_PREFERENCE_NAME, Activity.MODE_PRIVATE);
        authKey = sp.getString(Constants.AUTH_KEY, "");
        hK_UUID = sp.getString(Constants.HK_UUID, "");
        initCurrentActivityViews();
        updateUI();
    }

    private void initCurrentActivityViews() {
        eventTitleET = findViewById(R.id.edit_event_title_ET);
        eventDescriptionET = findViewById(R.id.edit_event_description_ET);
        eventLocationET = findViewById(R.id.edit_event_location_ET);
        eventLocationIMV = findViewById(R.id.edit_event_location_IMV);
        startDateET = findViewById(R.id.edit_start_date_n_time_ET);
        endDateET = findViewById(R.id.edit_end_date_n_time_ET);
        noOfInviteesTV = findViewById(R.id.edit_invitees_ET);
        inviteesTV = findViewById(R.id.edit_invitees_TV);
        TextView startDateTV = findViewById(R.id.edit_start_date_TV);
        TextView endDateTV = findViewById(R.id.edit_end_date_n_time_TV);

        // set up tool bar views
        titleTV.setText(getResources().getString(R.string.edit_event));
        toolbarCalenderIMV.setVisibility(View.GONE);
        toolbarListViewIMV.setVisibility(View.GONE);
        toolbarAddIMV.setVisibility(View.GONE);
        saveEventTV.setVisibility(View.VISIBLE);

        if (toolbar != null) {
            if (getSupportActionBar() != null) {
                toolbar.setNavigationIcon(R.mipmap.backarrow);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
            }
        }

        eventTitleET.setTypeface(Util.setTextViewTypeFace(EditEventDetails.this, "DroidSans.ttf"));
        eventDescriptionET.setTypeface(Util.setTextViewTypeFace(EditEventDetails.this, "DroidSans.ttf"));
        eventLocationET.setTypeface(Util.setTextViewTypeFace(EditEventDetails.this, "DroidSans.ttf"));
        startDateET.setTypeface(Util.setTextViewTypeFace(EditEventDetails.this, "DroidSans.ttf"));
        endDateET.setTypeface(Util.setTextViewTypeFace(EditEventDetails.this, "DroidSans.ttf"));
        noOfInviteesTV.setTypeface(Util.setTextViewTypeFace(EditEventDetails.this, "DroidSans.ttf"));
        startDateTV.setTypeface(Util.setTextViewTypeFace(EditEventDetails.this, "DroidSans.ttf"));
        endDateTV.setTypeface(Util.setTextViewTypeFace(EditEventDetails.this, "DroidSans.ttf"));
        inviteesTV.setTypeface(Util.setTextViewTypeFace(EditEventDetails.this, "DroidSans.ttf"));

        // setOnclick listener
        eventLocationIMV.setOnClickListener(this);
        startDateET.setOnClickListener(this);
        endDateET.setOnClickListener(this);
        inviteesTV.setOnClickListener(this);
        saveEventTV.setOnClickListener(this);
    }

    private void updateUI() {

        ArrayList<String> details = Util._db.getActivityDetails(activityID, activityOwner, hK_UUID);

        if (details != null && details.size() > 0) {
            eventTitleET.setText(details.get(1)); //Title
            try {
                DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date startDate = inputFormat.parse(details.get(17));
                Date endDate = inputFormat.parse(Util._db.getActivityEndDateBasedOnActivityID(activityID));
                startDateET.setText(mFormatter.format(startDate));
                endDateET.setText(mFormatter.format(endDate));
            } catch (Exception e) {
                e.printStackTrace();
                Date startDate = new Date();
                startDateET.setText(mFormatter.format(startDate));
                Date endDate = new Date(startDate.getTime() + (30 * ONE_MINUTE_IN_MILLIS));
                endDateET.setText(mFormatter.format(endDate));
            }
            if (details.get(12).trim().length() > 0) {
                eventDescriptionET.setText(details.get(12));
            } else {
                eventDescriptionET.setText("");
            }
            if (details.get(20).length() > 2) {
                eventLocationET.setText(details.get(20));
            } else {
                eventLocationET.setText("");
            }
            RSVPCount rsvpCount = Util._db.getActivityRSVPCount(activityID, hK_UUID);
            String toNoOfInvitees = String.valueOf(rsvpCount.getTotalNoOfInvitees() + " " + getResources().getString(R.string.invitees_selected));
            noOfInviteesTV.setText(toNoOfInvitees);

            /*if (details.size() > 23 && details.get(23).length() > 2) {
                eventLocationET.setText(details.get(20));
            }*/
            eventLocationET.setText(details.get(20));

            if (hK_UUID.equalsIgnoreCase(details.get(2))) {
                Util._db.SetInvitationStatus(activityID, hK_UUID);
            }
            if (oldDetails != null && !details.toString().equals(oldDetails.toString())) {
                if (updatedMsg.equalsIgnoreCase("participants ")) {
                    updatedMsg = "participants ";
                } else {
                    updatedMsg = "";
                }
                dataChanged = true;

                if (!oldDetails.get(20).equals(eventLocationET.getText().toString()) || !oldDetails.get(23).equals(eventLocationET.getText().toString())) {
                    updatedMsg += "Location ";
                }
                if (!oldDetails.get(17).equalsIgnoreCase(startDateET.getText().toString())) {
                    updatedMsg += "StartDate ";
                }
                if (!oldDetails.get(18).equalsIgnoreCase(startDateET.getText().toString())) {
                    updatedMsg += "EndDate ";
                }

                updatedMsg = updatedMsg.trim().replace(" ", ", ");
            }
        } else {
            finish();
        }

    }

    @Override
    public void onClick(View v) {
        if (v == eventLocationIMV) {
            Log.i(TAG, "onClick = eventLocationIMV");
            getLocation();
        } else if (v == startDateET) {
            Log.i(TAG, "onClick = startDateET");
            showStartDateAndTimeDialog();
        } else if (v == endDateET) {
            Log.i(TAG, "onClick = endDateET");
            if (startDateET.getText().toString().trim().length() > 0) {
                showEndDateAndTimeDialog();
            } else {
                Toast.makeText(EditEventDetails.this, getResources().getString(R.string.please_select_start_date_n_time), Toast.LENGTH_SHORT).show();
            }
        } else if (v == inviteesTV) {
            Log.i(TAG, "onClick = inviteesET");
            getInvitees();
        } else if (v == saveEventTV) {
            Util.hide_keyboard_from(EditEventDetails.this, eventTitleET);
            saveActivity();
        }
    }

    private void saveActivity() {
        Util.hide_keyboard_from(EditEventDetails.this, eventTitleET);
        final String notes = eventDescriptionET.getText().toString().trim();
        String eventTitle = eventTitleET.getText().toString();
        if (eventTitle.trim().equalsIgnoreCase("")) {
            alertDialog();
        } else {
            if (!oldDetails.get(1).equalsIgnoreCase(eventTitle)) {
                updateActivityDataToDB("activityTitle", eventTitle);
                if (!oldDetails.get(1).equalsIgnoreCase(eventTitleET.getText().toString())) {
                    if (!updatedMsg.contains("Title ")) {
                        updatedMsg += "Title ";
                        dataChanged = true;
                    }
                }
            }
            if (!oldDetails.get(12).equalsIgnoreCase(notes)) {
                updateActivityDataToDB("activityNotes", notes);
                if (!oldDetails.get(12).equalsIgnoreCase(eventDescriptionET.getText().toString())) {
                    if (!updatedMsg.contains("Notes ")) {
                        updatedMsg += "Notes ";
                        dataChanged = true;
                    }
                }
            }
            if (!oldDetails.get(20).equals(eventLocationET.getText().toString()) || !oldDetails.get(23).equals(eventLocationET.getText().toString())) {
                if (!locationSelected && !updatedMsg.contains("Location ")) {
                    updateActivityDataToDB("address", eventLocationET.getText().toString());
                    updatedMsg += "Location ";
                    dataChanged = true;
                }
            }
            super.onBackPressed();
        }
    }

    private void updateActivityDataToDB(String key, String value) {
        String actionType;
        if (!oldDetails.get(24).equalsIgnoreCase("ADD"))
            actionType = "Modify";
        else
            actionType = "ADD";

        ContentValues values = new ContentValues();
        values.put("actionType", actionType);
        values.put(key, value);
        Util._db.updateActivityDataToDBEventDetails(values, activityID);
    }

    private void getInvitees() {
//        if (UtilityHelper.checkConnectivity(EditEventDetails.this)) {
        Intent intent = new Intent(EditEventDetails.this, SelectedListOfInvitees.class);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.ACTIVITY_ID, activityID);
        bundle.putBoolean("isOwner", true);
        bundle.putString("hK_UUID", hK_UUID);
        bundle.putString("QuickbloxGroupID", " ");
        intent.putExtras(bundle);
        startActivityForResult(intent, CONTACTS_FROM_LIST);
//        } else {
//            Toast.makeText(EditEventDetails.this, "Not connected..!", Toast.LENGTH_SHORT).show();
//        }
    }

    private void getLocation() {
        try {
            checkLocationService();
            if (gps_enabled && network_enabled) {
                PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                startActivityForResult(intentBuilder.build(EditEventDetails.this), PLACE_PICKER_REQUEST);
            }
        } catch (GooglePlayServicesRepairableException e) {
            Log.i(TAG, " Google Repairable Exception  = ");
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.i(TAG, " Google SErviceNot Available Exception  = ");
            e.printStackTrace();
        }
    }


    @Override
    public void setContentLayout(int layout) {
        v = inflater.inflate(layout, null);
        lnr_content.addView(v);
    }

    @Override
    public void onTextLocationSelect(String address) {

    }

    @Override
    public void onGeoLocationSelect(String name, String address, String lattitude, String longitude) {

        updateActivityDetailsDataToDB("actionType", "Modify");
        updateActivityDetailsDataToDB("columnName", "latLong");
        updateActivityDetailsDataToDB("columnValue", lattitude + "," + longitude);
        updateActivityDataToDB("address", name + " , " + address);
        locationSelected = true;
        updateUI();
    }

    private void updateActivityDetailsDataToDB(String key, String value) {
        Util._db.updateActivityDetailsDataToDB(key, value, activityID);
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

    public void checkLocationService() {
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        try {
            assert lm != null;
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            Log.e(TAG, "checkLocationService Exception = " + ex.getMessage());
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            Log.e(TAG, "checkLocationService Exception = " + ex.getMessage());
        }
        if (!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(EditEventDetails.this);
            dialog.setMessage(getResources().getString(R.string.gps_alert));
            dialog.setPositiveButton(getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    //get gps
                    gps_enabled = true;
                    network_enabled = true;
                }
            });
            dialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    gps_enabled = false;
                    network_enabled = false;
                }
            });
            dialog.show();
        }
    }

    private void showStartDateAndTimeDialog() {
        startDateET.getText();
        try {
            Date date = mFormatter.parse(String.valueOf(startDateET.getText()));
            new SlideDateTimePicker.Builder(EditEventDetails.this.getSupportFragmentManager())
                    .setListener(startDateListener)
                    .setInitialDate(date)
                    .setIndicatorColor(getResources().getColor(R.color.colorAccent))
                    .build()
                    .show();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void showEndDateAndTimeDialog() {
        try {
            Date date = mFormatter.parse(String.valueOf(endDateET.getText()));
            new SlideDateTimePicker.Builder(EditEventDetails.this.getSupportFragmentManager())
                    .setListener(endDateListener)
                    .setInitialDate(date)
                    .setIndicatorColor(getResources().getColor(R.color.colorAccent))
                    .build()
                    .show();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Util.hide_keyboard_from(EditEventDetails.this, eventTitleET);
    }

    private SlideDateTimeListener startDateListener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date) {
            startDateET.setText(mFormatter.format(date));
            endDateET.setText(mFormatter.format(date.getTime() + (30 * ONE_MINUTE_IN_MILLIS)));
            Date scheduleFrom;
            Date scheduleTo;
            SimpleDateFormat dfDate_day = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy | hh:mm aaa", Locale.getDefault());
                SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                scheduleFrom = simpleDateFormat.parse(startDateET.getText().toString());
                scheduleTo = simpleDateFormat.parse(endDateET.getText().toString());
                if (!simpleDateFormat1.format(scheduleFrom).equalsIgnoreCase(oldDetails.get(17)) || !simpleDateFormat1.format(scheduleTo).equalsIgnoreCase(Util._db.getActivityEndDateBasedOnActivityID(activityID))) {
                    Util._db.deleteActivityDates(activityID);
                    HashMap<String, String> activityDates = new HashMap<>();
                    try {
                        int noOfDays;
                        activityDates.put("activityID", activityID);
                        activityDates.put("ActivityDateID", UUID.randomUUID().toString());
                        activityDates.put("actionType", "Modify");
                        activityDates.put("modifiedBy", hK_UUID);

                        noOfDays = UtilityHelper.daysBetweenDates(scheduleTo, scheduleFrom);

                        Calendar c = Calendar.getInstance();
                        Calendar c1 = Calendar.getInstance();
                        for (int i = 0; i < noOfDays; i++) {
                            c.setTime(scheduleFrom);
                            c1.setTime(scheduleTo);
                            c.add(Calendar.DATE, i);
                            c1.set(Calendar.DATE, c.get(Calendar.DATE));
                            c1.set(Calendar.MONTH, c.get(Calendar.MONTH));
                            c1.set(Calendar.YEAR, c.get(Calendar.YEAR));
                            activityDates.put("startDate", dfDate_day.format(c.getTime()));
                            activityDates.put("endDate", dfDate_day.format(c1.getTime()));
                            Util._db.createActivityDates(activityDates);
                        }
                        updateActivityDataToDB("blockCalendar", "1");
                    } catch (Exception e) {
                        Log.i(TAG, "datetime : " + e);
                    }
                    Log.i(TAG, "Date Param : " + simpleDateFormat1.format(scheduleFrom));
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception = " + e);
            }

            updateUI();
        }

        @Override
        public void onDateTimeCancel() {
        }
    };

    private SlideDateTimeListener endDateListener = new SlideDateTimeListener() {
        @Override
        public void onDateTimeSet(Date date) {
            String startDateNew = startDateET.getText().toString();
            String endDateNew = mFormatter.format(date);
            endDateET.setText(endDateNew);
            try {
                Date endDate = mFormatter.parse(endDateNew);
                Date startDate = mFormatter.parse(startDateNew);
                if (endDate.getTime() - (30 * ONE_MINUTE_IN_MILLIS) >= startDate.getTime()) {
                    endDateET.setText(mFormatter.format(date));
                    Date scheduleFrom;
                    Date scheduleTo;
                    SimpleDateFormat dfDate_day = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    try {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy | hh:mm aaa", Locale.getDefault());
                        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        scheduleFrom = simpleDateFormat.parse(startDateET.getText().toString());
                        scheduleTo = simpleDateFormat.parse(endDateET.getText().toString());
                        if (!simpleDateFormat1.format(scheduleFrom).equalsIgnoreCase(oldDetails.get(17)) || !simpleDateFormat1.format(scheduleTo).equalsIgnoreCase(Util._db.getActivityEndDateBasedOnActivityID(activityID))) {
                            Util._db.deleteActivityDates(activityID);
                            HashMap<String, String> activityDates = new HashMap<>();
                            try {
                                int noOfDays;
                                activityDates.put("activityID", activityID);
                                activityDates.put("ActivityDateID", UUID.randomUUID().toString());
                                activityDates.put("actionType", "Modify");
                                activityDates.put("modifiedBy", hK_UUID);

                                noOfDays = UtilityHelper.daysBetweenDates(scheduleTo, scheduleFrom);

                                Calendar c = Calendar.getInstance();
                                Calendar c1 = Calendar.getInstance();
                                for (int i = 0; i < noOfDays; i++) {
                                    c.setTime(scheduleFrom);
                                    c1.setTime(scheduleTo);
                                    c.add(Calendar.DATE, i);
                                    c1.set(Calendar.DATE, c.get(Calendar.DATE));
                                    c1.set(Calendar.MONTH, c.get(Calendar.MONTH));
                                    c1.set(Calendar.YEAR, c.get(Calendar.YEAR));
                                    activityDates.put("startDate", dfDate_day.format(c.getTime()));
                                    activityDates.put("endDate", dfDate_day.format(c1.getTime()));
                                    Util._db.createActivityDates(activityDates);
                                }
                            } catch (Exception e) {
                                Log.i(TAG, "datetime : " + e);
                            }
                            Log.i(TAG, "Date Param : " + simpleDateFormat1.format(scheduleFrom));
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Exception = " + e);
                    }
                } else {
                    endDateET.setText(mFormatter.format(startDate.getTime() + (30 * ONE_MINUTE_IN_MILLIS)));
                    Date scheduleFrom;
                    Date scheduleTo;
                    SimpleDateFormat dfDate_day = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    try {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy | hh:mm aaa", Locale.getDefault());
                        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        scheduleFrom = simpleDateFormat.parse(startDateET.getText().toString());
                        scheduleTo = simpleDateFormat.parse(endDateET.getText().toString());
                        if (!simpleDateFormat1.format(scheduleFrom).equalsIgnoreCase(oldDetails.get(17)) || !simpleDateFormat1.format(scheduleTo).equalsIgnoreCase(Util._db.getActivityEndDateBasedOnActivityID(activityID))) {
                            Util._db.deleteActivityDates(activityID);
                            HashMap<String, String> activityDates = new HashMap<>();
                            try {
                                int noOfDays;
                                activityDates.put("activityID", activityID);
                                activityDates.put("ActivityDateID", UUID.randomUUID().toString());
                                activityDates.put("actionType", "Modify");
                                activityDates.put("modifiedBy", hK_UUID);

                                noOfDays = UtilityHelper.daysBetweenDates(scheduleTo, scheduleFrom);

                                Calendar c = Calendar.getInstance();
                                Calendar c1 = Calendar.getInstance();
                                for (int i = 0; i < noOfDays; i++) {
                                    c.setTime(scheduleFrom);
                                    c1.setTime(scheduleTo);
                                    c.add(Calendar.DATE, i);
                                    c1.set(Calendar.DATE, c.get(Calendar.DATE));
                                    c1.set(Calendar.MONTH, c.get(Calendar.MONTH));
                                    c1.set(Calendar.YEAR, c.get(Calendar.YEAR));
                                    activityDates.put("startDate", dfDate_day.format(c.getTime()));
                                    activityDates.put("endDate", dfDate_day.format(c1.getTime()));
                                    Util._db.createActivityDates(activityDates);
                                }
                                updateActivityDataToDB("blockCalendar", "1");
                            } catch (Exception e) {
                                Log.i(TAG, "datetime : " + e);
                            }
                            Log.i(TAG, "Date Param : " + simpleDateFormat1.format(scheduleFrom));
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Exception = " + e);
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            updateUI();
        }

        @Override
        public void onDateTimeCancel() {
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       // super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (CONTACTS_FROM_LIST): {
                if (resultCode == Activity.RESULT_OK) {
                    dataChanged = true;
                    updatedMsg = "";
                    updatedMsg += "participants ";
                    updateUI();
                }
            }
            break;
            case (PLACE_PICKER_REQUEST): {
                if (resultCode == Activity.RESULT_OK) {
                    Place place = PlacePicker.getPlace(EditEventDetails.this, data);
                    CharSequence address = place.getAddress();
                    CharSequence name = place.getName();
                    PlaceAutocomplete.getStatus(EditEventDetails.this, data);
                    Log.i(TAG, "PlaceAutocomplete.getStatus(EditEventDetails.this, data) " + PlaceAutocomplete.getStatus(EditEventDetails.this, data));
                    LocationInterface locationInterface = EditEventDetails.this;
                    assert address != null;
                    locationInterface.onGeoLocationSelect(name.toString(), address.toString(), String.valueOf(place.getLatLng().latitude), String.valueOf(place.getLatLng().longitude));
                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(EditEventDetails.this, data);
                    if (status != null) {
                        Log.i(TAG, "PlaceAutocomplete.RESULT_ERROR = " + status.getStatusMessage());
                    }
                }
            }
            break;
        }

    }

    public void alertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EditEventDetails.this);
        alertDialogBuilder.setTitle(R.string.app_name);
        alertDialogBuilder.setMessage(getResources().getString(R.string.event_title_alert));
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setNeutralButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialogBuilder.show();

    }

    @Override
    protected void onDestroy() {
        if (dataChanged) {
            Util._db.updatedMsgOfActivity(activityID, updatedMsg);
            ArrayList<SelectedContactObject> nonHKUsers = Util._db.getNonHKUsers(activityID);
            if (nonHKUsers.size() > 0) {
                ArrayList<ServiceContactObject> serviceContactObjects = new ArrayList<>();
                for (SelectedContactObject selectedContactObject : nonHKUsers) {
                    String phoneNumberWOPlusNine = selectedContactObject.getNumber().replace("+91", "");
                    if (Util._db.getUser(selectedContactObject.getNumber()).contactNo.trim().length() > 1) {
                        serviceContactObjects.add(Util._db.getUser(selectedContactObject.getNumber()));
                    } else if (Util._db.getContactName(phoneNumberWOPlusNine, EditEventDetails.this).contactNo.trim().length() > 1) {
                        serviceContactObjects.add(Util._db.getContactName(phoneNumberWOPlusNine, EditEventDetails.this));
                    } else {
                        serviceContactObjects.add(Util._db.getContactName(selectedContactObject.getNumber(), EditEventDetails.this));
                    }
                }
                VerifyNonHKUsers verifyUserAsync = new VerifyNonHKUsers(serviceContactObjects, EditEventDetails.this, authKey, hK_UUID, activityID);
                verifyUserAsync.execute();
            } else {
                Executor threadPoolExecutor = UtilityHelper.getExecutor();
                new AddActivityAsync(getApplicationContext()).executeOnExecutor(threadPoolExecutor);
            }
        }
        super.onDestroy();
    }
}
