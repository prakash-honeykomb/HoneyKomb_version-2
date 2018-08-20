package com.honeykomb.honeykomb.dao;

import android.os.Parcel;
import android.os.Parcelable;

public class SelectedContactObject implements Parcelable {
    public static final Creator<SelectedContactObject> CREATOR = new Creator<SelectedContactObject>() {
        @Override
        public SelectedContactObject createFromParcel(Parcel in) {
            return new SelectedContactObject(in);
        }

        @Override
        public SelectedContactObject[] newArray(int size) {
            return new SelectedContactObject[size];
        }
    };
    private String contactName;
    private String contactNo;
    private String image;
    public String hkID;
    private String hkUUID;
    private String RSVPCount;
    private String deliveredTime;
    private boolean selected;
    private String invitationStatus;
    private String quickBloxID;
    private String userActivityStatus;


    public SelectedContactObject(Parcel in) {
        contactName = in.readString();
        contactNo = in.readString();
        image = in.readString();
        hkID = in.readString();
        hkUUID = in.readString();
        RSVPCount = in.readString();
        deliveredTime = in.readString();
        invitationStatus = in.readString();
        selected = in.readByte() != 0;
    }

    public String getUserActivityStatus() {
        return userActivityStatus;
    }

    public void setUserActivityStatus(String userActivityStatus) {
        this.userActivityStatus = userActivityStatus;
    }

    public String getQuickBloxID() {
        return quickBloxID;
    }

    public void setQuickBloxID(String quickBloxID) {
        this.quickBloxID = quickBloxID;
    }

    public String getInvitationStatus() {
        return invitationStatus;
    }

    public void setInvitationStatus(String invitationStatus) {
        this.invitationStatus = invitationStatus;
    }

    public String getDeliveredTime() {
        return deliveredTime;
    }

    public void setDeliveredTime(String deliveredTime) {
        this.deliveredTime = deliveredTime;
    }

    public String getRSVPCount() {
        return RSVPCount;
    }

    public void setRSVPCount(String RSVPCount) {
        this.RSVPCount = RSVPCount;
    }

    public String getName() {
        return contactName;
    }

    public void setName(String contactName) {
        this.contactName = contactName;
    }

    public String getNumber() {
        return contactNo;
    }

    public void setNumber(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getHkUUID() {
        return hkUUID;
    }

    public void setHkUUID(String hkUUID) {
        this.hkUUID = hkUUID;
    }

    public String getHkID() {
        return hkID;
    }

    public void setHkID(String hkID) {
        this.hkID = hkID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(contactName);
        parcel.writeString(contactNo);
        parcel.writeString(image);
        parcel.writeString(hkID);
        parcel.writeString(hkUUID);
        parcel.writeString(RSVPCount);
        parcel.writeString(deliveredTime);
        parcel.writeString(invitationStatus);
        parcel.writeByte((byte) (selected ? 1 : 0));
    }
}
