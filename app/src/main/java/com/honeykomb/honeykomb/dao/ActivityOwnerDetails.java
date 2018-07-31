package com.honeykomb.honeykomb.dao;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

@SuppressLint("ParcelCreator")
public class ActivityOwnerDetails implements Serializable, Parcelable {
    private String hK_UUID;
    private String hKID;
    private String photo;
    private String displayName;
    private String quickBloxID;
    private String phone;
    private String active;

    public String gethK_UUID() {
        return hK_UUID;
    }

    public void sethK_UUID(String hK_UUID) {
        this.hK_UUID = hK_UUID;
    }

    public String gethKID() {
        return hKID;
    }

    public void sethKID(String hKID) {
        this.hKID = hKID;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getQuickBloxID() {
        return quickBloxID;
    }

    public void setQuickBloxID(String quickBloxID) {
        this.quickBloxID = quickBloxID;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
