package com.honeykomb.honeykomb.dao;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

@SuppressLint("ParcelCreator")
public class HKUsers implements Serializable, Parcelable {
    private String phoneNumber = "";
    private String invitationStatus = "";
    private String userActivityStatus = "";
    private String hK_UUID = "";
    private String countRSVP = "0";
    private String quickBloxID = "";
    private String deliveredTime = "";
    private String displayName = "";

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDeliveredTime() {
        return deliveredTime;
    }

    public void setDeliveredTime(String deliveredTime) {
        this.deliveredTime = deliveredTime;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getInvitationStatus() {
        return invitationStatus;
    }

    public void setInvitationStatus(String invitationStatus) {
        this.invitationStatus = invitationStatus;
    }

    public String getUserActivityStatus() {
        return userActivityStatus;
    }

    public void setUserActivityStatus(String userActivityStatus) {
        this.userActivityStatus = userActivityStatus;
    }

    public String gethK_UUID() {
        return hK_UUID;
    }

    public void sethK_UUID(String hK_UUID) {
        this.hK_UUID = hK_UUID;
    }

    public String getCountRSVP() {
        return countRSVP;
    }

    public void setCountRSVP(String countRSVP) {
        this.countRSVP = countRSVP;
    }

    public String getQuickBloxID() {
        return quickBloxID;
    }

    public void setQuickBloxID(String quickBloxID) {
        this.quickBloxID = quickBloxID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
