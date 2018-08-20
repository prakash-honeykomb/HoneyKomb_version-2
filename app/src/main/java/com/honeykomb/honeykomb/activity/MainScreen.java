package com.honeykomb.honeykomb.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.azurechen.fcalendar.data.CalendarAdapter;
import com.azurechen.fcalendar.data.Day;
import com.azurechen.fcalendar.widget.FlexibleCalendar;
import com.honeykomb.honeykomb.R;
import com.honeykomb.honeykomb.adapters.EventListRCVAdapter;
import com.honeykomb.honeykomb.adapters.EventRCVAdapter;
import com.honeykomb.honeykomb.adapters.PendingEventRCVAdapter;
import com.honeykomb.honeykomb.dao.ActivityDetails;
import com.honeykomb.honeykomb.listeners.AppListeners;
import com.honeykomb.honeykomb.listeners.SwipeDetector;
import com.honeykomb.honeykomb.utils.Constants;
import com.honeykomb.honeykomb.utils.Util;

import org.joda.time.LocalDate;

import java.util.ArrayList;


public class MainScreen extends BaseActivity implements AppListeners.OnItemClickListener, AppListeners.RefreshMainScreen {

    private ImageView /*plusIMV, minusIMV,*/ arrowIMV;
    private RecyclerView eventRCV, pendingEventsRCV;
    private FlexibleCalendar viewCalendar;
    private String selectedDate = "";
    private LinearLayout calendarLL, eventListLL, noEventLL;
    private RelativeLayout pendingEventsCountRL;
    private TextView pendingInvitationCountTV, pendingInvitationTitleTV;
    private String hK_UUID;
    //    private boolean isCompleted = false;
//    private FrameLayout pendingInvitationsFL;
    private int pendingInvCount;
    private EventRCVAdapter eventRCVAdapter;
    private EventListRCVAdapter eventListRCVAdapter;
    private String TAG = MainScreen.class.getSimpleName();
    private Animation slide_down, slide_up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.main_screen);
        SharedPreferences sp = getSharedPreferences(Constants.APP_PREFERENCE_NAME, Activity.MODE_PRIVATE);
        hK_UUID = sp.getString("hK_UUID", "");
        initCurrentCalssViews();

    }

    private void initCurrentCalssViews() {
        setBatchCount(Util._db.getNotificationUnReadCount());
        eventRCV = findViewById(R.id.events_list_LV);
//        plusIMV = findViewById(R.id.plus_BTN);
//        minusIMV = findViewById(R.id.minus_BTN);
        calendarLL = findViewById(R.id.calendar_LL);
        pendingEventsCountRL = findViewById(R.id.pending_events_count_LL);
        pendingInvitationCountTV = findViewById(R.id.pending_TV);
        pendingInvitationTitleTV = findViewById(R.id.pending_invitation_title_TV);
        pendingEventsRCV = findViewById(R.id.pending_events_RCV);
        eventListLL = findViewById(R.id.event_list_LL);
        noEventLL = findViewById(R.id.no_event_LL);
        calendarLL.setVisibility(View.GONE);
        arrowIMV = findViewById(R.id.arrow_IMV);

//        plusIMV.setOnClickListener(this);
//        minusIMV.setOnClickListener(this);
        toolbarCalenderIMV.setOnClickListener(this);
        toolbarListViewIMV.setOnClickListener(this);
        pendingEventsCountRL.setOnClickListener(this);

        toolbarCalenderIMV.setVisibility(View.VISIBLE);
        toolbarListViewIMV.setVisibility(View.GONE);

        viewCalendar = findViewById(R.id.calendar);
        pendingInvitationTitleTV.setTypeface(Util.setTextViewTypeFace(MainScreen.this, "DroidSans.ttf"));

        setListAdapterAllEvents();
        setAnimation();
        pendingEventsCountRL.setAnimation(slide_down);
    }

    private void setBatchCount(int notificationUnReadCount) {
        if (toolbar != null) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setHomeAsUpIndicator(Util.setBadgeCount(this, R.mipmap.menu, notificationUnReadCount));
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setCalendarView() {

        calendarLL.setVisibility(View.VISIBLE);
//        eventListLL.setVisibility(View.GONE);
        java.util.Calendar cal = java.util.Calendar.getInstance();
        CalendarAdapter adapter = new CalendarAdapter(MainScreen.this, cal);
        viewCalendar.setAdapter(adapter);
        viewCalendar.setTextColor(getResources().getColor(R.color.eventtextblack));

        selectedDate = LocalDate.now().toString();
        setCalendarAdapter(selectedDate);

        new SwipeDetector(calendarLL).setOnSwipeListener(new SwipeDetector.onSwipeEvent() {
            @Override
            public void SwipeEventDetected(View v, SwipeDetector.SwipeTypeEnum swipeType) {
                if (swipeType == SwipeDetector.SwipeTypeEnum.TOP_TO_BOTTOM) {
                    viewCalendar.expand(500);
                } else if (swipeType == SwipeDetector.SwipeTypeEnum.BOTTOM_TO_TOP) {
                    viewCalendar.collapse(500);
                }
            }
        });

        viewCalendar.setCalendarListener(new FlexibleCalendar.CalendarListener() {
            @Override
            public void onDaySelect() {
                selectedDate = getSelectedDate();
                Log.i(TAG, "selectedDate = " + selectedDate);
                setCalendarAdapter(selectedDate);
            }

            @Override
            public void onItemClick(View v) {
                Log.i(TAG, "The Day of Clicked View: onItemClick");
            }

            @Override
            public void onDataUpdate() {
                Log.i(TAG, "The Day of Clicked View: onDataUpdate");
            }

            @Override
            public void onMonthChange() {
                Log.i(TAG, "The Day of Clicked View: onMonthChange");
            }

            @Override
            public void onWeekChange(int position) {
                Log.i(TAG, "The Day of Clicked View: onWeekChange");
            }

        });
    }

    private void setCalendarAdapter(String selectedDate) {
        if (selectedDate != null) {
            Log.i(TAG, "selectedDate VALUE before clicking date = " + selectedDate);
            ArrayList<ActivityDetails> listOfEvent = Util._db.getEventForCalendar(hK_UUID, selectedDate);
            Log.i(TAG, "listOfEvent size in CALENDAR = " + listOfEvent.size());
            if (listOfEvent.size() > 0) {
                eventListLL.setVisibility(View.VISIBLE);
                noEventLL.setVisibility(View.GONE);
                eventRCV.setHasFixedSize(true);
                eventRCVAdapter = new EventRCVAdapter(MainScreen.this, listOfEvent);
                eventRCV.setLayoutManager(new LinearLayoutManager(MainScreen.this));
                eventRCVAdapter.setClickListener(this);
                eventRCV.setAdapter(eventRCVAdapter);
            } else {
                eventListLL.setVisibility(View.GONE);
                noEventLL.setVisibility(View.VISIBLE);
            }
        }
    }

    private String getSelectedDate() {
        Day day = viewCalendar.getSelectedDay();
        if (day.getDay() < 10 && day.getMonth() + 1 < 10) {
            return day.getYear() + "-" + "0" + (day.getMonth() + 1) + "-" + "0" + day.getDay();
        } else if (day.getDay() <= 9 && day.getMonth() + 1 >= 10) {
            return day.getYear() + "-" + (day.getMonth() + 1) + "-" + "0" + day.getDay();
        } else if (day.getDay() >= 10 && day.getMonth() + 1 <= 9) {
            return day.getYear() + "-" + "0" + (day.getMonth() + 1) + "-" + day.getDay();
        } else {
            return day.getYear() + "-" + (day.getMonth() + 1) + "-" + day.getDay();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setBatchCount(Util._db.getNotificationUnReadCount());
        IntentFilter iff = new IntentFilter(Constants.REFRESH_VIEW);
        LocalBroadcastManager.getInstance(MainScreen.this).registerReceiver(onNotice, iff);
        if (toolbarCalenderIMV.getVisibility() == View.VISIBLE) {
            setListAdapterAllEvents();
        } else if (toolbarListViewIMV.getVisibility() == View.VISIBLE) {
            setCalendarView();
        }
    }

    private void setListAdapterAllEvents() {

        calendarLL.setVisibility(View.GONE);
        noEventLL.setVisibility(View.GONE);
        eventListLL.setVisibility(View.VISIBLE);

        ArrayList<ActivityDetails> listOfEvent = Util._db.getShortEventDataList("");
        Log.i(TAG, "listOfEvent size in CALENDAR = " + listOfEvent.size());

        eventRCV.setHasFixedSize(true);
        eventListRCVAdapter = new EventListRCVAdapter(MainScreen.this, listOfEvent);
        eventListRCVAdapter.setClickListener(this);
        eventRCV.setLayoutManager(new LinearLayoutManager(MainScreen.this));
        eventRCV.setAdapter(eventListRCVAdapter);

        pendingInvCount = Util._db.getPendingInvitationsCount(hK_UUID);
        pendingInvitationCountTV.setText(String.valueOf(pendingInvCount));

    }

    @Override
    public void onClick(View view) {
        if (view == toolbarCalenderIMV) {
            toolbarListViewIMV.setVisibility(View.VISIBLE);
            toolbarCalenderIMV.setVisibility(View.GONE);
            setCalendarView();
        } else if (view == toolbarListViewIMV) {
            toolbarListViewIMV.setVisibility(View.GONE);
            toolbarCalenderIMV.setVisibility(View.VISIBLE);
            setListAdapterAllEvents();
        } else if (view == pendingEventsCountRL) {
            if (pendingInvCount > 0) {
                pendingEventsCountRL.setBackgroundColor(getResources().getColor(R.color.transparent));
                pendingInvitationTitleTV.setVisibility(View.GONE);
                pendingInvitationCountTV.setVisibility(View.GONE);
                arrowIMV.setVisibility(View.GONE);
                setPendingEventsRCVAdapter();
                pendingEventsCountRL.startAnimation(slide_up);
            }
        } else if (view == toolbarAddIMV) {
            Intent intent = new Intent(MainScreen.this, AddEventActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            overridePendingTransition(0, 0);
            startActivity(intent);
        } else {
            super.onClick(view);
        }
    }

    private void setPendingEventsRCVAdapterToNull() {
        pendingEventsRCV.setLayoutManager(null);
    }

    private void setPendingEventsRCVAdapter() {
        String hk_uuid = Util._db.getUserName();
        ArrayList<ActivityDetails> mPIList = Util._db.getPendingInvitationsRCVList(hk_uuid);
        PendingEventRCVAdapter pendingEventRCVAdapter = new PendingEventRCVAdapter(MainScreen.this, mPIList);
        pendingEventRCVAdapter.setClickListener(this);
        pendingEventRCVAdapter.setRefreshViews(this);
        pendingEventsRCV.setLayoutManager(new LinearLayoutManager(MainScreen.this, LinearLayoutManager.HORIZONTAL, false));
        pendingEventsRCV.setAdapter(pendingEventRCVAdapter);
    }

    private void setAnimation() {
        slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down);

        slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up);
    }

    private BroadcastReceiver onNotice = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive Calendar");
            setBatchCount(Util._db.getNotificationUnReadCount());
            if (toolbarCalenderIMV.getVisibility() == View.VISIBLE) {
                setListAdapterAllEvents();
            } else {
                setCalendarView();
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(MainScreen.this).unregisterReceiver(onNotice);
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
    public void setContentLayout(int layout) {
        v = inflater.inflate(layout, null);
        lnr_content.addView(v);
    }

    @Override
    public void onClick(Object contactObject) {
        collapsePendingView();
        launchEventDetails((ActivityDetails) contactObject);
    }

    private void launchEventDetails(ActivityDetails activityDetails) {
        Intent intent = new Intent(MainScreen.this, EventDetails.class);
        intent.putExtra("activityID", activityDetails.getActivityID());
        intent.putExtra("activityDateID", activityDetails.getActivityDateID());
        /*if (isCompleted) {
            intent.putExtra("isCompleted", true);
        }*/
        intent.putExtra("activityOwner", activityDetails.getActivityOwner());
        intent.putExtra("startDate", activityDetails.getStartDate());
        intent.putExtra("endDate", activityDetails.getEndDate());
        intent.putExtra("blockCalendar", true);
        intent.putExtra("activityStatus", true);
        if (activityDetails.getActivityStatus() != null && activityDetails.getActivityStatus().equalsIgnoreCase("Completed")) {
            intent.putExtra("disableAll", true);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    public void setViews() {

        setPendingEventsRCVAdapterToNull();
        pendingEventsCountRL.setBackgroundColor(getResources().getColor(R.color.white));
        pendingInvitationTitleTV.setVisibility(View.VISIBLE);
        pendingInvitationCountTV.setVisibility(View.VISIBLE);
        arrowIMV.setImageDrawable(getResources().getDrawable(R.mipmap.indicator_arrow_up));
        pendingInvCount = Util._db.getPendingInvitationsCount(hK_UUID);
        pendingInvitationCountTV.setText(String.valueOf(pendingInvCount));

        if (toolbarCalenderIMV.getVisibility() == View.VISIBLE) {
            setListAdapterAllEvents();
        } else {
            setCalendarView();
        }
    }

    @Override
    public void onRefresh(Object object) {
        String result = (String) object;
        if (result.equalsIgnoreCase("collapseView")) {
            collapsePendingView();
        } else {
            setViews();
        }
    }

    private void collapsePendingView() {
        pendingEventsCountRL.startAnimation(slide_down);
        final int SPLASH_DISPLAY_LENGTH = 550;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setPendingEventsRCVAdapterToNull();
                pendingEventsCountRL.setBackgroundColor(getResources().getColor(R.color.white));
                pendingInvitationTitleTV.setVisibility(View.VISIBLE);
                pendingInvitationCountTV.setVisibility(View.VISIBLE);
                arrowIMV.setVisibility(View.VISIBLE);
                pendingInvCount = Util._db.getPendingInvitationsCount(hK_UUID);
                pendingInvitationCountTV.setText(String.valueOf(pendingInvCount));
            }
        }, SPLASH_DISPLAY_LENGTH);

    }
}
