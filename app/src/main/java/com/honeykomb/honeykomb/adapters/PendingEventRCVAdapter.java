package com.honeykomb.honeykomb.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.honeykomb.honeykomb.R;
import com.honeykomb.honeykomb.async.UpdateActivityUsersAsync;
import com.honeykomb.honeykomb.dao.ActivityDetails;
import com.honeykomb.honeykomb.listeners.AppListeners;
import com.honeykomb.honeykomb.utils.Constants;
import com.honeykomb.honeykomb.utils.Util;

import java.util.ArrayList;

public class PendingEventRCVAdapter extends RecyclerView.Adapter<PendingEventRCVAdapter.ViewHolder> {
    private Context context;
    private ArrayList<ActivityDetails> listOfEvent;
    private AppListeners.OnItemClickListener onItemClickListener;
    private AppListeners.RefreshMainScreen refreshMainScreen;
    private String TAG = PendingEventRCVAdapter.class.getSimpleName();
    private String authenticationKey;
    private String hK_UUID;

    @SuppressLint("UseSparseArrays")

    public PendingEventRCVAdapter(Context context, ArrayList<ActivityDetails> listOfEvent) {
        this.context = context;
        this.listOfEvent = listOfEvent;
        SharedPreferences sp = context.getSharedPreferences(Constants.APP_PREFERENCE_NAME, Activity.MODE_PRIVATE);
        this.authenticationKey = sp.getString(Constants.AUTH_KEY, "");
        this.hK_UUID = sp.getString(Constants.HK_UUID, "");
    }


    private void remove(int position) {
        if (position < getItemCount()) {
            listOfEvent.remove(position);
            notifyItemRemoved(position);
            notifyDataSetChanged();
            if (refreshMainScreen != null) {
                refreshMainScreen.onRefresh("refresh");
            }
        }
    }

    public void add(ActivityDetails s, int position) {
        position = position == -1 ? getItemCount() : position;
        listOfEvent.add(position, s);
        notifyItemInserted(position);
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView titleTV, locationTV, startDateTV, startTimeTV;
        private final RelativeLayout list_row_RL;
        private final Button acceptBTN, declineBTN;
        private final LinearLayout closeLL;

        ViewHolder(View view) {
            super(view);
            list_row_RL = view.findViewById(R.id.pending_inv_RL);
            titleTV = view.findViewById(R.id.event_title_TV);
            locationTV = view.findViewById(R.id.location_TV);
            startDateTV = view.findViewById(R.id.start_date_TV);
            startTimeTV = view.findViewById(R.id.start_time_TV);
            acceptBTN = view.findViewById(R.id.accept_BTN);
            declineBTN = view.findViewById(R.id.decline_BTN);
            closeLL = view.findViewById(R.id.close_LL);
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick " + " " + listOfEvent.get(getPosition()));
        }
    }


    @NonNull
    public PendingEventRCVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.pending_event_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PendingEventRCVAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final ActivityDetails activityDetailsDao = listOfEvent.get(position);
        final String activityID = activityDetailsDao.getActivityID();
        holder.locationTV.setTypeface(Util.setTextViewTypeFace(context, "DroidSans.ttf"));
        if (!activityDetailsDao.getAddress().trim().equalsIgnoreCase("")) {
            holder.locationTV.setVisibility(View.VISIBLE);
            holder.locationTV.setText(activityDetailsDao.getAddress());
        } else {
            holder.locationTV.setVisibility(View.GONE);
        }

        holder.startDateTV.setText(activityDetailsDao.getStartDate());
        holder.startTimeTV.setText(activityDetailsDao.getStartTime());
        holder.titleTV.setText(activityDetailsDao.getActivityTittle());

        holder.startDateTV.setTypeface(Util.setTextViewTypeFace(context, "DroidSans-Bold.ttf"));
        holder.startTimeTV.setTypeface(Util.setTextViewTypeFace(context, "DroidSans-Bold.ttf"));
        holder.titleTV.setTypeface(Util.setTextViewTypeFace(context, "FiraSans-SemiBold.otf"));
        holder.list_row_RL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null)
                    onItemClickListener.onClick(activityDetailsDao);
            }
        });
        holder.closeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (refreshMainScreen != null) {
                    refreshMainScreen.onRefresh("collapseView");
                }
            }
        });

        holder.acceptBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateActivityDataToDB("activityStatus", "Active", activityID);
                updateActivityDataToDB("active", "1", activityID);
                updateActivityDataToDB("invitationStatus", "Yes", activityID);
                updateActivityDataToDB("userActivityStatus", "Active", activityID);
                Util._db.updateActivityUserDataToDB("countRSVP", 1, activityID, hK_UUID);
                Util._db.udpateActivityUserDataToDB("invitationStatus", "Yes", activityID, hK_UUID);
                new UpdateActivityUsersAsync(context, authenticationKey, hK_UUID, activityID, "Yes", String.valueOf(1), "No", "Yes").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                remove(position);
            }
        });
        holder.declineBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UpdateActivityUsersAsync(context, authenticationKey, hK_UUID, activityID, "No", String.valueOf(0), "No", "No").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                Util._db.deleteActivityFromDBEventDetails(activityID);
                remove(position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return listOfEvent.size();
    }

    public void setClickListener(AppListeners.OnItemClickListener itemClickListener) {
        this.onItemClickListener = itemClickListener;
    }

    public void setRefreshViews(AppListeners.RefreshMainScreen refreshViews) {
        this.refreshMainScreen = refreshViews;
    }

    private void updateActivityDataToDB(String key, String value, String activityID)  //TODO Can be enhanced in future
    {
        ContentValues values = new ContentValues();
        values.put(key, value);
        Util._db.updateActivityDataToDBEventDetails(values, activityID);
    }
}
