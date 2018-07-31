package com.honeykomb.honeykomb.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.honeykomb.honeykomb.R;
import com.honeykomb.honeykomb.listeners.AppListeners;
import com.honeykomb.honeykomb.utils.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

public class NotificationsRCVAdapter extends RecyclerView.Adapter<NotificationsRCVAdapter.ViewHolder> {
    private Context context;
    private ArrayList<ArrayList<String>> notificationsList;
    private AppListeners.OnItemClickListener onItemClickListener;
    private String TAG = NotificationsRCVAdapter.class.getSimpleName();
    @SuppressLint("UseSparseArrays")
    private HashMap<Long, Integer> positionHeader = new HashMap<>();

    public NotificationsRCVAdapter(Context context, ArrayList<ArrayList<String>> listOfEvent) {
        this.context = context;
        this.notificationsList = listOfEvent;
    }

    public void add(ArrayList<String> s, int position) {
        position = position == -1 ? getItemCount() : position;
        notificationsList.add(position, s);
        notifyItemInserted(position);
    }

  /*  public void remove(int position) {
        if (position < getItemCount()) {
            notificationsList.remove(position);
            notifyItemRemoved(position);
        }
    }*/

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView ownerTV, actionTypeTV;
        private RelativeTimeTextView timeTV;
        private final RelativeLayout notificationLL;

        ViewHolder(View view) {
            super(view);
            notificationLL = view.findViewById(R.id.notification_LL);
            ownerTV = view.findViewById(R.id.tv_nf_ownerName);
            actionTypeTV = view.findViewById(R.id.action_type_TV);
            timeTV = view.findViewById(R.id.tv_notification_hours);
        }
    }


    @NonNull
    public NotificationsRCVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.notification_row, parent, false);
        return new NotificationsRCVAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsRCVAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final ArrayList<String> notification = notificationsList.get(position);
        String userName = Util._db.getUserName(notification.get(2));
        String notificationOwner;
        if (userName != null && userName.trim().length() > 0) {
            notificationOwner = userName;
        } else {
            notificationOwner = notification.get(3);
        }
        holder.ownerTV.setTypeface(Util.setTextViewTypeFace(context, "DroidSans-Bold.ttf"));
        holder.timeTV.setTypeface(Util.setTextViewTypeFace(context, "DroidSans.ttf"));
        holder.actionTypeTV.setTypeface(Util.setTextViewTypeFace(context, "DroidSans.ttf"));

        holder.ownerTV.setText(notificationOwner);
        holder.actionTypeTV.setText(notification.get(4) + " " + (Html.fromHtml(" " + "&ldquo;" + notification.get(5) + "&rdquo;")));

        try {
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date parsed = sf.parse(notification.get(7));
            TimeZone tz = TimeZone.getDefault();
            sf.setTimeZone(tz);
            String result = sf.format(parsed);
            Date dateOne = sf.parse(result);
            holder.timeTV.setReferenceTime(dateOne.getTime());
        } catch (Exception e) {
        }

        holder.notificationLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null)
                    onItemClickListener.onClick(notification.get(6));
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationsList.size();
    }

    public void setClickListener(AppListeners.OnItemClickListener itemClickListener) {
        this.onItemClickListener = itemClickListener;
    }
}

