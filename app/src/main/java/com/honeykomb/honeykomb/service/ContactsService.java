package com.honeykomb.honeykomb.service;


import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import com.honeykomb.honeykomb.dao.ServiceContactObject;
import com.honeykomb.honeykomb.utils.Util;

/**
 * Created by Rajashekar.Nimmala on 8/7/2017.
 */

public class ContactsService extends IntentService {

    private String TAG = ContactsService.class.getSimpleName();

    public ContactsService() {
        super("ContactsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Log.i(TAG, "----onHandleIntent----");
            executeRequest();
        } catch (Exception e) {
            Log.i(TAG + "----Exception----", ": " + e.getMessage());
            this.stopSelf();
        }
        this.stopSelf();
    }

    private void executeRequest() {

        ContentResolver cr = getContentResolver();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER;
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.Contacts._ID},
                selection, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        String CountryCode = Util._db.getUserCountryCode();
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                ServiceContactObject deviceContact = new ServiceContactObject();
                String contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                int contactID = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                deviceContact.contactName = contactName;
                deviceContact.hkUUID = "" + contactID;
                deviceContact.hkID = " ";
                Log.e(TAG, "contactNumber = " + contactNumber + " ==== contactName = " + contactName);
                if (contactNumber.substring(0, 1).equalsIgnoreCase("+")) {
                    deviceContact.contactNo = (contactNumber.replaceAll(" ", "").replaceAll("[^\\w+]+", ""));
                } else if (contactNumber.substring(0, 1).equalsIgnoreCase("0")) {
                    contactNumber = contactNumber.substring(0);
                    StringBuffer stringBuffer = new StringBuffer();

                    stringBuffer.append(CountryCode);
                    stringBuffer.append(contactNumber);

                    contactNumber = stringBuffer.toString();
                    deviceContact.contactNo = (contactNumber.replaceAll(" ", "").replaceAll("[^\\w+]+", ""));
                } else {
                    StringBuffer stringBuffer = new StringBuffer();

                    stringBuffer.append(CountryCode);
                    stringBuffer.append(contactNumber);

                    contactNumber = stringBuffer.toString();
                    deviceContact.contactNo = (contactNumber.replaceAll(" ", "").replaceAll("[^\\w+]+", ""));
                }
                if (!deviceContact.contactNo.contains("+91")) {
                    deviceContact.contactNo = "+91" + deviceContact.contactNo;
                }
                Log.i(TAG, "saved number = " + deviceContact.contactNo);
                Util._db.addOrUpdateContact(deviceContact);
                cursor.moveToNext();
            }
            cursor.close();
        }
    }
}


