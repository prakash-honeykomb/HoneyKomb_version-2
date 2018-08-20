package com.honeykomb.honeykomb.dao;

import android.os.Parcel;
import android.os.Parcelable;

public class ContactsObject implements Parcelable {
    public String contactName = "";
    public String contactNo = "";
    public String imagePath = "";
    public String hkID = "";
    public String hkUUID = "";

    public ContactsObject(String contactName, String contactNo, String imagePath, String hkID, String hkUUID) {
        this.contactName = contactName;
        this.contactNo = contactNo;
        this.imagePath = imagePath;
        this.hkID = hkID;
        this.hkUUID = hkUUID;
    }


    public ContactsObject(Parcel in) {
        contactName = in.readString();
        contactNo = in.readString();
        imagePath = in.readString();
        hkID = in.readString();
        hkUUID = in.readString();
    }

    public static final Creator<ContactsObject> CREATOR = new Creator<ContactsObject>() {
        @Override
        public ContactsObject createFromParcel(Parcel in) {
            return new ContactsObject(in);
        }

        @Override
        public ContactsObject[] newArray(int size) {
            return new ContactsObject[size];
        }
    };

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getHkID() {
        return hkID;
    }

    public void setHkID(String hkID) {
        this.hkID = hkID;
    }

    public String getHkUUID() {
        return hkUUID;
    }

    public void setHkUUID(String hkUUID) {
        this.hkUUID = hkUUID;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(contactName);
        parcel.writeString(contactNo);
        parcel.writeString(imagePath);
        parcel.writeString(hkID);
        parcel.writeString(hkUUID);
    }
}
