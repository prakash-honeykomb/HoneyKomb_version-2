package com.honeykomb.honeykomb.dao;

import android.os.Parcel;
import android.os.Parcelable;

public class ContactObject implements Parcelable {
    public static final Creator<ContactObject> CREATOR = new Creator<ContactObject>() {
        @Override
        public ContactObject createFromParcel(Parcel in) {
            return new ContactObject(in);
        }

        @Override
        public ContactObject[] newArray(int size) {
            return new ContactObject[size];
        }
    };
    private String contactName;
    private String contactNo;
    private String image;
    private String hkID;
    private String hkUUID;
    private String invitationStatus;
    private boolean selected;
    private String QBUserID;
    private String quickBlockID;
    private String userStatus;

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public ContactObject() {
        contactName = "";
        contactNo = "";
        image = "";
        hkID = "";
        hkUUID = "";
        selected = false;
        invitationStatus = "";
        quickBlockID = "";
        QBUserID = "";
        userStatus = "";
    }

    public ContactObject(Parcel in) {
        contactName = in.readString();
        contactNo = in.readString();
        image = in.readString();
        hkID = in.readString();
        hkUUID = in.readString();
        selected = in.readByte() != 0;
        invitationStatus = in.readString();
        QBUserID = in.readString();
    }

    public String getQuickBlockID() {
        return quickBlockID;
    }

    public void setQuickBlockID(String quickBlockID) {
        this.quickBlockID = quickBlockID;
    }

    public String getInvitationStatus() {
        return invitationStatus;
    }

    public void setInvitationStatus(String invitationStatus) {
        this.invitationStatus = invitationStatus;
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

    public String getQBUserID() {
        return QBUserID;
    }

    public void setQBUserID(String QBUserID) {
        this.QBUserID = QBUserID;
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
        parcel.writeByte((byte) (selected ? 1 : 0));
        parcel.writeString(QBUserID);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null) {
            return this.getNumber().equalsIgnoreCase(((ContactObject) obj).getNumber());
        } else {
            return false;
        }
    }
}

