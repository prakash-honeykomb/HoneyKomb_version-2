package com.honeykomb.honeykomb.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
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
import com.honeykomb.honeykomb.dao.ActivityOwnerDetails;
import com.honeykomb.honeykomb.dao.DBObject;
import com.honeykomb.honeykomb.dao.SelectedContactObject;
import com.honeykomb.honeykomb.dao.ServiceContactObject;
import com.honeykomb.honeykomb.listeners.LocationInterface;
import com.honeykomb.honeykomb.utils.Constants;
import com.honeykomb.honeykomb.utils.Util;
import com.honeykomb.honeykomb.utils.UtilityHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.Executor;

public class AddEventActivity extends BaseActivity implements LocationInterface {
    private EditText eventTitleET, eventDescriptionET, eventLocationET, startDateET, endDateET;
    private ImageView eventLocationIMV;
    private String TAG = AddEventActivity.class.getSimpleName();
    private String geoLocation = "";
    private String latLong = " ";
    boolean gps_enabled = false;
    boolean network_enabled = false;
    private SimpleDateFormat mFormatter = new SimpleDateFormat("EEE, MMM dd, yyyy | hh:mm aaa", Locale.getDefault());
    private Date startDate;
    private TextView noOfInviteesTV, inviteesTV;
    private ArrayList<SelectedContactObject> selectedContactObjects = new ArrayList<>();
    // constants
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final int CONTACTS = 111;
    static final long ONE_MINUTE_IN_MILLIS = 60000;
    private ArrayList<String> groupInvitees = new ArrayList<>();
    private ArrayList<String> contactHkIDOnBlank;
    private ArrayList<String> contactNoOnBlank;
    private ArrayList<String> contactHkUUIDOnBlank;
    private String activityID;
    private String authenticationKey;
    private String hK_UUID;
    private boolean geoLocationSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.add_event_activity);
        initCurrentActivityViews();
    }

    private void initCurrentActivityViews() {

        eventTitleET = findViewById(R.id.event_title_ET);
        eventDescriptionET = findViewById(R.id.event_description_ET);
        eventLocationET = findViewById(R.id.event_location_ET);
        eventLocationIMV = findViewById(R.id.event_location_IMV);
        startDateET = findViewById(R.id.start_date_n_time_ET);
        endDateET = findViewById(R.id.end_date_n_time_ET);
        noOfInviteesTV = findViewById(R.id.invitees_ET);
        inviteesTV = findViewById(R.id.invitees_TV);
        TextView startDateTV = findViewById(R.id.start_date_TV);
        TextView endDateTV = findViewById(R.id.end_date_n_time_TV);
        TextInputLayout txt_one =  findViewById(R.id.event_title_TIL);
        TextInputLayout txt_two =  findViewById(R.id.event_description_TIL);
        TextInputLayout txt_three =  findViewById(R.id.event_location_TIL);

        // set up tool bar views
        titleTV.setText(getResources().getString(R.string.add_event));
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

        eventTitleET.setTypeface(Util.setTextViewTypeFace(AddEventActivity.this, "DroidSans.ttf"));
        eventDescriptionET.setTypeface(Util.setTextViewTypeFace(AddEventActivity.this, "DroidSans.ttf"));
        eventLocationET.setTypeface(Util.setTextViewTypeFace(AddEventActivity.this, "DroidSans.ttf"));
        startDateET.setTypeface(Util.setTextViewTypeFace(AddEventActivity.this, "DroidSans.ttf"));
        endDateET.setTypeface(Util.setTextViewTypeFace(AddEventActivity.this, "DroidSans.ttf"));
        noOfInviteesTV.setTypeface(Util.setTextViewTypeFace(AddEventActivity.this, "DroidSans.ttf"));
        startDateTV.setTypeface(Util.setTextViewTypeFace(AddEventActivity.this, "DroidSans.ttf"));
        endDateTV.setTypeface(Util.setTextViewTypeFace(AddEventActivity.this, "DroidSans.ttf"));
        inviteesTV.setTypeface(Util.setTextViewTypeFace(AddEventActivity.this, "DroidSans.ttf"));
        txt_one.setTypeface(Util.setTextViewTypeFace(AddEventActivity.this, "DroidSans.ttf"));
        txt_two.setTypeface(Util.setTextViewTypeFace(AddEventActivity.this, "DroidSans.ttf"));
        txt_three.setTypeface(Util.setTextViewTypeFace(AddEventActivity.this, "DroidSans.ttf"));

        // setOnclick listener
        eventLocationIMV.setOnClickListener(this);
        startDateET.setOnClickListener(this);
        endDateET.setOnClickListener(this);
        inviteesTV.setOnClickListener(this);
        saveEventTV.setOnClickListener(this);
        startDate = new Date();
        startDateET.setText(mFormatter.format(startDate));
        Date endDate = new Date(startDate.getTime() + (30 * ONE_MINUTE_IN_MILLIS));
        endDateET.setText(mFormatter.format(endDate));
    }

    @Override
    public void setContentLayout(int layout) {
        v = inflater.inflate(layout, null);
        lnr_content.addView(v);
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
                Toast.makeText(AddEventActivity.this, getResources().getString(R.string.please_select_start_date_n_time), Toast.LENGTH_SHORT).show();
            }
        } else if (v == inviteesTV) {
            Log.i(TAG, "onClick = inviteesET");
            getInvitees();
        } else if (v == saveEventTV) {
            saveActivity();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Util.hide_keyboard_from(AddEventActivity.this, eventTitleET);
    }

    private void getInvitees() {
        if (selectedContactObjects.size() > 0) {
            Intent intent1 = new Intent(AddEventActivity.this, ContactSelected.class);
            Bundle bundle1 = new Bundle();
            bundle1.putParcelableArrayList("selectedContactObjects", selectedContactObjects);
            intent1.putExtras(bundle1);
            startActivityForResult(intent1, CONTACTS);
        } else {
            Intent intentInvitee = new Intent(AddEventActivity.this, Contact.class);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("selectedContactObjects", selectedContactObjects);
            intentInvitee.putExtras(bundle);
            startActivityForResult(intentInvitee, CONTACTS);
        }
    }

    private void showStartDateAndTimeDialog() {
        new SlideDateTimePicker.Builder(AddEventActivity.this.getSupportFragmentManager())
                .setListener(startDateListener)
                .setInitialDate(new Date())
                //.setMinDate(minDate)
                //.setMaxDate(maxDate)
                //.setIs24HourTime(true)
                //.setTheme(SlideDateTimePicker.HOLO_DARK)
                .setIndicatorColor(getResources().getColor(R.color.colorAccent))
                .build()
                .show();
    }

    private void showEndDateAndTimeDialog() {
        Date minDate = new Date(new Date().getTime() + (30 * ONE_MINUTE_IN_MILLIS));
        new SlideDateTimePicker.Builder(AddEventActivity.this.getSupportFragmentManager())
                .setListener(endDateListener)
                .setInitialDate(minDate)
//                .setMinDate(minDate)
                //.setMaxDate(maxDate)
                //.setIs24HourTime(true)
                //.setTheme(SlideDateTimePicker.HOLO_DARK)
                .setIndicatorColor(getResources().getColor(R.color.colorAccent))
                .build()
                .show();
    }

    private void getLocation() {
        try {
            checkLocationService();
            if (gps_enabled && network_enabled) {
                PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                startActivityForResult(intentBuilder.build(AddEventActivity.this), PLACE_PICKER_REQUEST);
            }
        } catch (GooglePlayServicesRepairableException e) {
            Log.i(TAG, " Google Repairable Exception  = ");
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.i(TAG, " Google SErviceNot Available Exception  = ");
            e.printStackTrace();
        }
    }

    private void saveActivity() {
        Util.hide_keyboard_from(AddEventActivity.this, eventTitleET);
        final String notes = eventDescriptionET.getText().toString().trim();
        String eventTitle = eventTitleET.getText().toString();
        if (eventTitle.trim().equalsIgnoreCase("")) {
            alertDialog();
            return;
        }
        final String reminder = "30 Min"; // TODO hard coded need to change

        DBObject dbObject = new DBObject();
        dbObject.setNotes(notes);
        if (geoLocationSelected) {
            dbObject.setLocation(geoLocation);
        } else {
            geoLocation = eventLocationET.getText().toString();
            latLong = " ";
            dbObject.setLocation(geoLocation);
        }
        dbObject.setReminder(reminder);
        Date scheduleFrom = new Date();
        Date scheduleTo = new Date();

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy | hh:mm aaa", Locale.getDefault());
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            scheduleFrom = simpleDateFormat.parse(startDateET.getText().toString());
            scheduleTo = simpleDateFormat.parse(endDateET.getText().toString());

            dbObject.setScheduleFrom(simpleDateFormat1.format(scheduleFrom));
            dbObject.setScheduleTo(simpleDateFormat1.format(scheduleTo));
        } catch (Exception e) {
            Log.i(TAG, "SimpleDateFormat " + " exception " + e);
        }
        activityID = UUID.randomUUID().toString();

        String activityDateID = UUID.randomUUID().toString();
        String activityDetailsID = UUID.randomUUID().toString();
        String activityUserID = UUID.randomUUID().toString();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dfDate_day = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date createdDate = calendar.getTime();

