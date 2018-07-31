package com.honeykomb.honeykomb.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.honeykomb.honeykomb.R;
import com.honeykomb.honeykomb.adapters.NotificationsRCVAdapter;
import com.honeykomb.honeykomb.listeners.AppListeners;
import com.honeykomb.honeykomb.utils.Constants;
import com.honeykomb.honeykomb.utils.Util;

import java.util.ArrayList;

public class NotificationsActivity extends BaseActivity implements AppListeners.OnItemClickListener {
    private RecyclerView notificationsRCV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.notification_activity);
        notificationsRCV = findViewById(R.id.notifications_RCV);
        // set up tool bar views
        titleTV.setText(getResources().getString(R.string.notification));
        toolbarCalenderIMV.setVisibility(View.GONE);
        toolbarListViewIMV.setVisibility(View.GONE);
        toolbarAddIMV.setVisibility(View.GONE);
        saveEventTV.setVisibility(View.GONE);

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


        setNotificationsAdapter();
    }

    private void setNotificationsAdapter() {
        ArrayList<ArrayList<String>> notificationsList = Util._db.getNotificationData();
        NotificationsRCVAdapter notificationsRCVAdapter = new NotificationsRCVAdapter(NotificationsActivity.this, notificationsList);
        LinearLayoutManager llm = new LinearLayoutManager(NotificationsActivity.this);
        notificationsRCVAdapter.setClickListener(this);
        notificationsRCV.setLayoutManager(llm);
        notificationsRCV.setAdapter(notificationsRCVAdapter);
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
    protected void onResume() {
        super.onResume();
        IntentFilter iff = new IntentFilter(Constants.REFRESH_VIEW);
        LocalBroadcastManager.getInstance(NotificationsActivity.this).registerReceiver(onNotice, iff);
        ContentValues contentValues = new ContentValues();
        contentValues.put("isRead", 1);
        Util._db.updateVPF1(contentValues);
    }

    private BroadcastReceiver onNotice = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive Calendar");
            setNotificationsAdapter();
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(NotificationsActivity.this).unregisterReceiver(onNotice);
    }

    @Override
    public void onClick(Object object) {
        String activityID = (String) object;
        String activityOwner = Util._db.getActivityOwnerIDBasedOnActivityID(activityID);
        SharedPreferences sp = getSharedPreferences(Constants.APP_PREFERENCE_NAME, Activity.MODE_PRIVATE);
        String hK_UUID = sp.getString("hK_UUID", "");
        ArrayList<String> noteList = Util._db.getActivityDetailsFromNotification(activityID, activityOwner, hK_UUID);
        Intent intent = new Intent(NotificationsActivity.this, EventDetails.class);
        intent.putExtra("activityID", noteList.get(0));
        intent.putExtra("activityOwner", noteList.get(2));
        intent.putExtra("blockCalender", noteList.get(3));
        intent.putExtra("activityDateID", Util._db.getActivityDateIDBasedOnActivityID(noteList.get(0)));
        startActivity(intent);
    }
}
