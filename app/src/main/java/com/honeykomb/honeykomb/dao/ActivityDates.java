package com.honeykomb.honeykomb.dao;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class ActivityDates implements Serializable, Parcelable {
    private String startDate;
    private String endDate;
    private String endTime;
    private String startTime;

    protected ActivityDates(Parcel in) {
        startDate = in.readString();
        endDate = in.readString();
        endTime = in.readString();
        startTime = in.readString();
    }

    public static final Creator<ActivityDates> CREATOR = new Creator<ActivityDates>() {
        @Override
        public ActivityDates createFromParcel(Parcel in) {
            return new ActivityDates(in);
        }

        @Override
        public ActivityDates[] newArray(int size) {
            return new ActivityDates[size];
        }
    };

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(startDate);
        parcel.writeString(endDate);
        parcel.writeString(endTime);
        parcel.writeString(startTime);
    }
}