//        String uName = db_Helper.getUserName();
        String uName = Util._db.getUserName();
        Log.i(TAG, "uName saveActivity: " + uName);
        Bundle bundle = new Bundle();
        bundle.putString("activityTitle", eventTitle);
        bundle.putString("activityID", activityID);
        bundle.putString("active", "1");
        bundle.putString("activityUserID", activityUserID);
        if (dbObject.getNotes().equalsIgnoreCase("")) {
            bundle.putString("activityNotes", " ");
        } else {
            bundle.putString("activityNotes", dbObject.getNotes());
        }
        bundle.putString("activityStatus", "Active");
        bundle.putString("activityOwner", uName);
        bundle.putString("activityOwnerName", uName);
        bundle.putString("actionType", "ADD");
        if (dbObject.getLocation().equalsIgnoreCase("")) {
            bundle.putString("address", " ");
        } else {
            bundle.putString("address", dbObject.getLocation());
        }
        if (dbObject.getReminder().equalsIgnoreCase("")) {
            bundle.putString("reminder", " ");
        } else {
            bundle.putString("reminder", dbObject.getReminder());
        }
        bundle.putString("blockCalendar", "1");

        bundle.putString("allowOtherToModify", "0");
        bundle.putString("dateCreated", dfDate_day.format(createdDate));
        bundle.putString("dateModified", dfDate_day.format(createdDate));
        bundle.putString("createdBy", uName);
        bundle.putString("modifiedBy", uName);
        bundle.putString("countRSVP", "1");
        bundle.putString("userActivityStatus", "Active");
        bundle.putString("location", latLong);

        bundle.putString("QuickbloxGroupID", "0");
        bundle.putString("applozicgroupid", " ");
        bundle.putString("QuickbloxRoomJID", "0"/*object.getString("quickbloxRoomJID")*/);
        bundle.putString("PendingRoomJID", "1");

        //these bundle values are used for creating the ActivityDetails table
        bundle.putString("activityDetailID", activityDetailsID);
        bundle.putString("columnName", "latLong");
        bundle.putString("columnValue", latLong);
        /*if (blockedCalender.equalsIgnoreCase("0")) {
            bundle.putString("invitationStatus", "Yes");
        }*/

        String startTime = " ";
        String endTime = " ";
        if (dbObject.getScheduleFrom() != null && !dbObject.getScheduleFrom().equalsIgnoreCase("")) {
            startTime = dbObject.getScheduleFrom();
        } else {
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            startTime = simpleDateFormat1.toPattern();
        }
        if (dbObject.getScheduleTo() != null && !dbObject.getScheduleTo().equalsIgnoreCase("")) {
            endTime = dbObject.getScheduleTo();
        } else {
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            endTime = simpleDateFormat1.toPattern();
        }
        //these bundle values has been used for creating the ActivityDates table
        bundle.putString("activityDateID", activityDateID);
        bundle.putString("startTime", startTime);
        bundle.putString("endTime", endTime);
        bundle.putStringArrayList("phoneNumber", contactNoOnBlank);
        bundle.putStringArrayList("phoneHKUUID", contactHkUUIDOnBlank);

        if (groupInvitees.size() > 0) {
            bundle.putString("invitesToActivity", String.valueOf(groupInvitees.size()));
        }

        Log.i(TAG, "before passing to bundle " + groupInvitees.size());
        SharedPreferences sp = getSharedPreferences(Constants.APP_PREFERENCE_NAME, Activity.MODE_PRIVATE);
        authenticationKey = sp.getString(Constants.AUTH_KEY, "");
        hK_UUID = sp.getString(Constants.HK_UUID, "");

        Constants.GROUP_INVITEES_SIZE = groupInvitees.size();
        Util._db.createActivity(bundle, groupInvitees.size());
        Util._db.createActivityForChatIcon(bundle, groupInvitees.size(), "");
        if (UtilityHelper.checkConnectivity(AddEventActivity.this)) {
            Util._db.createActivityUsers(bundle);
        } else {
            Toast.makeText(AddEventActivity.this, "Sorry....!\nCan not add invitees offline.", Toast.LENGTH_SHORT).show();
        }
        Util._db.createActivityDetails(bundle);

        ArrayList<String> ownerDetails = Util._db.getOwnerDetails(uName);
        ActivityOwnerDetails activityOwnerDetails = new ActivityOwnerDetails();
        Bundle bundle1 = new Bundle();
        activityOwnerDetails.sethK_UUID(uName);
        activityOwnerDetails.setQuickBloxID(ownerDetails.get(3));
        activityOwnerDetails.setPhone(ownerDetails.get(4));
        bundle1.putParcelable("activityOwnerDetails", activityOwnerDetails);
        bundle1.putString("activityID", activityID);
        bundle1.putString("actionType", "ADD");
        bundle1.putString("dateModified", dfDate_day.format(createdDate));
        bundle1.putString("createdBy", uName);
        bundle1.putString("modifiedBy", uName);

        Util._db.createActivityOwner(bundle1);

        HashMap<String, String> activityDates = new HashMap<>();
        try {
            int noOfDays;
            activityDates.put("activityID", activityID);
            activityDates.put("ActivityDateID", UUID.randomUUID().toString());
            activityDates.put("actionType", "ADD");
            activityDates.put("createdBy", uName);
            activityDates.put("dateModified", dfDate_day.format(createdDate));
            activityDates.put("modifiedBy", uName);

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

        Log.i(TAG, "Date Param : " + startTime);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Util._db.setReminder(activityID, startTime, dbObject.getReminder());
        }

        // TODO update flag for applozic group creation YES/NO.
        Util._db.UpdateFlagDetailsActivity(activityID);
        createGroup(AddEventActivity.this);

        finish();
    }

    public void alertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddEventActivity.this);
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
    public void onTextLocationSelect(String address) {
        eventLocationET.setText(address);
        geoLocation = address;
        latLong = " ";
    }

    @Override
    public void onGeoLocationSelect(String name, String address, String lattitude, String longitude) {
        eventLocationET.setText(name);
        geoLocation = name + " , " + address;
        latLong = lattitude + "," + longitude;
        geoLocationSelected = true;
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
            AlertDialog.Builder dialog = new AlertDialog.Builder(AddEventActivity.this);
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (PLACE_PICKER_REQUEST): {
                if (resultCode == Activity.RESULT_OK) {
                    Place place = PlacePicker.getPlace(AddEventActivity.this, data);
                    CharSequence address = place.getAddress();
                    CharSequence name = place.getName();
                    PlaceAutocomplete.getStatus(AddEventActivity.this, data);
                    Log.i(TAG, "PlaceAutocomplete.getStatus(AddEventActivity.this, data) " + PlaceAutocomplete.getStatus(AddEventActivity.this, data));
                    LocationInterface locationInterface = AddEventActivity.this;
                    assert address != null;
                    locationInterface.onGeoLocationSelect(name.toString(), address.toString(), String.valueOf(place.getLatLng().latitude), String.valueOf(place.getLatLng().longitude));
                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(AddEventActivity.this, data);
                    if (status != null) {
                        Log.i(TAG, "PlaceAutocomplete.RESULT_ERROR = " + status.getStatusMessage());
                    }
                }
            }
            break;
            case (CONTACTS): {
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        if (data.getExtras() != null) {

                            contactNoOnBlank = new ArrayList<>();
                            contactHkUUIDOnBlank = new ArrayList<>();
                            contactHkIDOnBlank = new ArrayList<>();

                            if (data.getStringArrayListExtra(Constants.SELECTED_OBJECT_KEY) != null) {
                                selectedContactObjects = new ArrayList<>();
                                selectedContactObjects = data.getParcelableArrayListExtra(Constants.SELECTED_OBJECT_KEY);
                                Log.i(TAG, "selectedContactObjects size = " + selectedContactObjects.size());
                                if (selectedContactObjects.size() > 0) {
                                    for (int i = 0; i < selectedContactObjects.size(); i++) {
                                        SelectedContactObject selectedContactObject = selectedContactObjects.get(i);
                                        if (selectedContactObject.getHkUUID() != null && !selectedContactObject.getHkUUID().equalsIgnoreCase("")) {
                                            contactHkUUIDOnBlank.add(selectedContactObject.getHkUUID());
                                            contactNoOnBlank.add(selectedContactObject.getNumber());
                                        } else {
                                            contactHkUUIDOnBlank.add(null);
                                            contactNoOnBlank.add(selectedContactObject.getNumber());
                                        }
                                    }
                                }

                            }
                        }
                        contactNoOnBlank = data.getStringArrayListExtra(Constants.CONTACT_NO_OBJECT_KEY);
                        if (contactNoOnBlank != null && contactNoOnBlank.size() > 0) {
                            ArrayList<String> contactNameOnBlank = data.getStringArrayListExtra(Constants.CONTACT_NAME_OBJECT_KEY);
                            if (data.getStringArrayListExtra(Constants.CONTACT_HKID_OBJECT_KEY) != null) {
                                contactHkIDOnBlank = data.getStringArrayListExtra(Constants.CONTACT_HKID_OBJECT_KEY);
                            }
                            contactHkUUIDOnBlank = data.getStringArrayListExtra(Constants.CONTACT_HK_UUID_OBJECT_KEY);

                            if (selectedContactObjects != null && selectedContactObjects.size() != 0) {
                                for (int i = 0; i < contactNoOnBlank.size(); i++) {
                                    SelectedContactObject sb = new SelectedContactObject(Parcel.obtain());
                                    sb.setName(contactNameOnBlank.get(i));
                                    sb.setNumber(contactNoOnBlank.get(i));
                                    if (contactHkIDOnBlank.get(i) != null || !contactHkIDOnBlank.get(i).equals("")) {
                                        sb.setHkID(contactHkIDOnBlank.get(i));
                                    }
                                    sb.setHkUUID(contactHkUUIDOnBlank.get(i));
                                    selectedContactObjects.add(sb);
                                    ArrayList<SelectedContactObject> ltc2 = new ArrayList<>();// unique
                                    for (SelectedContactObject element : selectedContactObjects) {
                                        if (!ltc2.contains(element)) {
                                            System.out.println(element);
                                            ltc2.add(element);
                                        }
                                    }
                                }
                            } else {
                                for (int i = 0; i < contactNoOnBlank.size(); i++) {
                                    SelectedContactObject sb = new SelectedContactObject(Parcel.obtain());
                                    sb.setName(contactNameOnBlank.get(i));
                                    sb.setNumber(contactNoOnBlank.get(i));
                                    if (contactHkIDOnBlank.get(i) != null || !contactHkIDOnBlank.get(i).equals("")) {
                                        sb.setHkID(contactHkIDOnBlank.get(i));
                                        groupInvitees.add(contactHkIDOnBlank.get(i));
                                    }
                                    sb.setHkUUID(contactHkUUIDOnBlank.get(i));
                                    selectedContactObjects.add(sb);
                                    ArrayList<SelectedContactObject> ltc2 = new ArrayList<>();// unique
                                    for (SelectedContactObject element : selectedContactObjects) {
                                        if (!ltc2.contains(element)) {
                                            System.out.println(element);
                                            ltc2.add(element);
                                        }
                                    }
                                }
                            }

                            if (selectedContactObjects != null && selectedContactObjects.size() > 0) {
                                String toNoOfInvitees = String.valueOf(selectedContactObjects.size() + " " + getResources().getString(R.string.invitees_selected));
                                noOfInviteesTV.setText(toNoOfInvitees);
                            }
                        }
                    }
                    break;
                }
            }
            default:
                break;
        }
    }

    private SlideDateTimeListener startDateListener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date) {
            startDate = date;
            startDateET.setText(mFormatter.format(date));
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
            try {
                Date endDate = mFormatter.parse(endDateNew);
                Date startDate = mFormatter.parse(startDateNew);
                if (endDate.getTime() - (30 * ONE_MINUTE_IN_MILLIS) >= startDate.getTime()) {
                    endDateET.setText(mFormatter.format(date));
                } else {
                    endDateET.setText(mFormatter.format(startDate.getTime() + (30 * ONE_MINUTE_IN_MILLIS)));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onDateTimeCancel() {
        }
    };

    private void createGroup(Context context) {

        ArrayList<SelectedContactObject> nonHKUsers = Util._db.getNonHKUsers(activityID);
        if (nonHKUsers.size() > 0) {
            ArrayList<ServiceContactObject> serviceContactObjects = new ArrayList<>();
            for (SelectedContactObject selectedContactObject : nonHKUsers) {
                String phoneNumberWOPlusNine = selectedContactObject.getNumber().replace("+91", "");
                if (Util._db.getUser(selectedContactObject.getNumber()).contactNo.trim().length() > 1) {
                    serviceContactObjects.add(Util._db.getUser(selectedContactObject.getNumber()));
                } else if (Util._db.getContactName(phoneNumberWOPlusNine, AddEventActivity.this).contactNo.trim().length() > 1) {
                    serviceContactObjects.add(Util._db.getContactName(phoneNumberWOPlusNine, AddEventActivity.this));
                } else {
                    serviceContactObjects.add(Util._db.getContactName(selectedContactObject.getNumber(), AddEventActivity.this));
                }
            }
            VerifyNonHKUsers verifyUserAsync = new VerifyNonHKUsers(serviceContactObjects, context, authenticationKey, hK_UUID, activityID);
            verifyUserAsync.execute();
        }
        Util._db.updateFromDIB(activityID);
        Executor threadPoolExecutor = UtilityHelper.getExecutor();
        new AddActivityAsync(getApplicationContext()).executeOnExecutor(threadPoolExecutor);
    }
}