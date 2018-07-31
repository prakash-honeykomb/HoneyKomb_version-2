package com.honeykomb.honeykomb.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.honeykomb.honeykomb.R;
import com.honeykomb.honeykomb.async.AddActivityAsync;
import com.honeykomb.honeykomb.async.DeliverdAndReadStatusAsync;
import com.honeykomb.honeykomb.async.UpdateActivityUsersAsync;
import com.honeykomb.honeykomb.dao.RSVPCount;
import com.honeykomb.honeykomb.dao.SelectedContactObject;
import com.honeykomb.honeykomb.utils.Constants;
import com.honeykomb.honeykomb.utils.Util;
import com.honeykomb.honeykomb.utils.UtilityHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;

public class EventDetails extends BaseActivity {
    private String activityID;
    private String activityDateID;
    private String activityOwner;
    private String authKey;
    private String hK_UUID;
    private ArrayList<String> oldDetails = null;
    private ArrayList<String> details = new ArrayList<>();
    private String updatedMsg = "";
    private boolean isOwner = false;
    private TextView titleTV, locationTV, dateTV, monthTV, timeTV, reminderTV, descriptionValueTV, noOfInviteesTV, nameOfInviteesTV;
    private Button acceptEventBTN, declineEventBTN, cancelEventBTN, declineAcceptedEventBTN;
    private ImageView editEventIMV, backIMV, reminderViewExpandIMV;
    private boolean disableAll = false;
    private LinearLayout acceptOrDeclinePendingEventLL;
    private RelativeLayout reminderRL;
    private Animation slide_left, slide_right;
    private LinearLayout fiveTimeLL, tenTimeLL, fifteenTimeLL, thirtyTimeLL, oneHourTimeLL;
    private RelativeLayout inviteesRL;
    private static final int CONTACTS_FROM_LIST = 1121;
    // reminder views
    private TextView fiveTimeTV, tenTimeTV, fifteenTimeTV, thirtyTimeTV, oneHourTimeTV, fiveTimeCircleTV,
            tenTimeCircleTV, fifteenTimeCircleTV, thirtyTimeCircleTV, oneHourTimeCircleTV;
    private View fiveTimeVIEW, tenTimeVIEW, fifteenTimeVIEW, thirtyTimeVIEW, oneHourTimeVIEW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.event_details);
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        activityID = (String) bundle.get("activityID");
        activityDateID = (String) bundle.get("activityDateID");
        activityOwner = (String) bundle.get("activityOwner");

        if (bundle.get("disableAll") != null) {
            disableAll = bundle.getBoolean("disableAll");
            Log.e(TAG, "disableAll = " + disableAll);
        }

        SharedPreferences sp = getSharedPreferences(Constants.APP_PREFERENCE_NAME, Activity.MODE_PRIVATE);
        authKey = sp.getString(Constants.AUTH_KEY, "");
        hK_UUID = sp.getString(Constants.HK_UUID, "");

        new DeliverdAndReadStatusAsync(EventDetails.this, authKey, hK_UUID, activityID).execute();

        initCurrentActivityViews();
    }

    private void initCurrentActivityViews() {
        // set up tool bar views
        toolbar.setVisibility(View.GONE);
        titleTV = findViewById(R.id.event_title_TV);
        locationTV = findViewById(R.id.location_TV);
        dateTV = findViewById(R.id.date_value_TV);
        monthTV = findViewById(R.id.month_n_year_value_TV);
        timeTV = findViewById(R.id.time_value_TV);
        reminderTV = findViewById(R.id.reminder_TV);
        TextView descriptionTitleTV = findViewById(R.id.description_title_TV);
        descriptionValueTV = findViewById(R.id.description_value_TV);
        noOfInviteesTV = findViewById(R.id.no_of_invitees_TV);
        nameOfInviteesTV = findViewById(R.id.invitees_names_TV);
        backIMV = findViewById(R.id.event_back_arrow_IMV);
        editEventIMV = findViewById(R.id.edit_event_IMV);
        reminderRL = findViewById(R.id.reminder_RL);
        reminderViewExpandIMV = findViewById(R.id.reminder_view_expand_IMV);
        inviteesRL = findViewById(R.id.invitees_RL);

        // reminder lay out views
        fiveTimeLL = findViewById(R.id.five_time_LL);
        tenTimeLL = findViewById(R.id.ten_time_LL);
        fifteenTimeLL = findViewById(R.id.fifteen_time_LL);
        thirtyTimeLL = findViewById(R.id.thirty_time_LL);
        oneHourTimeLL = findViewById(R.id.one_hour_time_LL);

        // reminder text view and circle views
        fiveTimeTV = findViewById(R.id.five_time_TV);
        tenTimeTV = findViewById(R.id.ten_time_TV);
        fifteenTimeTV = findViewById(R.id.fifteen_time_TV);
        thirtyTimeTV = findViewById(R.id.thirty_time_TV);
        oneHourTimeTV = findViewById(R.id.one_hour_time_TV);

        fiveTimeCircleTV = findViewById(R.id.five_time_circle_TV);
        tenTimeCircleTV = findViewById(R.id.ten_time_circle_TV);
        fifteenTimeCircleTV = findViewById(R.id.fifteen_time_circle_TV);
        thirtyTimeCircleTV = findViewById(R.id.thirty_time_circle_TV);
        oneHourTimeCircleTV = findViewById(R.id.one_hour_time_circle_TV);

        fiveTimeVIEW = findViewById(R.id.five_time_VIEW);
        tenTimeVIEW = findViewById(R.id.ten_time_VIEW);
        fifteenTimeVIEW = findViewById(R.id.fifteen_time_VIEW);
        thirtyTimeVIEW = findViewById(R.id.thirty_time_VIEW);
        oneHourTimeVIEW = findViewById(R.id.one_hour_time_VIEW);

        titleTV.setTypeface(Util.setTextViewTypeFace(this, "FiraSans-Bold.otf"));
        locationTV.setTypeface(Util.setTextViewTypeFace(this, "DroidSans.ttf"));
        dateTV.setTypeface(Util.setTextViewTypeFace(this, "timesbd.ttf"));
        monthTV.setTypeface(Util.setTextViewTypeFace(this, "DroidSans.ttf"));
        timeTV.setTypeface(Util.setTextViewTypeFace(this, "DroidSans.ttf"));
        reminderTV.setTypeface(Util.setTextViewTypeFace(this, "DroidSans-Bold.ttf"));
        descriptionTitleTV.setTypeface(Util.setTextViewTypeFace(this, "DroidSans-Bold.ttf"));
        descriptionValueTV.setTypeface(Util.setTextViewTypeFace(this, "DroidSans.ttf"));
        noOfInviteesTV.setTypeface(Util.setTextViewTypeFace(this, "DroidSans-Bold.ttf"));
        nameOfInviteesTV.setTypeface(Util.setTextViewTypeFace(this, "DroidSans.ttf"));


        acceptOrDeclinePendingEventLL = findViewById(R.id.accept_or_decline_pending_event_LL); // as invitee when event is pending


        // buttons
        acceptEventBTN = findViewById(R.id.accept_event_BTN);
        declineEventBTN = findViewById(R.id.decline_event_BTN);
        cancelEventBTN = findViewById(R.id.cancel_BTN);
        declineAcceptedEventBTN = findViewById(R.id.decline_accepted_event_BTN);

        backIMV.setOnClickListener(this);
        editEventIMV.setOnClickListener(this);
        acceptEventBTN.setOnClickListener(this);
        declineEventBTN.setOnClickListener(this);
        cancelEventBTN.setOnClickListener(this);
        declineAcceptedEventBTN.setOnClickListener(this);
        reminderTV.setOnClickListener(this);
        reminderViewExpandIMV.setOnClickListener(this);
        fiveTimeLL.setOnClickListener(this);
        tenTimeLL.setOnClickListener(this);
        fifteenTimeLL.setOnClickListener(this);
        thirtyTimeLL.setOnClickListener(this);
        oneHourTimeLL.setOnClickListener(this);
        inviteesRL.setOnClickListener(this);
        locationTV.setOnClickListener(this);

        setAnimation();
        reminderRL.setAnimation(slide_right);
        updateUI();
    }

    private void updateUI() {
        details = Util._db.getActivityDetails(activityID, activityOwner, hK_UUID);
        if (details != null && details.size() > 0) {

            if (oldDetails == null)
                oldDetails = new ArrayList<>(details);

            if (hK_UUID.equalsIgnoreCase(details.get(2))) {
                Util._db.SetInvitationStatus(activityID, hK_UUID);
            }
            String tempReminder = details.get(16);
            oldDetails.set(16, "");
            details.set(16, "");
            if (oldDetails != null && !details.toString().equals(oldDetails.toString())) {
                if (updatedMsg.equalsIgnoreCase("participants ")) {
                    updatedMsg = "participants ";
                } else {
                    updatedMsg = "";
                }
                if (!oldDetails.get(1).equals(details.get(1)))
                    updatedMsg += "Title ";
                if (!oldDetails.get(12).equals(details.get(12)))
                    updatedMsg += "Notes ";
                if (!oldDetails.get(20).equals(details.get(20)) || !oldDetails.get(23).equals(details.get(23)))
                    updatedMsg += "Location ";
                if (!oldDetails.get(17).equals(details.get(17)))
                    updatedMsg += "StartDate ";
                if (!oldDetails.get(18).equals(details.get(18)))
                    updatedMsg += "EndDate ";
                if (!oldDetails.get(3).equals(details.get(3)))
                    updatedMsg += "TASK ";
                if (!oldDetails.get(16).equals(details.get(16)))
                    updatedMsg += "Reminder ";

                updatedMsg = updatedMsg.trim().replace(" ", ", ");
            }
            details.set(16, tempReminder);

            titleTV.setText(details.get(1)); //Title
            if (details.get(3).equals("1")) {
                String outputDateStr;
                String activityStatus = details.get(4);  //activeStatus
                RSVPCount rsvpCount = Util._db.getActivityRSVPCount(activityID, hK_UUID);
                isOwner = details.get(2).equals(hK_UUID);
                //Both
                reminderTV.setText(details.get(16).equals("") ? "30 MINUTES" : details.get(16));
                setReminderViewColor(details.get(16));
                try {
                    DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    DateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
                    Date date = inputFormat.parse(details.get(17));
                    outputDateStr = outputFormat.format(date);
                    dateTV.setText(outputDateStr.substring(0, 2));
                    monthTV.setText(outputDateStr.substring(3));
                } catch (Exception e) {
                    e.printStackTrace();
                    dateTV.setText("");
                    monthTV.setText("");
                }
                timeTV.setText(UtilityHelper.getTime(details.get(17)));
                if (details.get(12).trim().length() > 0) {
                    descriptionValueTV.setText(details.get(12));
                } else {
                    descriptionValueTV.setText("");
                }
                if (details.get(20).length() > 2) {
                    locationTV.setText(details.get(20));
                } else {
                    locationTV.setText("");
                }

                // total accepted count
                int totalAcceptedInvitationCount = rsvpCount.getYesCount();
                String noOfInv = String.valueOf(totalAcceptedInvitationCount) + getResources().getString(R.string.going);
                if (totalAcceptedInvitationCount > 999) {
                    noOfInviteesTV.setTextSize(12);
                    noOfInviteesTV.setText(noOfInv);
                } else if (totalAcceptedInvitationCount > 99) {
                    noOfInviteesTV.setTextSize(15);
                    noOfInviteesTV.setText(noOfInv);
                } else {
                    noOfInviteesTV.setText(noOfInv);
                }
                ArrayList<SelectedContactObject> acceptedUsers = Util._db.getEventAcceptedUsers(activityID);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < acceptedUsers.size(); i++) {
                    if (i == 0) {
                        nameOfInviteesTV.setText(sb.append(acceptedUsers.get(i).getName().trim()));
                    } else {
                        nameOfInviteesTV.setText(sb.append(", ").append(acceptedUsers.get(i).getName().trim()));
                    }
                }

                if (details.size() > 23 && details.get(23).length() > 2) {
//                    String[] latLong = details.get(23).split(",");
//                    String lattitudeFinal = latLong[0];
//                    String longitudeFinal = latLong[1];
                    locationTV.setText(details.get(20));
                } else if (details.get(20).trim().length() > 0) {
                    locationTV.setVisibility(View.VISIBLE);
                    locationTV.setText(details.get(20));
                } else {
                    locationTV.setVisibility(View.GONE);
                }

                //TODOwner
                if (isOwner) {
                    cancelEventBTN.setVisibility(View.VISIBLE);
                } else { /// Invitees //active
                    if (activityStatus.equals("Active")) {
                        if (details.get(11).equals("Yes")) {
                            declineAcceptedEventBTN.setVisibility(View.VISIBLE);
                        } else if (details.get(11).equals("Pending")) {
                            acceptOrDeclinePendingEventLL.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Log.e("Activity status ", "inactive and is not owner...");
                        cancelEventBTN.setVisibility(View.VISIBLE);
                        cancelEventBTN.setText(getResources().getString(R.string.canceled_event));
                    }
                }
                if (disableAll) {
                    cancelEventBTN.setOnClickListener(null);
                    reminderTV.setOnClickListener(null);
                    acceptEventBTN.setOnClickListener(null);
                    declineEventBTN.setOnClickListener(null);
                    noOfInviteesTV.setOnClickListener(null);
                    nameOfInviteesTV.setOnClickListener(null);
                    declineAcceptedEventBTN.setOnClickListener(null);
                }

                //Both Inactive
                if (!activityStatus.equals("Active")) {
                    cancelEventBTN.setText(getResources().getString(R.string.canceled_event));
                    editEventIMV.setOnClickListener(null);
                    reminderViewExpandIMV.setOnClickListener(null);
                    fiveTimeLL.setOnClickListener(null);
                    tenTimeLL.setOnClickListener(null);
                    fifteenTimeLL.setOnClickListener(null);
                    thirtyTimeLL.setOnClickListener(null);
                    oneHourTimeLL.setOnClickListener(null);
                    inviteesRL.setOnClickListener(null);
                    locationTV.setOnClickListener(null);
                    cancelEventBTN.setOnClickListener(null);
                    reminderTV.setOnClickListener(null);
                    acceptEventBTN.setOnClickListener(null);
                    declineEventBTN.setOnClickListener(null);
                    noOfInviteesTV.setOnClickListener(null);
                    nameOfInviteesTV.setOnClickListener(null);
                    declineAcceptedEventBTN.setOnClickListener(null);
                }
            } else {
                finish();
            }
        } else {
            finish();
        }

    }

    private void setReminderViewColor(String reminderTime) {
        if (reminderTime.equalsIgnoreCase("30 MINUTES") || reminderTime.equalsIgnoreCase("30 Min")) {

            // reminder time textView
            fiveTimeTV.setTextColor(getResources().getColor(R.color.black));
            tenTimeTV.setTextColor(getResources().getColor(R.color.black));
            fifteenTimeTV.setTextColor(getResources().getColor(R.color.black));
            thirtyTimeTV.setTextColor(getResources().getColor(R.color.button_border_color));
            oneHourTimeTV.setTextColor(getResources().getColor(R.color.black));

            // reminder view
            fiveTimeVIEW.setBackgroundColor(getResources().getColor(R.color.black));
            tenTimeVIEW.setBackgroundColor(getResources().getColor(R.color.black));
            fifteenTimeVIEW.setBackgroundColor(getResources().getColor(R.color.black));
            thirtyTimeVIEW.setBackgroundColor(getResources().getColor(R.color.button_border_color));
            oneHourTimeVIEW.setBackgroundColor(getResources().getColor(R.color.black));

            // reminder circle
            fiveTimeCircleTV.setBackground(getResources().getDrawable(R.drawable.small_circle_new));
            tenTimeCircleTV.setBackground(getResources().getDrawable(R.drawable.small_circle_new));
            fifteenTimeCircleTV.setBackground(getResources().getDrawable(R.drawable.small_circle_new));
            thirtyTimeCircleTV.setBackground(getResources().getDrawable(R.drawable.small_circle_selected));
            oneHourTimeCircleTV.setBackground(getResources().getDrawable(R.drawable.small_circle_new));

        } else if (reminderTime.equalsIgnoreCase("5 Min")) {
            // reminder time textView
            fiveTimeTV.setTextColor(getResources().getColor(R.color.button_border_color));
            tenTimeTV.setTextColor(getResources().getColor(R.color.black));
            fifteenTimeTV.setTextColor(getResources().getColor(R.color.black));
            thirtyTimeTV.setTextColor(getResources().getColor(R.color.black));
            oneHourTimeTV.setTextColor(getResources().getColor(R.color.black));

            // reminder view
            fiveTimeVIEW.setBackgroundColor(getResources().getColor(R.color.button_border_color));
            tenTimeVIEW.setBackgroundColor(getResources().getColor(R.color.black));
            fifteenTimeVIEW.setBackgroundColor(getResources().getColor(R.color.black));
            thirtyTimeVIEW.setBackgroundColor(getResources().getColor(R.color.black));
            oneHourTimeVIEW.setBackgroundColor(getResources().getColor(R.color.black));

            // reminder circle
            fiveTimeCircleTV.setBackground(getResources().getDrawable(R.drawable.small_circle_selected));
            tenTimeCircleTV.setBackground(getResources().getDrawable(R.drawable.small_circle_new));
            fifteenTimeCircleTV.setBackground(getResources().getDrawable(R.drawable.small_circle_new));
            thirtyTimeCircleTV.setBackground(getResources().getDrawable(R.drawable.small_circle_new));
            oneHourTimeCircleTV.setBackground(getResources().getDrawable(R.drawable.small_circle_new));


        } else if (reminderTime.equalsIgnoreCase("10 Min")) {
            // reminder time textView
            fiveTimeTV.setTextColor(getResources().getColor(R.color.black));
            tenTimeTV.setTextColor(getResources().getColor(R.color.button_border_color));
            fifteenTimeTV.setTextColor(getResources().getColor(R.color.black));
            thirtyTimeTV.setTextColor(getResources().getColor(R.color.black));
            oneHourTimeTV.setTextColor(getResources().getColor(R.color.black));

            // reminder view
            fiveTimeVIEW.setBackgroundColor(getResources().getColor(R.color.black));
            tenTimeVIEW.setBackgroundColor(getResources().getColor(R.color.button_border_color));
            fifteenTimeVIEW.setBackgroundColor(getResources().getColor(R.color.black));
            thirtyTimeVIEW.setBackgroundColor(getResources().getColor(R.color.black));
            oneHourTimeVIEW.setBackgroundColor(getResources().getColor(R.color.black));

            // reminder circle
            fiveTimeCircleTV.setBackground(getResources().getDrawable(R.drawable.small_circle_new));
            tenTimeCircleTV.setBackground(getResources().getDrawable(R.drawable.small_circle_selected));
            fifteenTimeCircleTV.setBackground(getResources().getDrawable(R.drawable.small_circle_new));
            thirtyTimeCircleTV.setBackground(getResources().getDrawable(R.drawable.small_circle_new));
            oneHourTimeCircleTV.setBackground(getResources().getDrawable(R.drawable.small_circle_new));


        } else if (reminderTime.equalsIgnoreCase("15 Min")) {
            // reminder time textView
            fiveTimeTV.setTextColor(getResources().getColor(R.color.black));
            tenTimeTV.setTextColor(getResources().getColor(R.color.black));
            fifteenTimeTV.setTextColor(getResources().getColor(R.color.button_border_color));
            thirtyTimeTV.setTextColor(getResources().getColor(R.color.black));
            oneHourTimeTV.setTextColor(getResources().getColor(R.color.black));

            // reminder view
            fiveTimeVIEW.setBackgroundColor(getResources().getColor(R.color.black));
            tenTimeVIEW.setBackgroundColor(getResources().getColor(R.color.black));
            fifteenTimeVIEW.setBackgroundColor(getResources().getColor(R.color.button_border_color));
            thirtyTimeVIEW.setBackgroundColor(getResources().getColor(R.color.black));
            oneHourTimeVIEW.setBackgroundColor(getResources().getColor(R.color.black));

            // reminder circle
            fiveTimeCircleTV.setBackground(getResources().getDrawable(R.drawable.small_circle_new));
            tenTimeCircleTV.setBackground(getResources().getDrawable(R.drawable.small_circle_new));
            fifteenTimeCircleTV.setBackground(getResources().getDrawable(R.drawable.small_circle_selected));
            thirtyTimeCircleTV.setBackground(getResources().getDrawable(R.drawable.small_circle_new));
            oneHourTimeCircleTV.setBackground(getResources().getDrawable(R.drawable.small_circle_new));


        } else if (reminderTime.equalsIgnoreCase("1 Hr")) {
            // reminder time textView
            fiveTimeTV.setTextColor(getResources().getColor(R.color.black));
            tenTimeTV.setTextColor(getResources().getColor(R.color.black));
            fifteenTimeTV.setTextColor(getResources().getColor(R.color.black));
            thirtyTimeTV.setTextColor(getResources().getColor(R.color.black));
            oneHourTimeTV.setTextColor(getResources().getColor(R.color.button_border_color));

            // reminder view
            fiveTimeVIEW.setBackgroundColor(getResources().getColor(R.color.black));
            tenTimeVIEW.setBackgroundColor(getResources().getColor(R.color.black));
            fifteenTimeVIEW.setBackgroundColor(getResources().getColor(R.color.black));
            thirtyTimeVIEW.setBackgroundColor(getResources().getColor(R.color.black));
            oneHourTimeVIEW.setBackgroundColor(getResources().getColor(R.color.button_border_color));

            // reminder circle
            fiveTimeCircleTV.setBackground(getResources().getDrawable(R.drawable.small_circle_new));
            tenTimeCircleTV.setBackground(getResources().getDrawable(R.drawable.small_circle_new));
            fifteenTimeCircleTV.setBackground(getResources().getDrawable(R.drawable.small_circle_new));
            thirtyTimeCircleTV.setBackground(getResources().getDrawable(R.drawable.small_circle_new));
            oneHourTimeCircleTV.setBackground(getResources().getDrawable(R.drawable.small_circle_selected));
        }
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
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(Constants.REFRESH_VIEW);
        LocalBroadcastManager.getInstance(EventDetails.this).registerReceiver(onNotice, intentFilter);
        updateUI();
    }

    @Override
    public String getValues() {
        return null;
    }

    private BroadcastReceiver onNotice = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive EventDetails");
            updateUI();
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(EventDetails.this).unregisterReceiver(onNotice);
    }

    @SuppressLint("NewApi")
    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == backIMV) {
            onBackPressed();
        } else if (v == editEventIMV) {
            Log.i(TAG, "editEventIMV");
            if (isOwner) {
                Intent intent = new Intent(EventDetails.this, EditEventDetails.class);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("eventDetails", details);
                intent.putExtra(Constants.ACTIVITY_ID, activityID);
                intent.putExtra(Constants.ACTIVITY_DATE_ID, activityDateID);
                intent.putExtra(Constants.ACTIVITY_OWNER, activityOwner);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        } else if (v == acceptEventBTN) {
            Log.i(TAG, "acceptEventBTN");
            updateActivityDataToDB("activityStatus", "Active");
            updateActivityDataToDB("active", "1");
            updateActivityDataToDB("invitationStatus", "Yes");
            updateActivityDataToDB("userActivityStatus", "Active");
            Util._db.updateActivityUserDataToDB("countRSVP", 1, activityID, hK_UUID);
            Util._db.udpateActivityUserDataToDB("invitationStatus", "Yes", activityID, hK_UUID);

            new UpdateActivityUsersAsync(getApplicationContext(), authKey, hK_UUID, activityID, "Yes", String.valueOf("1"), "No", "Yes").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            updateUI();
        } else if (v == declineEventBTN || v == declineAcceptedEventBTN) {
            Log.i(TAG, "declineEventBTN");
            new UpdateActivityUsersAsync(getApplicationContext(), authKey, hK_UUID, activityID, "No", String.valueOf(0), "No", "No").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            Util._db.deleteActivityFromDBEventDetails(activityID);
            finish();
        } else if (v == cancelEventBTN) {
            Log.i(TAG, "cancelEventBTN");
            updateActivityDataToDB("activityStatus", "inActive");
            updateActivityDataToDB("actionType", "Cancel");
            Executor threadPoolExecutor = UtilityHelper.getExecutor();
            new AddActivityAsync(EventDetails.this).executeOnExecutor(threadPoolExecutor);
            finish();
        } else if (v == reminderTV) {
            expandReminderView();
        } else if (v == reminderViewExpandIMV) {
            collapseReminderView();
        } else if (v == fiveTimeLL) {
            collapseReminderView();
            updateActivityDataToDB("reminder", getResources().getString(R.string.five_mins));
            Util._db.setReminderNew(activityID, details.get(17), details.get(16));
            updateUI();
        } else if (v == tenTimeLL) {
            collapseReminderView();
            updateActivityDataToDB("reminder", getResources().getString(R.string.ten_mins));
            Util._db.setReminderNew(activityID, details.get(17), details.get(16));
            updateUI();
        } else if (v == fifteenTimeLL) {
            collapseReminderView();
            updateActivityDataToDB("reminder", getResources().getString(R.string.fifteen_mins));
            Util._db.setReminderNew(activityID, details.get(17), details.get(16));
            updateUI();
        } else if (v == thirtyTimeLL) {
            collapseReminderView();
            updateActivityDataToDB("reminder", getResources().getString(R.string.thirty_mins));
            Util._db.setReminderNew(activityID, details.get(17), details.get(16));
            updateUI();
        } else if (v == oneHourTimeLL) {
            collapseReminderView();
            updateActivityDataToDB("reminder", getResources().getString(R.string.one_hour));
            Util._db.setReminderNew(activityID, details.get(17), details.get(16));
            updateUI();
        } else if (v == reminderRL) {
            collapseReminderView();
        } else if (v == inviteesRL) {
            Intent intent = new Intent(EventDetails.this, SelectedListOfInvitees.class);
            Bundle bundle = new Bundle();
            bundle.putString("activityID", activityID);
            bundle.putBoolean("isOwner", false);
            bundle.putString("hK_UUID", hK_UUID);
            bundle.putString("QuickbloxGroupID", " ");
            intent.putExtras(bundle);
            startActivityForResult(intent, CONTACTS_FROM_LIST);
        } else if (v == locationTV) {
            Intent intent = new Intent(EventDetails.this, EventLocationMapView.class);
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("eventDetails", details);
            intent.putExtras(bundle);
            startActivity(intent);

        }
    }

    private void setAnimation() {
        slide_left = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_left);

        slide_right = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_right);
    }

    private void collapseReminderView() {
        reminderRL.startAnimation(slide_right);
        final int SPLASH_DISPLAY_LENGTH = 550;
        reminderRL.setOnClickListener(null);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                reminderRL.setVisibility(View.GONE);
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    private void expandReminderView() {
        reminderRL.startAnimation(slide_left);
        final int SPLASH_DISPLAY_LENGTH = 550;
        reminderRL.setOnClickListener(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                reminderRL.setVisibility(View.VISIBLE);
            }
        }, SPLASH_DISPLAY_LENGTH);

    }

    private void updateActivityDataToDB(String key, String value)  //TODO Can be enhanced in future
    {
        String actionType;
        if (!details.get(24).equalsIgnoreCase("ADD"))
            actionType = "Modify";
        else
            actionType = "ADD";

        ContentValues values = new ContentValues();
        if (value.equals("Cancel"))
            actionType = "Cancel";
        if (isOwner)
            values.put("actionType", actionType);
        values.put(key, value);
        Util._db.updateActivityDataToDBEventDetails(values, activityID);
    }
}
