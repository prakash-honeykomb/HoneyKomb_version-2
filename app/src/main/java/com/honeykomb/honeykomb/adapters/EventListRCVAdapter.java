package com.honeykomb.honeykomb.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.honeykomb.honeykomb.R;
import com.honeykomb.honeykomb.dao.ActivityDetails;
import com.honeykomb.honeykomb.listeners.AppListeners;
import com.honeykomb.honeykomb.utils.Util;
import com.honeykomb.honeykomb.utils.UtilityHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


public class EventListRCVAdapter extends RecyclerView.Adapter<EventListRCVAdapter.ViewHolder> {
    private Context context;
    private ArrayList<ActivityDetails> listOfEvent;
    private AppListeners.OnItemClickListener onItemClickListener;
    private String TAG = EventListRCVAdapter.class.getSimpleName();
    @SuppressLint("UseSparseArrays")
    private HashMap<Long, Integer> positionHeader = new HashMap<>();

    public EventListRCVAdapter(Context context, ArrayList<ActivityDetails> listOfEvent) {
        this.context = context;
        this.listOfEvent = listOfEvent;
    }

    public void add(ActivityDetails s, int position) {
        position = position == -1 ? getItemCount() : position;
        listOfEvent.add(position, s);
        notifyItemInserted(position);
    }

  /*  public void remove(int position) {
        if (position < getItemCount()) {
            listOfEvent.remove(position);
            notifyItemRemoved(position);
        }
    }*/

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTV, locationTV, timeTV, dateTV,timeAmPmTV/*, yearTV*/;
        private final RelativeLayout /*list_row_RL,*/ header;
        private  final LinearLayout list_row_RL;
        ViewHolder(View view) {
            super(view);
            header = view.findViewById(R.id.rl_agenda_header);
            list_row_RL = view.findViewById(R.id.event_list_RL);
            titleTV = view.findViewById(R.id.event_title_TV);
            locationTV = view.findViewById(R.id.location_TV);
            timeTV = view.findViewById(R.id.event_time_TV);
            timeAmPmTV=view.findViewById(R.id.event_time_AM_PM_TV);
            dateTV = view.findViewById(R.id.tv_date);
//            yearTV = view.findViewById(R.id.tv_date_year);
        }
    }


    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.event_row_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final ActivityDetails activityDetailsDao = listOfEvent.get(position);

        holder.locationTV.setTypeface(Util.setTextViewTypeFace(context, "DroidSans.ttf"));
        if(!activityDetailsDao.getAddress().trim().equalsIgnoreCase("")) {
            holder.locationTV.setVisibility(View.VISIBLE);
            holder.locationTV.setText(activityDetailsDao.getAddress());
        }else {
            holder.locationTV.setVisibility(View.GONE);
        }
        holder.timeTV.setText(activityDetailsDao.getStartTime().substring(0,5));
        holder.timeTV.setTypeface(Util.setTextViewTypeFace(context, "Roboto-Black.ttf"));

        holder.timeAmPmTV.setText(activityDetailsDao.getStartTime().substring(6,8));
        holder.timeAmPmTV.setTypeface(Util.setTextViewTypeFace(context, "DroidSans-Bold.ttf"));

        holder.titleTV.setText(activityDetailsDao.getActivityTittle());
        holder.titleTV.setTypeface(Util.setTextViewTypeFace(context, "FiraSans-SemiBold.otf"));
        holder.list_row_RL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null)
                    onItemClickListener.onClick(activityDetailsDao);
            }
        });

        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date activityDate = dateFormat.parse(activityDetailsDao.getStartDate());
            long noOfDays;
            Date date = new Date();

            String result = dateFormat.format(date);
            try {
                date = dateFormat.parse(result);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            noOfDays = UtilityHelper.daysBetweenDates1(activityDate, date);
            Log.e("noOfDays1 ", "========== " + noOfDays);

            if (noOfDays == 0 && (!positionHeader.containsKey(noOfDays) || positionHeader.get(noOfDays) == position)) {
                Log.e("noOfDays1 ", "====111====== " + noOfDays);
                positionHeader.put(noOfDays, position);
                holder.dateTV.setText(UtilityHelper.getUIDate(activityDetailsDao.getStartDate()));
                holder.dateTV.setTypeface(Util.setTextViewTypeFace(context, "FiraSans-SemiBold.otf"));

                holder.header.setVisibility(View.VISIBLE);
            } else if (noOfDays == 1 && (!positionHeader.containsKey(noOfDays) || positionHeader.get(noOfDays) == position)) {
                Log.e("noOfDays1 ", "====2222====== " + noOfDays);
                holder.dateTV.setText(UtilityHelper.getUIDate(activityDetailsDao.getStartDate()));
                holder.dateTV.setTypeface(Util.setTextViewTypeFace(context, "FiraSans-SemiBold.otf"));
//                holder.yearTV.setText(UtilityHelper.getUIDate(activityDetailsDao.getStartDate()));

                holder.header.setVisibility(View.VISIBLE);
                positionHeader.put(noOfDays, position);
            } else if (!positionHeader.containsKey(noOfDays) || positionHeader.get(noOfDays) == position) {
                Log.e("noOfDays1 ", "====3333====== " + noOfDays);
                holder.dateTV.setText(UtilityHelper.getUIDate(activityDetailsDao.getStartDate()));
                holder.dateTV.setTypeface(Util.setTextViewTypeFace(context, "FiraSans-SemiBold.otf"));
//                holder.yearTV.setText(UtilityHelper.getUIDate(activityDetailsDao.getStartDate()));
                holder.header.setVisibility(View.VISIBLE);
                positionHeader.put(noOfDays, position);
            } else {
                holder.header.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            Log.i(TAG, "date" + e);
        }
    }

    @Override
    public int getItemCount() {
        return listOfEvent.size();
    }

    public void setClickListener(AppListeners.OnItemClickListener itemClickListener) {
        this.onItemClickListener = itemClickListener;
    }
}