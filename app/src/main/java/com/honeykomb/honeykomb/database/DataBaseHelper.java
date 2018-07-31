package com.honeykomb.honeykomb.database;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.honeykomb.honeykomb.R;
import com.honeykomb.honeykomb.dao.ActivityDates;
import com.honeykomb.honeykomb.dao.ActivityDetails;
import com.honeykomb.honeykomb.dao.ActivityDetailsDao;
import com.honeykomb.honeykomb.dao.ActivityOwnerDetails;
import com.honeykomb.honeykomb.dao.AppUser;
import com.honeykomb.honeykomb.dao.ContactObject;
import com.honeykomb.honeykomb.dao.HKUsers;
import com.honeykomb.honeykomb.dao.NonHKContact;
import com.honeykomb.honeykomb.dao.RSVPCount;
import com.honeykomb.honeykomb.dao.SelectedContactObject;
import com.honeykomb.honeykomb.dao.ServiceContactObject;
import com.honeykomb.honeykomb.utils.Constants;
import com.honeykomb.honeykomb.utils.NotificationReceiverActivity;
import com.honeykomb.honeykomb.utils.UtilityHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.loopj.android.http.AsyncHttpClient.log;

public class DataBaseHelper {
    private static final String TAG = "DataBaseHelper";
    private DatabaseHelper dataHelper;
    private SQLiteDatabase db;
    private Context context;
    public static final String TABLE_HKACTIVITIES_FOR_CHAT_ICON = "HKActivitiesForChatIcon";
    public static final String TABLE_HKACTIVITIES = "HKActivities";
    public static final String TABLE_HKACTIVITIES_DETAILS = "HKActivitiesDetails";
    public static final String TABLE_ACTIVITY_ATTACHMENTS = "ActivityAttachments";
    public static final String TABLE_ACTIVITY_DATES = "ActivityDates";
    public static final String TABLE_ACTIVITY_USERS_TEMP = "ActivityUsersTemp";
    public static final String TABLE_ACTIVITY_USERS = "ActivityUsers";
    private static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "HONEYKOMB";
    private static final String TABLE_USERS = "Users";
    private static final String TABLE_USER_CONTACTS = "UserContacts";
    private static final String TABLE_DEVICE_CONTACTS = "DeviceContacts";
    private static final String TABLE_SNOOZE_TASKS = "SnoozeTasks";
    private static final String TABLE_NOTIFICATION = "Notification";
    private static final String TABLE_PENDINGINVITATIONS = "PendingInvitations";
    private static final String TABLE_QB_USER = "QBUsers";
    private static final String INDEX_HKActivities_activityID = "HKActivities_activityID";
    private static final String USER_PHONE_CONTACTS = "user_phone_contacts";

    // TODO added new Table to get/set to add/remove from APPLOZIC group 24-07-2017
    private static final String TABLE_REMOVE_INVITE_APL_GROUP = "add_remove_app_lozic_user";

    public static JSONArray jArrayActivity = null;
    public static String DB_PATH = "data/data/com.honeykomb.honeykomb/databases/";

    // TODO Application user Table
    private static final String CREATE_TABLE_USERS = "CREATE TABLE IF NOT EXISTS " + TABLE_USERS + "(" + "_id"
            + " integer NOT NULL PRIMARY KEY AUTOINCREMENT," + "HK_UUID" + " VARCHAR," + "DisplayName"
            + " VARCHAR," + "StatusMessage" + " VARCHAR," + "PhotoPath" + " VARCHAR," + "Settings" + " VARCHAR,"
            + "HK_ID" + " VARCHAR," + "QuickBloxID" + " VARCHAR,"
            + "PhoneNumber" + " VARCHAR" + ")";

    // TODO Quick blox chat user Table
    private static final String CREATE_TABLE_QB_USER = "CREATE TABLE IF NOT EXISTS " + TABLE_QB_USER + "(" + "_id"
            + " integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
            + "QBUserEmail" + " VARCHAR,"
            + "QBUserLoginID" + " VARCHAR,"
            + "QBUserOldPassword" + " VARCHAR,"
            + "QBUserNewPassword" + " VARCHAR,"
            + "QBUserTwitterDigitsId" + " VARCHAR,"
            + "QBUserID" + " VARCHAR,"
            + "QBUserDigitsSessionID" + " VARCHAR,"
            + "QBUserPhone" + " VARCHAR,"
            + "QBUserName" + " VARCHAR" + ")";

    private static final String CREATE_TABLE_ACTIVITY_USERS_TEMP = "CREATE TABLE IF NOT EXISTS " + TABLE_ACTIVITY_USERS_TEMP + "(" + "_id"
            + " integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
            + "ActivityUserID" + " VARCHAR,"
            + "activityId" + " VARCHAR,"
            + "hK_UUID" + " VARCHAR,"
            + "phoneNumber" + " VARCHAR,"
            + "deliveredTime" + " VARCHAR,"
            + "invitationStatus" + " VARCHAR,"
            + "userActivityStatus" + " VARCHAR,"
            + "QuickBloxID" + " VARCHAR,"
            + "QBUserID" + " VARCHAR,"
            + "ActionType" + " VARCHAR,"
            + "dateModified" + " VARCHAR,"
            + "createdBy" + " VARCHAR,"
            + "modifiedBy" + " VARCHAR,"
            + "newStatus" + " VARCHAR,"
            + "NewActionType" + " VARCHAR,"
            + "countRSVP" + " VARCHAR"
            /*+ "UNIQUE ( activityId, hK_UUID, phoneNumber )"*/ + ")";

    // TODO User contacts table
    private static final String CREATE_TABLE_USER_CONTACTS = "CREATE TABLE IF NOT EXISTS " + TABLE_USER_CONTACTS + "(" + "_id"
            + " integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
            + "HK_UUID" + " VARCHAR,"
            + "PhoneNumber" + " VARCHAR,"
            + "DisplayName" + " VARCHAR,"
            + "HK_ID" + " VARCHAR,"
            + "StatusMessage" + " VARCHAR,"
            + "PhotoPath" + " VARCHAR,"
            + "QuickBloxID" + " VARCHAR,"
            + "QBUserID" + " VARCHAR,"
            + "active" + " VARCHAR,"
            + "deviceUserName" + " VARCHAR" + ")";

    // TODO Activity table with activityID as unique key table
    private static final String CREATE_TABLE_HKACTIVITIES = "CREATE TABLE IF NOT EXISTS " + TABLE_HKACTIVITIES + "(" + "_id"
            + " integer PRIMARY KEY AUTOINCREMENT,"
            + "activityID" + " VARCHAR,"
            + "activityTitle" + " VARCHAR,"
            + "active" + " VARCHAR,"
            + "activityOwner" + " VARCHAR,"
            + "activityOwnerName" + " VARCHAR,"
            + "actionType" + " VARCHAR,"
            + "blockCalendar" + " VARCHAR,"
            + "allowOtherToModify" + " VARCHAR,"
            + "activityStatus" + " VARCHAR,"
            + "dateCreated" + " TIMESTAMP,"
            + "dateModified" + " TIMESTAMP,"
            + "createdBy" + " VARCHAR,"
            + "modifiedBy" + " VARCHAR,"
            + "QuickbloxGroupID" + " VARCHAR,"
            + "QuickbloxRoomJID" + " VARCHAR,"
            + "activityupdate" + " VARCHAR,"
            + "countRSVP" + " VARCHAR,"
            + "activityRSVP" + " VARCHAR,"
            + "reminder" + " VARCHAR,"
            + "address" + " VARCHAR,"
            + "userActivityStatus" + " VARCHAR,"
            + "activityNotes" + " VARCHAR,"
            + "invitationStatus" + " VARCHAR,"
            + "unreadCount" + " VARCHAR,"
            + "accept" + " VARCHAR,"
            + "PendingRoomJID" + " VARCHAR,"
            + "invitesToActivity" + " integer,"//+ "invitesToActivity" + " integer DEFAULT 0 ," changed by prakash 06 july 2017
            + "updatedMsg" + " VARCHAR,"
            + "activitylocation" + " VARCHAR,"
            + "activitydates" + " VARCHAR,"
            + "imagepath" + " VARCHAR,"
            + "ApplozicGroupCreated" + " VARCHAR,"
            + "applozicgroupid" + " VARCHAR" + ")";


    //TODO for chat icon
    private static final String CREATE_TABLE_HKACTIVITIES_FOR_CHAT_ICON = "CREATE TABLE IF NOT EXISTS " + TABLE_HKACTIVITIES_FOR_CHAT_ICON + "(" + "_id"
            + " integer PRIMARY KEY AUTOINCREMENT,"
            + "activityID" + " VARCHAR,"
            + "groupID" + " VARCHAR,"
            + "invitesToActivity integer DEFAULT 0 " + ")";

    //TODO for chat icon
    private static final String CREATE_TABLE_ACTIVITY_ATTACHMENTS = "CREATE TABLE IF NOT EXISTS " + TABLE_ACTIVITY_ATTACHMENTS + "(" + "_id"
            + " integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
            + "activityID" + " VARCHAR,"
            + "attachmentID" + " VARCHAR,"
            + "attachmentPath" + " VARCHAR,"
            + "attachmentType" + " VARCHAR,"
            + "active" + " INTEGER,"
            + "actionType" + " VARCHAR,"
            + "dateModified" + " TIMESTAMP,"
            + "createdBy" + " VARCHAR,"
            + "modifiedBy" + " VARCHAR" + ")";

    //TODO table contains the activity details
    private static final String CREATE_TABLE_HKACTIVITIES_DETAILS = "CREATE TABLE IF NOT EXISTS " + TABLE_HKACTIVITIES_DETAILS + "(" + "_id"
            + " integer PRIMARY KEY AUTOINCREMENT,"
            + "ActivityDetailId" + " VARCHAR,"
            + "activityID" + " VARCHAR,"
            + "columnName" + " VARCHAR,"
            + "columnValue" + " VARCHAR,"
            + "actionType" + " VARCHAR,"
            + "dateCreated" + " TIMESTAMP,"
            + "dateModified" + " TIMESTAMP,"
            + "createdBy" + " VARCHAR,"
            + "modifiedBy" + " VARCHAR"
            /*+ "UNIQUE " + "(" + "activityID,"
            + " columnName" + ")"*/ + ")";

    // TODO table contains start date and end date of the activity
    private static final String CREATE_TABLE_ACTIVITY_DATES = "CREATE TABLE IF NOT EXISTS " + TABLE_ACTIVITY_DATES + "(" + "_id"
            + " integer PRIMARY KEY AUTOINCREMENT UNIQUE,"
            + "ActivityDateID" + " VARCHAR,"
            + "activityID" + " VARCHAR,"
            + "startDate" + " VARCHAR,"
            + "startTime" + " VARCHAR,"
            + "endDate" + " VARCHAR,"
            + "endTime" + " VARCHAR,"
            + "actionType" + " VARCHAR,"
            + "dateModified" + " VARCHAR,"
            + "createdBy" + " VARCHAR,"
            + "modifiedBy" + " VARCHAR"
            + /*"UNIQUE " + "(" + "activityID,"
            + " startDate" + ")" +*/ ")";

    // TODO table contains respective activity users with respective to activityID
    private static final String CREATE_TABLE_ACTIVITY_USERS = "CREATE TABLE IF NOT EXISTS " + TABLE_ACTIVITY_USERS + "(" + "_id"
            + " integer PRIMARY KEY AUTOINCREMENT,"
            + "ActivityUserID" + " VARCHAR,"
            + "activityID" + " VARCHAR,"
            + "hK_UUID" + " VARCHAR,"
            + "phoneNumber" + " VARCHAR,"
            + "invitationStatus" + " VARCHAR,"
            + "userActivityStatus" + " VARCHAR,"
            + "QuickBloxID" + " VARCHAR,"
            + "ActionType" + " VARCHAR,"
            + "dateModified" + " VARCHAR,"
            + "accept" + " VARCHAR,"
            + "createdBy" + " VARCHAR,"
            + "modifiedBy" + " VARCHAR,"
            + "countRSVP" + " VARCHAR,"
            + "deliveredTime" + " TIMESTAMP,"
            + "QBUserID" + " VARCHAR,"
            + "longUrl" + " VARCHAR,"
            + "deviceUserName" + " VARCHAR"
            /*+ "UNIQUE " + "(" + "activityID," + " hK_UUID," + " phoneNumber" + ")"*/ + ")";

    // TODO table contains local contacts of the app User
    private static final String CREATE_TABLE_DEVICE_CONTACTS = "CREATE TABLE IF NOT EXISTS " + TABLE_DEVICE_CONTACTS + "(" + "_id"
            + " integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
            + "PhoneNumber" + " VARCHAR UNIQUE,"
            + "DisplayName" + " VARCHAR,"
            + "UNIQUE " + "(" + "PhoneNumber" + ")" + ")";

    // TODO Contains notification details of any Push notification received whcih contains meta data
    private static final String CREATE_TABLE_NOTIFICATION = "CREATE TABLE IF NOT EXISTS " + TABLE_NOTIFICATION + "(" + "_id"
            + " integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
            + "notificationId" + " VARCHAR,"
            + "notificaitonText" + " VARCHAR,"
            + "Sendto" + " VARCHAR,"
            + "SendFrom" + " VARCHAR,"
            + "actionType" + " VARCHAR,"
            + "objectType" + " VARCHAR,"
            + "objectId" + " VARCHAR,"
            + "dateCreated" + " TIMESTAMP,"
            + "pendingInvCount" + " INTEGER,"
            + "isPending" + " VARCHAR,"
            + "createdby" + " VARCHAR,"
            + "isRead" + " integer DEFAULT 0,"
            + "lastUpdated" + " TIMESTAMP" + ")";

    // TODO Contains task snooze time details.
    private static final String CREATE_TABLE_SNOOZE_TASKS = "CREATE TABLE IF NOT EXISTS " + TABLE_SNOOZE_TASKS + "(" + "_id"
            + " integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
            + "activityTaskID" + " VARCHAR,"
            + "SnoozeDate" + " VARCHAR" + ")";

    // TODO Contains received activity details where APP USER is been invited, which are by default pending.
    private static final String CREATE_TABLE_PENDING_INVITATIONS = "CREATE TABLE IF NOT EXISTS " + TABLE_PENDINGINVITATIONS + "(" + "_id" +
            " integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
            + "activityId" + " VARCHAR,"
            + "activityStatus" + " VARCHAR,"
            + "activityOwner" + " VARCHAR,"
            + "OwnerDisplayName" + " VARCHAR,"
            + "hK_UUID" + " VARCHAR,"
            + "userActivityStatus" + " VARCHAR,"
            + "activityTitle" + " VARCHAR,"
            + "invitationStatus" + " VARCHAR,"
            + "lastUpdated" + " VARCHAR,"
            + "Active" + " VARCHAR,"
            + "DateCreated" + " TIMESTAMP,"
            + "response" + " VARCHAR,"
            + "countRSVP" + " VARCHAR" + ")";

    // TODO added new Table to get uset to add/remove from APPLOZIC group 24-07-2017
    private static final String CREATE_TABLE_REMOVE_INVITE_APL_GROUP = "CREATE TABLE IF NOT EXISTS " + TABLE_REMOVE_INVITE_APL_GROUP + "(" + "_id"
            + " integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
            + "UniqueID" + " VARCHAR,"
            + "AppLoxicGroupID" + " INTEGER,"
            + "HK_ID" + " VARCHAR,"
            + "ActionPerformed" + " VARCHAR,"
            + "Type" + " VARCHAR" + ")";

    // TODO Contains all contacts of APPUSER as well as HK users.
    private static final String CREATE_TABLE_USER_PHONE_CONTACTS = "CREATE TABLE IF NOT EXISTS " + USER_PHONE_CONTACTS + "(" + "_id" + " integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
            + "contactName" + " VARCHAR,"
            + "contactNo" + " VARCHAR,"
            + "image" + " VARCHAR,"
            + "hkID" + " VARCHAR,"
            + "hkUUID" + " VARCHAR" + ")";

    private static final String CREATE_INDEX_HKActivities_activityID = "CREATE UNIQUE INDEX IF NOT EXISTS " + INDEX_HKActivities_activityID + " ON " + TABLE_HKACTIVITIES + "";
    private static final String createUsersIndex = "CREATE UNIQUE INDEX IF NOT EXISTS Users_HK_UUID ON Users(HK_UUID)";
    private static final String createUsersContactsIndex = "CREATE UNIQUE INDEX IF NOT EXISTS UserContacts_HK_UUID ON UserContacts(HK_UUID)";
    private static final String createActivityIndex = "CREATE UNIQUE INDEX IF NOT EXISTS HKActivities_activityId ON HKActivities(activityId)";
    //+ "DisplayName" + " VARCHAR," + "DisplayPhoneNumber" + " VARCHAR," + "UNIQUE" + "(" + "PhoneNumber" +")"  ON CONFLICT REPLACE+ ")";
    private static final String createActivityDetailsIndex = "CREATE UNIQUE INDEX IF NOT EXISTS HKActivitiesDetails_activityID ON HKActivitiesDetails(activityID)";
    private static final String createActivityAttachmentsIndex = "CREATE UNIQUE INDEX IF NOT EXISTS ActivityAttachments_ActivityAttachmentID ON ActivityAttachments(attachmentID)";
    private static final String createPendingInvitationIndex = "CREATE UNIQUE INDEX IF NOT EXISTS PendingInvitations_activityId ON PendingInvitations(activityId)";
    private static final String createSnoozeIndex = "CREATE UNIQUE INDEX IF NOT EXISTS SnoozeTasks_activityId ON SnoozeTasks(activityTaskID)";
    private static final String activityUsersIndex = "CREATE UNIQUE INDEX ActivityUsers_ActivityUserID ON ActivityUsers(activityID,phoneNumber)";
    private static final String createNotificationIndex = "CREATE UNIQUE INDEX IF NOT EXISTS Notification_notificationId ON Notification(notificationId)";
    private static final String createActivityDatesIndex = "CREATE UNIQUE INDEX IF NOT EXISTS ActivityDates_activityId_startDate_endDate ON ActivityDates(activityId,startDate,endDate)";
    private static final String create_index_REMOVE_INVITE_APL_GROUP = "CREATE UNIQUE INDEX IF NOT EXISTS add_remove_app_lozic_user_UniqueID ON add_remove_app_lozic_user(UniqueID)";
    private static final String create_index_USER_PHONE_CONTACTS = "CREATE UNIQUE INDEX IF NOT EXISTS user_phone_contacts_contactNo ON user_phone_contacts(contactNo)";

    /**
     * Parameterized Constructor to initialize dataHelper Object
     *
     * @param ctx - stores the current Activity context
     */
    public DataBaseHelper(Context ctx) {
        this.context = ctx;
        dataHelper = new DatabaseHelper(context);
    }

    public void UpdateFlagDetailsActivity(String activityID) {
        String query1 = "UPDATE HKActivities SET ApplozicGroupCreated ='NO' WHERE activityID = '" + activityID + "'";
        db.execSQL(query1);
    }


    public Cursor getCursorContactNew() {
        String query = "SELECT * FROM user_phone_contacts ORDER BY CASE WHEN trim(hkid) = '' THEN 2 ELSE 1 END ASC, contactName ASC";
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    public void deleteActivityFromDBEventDetails(String activityID) {
        db.delete("HKActivities", "activityID='" + activityID + "'", null);
        db.delete("ActivityDates", "activityID='" + activityID + "'", null);
        db.delete("hkactivitiesdetails", "activityID='" + activityID + "'", null);
        db.delete("ActivityUsers", "activityID='" + activityID + "'", null);
    }

    public void updateActivityDataToDBEventDetails(ContentValues value, String activityID) {
        db.update("HKActivities", value, "activityID=?", new String[]{activityID});
    }

    public void updateActivityDatesDataToDB(String key, String value, String activityDateID) {
        String query = "UPDATE ActivityDates SET " + key + " ='" + value + "' WHERE activityDateID = '" + activityDateID + "'";
        db.execSQL(query);
    }

    public void updateActivityDetailsDataToDB(String key, String value, String activityID) {
        String query = "UPDATE hkactivitiesdetails SET " + key + " ='" + value + "' WHERE activityID = '" + activityID + "'";
        db.execSQL(query);
    }

    public void updateActivityUserDataToDB(String key, int value, String activityID, String hK_UUID) {
        String query = "UPDATE ActivityUsers SET " + key + " ='" + value + "' WHERE activityID = '" + activityID + "' and hK_UUID = '" + hK_UUID + "'";
        db.execSQL(query);
    }

    public void udpateActivityUserDataToDB(String key, String value, String activityID, String hK_UUID) {
        String query = "UPDATE ActivityUsers SET " + key + " ='" + value + "' WHERE activityID = '" + activityID + "' and hK_UUID = '" + hK_UUID + "'";
        db.execSQL(query);
    }


    public void SetInvitationStatus(String activityID, String hK_UUID) {
        String query1 = "UPDATE HKActivities SET invitationStatus ='Yes' WHERE activityID = '" + activityID + "'";
        String query2 = "UPDATE ActivityUsers SET invitationStatus ='Yes' WHERE activityID = '" + activityID + "' AND hK_UUID = '" + hK_UUID + "'";
        db.execSQL(query1);
        db.execSQL(query2);
    }

    public ArrayList<String> GetPhoneNumbers() {
        ArrayList<String> phoneList = new ArrayList<>();

        String buildSQL = "SELECT * FROM Users";
        Cursor cursor = db.rawQuery(buildSQL, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    phoneList.add((cursor.getString(cursor.getColumnIndex("QuickBloxID"))));
                } while (cursor.moveToNext());
            }
        }
        cursor.close();

        return phoneList;
    }

    public ArrayList<String> GetPhoneNumbersMF(String similarities, ArrayList<String> phoneNumberList) {
        String buildSQL1 = "SELECT * FROM UserContacts WHERE PhoneNumber = " + "'" + similarities + "'";
        Cursor cursor1 = db.rawQuery(buildSQL1, null);

        if (cursor1 != null) {
            if (cursor1.moveToFirst()) {
                do {
                    phoneNumberList.add((cursor1.getString(cursor1.getColumnIndex("QuickBloxID"))));
                } while (cursor1.moveToNext());
            }
        }
        cursor1.close();
        return phoneNumberList;
    }

    public void updateSD(String actionType, String activityID) {
        String query = "UPDATE HKActivities SET active ='1',actionType ='" + actionType + "',activityStatus ='Active',userActivityStatus ='Active' WHERE activityID = '" + activityID + "'";
        db.execSQL(query);
    }

    public void updateHKActivitySD(String actionType, String activityID) {
        String query = "UPDATE HKActivities SET active ='1',actionType ='" + actionType + "',activityStatus ='Active',userActivityStatus ='Active' WHERE activityID = '" + activityID + "'";
        db.execSQL(query);
    }

    public void updateAppLozicID(String uniqueID) {
        String query = "UPDATE add_remove_app_lozic_user SET ActionPerformed ='YES' WHERE uniqueID = '" + uniqueID + "'";
        db.execSQL(query);
    }


    public void addUserToGroupUH(String uniqueID) {
        db.delete("add_remove_app_lozic_user", "uniqueID=?", new String[]{uniqueID});
    }

    public void updateChatDialogTittleUH(String uniqueID) {
        String query = "UPDATE add_remove_app_lozic_user SET ActionPerformed ='YES' WHERE UniqueID = '" + uniqueID + "'";
        db.execSQL(query);
    }

    public Cursor getCursorUH(String strItemCode) {
        String query = "SELECT * FROM user_phone_contacts WHERE contactName || contactNo LIKE '" + strItemCode + "'" +
                "ORDER BY CASE WHEN trim(hkid) = '' THEN 2 ELSE 1 END ASC, contactName ASC";
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    public Cursor getCursorWithFilterUH(String inClause, String strItemCode) {
        String query = "SELECT * FROM user_phone_contacts WHERE contactNo not in " + inClause + " and contactName || contactNo LIKE '" + strItemCode + "'" +
                "ORDER BY CASE WHEN trim(hkid) = '' THEN 2 ELSE 1 END ASC";
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    public Cursor getCursor(String inClause) {
        String query = "SELECT * FROM user_phone_contacts WHERE contactNo not in " + inClause + " ORDER BY CASE WHEN trim(hkid) = '' THEN 2 ELSE 1 END ASC, contactName ASC";
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    public String getUserName(String hkUUID) {
        String userName = null;
        String query = "SELECT contactName FROM user_phone_contacts WHERE hkUUID = '" + hkUUID + "'";
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            userName = cursor.getString(0);
            cursor.moveToNext();
        }
        return userName;
    }

    public void updateActivitySelectedListOfInvitees(String activityID) {
        ContentValues contentValues1 = new ContentValues();
        contentValues1.put("ActionType", "Modify");
        db.update("HKActivities", contentValues1, "activityID = '" + activityID + "'", null);
    }

    public void insertOrUpdateSLOfINV(ContentValues contentValues) {
        db.insertWithOnConflict(TABLE_ACTIVITY_USERS, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public boolean isTrueSelectedListOfInviteesAdapter(String key, String value, String key1, String value1, String key2, String value2, String activityID, String hK_UUID) {


        String query = "UPDATE ActivityUsers SET " + key + " ='" + value + "' WHERE activityID = '" + activityID + "'AND hK_UUID = '" + hK_UUID + "'";
        db.execSQL(query);

        String query1 = "UPDATE ActivityUsers SET " + key1 + " ='" + value1 + "' WHERE activityID = '" + activityID + "'AND hK_UUID = '" + hK_UUID + "'";
        db.execSQL(query1);

        String query2 = "UPDATE ActivityUsers SET " + key2 + " ='" + value2 + "' WHERE activityID = '" + activityID + "'AND hK_UUID = '" + hK_UUID + "'";
        db.execSQL(query2);

        return true;
    }

    public void updateTableFromSLIADPTER(String key, String value, String key1, String value1, String key2, String value2, String activityID, String nuber) {

        String query = "UPDATE ActivityUsers SET " + key + " ='" + value + "' WHERE activityID = '" + activityID + "'AND phoneNumber = '" + nuber + "'";
        db.execSQL(query);

        String query1 = "UPDATE ActivityUsers SET " + key1 + " ='" + value1 + "' WHERE activityID = '" + activityID + "'AND phoneNumber = '" + nuber + "'";
        db.execSQL(query1);

        String query2 = "UPDATE ActivityUsers SET " + key2 + " ='" + value2 + "' WHERE activityID = '" + activityID + "'AND phoneNumber = '" + nuber + "'";
        db.execSQL(query2);


    }

    public void isTrueSelectedListOfInviteesAdapterNew(String key, String value, String key1, String value1, String key2, String value2, String key3, String value3, String activityID, String hK_UUID) {
        String query = "UPDATE ActivityUsers SET " + key + " ='" + value + "' WHERE activityID = '" + activityID + "'AND hK_UUID = '" + hK_UUID + "'";
        db.execSQL(query);

        String query1 = "UPDATE ActivityUsers SET " + key1 + " ='" + value1 + "' WHERE activityID = '" + activityID + "'AND hK_UUID = '" + hK_UUID + "'";
        db.execSQL(query1);

        String query2 = "UPDATE ActivityUsers SET " + key2 + " ='" + value2 + "' WHERE activityID = '" + activityID + "'AND hK_UUID = '" + hK_UUID + "'";
        db.execSQL(query2);

        String query3 = "UPDATE ActivityUsers SET " + key3 + " ='" + value3 + "' WHERE activityID = '" + activityID + "'AND hK_UUID = '" + hK_UUID + "'";
        db.execSQL(query3);

    }

    public void updateTableFromSLIADPTERNew(String key, String value, String key1, String value1, String key2, String value2, String key3, String value3, String activityID, String number) {
        String query = "UPDATE ActivityUsers SET " + key + " ='" + value + "' WHERE activityID = '" + activityID + "'AND phoneNumber = '" + number + "'";
        db.execSQL(query);

        String query1 = "UPDATE ActivityUsers SET " + key1 + " ='" + value1 + "' WHERE activityID = '" + activityID + "'AND phoneNumber = '" + number + "'";
        db.execSQL(query1);

        String query2 = "UPDATE ActivityUsers SET " + key2 + " ='" + value2 + "' WHERE activityID = '" + activityID + "'AND phoneNumber = '" + number + "'";
        db.execSQL(query2);

        String query3 = "UPDATE ActivityUsers SET " + key3 + " ='" + value3 + "' WHERE activityID = '" + activityID + "'AND phoneNumber = '" + number + "'";
        db.execSQL(query3);

    }

    public void isTrueSelectedListOfInviteesAdapterNew(String key, String value, String key1, String value1, String key2, String value2, String activityID, String hK_UUID) {
        String query = "UPDATE ActivityUsers SET " + key + " ='" + value + "' WHERE activityID = '" + activityID + "'AND hK_UUID = '" + hK_UUID + "'";
        db.execSQL(query);

        String query1 = "UPDATE ActivityUsers SET " + key1 + " ='" + value1 + "' WHERE activityID = '" + activityID + "'AND hK_UUID = '" + hK_UUID + "'";
        db.execSQL(query1);

        String query2 = "UPDATE ActivityUsers SET " + key2 + " ='" + value2 + "' WHERE activityID = '" + activityID + "'AND hK_UUID = '" + hK_UUID + "'";
        db.execSQL(query2);

    }

    public void updateTableFromSLIADPTERNew(String key, String value, String key1, String value1, String key2, String value2, String activityID, String number) {
        String query = "UPDATE ActivityUsers SET " + key + " ='" + value + "' WHERE activityID = '" + activityID + "'AND phoneNumber = '" + number + "'";
        db.execSQL(query);

        String query1 = "UPDATE ActivityUsers SET " + key1 + " ='" + value1 + "' WHERE activityID = '" + activityID + "'AND phoneNumber = '" + number + "'";
        db.execSQL(query1);

        String query2 = "UPDATE ActivityUsers SET " + key2 + " ='" + value2 + "' WHERE activityID = '" + activityID + "'AND phoneNumber = '" + number + "'";
        db.execSQL(query2);

    }


    public void updateTodoDetails(String activityID) {
        String actionType = "Modify";
        String query = "UPDATE HKActivities SET actionType ='" + actionType + "',activityStatus ='Active',userActivityStatus ='Completed',invitationStatus ='Yes' WHERE activityID = '" + activityID + "'";
        db.execSQL(query);
    }

    public void updateTodoDetailsNew(ContentValues values, String activityID) {
        db.update("HKActivities", values, "activityID=?", new String[]{activityID});
    }

    public void updateActivityToDo(String key, String value, String activityDateID) {
        String query = "UPDATE ActivityDates SET " + key + " ='" + value + "' WHERE activityDateID = '" + activityDateID + "'";
        db.execSQL(query);
    }

    public void updateActivityToDoNew(String key, String value, String activityID) {
        String query = "UPDATE hkactivitiesdetails SET " + key + " ='" + value + "' WHERE activityID = '" + activityID + "'";
        db.execSQL(query);
    }

    public void deleteDataFromDB(String activityID) {
        db.delete("HKActivities", "activityID='" + activityID + "'", null);
        db.delete("ActivityDates", "activityID='" + activityID + "'", null);
        db.delete("hkactivitiesdetails", "activityID='" + activityID + "'", null);
        db.delete("ActivityUsers", "activityID='" + activityID + "'", null);
    }

    public void updateVPF1(ContentValues contentValues) {
        db.update("Notification", contentValues, "", null);
    }

    public void udpateHKActivitiesDataToDB(String actionType, String activityIDToUpdateDB) {
        String query = "UPDATE HKActivities SET actionType ='" + actionType + "',activityStatus ='Active',userActivityStatus ='Completed',invitationStatus ='Yes' WHERE activityID = '" + activityIDToUpdateDB + "'";
        db.execSQL(query);
    }

    public void udpateHKActivitiesDataToDB1(String key, String value, String activityIDToUpdateDB, String hK_UUID) {
        String query = "UPDATE ActivityUsers SET " + key + " ='" + value + "' WHERE activityID = '" + activityIDToUpdateDB + "' and hK_UUID = '" + hK_UUID + "'";
        db.execSQL(query);
    }

    public void updateAllTabelsUpdateUserAsync(String tableName, String displayName, String hK_uuid) {
        String query = "UPDATE '" + tableName + "' SET DisplayName ='" + displayName + "' WHERE HK_UUID = '" + hK_uuid + "'";
        db.execSQL(query);
    }

    public void updateActivityUsersDataToDbNew(String deliveredTime, String deliveredTimeValue, String activityId, String hK_uuid) {
        String query = "UPDATE ActivityUsers SET " + deliveredTime + " ='" + deliveredTimeValue + "' WHERE activityID = '" + activityId + "' and hK_UUID = '" + hK_uuid + "'";
        db.execSQL(query);
    }

    public void updateFromAddActivityAsync(ContentValues contentValues) {
        db.update("HKActivities", contentValues, "", null);
    }

    public void updateActivityUsersDataToDbTADAPTER(String key, String value, String activityIDToUpdateDB, String hK_UUID) {
        String query = "UPDATE ActivityUsers SET " + key + " ='" + value + "' WHERE activityID = '" + activityIDToUpdateDB + "' and hK_UUID = '" + hK_UUID + "'";
        db.execSQL(query);
    }

    public boolean getActionTypeBasedOnActivityID(String activityID) {
        String actionType = "";
        String query = "select actionType from HKActivities where activityID = '" + activityID + "'";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() != 0) {
            if (!cursor.isLast()) {
                cursor.moveToNext();
                actionType = cursor.getString(0);
            }
        }
        cursor.close();
        if (actionType.equalsIgnoreCase("ADD")) {
            return false;
        } else {
            return true;
        }
    }


    public String getActivityEndDateBasedOnActivityID(String activityID, String selectedDateFromCalendar) {
        String EndDate = "";
        String query = "SELECT endDate FROM ActivityDates WHERE ActivityID = '" + activityID + "' AND startDate LIKE '%" + selectedDateFromCalendar + "%'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() != 0) {
            if (!cursor.isLast()) {
                cursor.moveToNext();
                EndDate = cursor.getString(0);
            }
        }
        cursor.close();
        return EndDate;
    }

    public String getActivityEndDateBasedOnActivityID(String activityID) {
        String EndDate = "";
        String query = "SELECT max(endDate) as endDate FROM ActivityDates WHERE ActivityID = '" + activityID + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() != 0) {
            if (!cursor.isLast()) {
                cursor.moveToNext();
                EndDate = cursor.getString(0);
            }
        }
        cursor.close();
        return EndDate;
    }

    public String getActivityStatus(String activityID, String selectedDateFromCalendar) {
        String status = "";
        String query = "SELECT CASE WHEN strftime('%Y-%m-%d' ,EndDate) = " + "'" + selectedDateFromCalendar + "'" + " AND strftime('%Y-%m-%d',EndDate) < date('now') THEN 'Completed' ELSE 'Pending' END AS Status " +
                "FROM ActivityDates WHERE ActivityID=" + "'" + activityID + "'" + " AND " +
                "strftime('%Y-%m-%d' ,EndDate) =" + "'" + selectedDateFromCalendar + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() != 0) {
            if (!cursor.isLast()) {
                cursor.moveToNext();
                status = cursor.getString(0);
            }
        }
        cursor.close();
        return status;
    }

    public int getTaskCount(String activityOwner) {
        int count = 0;
        String query = "SELECT COUNT(1) AS ActivityCount ," +
                " CASE WHEN HA.activityOwner =" + "'" + activityOwner + "'" + " THEN 'Owner'" +
                " WHEN IFNULL(AU.InvitationStatus,'') = 'Pending' THEN 'Pending'" +
                " WHEN IFNULL(AU.InvitationStatus,'') = 'Yes' THEN 'Yes' END Status ," +
                " CASE WHEN HA.ActivityStatus = 'Completed' THEN HA.ActivityStatus ELSE " +
                " CASE WHEN strftime('%Y-%m-%d %H:%M', IFNULL(ST.SnoozeDate, AD.EndDate)) <= strftime('%Y-%m-%d %H:%M', datetime('now', 'localtime')) " +
                " THEN 'OD' ELSE 'FE' END " +
                " END AS EventType, ST.SnoozeDate " +
                " FROM HKActivities HA" +
                " LEFT OUTER JOIN UserContacts UC ON HA.ActivityOwner = UC.HK_UUID" +
                " LEFT OUTER JOIN ActivityUsers AU ON AU.ActivityID = HA.ActivityID AND AU.HK_UUID = '6420ef62-6ae3-4038-86d3-0f463490ca6e'" +
                " LEFT OUTER JOIN DeviceContacts DC ON UC.PhoneNumber = DC.PhoneNumber" +
                " LEFT OUTER JOIN ActivityDates AD ON HA.ActivityID =  AD.ActivityID" +
                " LEFT OUTER JOIN SnoozeTasks ST ON HA.ActivityID = ST.ActivityTaskID" +
                " WHERE HA.Active = 1 AND HA.blockCalendar = 0 AND IFNULL(HA.ActionType,'') != 'DELETE' AND" +
                " HA.activityStatus  != 'inActive' AND IFNULL(HA.invitationStatus,'Yes') IN ('Pending', 'Yes')AND HA.userActivityStatus != 'Completed' AND strftime('%Y-%m-%d %H:%M',IFNULL(ST.SnoozeDate,AD.EndDate)) <= strftime('%Y-%m-%d %H:%M',datetime('now','localtime')) " +
                "GROUP BY HA.ActivityID ORDER BY IFNULL(ST.SnoozeDate,AD.EndDate) DESC";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() > 0) {
            if (!cursor.isLast()) {
                count = cursor.getCount();
            }
        }
        cursor.close();
        return count;
    }

    public String getStatusByActivityID(String activityID) {
        String status = "";
        String query = "select activityStatus from HKActivities where activityID='" + activityID + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() != 0) {
            if (!cursor.isLast()) {
                cursor.moveToNext();
                status = cursor.getString(0);
            }
        }
        cursor.close();
        return status;
    }


    private static class DatabaseHelper extends SQLiteOpenHelper {

        private Context context;

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
        }

        /**
         * This method is used to check whether the database already exist in
         * device.
         *
         * @return
         */
        public boolean checkDataBase() {

            try {
                File f = new File(DB_PATH + DATABASE_NAME);

                return f.exists();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        public void onCreate(SQLiteDatabase db) {
            try {
                // TODO creating required tables
                db.execSQL(CREATE_TABLE_USERS);
                db.execSQL(CREATE_TABLE_USER_CONTACTS);
                db.execSQL(CREATE_TABLE_HKACTIVITIES);
                db.execSQL(CREATE_TABLE_HKACTIVITIES_FOR_CHAT_ICON);
                db.execSQL(CREATE_TABLE_HKACTIVITIES_DETAILS);
                db.execSQL(CREATE_TABLE_ACTIVITY_ATTACHMENTS);
                db.execSQL(CREATE_TABLE_ACTIVITY_DATES);
                db.execSQL(CREATE_TABLE_NOTIFICATION);
                db.execSQL(CREATE_TABLE_DEVICE_CONTACTS);
                db.execSQL(CREATE_TABLE_ACTIVITY_USERS);
                db.execSQL(CREATE_TABLE_SNOOZE_TASKS);
                db.execSQL(CREATE_TABLE_PENDING_INVITATIONS);
                db.execSQL(CREATE_TABLE_ACTIVITY_USERS_TEMP);
                db.execSQL(createUsersIndex);
                db.execSQL(createUsersContactsIndex);
                db.execSQL(createActivityIndex);
                db.execSQL(createActivityDetailsIndex);
                db.execSQL(createActivityAttachmentsIndex);
                db.execSQL(createPendingInvitationIndex);
                db.execSQL(createNotificationIndex);
                db.execSQL(createSnoozeIndex);
                db.execSQL(activityUsersIndex);
                db.execSQL(CREATE_TABLE_QB_USER);
                db.execSQL(createActivityDatesIndex);
                db.execSQL(CREATE_TABLE_REMOVE_INVITE_APL_GROUP);
                db.execSQL(create_index_REMOVE_INVITE_APL_GROUP);
                db.execSQL(CREATE_TABLE_USER_PHONE_CONTACTS);
                db.execSQL(create_index_USER_PHONE_CONTACTS);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("DBHelper", "onCreate Exception " + e);
            }

        }

        /**
         * This method is used to upgrade the database
         */

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                // TODO on upgrade drop older tables
                db.execSQL(context.getResources().getString(R.string.DROP_TABLE_IF_EXISTS) + CREATE_TABLE_USERS);
                db.execSQL(context.getResources().getString(R.string.DROP_TABLE_IF_EXISTS) + CREATE_TABLE_USER_CONTACTS);
                db.execSQL(context.getResources().getString(R.string.DROP_TABLE_IF_EXISTS) + CREATE_TABLE_HKACTIVITIES);
                db.execSQL(context.getResources().getString(R.string.DROP_TABLE_IF_EXISTS) + CREATE_TABLE_HKACTIVITIES_FOR_CHAT_ICON);
                db.execSQL(context.getResources().getString(R.string.DROP_TABLE_IF_EXISTS) + CREATE_TABLE_HKACTIVITIES_DETAILS);
                db.execSQL(context.getResources().getString(R.string.DROP_TABLE_IF_EXISTS) + CREATE_TABLE_ACTIVITY_ATTACHMENTS);
                db.execSQL(context.getResources().getString(R.string.DROP_TABLE_IF_EXISTS) + CREATE_TABLE_ACTIVITY_DATES);
                db.execSQL(context.getResources().getString(R.string.DROP_TABLE_IF_EXISTS) + CREATE_TABLE_NOTIFICATION);
                db.execSQL(context.getResources().getString(R.string.DROP_TABLE_IF_EXISTS) + CREATE_TABLE_DEVICE_CONTACTS);
                db.execSQL(context.getResources().getString(R.string.DROP_TABLE_IF_EXISTS) + CREATE_TABLE_ACTIVITY_USERS);
                db.execSQL(context.getResources().getString(R.string.DROP_TABLE_IF_EXISTS) + CREATE_TABLE_SNOOZE_TASKS);
                db.execSQL(context.getResources().getString(R.string.DROP_TABLE_IF_EXISTS) + CREATE_TABLE_PENDING_INVITATIONS);
                db.execSQL(context.getResources().getString(R.string.DROP_TABLE_IF_EXISTS) + CREATE_TABLE_ACTIVITY_USERS_TEMP);
                db.execSQL(context.getResources().getString(R.string.DROP_TABLE_IF_EXISTS) + createUsersIndex);
                db.execSQL(context.getResources().getString(R.string.DROP_TABLE_IF_EXISTS) + createUsersContactsIndex);
                db.execSQL(context.getResources().getString(R.string.DROP_TABLE_IF_EXISTS) + createActivityIndex);
                db.execSQL(context.getResources().getString(R.string.DROP_TABLE_IF_EXISTS) + createActivityDetailsIndex);
                db.execSQL(context.getResources().getString(R.string.DROP_TABLE_IF_EXISTS) + createActivityAttachmentsIndex);
                db.execSQL(context.getResources().getString(R.string.DROP_TABLE_IF_EXISTS) + createPendingInvitationIndex);
                db.execSQL(context.getResources().getString(R.string.DROP_TABLE_IF_EXISTS) + createNotificationIndex);
                db.execSQL(context.getResources().getString(R.string.DROP_TABLE_IF_EXISTS) + createSnoozeIndex);
                db.execSQL(context.getResources().getString(R.string.DROP_TABLE_IF_EXISTS) + createActivityDatesIndex);
                db.execSQL(context.getResources().getString(R.string.DROP_TABLE_IF_EXISTS) + CREATE_TABLE_QB_USER);
                db.execSQL(context.getResources().getString(R.string.DROP_TABLE_IF_EXISTS) + CREATE_TABLE_REMOVE_INVITE_APL_GROUP);
                db.execSQL(context.getResources().getString(R.string.DROP_TABLE_IF_EXISTS) + CREATE_TABLE_USER_PHONE_CONTACTS);
                db.execSQL(context.getResources().getString(R.string.DROP_TABLE_IF_EXISTS) + create_index_USER_PHONE_CONTACTS);


                onCreate(db);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void createUser(String hk_Uuid, String displayName, String statusMessage, String photoPath,
                           String settings, String hk_Id, String quickBloxID, String phoneNumber) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("HK_UUID", hk_Uuid == null ? "" : hk_Uuid);
        contentValues.put("DisplayName", displayName == null ? "" : displayName);
        contentValues.put("StatusMessage", statusMessage == null ? "" : statusMessage);
        contentValues.put("PhotoPath", photoPath == null ? "" : photoPath);
        contentValues.put("Settings", settings == null ? "" : settings);
        contentValues.put("HK_ID", hk_Id == null ? "" : hk_Id);
        contentValues.put("QuickBloxID", quickBloxID == null ? "" : quickBloxID);
        contentValues.put("PhoneNumber", phoneNumber == null ? "" : phoneNumber);

        db.insertWithOnConflict(TABLE_USERS, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    }


    public void createActivity(Bundle bundle, int invitesToActivity) {

        String dateCreated = bundle.getString("dateCreated");
        String dateModified = bundle.getString("dateCreated");

        ActivityOwnerDetails activityOwnerDetails = bundle.getParcelable("activityOwnerDetails");

        ContentValues contentValues = new ContentValues();
        if (invitesToActivity > 1) {
            contentValues.put("invitesToActivity", invitesToActivity);
            Log.i(TAG, "invitesToActivity count**************** " + invitesToActivity);
        } else {
            Log.i(TAG, "invitesToActivity count**************** " + invitesToActivity);
        }

        contentValues.put("activityID", bundle.getString("activityID") == null ? "" : bundle.getString("activityID"));
        contentValues.put("activityTitle", bundle.getString("activityTitle") == null ? "" : bundle.getString("activityTitle"));
        contentValues.put("active", bundle.getString("active") == null ? "" : bundle.getString("active"));
        contentValues.put("activityOwner", bundle.getString("activityOwner") == null ? "" : bundle.getString("activityOwner"));
        if (activityOwnerDetails != null && !activityOwnerDetails.getDisplayName().equalsIgnoreCase("")) {
            contentValues.put("activityOwnerName", activityOwnerDetails.getDisplayName() == null ? "" : activityOwnerDetails.getDisplayName());
        } else {
            contentValues.put("activityOwnerName", bundle.getString("displayName") == null ? "" : bundle.getString("displayName"));
        }
        contentValues.put("actionType", bundle.getString("actionType") == null ? "" : bundle.getString("actionType"));
        contentValues.put("blockCalendar", bundle.getString("blockCalendar") == null ? "" : bundle.getString("blockCalendar"));
        contentValues.put("allowOtherToModify", bundle.getString("allowOtherToModify") == null ? "" : bundle.getString("allowOtherToModify"));
        contentValues.put("activityStatus", bundle.getString("activityStatus") == null ? "" : bundle.getString("activityStatus"));
        contentValues.put("dateCreated", dateCreated == null ? "" : bundle.getString("dateCreated"));
        contentValues.put("dateModified", dateModified == null ? "" : bundle.getString("dateModified"));
        contentValues.put("createdBy", bundle.getString("createdBy") == null ? "" : bundle.getString("createdBy"));
        contentValues.put("modifiedBy", bundle.getString("modifiedBy") == null ? "" : bundle.getString("modifiedBy"));
        contentValues.put("activityRSVP", bundle.getString("activityRSVP") == null ? "" : bundle.getString("activityRSVP"));
        contentValues.put("QuickbloxRoomJID", bundle.getString("QuickbloxRoomJID") == null ? "" : bundle.getString("QuickbloxRoomJID"));
        contentValues.put("PendingRoomJID", bundle.getString("PendingRoomJID") == null ? " " : bundle.getString("PendingRoomJID"));
        contentValues.put("countRSVP", bundle.getString("countRSVP") == null ? "" : bundle.getString("countRSVP"));
        contentValues.put("reminder", bundle.getString("reminder") == null ? "" : bundle.getString("reminder"));
        contentValues.put("activityNotes", bundle.getString("activityNotes") == null ? "" : bundle.getString("activityNotes"));
        contentValues.put("userActivityStatus", bundle.getString("userActivityStatus") == null ? "" : bundle.getString("userActivityStatus"));
        contentValues.put("address", bundle.getString("address") == null ? "" : bundle.getString("address"));
        contentValues.put("invitationStatus", bundle.getString("invitationStatus") == null ? "" : bundle.getString("invitationStatus"));
        contentValues.put("activityupdate", bundle.getString("activityupdate") == null ? "" : bundle.getString("activityupdate"));
        contentValues.put("unreadCount", bundle.getString("unreadCount") == null ? "" : bundle.getString("unreadCount"));
        contentValues.put("accept", bundle.getString("accept") == null ? "" : bundle.getString("accept"));

        contentValues.put("activitylocation", "");
        contentValues.put("activitydates", "");
        contentValues.put("imagepath", "");
        contentValues.put("QuickbloxGroupID", bundle.getString("QuickbloxGroupID") == null ? "0" : bundle.getString("QuickbloxGroupID"));
        contentValues.put("invitesToActivity", bundle.getString("invitesToActivity") == null ? "0" : bundle.getString("invitesToActivity"));
        contentValues.put("applozicgroupid", bundle.getString("QuickbloxGroupID") == null ? "0" : bundle.getString("QuickbloxGroupID"));

        db.insertWithOnConflict(TABLE_HKACTIVITIES, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);

    }

    public void createActivityForChatIcon(Bundle bundle, int invitesToActivity, String groupID) {

        ContentValues contentValues = new ContentValues();
        if (groupID != null) {
            contentValues.put("groupID", groupID);
        }

        if (invitesToActivity > 1)

        {
            contentValues.put("invitesToActivity", invitesToActivity);
            Log.i(TAG, "invitesToActivity count**************** " + invitesToActivity);
        } else {
            Log.i(TAG, "invitesToActivity count**************** " + invitesToActivity);
        }

        contentValues.put("activityID", bundle.getString("activityID") == null ? "" : bundle.getString("activityID"));

        db.insertWithOnConflict(TABLE_HKACTIVITIES_FOR_CHAT_ICON, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);

    }

    public JSONArray getAllActivitysFromDBToServer() {
        try {
            jArrayActivity = new JSONArray();
            String buildSQL = "SELECT * FROM " + TABLE_HKACTIVITIES + " where actionType='ADD' or actionType='Modify' or actionType='Complete' or actionType='REMOVE' or actionType='Cancel'";
            Cursor cursor = db.rawQuery(buildSQL, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {

                    JSONObject jObjectActivity = new JSONObject();

                    jObjectActivity.putOpt("quickbloxRoomJID", " ");
                    jObjectActivity.put("quickbloxGroupID", cursor.getString(cursor.getColumnIndex("QuickbloxGroupID")));

                    if (cursor.getString(cursor.getColumnIndex("applozicgroupid")) != null
                            && cursor.getString(cursor.getColumnIndex("applozicgroupid")).trim().length() > 0) {
                        jObjectActivity.put("applozicgroupid", cursor.getString(cursor.getColumnIndex("applozicgroupid")));
                    } else {
                        jObjectActivity.put("applozicgroupid", " ");
                    }

                    String activityId = cursor.getString(cursor.getColumnIndex("activityID"));
                    String actionTypeToCheck = cursor.getString(cursor.getColumnIndex("actionType"));

                    String updateMsg = " ";
                    jObjectActivity.putOpt("active", cursor.getString(cursor.getColumnIndex("active")));
                    jObjectActivity.putOpt("dateCreated", UtilityHelper.getServerDate(cursor.getString(cursor.getColumnIndex("dateCreated"))));
                    jObjectActivity.putOpt("activityStatus", cursor.getString(cursor.getColumnIndex("activityStatus")));
                    if (cursor.getString(cursor.getColumnIndex("updatedMsg")) != null) {
                        if (cursor.getString(cursor.getColumnIndex("updatedMsg")).trim().length() == 0)
                            updateMsg = " ";
                        else
                            updateMsg = cursor.getString(cursor.getColumnIndex("updatedMsg"));
                    }

                    if (updateMsg.contains("EVENT")) {
                        jObjectActivity.putOpt("updatedType", "EVENT");
                        updateMsg = updateMsg.replace(",EVENT", "").replace("EVENT", "");
                        updateMsg = updateMsg.replace("EndDate, ", "").replace("EndDate", "");
                        jObjectActivity.put("reminder", cursor.getString(cursor.getColumnIndex("reminder")));
                    } else if (updateMsg.contains("TASK")) {
                        jObjectActivity.putOpt("updatedType", "TASK");
                        updateMsg = updateMsg.replace(",TASK", "").replace("TASK", "");
                        updateMsg = updateMsg.replace("EndDate, ", "").replace("EndDate", "");
                    }

                    if (updateMsg != null && updateMsg.trim().length() > 1) {
                        jObjectActivity.putOpt("updateStringText", updateMsg);
                    }

                    String userHK_UUIDs = "SELECT * FROM " + TABLE_ACTIVITY_USERS + " WHERE activityId = " + "'" + activityId + "'";
                    Cursor HK_UUIDS_curser = db.rawQuery(userHK_UUIDs, null);
                    JSONArray jArrayHKUsers = new JSONArray();
                    if (HK_UUIDS_curser != null && HK_UUIDS_curser.moveToFirst()) {
                        do {
                            JSONObject jObjHKUsers = new JSONObject();
                            if (cursor.getString(cursor.getColumnIndex("activityOwner")).equalsIgnoreCase(HK_UUIDS_curser.getString(HK_UUIDS_curser.getColumnIndex("hK_UUID")))) {
                                jObjHKUsers.put("actionType", " ");
                            } else {
                                jObjHKUsers.put("actionType", HK_UUIDS_curser.getString(HK_UUIDS_curser.getColumnIndex("ActionType")));
                            }
                            jObjHKUsers.put("actionType", HK_UUIDS_curser.getString(HK_UUIDS_curser.getColumnIndex("ActionType")));
                            if (HK_UUIDS_curser.getString(HK_UUIDS_curser.getColumnIndex("hK_UUID")) != null
                                    && HK_UUIDS_curser.getString(HK_UUIDS_curser.getColumnIndex("ActionType")).trim().length() > 0) {
                                String HKUUID = HK_UUIDS_curser.getString(HK_UUIDS_curser.getColumnIndex("hK_UUID"));
                                jObjHKUsers.put("countRSVP", HK_UUIDS_curser.getString(HK_UUIDS_curser.getColumnIndex("countRSVP")));
                                jObjHKUsers.put("hK_UUID", HK_UUIDS_curser.getString(HK_UUIDS_curser.getColumnIndex("hK_UUID")));
                                if (HK_UUIDS_curser.getString(HK_UUIDS_curser.getColumnIndex("QuickBloxID")) != null
                                        && HK_UUIDS_curser.getString(HK_UUIDS_curser.getColumnIndex("QuickBloxID")).trim().length() > 0) {
                                    jObjHKUsers.put("quickBloxID", HK_UUIDS_curser.getString(HK_UUIDS_curser.getColumnIndex("QuickBloxID")));
                                } else {
                                    jObjHKUsers.put("quickBloxID", " ");
                                }
                                if (HK_UUIDS_curser.getString(HK_UUIDS_curser.getColumnIndex("ActionType")).equalsIgnoreCase("ADD")) {
                                    String longUrl = getLongUrl(HKUUID, activityId);
                                    jObjHKUsers.put("longurl", longUrl);
                                }
                            } else {
                                jObjHKUsers.put("countRSVP", " ");
                                jObjHKUsers.put("hK_UUID", " ");
                                jObjHKUsers.put("quickBloxID", " ");
                            }
                            jObjHKUsers.put("userActivityStatus", HK_UUIDS_curser.getString(HK_UUIDS_curser.getColumnIndex("userActivityStatus")));
                            jObjHKUsers.put("invitationStatus", HK_UUIDS_curser.getString(HK_UUIDS_curser.getColumnIndex("invitationStatus")));
                            jObjHKUsers.put("phoneNumber", HK_UUIDS_curser.getString(HK_UUIDS_curser.getColumnIndex("phoneNumber")));

                            jArrayHKUsers.put(jObjHKUsers);

                        } while (HK_UUIDS_curser.moveToNext());
                        HK_UUIDS_curser.close();
                    }

                    jObjectActivity.putOpt("hkUsers", jArrayHKUsers);
                    jObjectActivity.put("activityId", activityId);
                    jObjectActivity.put("blockCalendar", cursor.getString(cursor.getColumnIndex("blockCalendar")));
                    jObjectActivity.put("activityTitle", cursor.getString(cursor.getColumnIndex("activityTitle")));
                    jObjectActivity.put("actionType", cursor.getString(cursor.getColumnIndex("actionType")));
                    jObjectActivity.put("allowOtherToModify", cursor.getString(cursor.getColumnIndex("allowOtherToModify")));


                    String activityDateQuery = "SELECT * FROM " + TABLE_ACTIVITY_DATES + " WHERE activityID = " + "'" + activityId + "'";
                    Cursor datesCurser = db.rawQuery(activityDateQuery, null);
                    JSONArray jArrayDates = new JSONArray();
                    if (datesCurser != null && datesCurser.moveToFirst()) {
                        do {
                            JSONObject jObjDates = new JSONObject();
                            jObjDates.put("startDate", UtilityHelper.getServerDate(datesCurser.getString(datesCurser.getColumnIndex("startDate"))));
                            jObjDates.put("endDate", UtilityHelper.getServerDate(datesCurser.getString(datesCurser.getColumnIndex("endDate"))));
                            jObjDates.put("startTime", UtilityHelper.getTime(datesCurser.getString(datesCurser.getColumnIndex("startDate"))));
                            jObjDates.put("endTime", UtilityHelper.getTime(datesCurser.getString(datesCurser.getColumnIndex("endDate"))));
                            jArrayDates.put(jObjDates);
                        } while (datesCurser.moveToNext());
                        datesCurser.close();
                        jObjectActivity.putOpt("activityDates", jArrayDates);
                    }
                    jObjectActivity.put("dateModified", cursor.getString(cursor.getColumnIndex("dateModified")));
                    jObjectActivity.put("createdBy", cursor.getString(cursor.getColumnIndex("createdBy")));
                    if (actionTypeToCheck.equalsIgnoreCase("Add")) {
                        jObjectActivity.put("reminder", cursor.getString(cursor.getColumnIndex("reminder")));
                    }
                    jObjectActivity.put("activityOwner", cursor.getString(cursor.getColumnIndex("activityOwner")));
                    String buildSQL3 = "SELECT columnValue FROM " + TABLE_HKACTIVITIES_DETAILS + " WHERE activityId = " + "'" + activityId + "' and columnName = 'latLong'";
                    Cursor cursorLatLong = db.rawQuery(buildSQL3, null);
                    if (cursorLatLong != null) {
                        if (cursorLatLong.moveToFirst()) {
                            do {
                                jObjectActivity.put("location", cursorLatLong.getString(0));
                            } while (cursorLatLong.moveToNext());
                            cursorLatLong.close();
                        }
                    }
                    jObjectActivity.put("address", cursor.getString(cursor.getColumnIndex("address")));
                    jObjectActivity.put("activityNotes", cursor.getString(cursor.getColumnIndex("activityNotes")));

                    jArrayActivity.put(jObjectActivity);

                } while (cursor.moveToNext());
                cursor.close();
            }

        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }
        return jArrayActivity;
    }

    private String getLongUrl(String hkuuid, String activityID) {

        String userStatus = "";
        String query = "SELECT active FROM UserContacts WHERE HK_UUID = '" + hkuuid + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                userStatus = cursor.getString(0);
            } while (cursor.moveToNext());
            cursor.close();
        }
        if (userStatus.equalsIgnoreCase("false")) {

            String longUrl = Constants.LONG_URL +
                    "activityID=" + activityID +
                    "&hK_UUID=" + hkuuid;
            Log.i(TAG, "longUrl = " + longUrl);
            longUrl = longUrl.replaceAll(" ", "%20");
            return longUrl;
        } else {
            return " ";
        }
    }

    // TODO 19-07-2017 from here
    /*public ArrayList<QB_ID_And_Activity_ID> getAllActivityWithOutQBID() {
        ArrayList<QB_ID_And_Activity_ID> arrayList = new ArrayList<QB_ID_And_Activity_ID>();
        try {
            String buildSQL = "SELECT * FROM " + TABLE_HKACTIVITIES + " where ApplozicGroupCreated ='NO' OR QuickbloxGroupID = '0'";
            Cursor cursor = db.rawQuery(buildSQL, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    QB_ID_And_Activity_ID qb_id_and_activity_id = new QB_ID_And_Activity_ID();
                    qb_id_and_activity_id.setActivityID(cursor.getString(cursor.getColumnIndex("activityID")));
                    qb_id_and_activity_id.setActivityTitle(cursor.getString(cursor.getColumnIndex("activityTitle")));
                    arrayList.add(qb_id_and_activity_id);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception jsonException) {
            jsonException.printStackTrace();
        }
        return arrayList;
    }*/

    // TODO 19-07-2017 till here
    public void createActivityDetails(Bundle bundle) {

        ContentValues contentValues = new ContentValues();

        contentValues.put("activityDetailID", bundle.getString("activityDetailID") == null ? "" : bundle.getString("activityDetailID"));
        contentValues.put("activityID", bundle.getString("activityID") == null ? "" : bundle.getString("activityID"));
        if (bundle.containsKey("location")) {
            contentValues.put("columnName", "latLong");
            contentValues.put("columnValue", bundle.getString("location") == null ? "" : bundle.getString("location"));
        }
        contentValues.put("actionType", bundle.getString("actionType") == null ? "" : bundle.getString("actionType"));
        contentValues.put("dateCreated", bundle.getString("dateCreated") == null ? "" : bundle.getString("dateCreated"));
        contentValues.put("dateModified", bundle.getString("dateModified") == null ? "" : bundle.getString("dateModified"));
        contentValues.put("createdBy", bundle.getString("createdBy") == null ? "" : bundle.getString("createdBy"));
        contentValues.put("modifiedBy", bundle.getString("modifiedBy") == null ? "" : bundle.getString("modifiedBy"));

        db.insertWithOnConflict(TABLE_HKACTIVITIES_DETAILS, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void createActivityDates(HashMap<String, String> bundle) {

        ContentValues contentValues = new ContentValues();

        contentValues.put("ActivityDateID", bundle.get("ActivityDateID"));
//        contentValues.put("ActivityDateID", UUID.randomUUID().toString());
        contentValues.put("activityID", bundle.get("activityID"));
        contentValues.put("startDate", bundle.get("startDate"));
        contentValues.put("endDate", bundle.get("endDate"));
        contentValues.put("actionType", bundle.get("actionType"));
        contentValues.put("dateModified", bundle.get("dateModified"));
        contentValues.put("createdBy", bundle.get("createdBy"));
        contentValues.put("modifiedBy", bundle.get("modifiedBy"));
        contentValues.put("startTime", "");
        contentValues.put("endTime", "");
        contentValues.put("actionType", bundle.get("actionType") == null ? "" : bundle.get("actionType"));

        db.insertWithOnConflict(TABLE_ACTIVITY_DATES, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    }


    public void deleteActivityDates(String activityId) {
        db.delete(TABLE_ACTIVITY_DATES, "activityID= '" + activityId + "'", null);
    }

    public void createUserContacts(JSONObject jsonObject) {
        ContentValues contentValues = new ContentValues();
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("contactList");
            final int numberOfItemsInResp = jsonArray.length();
            Log.i(TAG, "contactListARRAY  : " + jsonArray.toString());

            for (int i = 0; i < numberOfItemsInResp; i++) {

                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                contentValues.put("HK_UUID", jsonObject1.optString("hK_UUID", "") == null ? "" : jsonObject1.optString("hK_UUID", ""));// jsonArray.getJSONObject(0).getString("hK_UUID").toString());//getJSONArray("contactList").getJSONObject(0).toString());
                contentValues.put("HK_ID", jsonObject1.optString("hKID", "") == null ? "" : jsonObject1.optString("hKID", ""));
                contentValues.put("PhotoPath", jsonObject1.optString("photo", "") == null ? "" : jsonObject1.optString("photo", ""));
                contentValues.put("DisplayName", jsonObject1.optString("displayName", "") == null ? "" : jsonObject1.optString("displayName", ""));
                contentValues.put("QuickBloxID", jsonObject1.optString("quickBloxID", "") == null ? "" : jsonObject1.optString("quickBloxID", ""));
                contentValues.put("StatusMessage", " ");
                contentValues.put("PhoneNumber", jsonObject1.optString("phone", "") == null ? "" : jsonObject1.optString("phone", ""));
                contentValues.put("active", jsonObject1.optString("active", "") == null ? "" : jsonObject1.optString("active", ""));

                db.insertWithOnConflict(TABLE_USER_CONTACTS, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);

                ContentValues values1 = new ContentValues();
                values1.put("hkID", jsonObject1.optString("hKID", "") == null ? "" : jsonObject1.optString("hKID", ""));
                values1.put("hkUUID", jsonObject1.optString("hK_UUID", "") == null ? "" : jsonObject1.optString("hK_UUID", ""));

                if (isExists(USER_PHONE_CONTACTS, "where contactNo = '" + jsonObject1.optString("phone", "") + "'")) {
                    db.update(USER_PHONE_CONTACTS, values1, "contactNo=?", new String[]{jsonObject1.optString("phone", "")});
                } else {
//                    db.insert(USER_PHONE_CONTACTS, null, values1);
                }

//                db.insertWithOnConflict(USER_PHONE_CONTACTS, null, values1, SQLiteDatabase.CONFLICT_REPLACE);
//                db.update(USER_PHONE_CONTACTS, values1, "contactNo=?", new String[]{jsonObject1.optString("phone", "")});

            }
        } catch (JSONException je) {
            je.printStackTrace();
        }

    }

    public ArrayList<ArrayList<String>> getFinalContactList() {

        String getFinalContacts = "SELECT IFNULL(UC.HK_UUID,'') AS HK_UUID, " +
                " DC.PhoneNumber, DC.DisplayName, UC.HK_ID, UC.StatusMessage, UC.PhotoPath , " +
                " UC.QuickBloxID, DC.DisplayPhoneNumber " +
                " FROM  DeviceContacts DC LEFT OUTER JOIN UserContacts UC ON  " +
                " LTRIM(RTRIM(UC.PhoneNumber)) =  LTRIM(RTRIM(DC.PhoneNumber)) " +
                " WHERE IFNULL(UC.HK_UUID,'') NOT IN (SELECT HK_UUID FROM Users) ORDER BY DC.DisplayName ASC ";

        Cursor cursor = db.rawQuery(getFinalContacts, null);
        ArrayList<ArrayList<String>> contactsList = new ArrayList<ArrayList<String>>();
        for (int i = 0; i < cursor.getCount(); i++) {
            ArrayList<String> values = new ArrayList<>();
            if (!cursor.isLast()) {
                cursor.moveToNext();
                values.add(cursor.getString(0));
                values.add(cursor.getString(1));
                values.add(cursor.getString(2));
                values.add(cursor.getString(3));
                values.add(cursor.getString(4));
                values.add(cursor.getBlob(5) == null ? "" : new String(cursor.getBlob(5)));
                contactsList.add(values);
            }
        }
        Log.i(TAG, "getFinalContact:  getFinalContactListCALLED");
        cursor.close();
        return contactsList;
    }


    public String getUserCountryCode() {

        String result = "";

        String getUserCountryCode = "SELECT REPLACE(PhoneNumber,SUBSTR(PhoneNumber,-10),'') AS UserCountryCode FROM Users";
        Cursor cursor = db.rawQuery(getUserCountryCode, null);
        if (cursor != null && cursor.getCount() > 0)
            cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            result = cursor.getString(0);
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }

    public ArrayList<ArrayList<String>> getNotificationData() {

        Log.i(TAG, "1getNotificationDataCALLED");

        String getNotificationData = "Select N._id,N.NotificationID, UC.HK_UUID, UC.DisplayName,"
                + "N.ActionType, A.ActivityTitle, N.ObjectID, N.dateCreated, A.BlockCalendar, isRead, UC.HK_ID "
                + "from Notification N INNER JOIN UserContacts UC ON n.SendFrom = UC.HK_UUID "
                + "INNER JOIN HKActivities A ON N.ObjectID = A.ActivityID "
                + "ORDER BY strftime('%Y-%m-%d %H:%M:%S',N.dateCreated) DESC";

        Cursor cursor = db.rawQuery(getNotificationData, null);

        ArrayList<ArrayList<String>> dateList = new ArrayList<ArrayList<String>>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ArrayList<String> values = new ArrayList<>();
            values.add(cursor.getString(0) == null ? "" : cursor.getString(0));
            values.add(cursor.getString(1) == null ? "" : cursor.getString(1));
            values.add(cursor.getString(2) == null ? "" : cursor.getString(2));
            values.add(cursor.getString(3) == null ? "" : cursor.getString(3));
            values.add(cursor.getString(4) == null ? "" : cursor.getString(4));
            values.add(cursor.getString(5) == null ? "" : cursor.getString(5));
            values.add(cursor.getString(6) == null ? "" : cursor.getString(6));
            values.add(cursor.getString(7) == null ? "" : cursor.getString(7));
            dateList.add(values);
            cursor.moveToNext();
        }
        cursor.close();
        return dateList;
    }


    public ArrayList<ArrayList<String>> getPendingInvitationsList(String hk_UUID) {

        ArrayList<ArrayList<String>> pIList = new ArrayList<ArrayList<String>>();

        Log.i(TAG, "in getPendingInvitationsList");

        String buildSQL = "Select HA._id,HA.activityTitle, HA.ActivityID, HA.ActivityOwner,"
                + " IFNULL(DC.DisplayName,IFNULL(UC.DisplayName,UC.PhoneNumber)) AS DisplayName, MIN(AD.StartDate) AS StartDate, MAX(AD.EndDate) AS EndDate,"
                + " HA.BlockCalendar, HA.allowOtherToModify AS CanEdit, MIN(AD.StartTime) AS StartTime, MAX(AD.EndTime) AS EndTime,"
                + " HA.QuickbloxGroupID ,"
                + " HA.QuickbloxRoomJID," +
                "  HA.unreadCount,"
                + " CASE WHEN HA.activityOwner = '" + hk_UUID + "' THEN 'Owner'"
                + " WHEN IFNULL(AU.InvitationStatus,'') = 'Pending' THEN 'Pending'"
                + " WHEN IFNULL(AU.InvitationStatus,'') = 'Yes' THEN 'Yes' END Status, HA.dateCreated "
                + " FROM HKActivities HA "
                + " INNER JOIN ActivityUsers AU ON HA.ActivityID = AU.ActivityID AND AU.HK_UUID = '" + hk_UUID + "'" //changed by prakash 06-07 6:45pm
                + " LEFT OUTER JOIN UserContacts UC ON HA.ActivityOwner = UC.HK_UUID "
                + " INNER JOIN ActivityDates AD ON HA.ActivityID = AD.ActivityID " //changed by prakash 06-07 6:45pm
                + " LEFT OUTER JOIN DeviceContacts DC ON UC.PhoneNumber = DC.PhoneNumber "
                + " WHERE strftime('%Y-%m-%d', AD.EndDate) >= DATE('now') AND HA.Active = 1 AND HA.ActivityStatus = 'Active' AND AU.InvitationStatus = 'Pending'"
                + " GROUP BY HA.ActivityID "
                + " ORDER BY strftime('%Y-%m-%d %H:%M:%S', AD.EndDate) DESC";


        Cursor cursor = db.rawQuery(buildSQL, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                ArrayList<String> values = new ArrayList<>();
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    values.add(cursor.getString(i));
                }
                pIList.add(values);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return pIList;
    }

    public ArrayList<ActivityDetails> getPendingInvitationsRCVList(String hk_UUID) {

        ArrayList<ActivityDetails> detailsArray = new ArrayList<>();
        Log.i(TAG, "in getPendingInvitationsList");

        String buildSQL = "Select HA._id,HA.activityTitle, HA.ActivityID, HA.ActivityOwner,"
                + " IFNULL(DC.DisplayName,IFNULL(UC.DisplayName,UC.PhoneNumber)) AS DisplayName, MIN(AD.StartDate) AS StartDate, MAX(AD.EndDate) AS EndDate,"
                + " HA.BlockCalendar, HA.allowOtherToModify AS CanEdit, MIN(AD.StartTime) AS StartTime, MAX(AD.EndTime) AS EndTime,"
                + " HA.QuickbloxGroupID ,"
                + " HA.QuickbloxRoomJID,"
                + " HA.unreadCount,"
                + " HA.address,HA.reminder,"
                + " CASE WHEN HA.activityOwner = '" + hk_UUID + "' THEN 'Owner'"
                + " WHEN IFNULL(AU.InvitationStatus,'') = 'Pending' THEN 'Pending'"
                + " WHEN IFNULL(AU.InvitationStatus,'') = 'Yes' THEN 'Yes' END Status, HA.dateCreated, HA.invitesToActivity "
                + " FROM HKActivities HA "
                + " INNER JOIN ActivityUsers AU ON HA.ActivityID = AU.ActivityID AND AU.HK_UUID = '" + hk_UUID + "'" //changed by prakash 06-07 6:45pm
                + " LEFT OUTER JOIN UserContacts UC ON HA.ActivityOwner = UC.HK_UUID "
                + " INNER JOIN ActivityDates AD ON HA.ActivityID = AD.ActivityID " //changed by prakash 06-07 6:45pm
                + " LEFT OUTER JOIN DeviceContacts DC ON UC.PhoneNumber = DC.PhoneNumber "
                + " WHERE strftime('%Y-%m-%d', AD.EndDate) >= DATE('now') AND HA.Active = 1 AND HA.ActivityStatus = 'Active' AND AU.InvitationStatus = 'Pending'"
                + " GROUP BY HA.ActivityID "
                + " ORDER BY strftime('%Y-%m-%d %H:%M:%S', AD.EndDate) DESC";


        Cursor cursor = db.rawQuery(buildSQL, null);
        if (cursor != null) {
            if (cursor.getCount() != 0) {
                for (int i = 0; i <= cursor.getCount(); i++) {
                    if (!cursor.isLast()) {
                        cursor.moveToNext();
                        ActivityDetails activityDetailsDao = new ActivityDetails();
                        activityDetailsDao.setActivityTittle(cursor.getString(1));
                        activityDetailsDao.setActivityID(cursor.getString(2));
                        activityDetailsDao.setActivityOwner(cursor.getString(3));
                        activityDetailsDao.setDisplayName(cursor.getString(4));
                        activityDetailsDao.setStartDate(cursor.getString(5));
                        activityDetailsDao.setEndDate(cursor.getString(6));
                        activityDetailsDao.setStartTime(UtilityHelper.getTime(cursor.getString(5)));
                        activityDetailsDao.setEndTime(UtilityHelper.getTime(cursor.getString(6)));
                        activityDetailsDao.setBlockCalendar(cursor.getString(7));
                        activityDetailsDao.setCanEdit(cursor.getString(8));
                        activityDetailsDao.setActivityStatus(cursor.getString(14));
                        activityDetailsDao.setQuickbloxGroupID(cursor.getString(11));
                        activityDetailsDao.setQuickbloxRoomJID(cursor.getString(12));
                        activityDetailsDao.setUnReadCount(cursor.getInt(13));
                        activityDetailsDao.setAddress(cursor.getString(14));
                        activityDetailsDao.setReminder(cursor.getString(15));
                        activityDetailsDao.setStatus(cursor.getString(16));
                        activityDetailsDao.setInviteesToActivity(cursor.getInt(18));

                        detailsArray.add(activityDetailsDao);
                    }
                }
            }
            cursor.close();
        }
        return detailsArray;
    }


    public int getPendingInvitationsCount(String hk_UUID) {

        int pendingInvitationCount = 0;
        Log.i(TAG, "in getPendingInvitationsCount");

        String buildSQL = "select DISTINCT HA.ActivityID FROM HKActivities HA " +
                "INNER JOIN ActivityUsers AU ON HA.ActivityID = AU.ActivityID AND AU.HK_UUID = '" + hk_UUID + "' " +
                "LEFT OUTER JOIN UserContacts UC ON HA.ActivityOwner = UC.HK_UUID " +
                "INNER JOIN ActivityDates AD ON HA.ActivityID = AD.ActivityID " +
                "LEFT OUTER JOIN DeviceContacts DC ON UC.PhoneNumber = DC.PhoneNumber " +
                "where strftime('%Y-%m-%d', AD.EndDate) >= DATE('now') AND HA.Active = 1 " +
                "AND HA.ActivityStatus = 'Active' AND AU.InvitationStatus = 'Pending' " +
                "AND HA.ActivityOwner !='" + hk_UUID + "'";

        Cursor cursor = db.rawQuery(buildSQL, null);
        if (cursor != null) {
            pendingInvitationCount = cursor.getCount();
            cursor.close();
        }
        return pendingInvitationCount;
    }


    public String getUserName() {
        String result = "";
        String query = "SELECT HK_UUID FROM " + TABLE_USERS;
        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            result = cursor.getString(0);
            cursor.moveToNext();
        }
        return result;
    }

    public int getInviteesCount(String ActivityID) {
        int result = 1;
        String query = "SELECT invitesToActivity FROM " + TABLE_HKACTIVITIES_FOR_CHAT_ICON + " WHERE ActivityID = '" + ActivityID + "'";
        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            result = cursor.getInt(0);
            cursor.moveToNext();
        }
        return result;
    }

    public String getGroupID(String ActivityID) {
        String result = "";
        String query = "SELECT groupID FROM " + TABLE_HKACTIVITIES_FOR_CHAT_ICON + " WHERE ActivityID = '" + ActivityID + "'";
        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            result = cursor.getString(0);
            cursor.moveToNext();
        }
        return result;
    }


    public ArrayList<String> getUserDetails() {
        ArrayList<String> result = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_USERS;
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            result.add(cursor.getString(2));
            result.add(cursor.getString(8));
            cursor.moveToNext();
        }
        return result;
    }

    public AppUser getAppUser() {

        AppUser result = new AppUser();

        String query = "SELECT * FROM " + TABLE_USERS;

        Cursor cursor = db.rawQuery(query, null);
        Log.i(TAG, "cursor count = " + cursor.getCount());
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            result.HK_UUID = (cursor.getString(1));
            result.DisplayName = (cursor.getString(2));
            result.PhotoPath = (cursor.getString(4));
            result.Settings = (cursor.getString(5));
            result.HK_ID = (cursor.getString(6));
            result.QuickBloxID = (cursor.getString(7));
            result.PhoneNumber = (cursor.getString(8));
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }

    public ArrayList<ActivityDetailsDao> getActivityTasksData(String taskDisplay, String status, String hk_UUID) {
        ArrayList<ActivityDetailsDao> detailsArray = new ArrayList<ActivityDetailsDao>();

        StringBuilder finalQuery = new StringBuilder();

        String query = "SELECT HA._id,HA.ActivityID,HA.activityTitle, HA.activityOwner, HA.blockCalendar, HA.activityStatus," +
                "HA.invitesToActivity," +
                " HA.allowOtherToModify AS CanEdit," +
                " MIN(AD.StartTime) AS StartTime, MAX(AD.EndTime) AS EndTime,CASE WHEN HA.ActivityOwner = (SELECT HK_UUID FROM Users) THEN 'You' ELSE IFNULL(DC.DisplayName,IFNULL(UC.DisplayName,UC.PhoneNumber)) END AS DisplayName ," +
                " HA.QuickbloxGroupID , " +
                "HA.QuickbloxRoomJID ," +
                " AD.startDate AS StartDate,IFNULL(ST.SnoozeDate,AD.EndDate) AS EndDate, " +
                "HA.unreadCount ," +
                "CASE WHEN HA.activityOwner = '" + hk_UUID + "' THEN 'Owner'" +
                " WHEN IFNULL(AU.InvitationStatus,'') = 'Pending' THEN 'Pending'" +
                " WHEN IFNULL(AU.InvitationStatus,'') = 'Yes' THEN 'Yes' END Status , " +
                "HA.userActivityStatus," +
                " CASE WHEN HA.ActivityStatus = 'Completed' THEN HA.ActivityStatus ELSE " +
                "CASE WHEN strftime('%Y-%m-%d %H:%M', IFNULL(ST.SnoozeDate, AD.EndDate)) <= strftime('%Y-%m-%d %H:%M', datetime('now', 'localtime')) " +
                " THEN 'OD' ELSE 'FE' END " +
                " END AS EventType, ST.SnoozeDate " +
                " FROM HKActivities HA" +
                " LEFT OUTER JOIN UserContacts UC ON HA.ActivityOwner = UC.HK_UUID" +
                " LEFT OUTER JOIN ActivityUsers AU ON AU.ActivityID = HA.ActivityID AND AU.HK_UUID = '" + hk_UUID + "'" +
                " LEFT OUTER JOIN DeviceContacts DC ON UC.PhoneNumber = DC.PhoneNumber" +
                " LEFT OUTER JOIN ActivityDates AD ON HA.ActivityID =  AD.ActivityID" +
                " LEFT OUTER JOIN SnoozeTasks ST ON HA.ActivityID = ST.ActivityTaskID" +
                " WHERE HA.Active = 1 AND HA.blockCalendar = 0 AND IFNULL(HA.ActionType,'') != 'DELETE' AND" +
                " HA.activityStatus  != 'inActive' AND IFNULL(HA.invitationStatus,'Yes') IN ('Pending', 'Yes')"/* +
                " GROUP BY HA.ActivityID"*/;

        finalQuery.append(query);

        if (taskDisplay.equalsIgnoreCase("FutureTasks")) {//TODO Future Task Query
            String futureTaskQuery = " AND HA.userActivityStatus != 'Completed' AND strftime('%Y-%m-%d %H:%M',IFNULL(ST.SnoozeDate,AD.EndDate)) > strftime('%Y-%m-%d %H:%M',datetime('now','localtime')) ";
            finalQuery.append(futureTaskQuery);
            finalQuery.append("GROUP BY HA.ActivityID ORDER BY IFNULL(ST.SnoozeDate,AD.EndDate) ASC");
        } else if (taskDisplay.equalsIgnoreCase("CompletedTasks")) {//TODO Completed Task Query
            String completedTaskQuery = " AND HA.userActivityStatus = 'Completed' ";
            finalQuery.append(completedTaskQuery);
            finalQuery.append("GROUP BY HA.ActivityID ORDER BY IFNULL(ST.SnoozeDate,AD.EndDate) DESC");
        } else {    //OverDue Tasks Query
            String overDueTaskQuery = " AND HA.userActivityStatus != 'Completed' AND strftime('%Y-%m-%d %H:%M',IFNULL(ST.SnoozeDate,AD.EndDate)) <= strftime('%Y-%m-%d %H:%M',datetime('now','localtime')) ";
            finalQuery.append(overDueTaskQuery);
            finalQuery.append("GROUP BY HA.ActivityID ORDER BY IFNULL(ST.SnoozeDate,AD.EndDate) DESC");
        }
        Cursor cursor = db.rawQuery(finalQuery.toString(), null);
        if (cursor != null) {
            if (cursor.getCount() != 0) {
                for (int i = 0; i <= cursor.getCount(); i++) {
                    if (!cursor.isLast()) {
                        cursor.moveToNext();

                        ActivityDetailsDao activityDetailsDao = new ActivityDetailsDao();
                        activityDetailsDao.setActivityID(cursor.getString(1));
                        activityDetailsDao.setActivityTitle(cursor.getString(2));
                        activityDetailsDao.setActivityOwner(cursor.getString(3));
                        activityDetailsDao.setBlockCalendar(cursor.getString(4));
                        activityDetailsDao.setActivityStatus(cursor.getString(5));
                        activityDetailsDao.setInvitiesToActivity(cursor.getInt(6));
                        activityDetailsDao.setCanEdit(cursor.getString(7));
                        activityDetailsDao.setStartTime(cursor.getString(8));
                        activityDetailsDao.setEndTime(cursor.getString(9));
                        activityDetailsDao.setDisplayName(cursor.getString(10));
                        activityDetailsDao.setQuickbloxGroupID(cursor.getString(11));
                        activityDetailsDao.setQuickbloxRoomJID(cursor.getString(12));
                        activityDetailsDao.setStartDate(cursor.getString(13));
                        activityDetailsDao.setEndDate(cursor.getString(14));
                        activityDetailsDao.setUnreadCount(cursor.getInt(15));
                        activityDetailsDao.setStatus(cursor.getString(16));
                        activityDetailsDao.setUserActivityStatus(cursor.getString(17));
                        activityDetailsDao.setEventType(cursor.getString(18));
                        activityDetailsDao.setSnoozeDateNTime(cursor.getString(19));
                        detailsArray.add(activityDetailsDao);
                    }
                }
            }

        }
        cursor.close();

        return detailsArray;
    }


    public ArrayList<String> getActivityDetails(String activityId, String activityOwner, String hk_UUID) {
        String query = "SELECT HA.ActivityID,HA.activityTitle, HA.activityOwner, HA.blockCalendar, HA.activityStatus, HA.allowOtherToModify AS CanEdit, " +
                "MIN(AD.StartTime) AS StartTime, MAX(AD.EndTime) AS EndTime  , CASE WHEN HA.ActivityOwner = (SELECT HK_UUID FROM Users) THEN 'You' ELSE IFNULL(DC.DisplayName,IFNULL(UC.DisplayName,UC.PhoneNumber)) END AS DisplayName , " +
                "HA.QuickbloxGroupID , HA.QuickbloxRoomJID ,HA.invitationStatus,HA.ActivityNotes,HA.userActivityStatus,HA.countRSVP,HA.activityRSVP,HA.reminder,   AD.StartDate AS StartDate,  AD.EndDate AS EndDate, " +
                "HA.unreadCount ,HA.address, " +
                "CASE WHEN HA.activityOwner = '" + activityOwner + "'" + " THEN 'Owner' " +
                "WHEN IFNULL(AU.InvitationStatus,'') = 'Pending' THEN 'Pending' " +
                "WHEN IFNULL(AU.InvitationStatus,'') = 'Yes' THEN 'Yes' END Status, " +
                "CASE WHEN  strftime('%Y-%m-%d %H:%M',AD.EndDate) <= strftime('%Y-%m-%d %H:%M',datetime('now','localtime'))  THEN 'OD' ELSE 'FE' END AS EventType, " +
                "IFNULL(HAD.ColumnValue,'') AS LatLong,HA.actionType " + // added this line to get latLong values for event
                "FROM HKActivities HA " +// added ",HA.actionType " by Raj at line number 2105 to read the action type of respective activity on 02-3-2017
                "LEFT OUTER JOIN UserContacts UC ON HA.ActivityOwner = UC.HK_UUID " +
                "LEFT OUTER JOIN ActivityUsers AU ON AU.ActivityID = HA.ActivityID AND AU.HK_UUID = '" + hk_UUID + "'" +
                "LEFT OUTER JOIN DeviceContacts DC ON UC.PhoneNumber = DC.PhoneNumber " +
                "LEFT OUTER JOIN ActivityDates AD ON HA.ActivityID =  AD.ActivityID " +
                "LEFT OUTER JOIN HKActivitiesDetails HAD ON HAD.ActivityID = HA.ActivityID AND HAD.ColumnName = 'latLong'" + // added this line to get latLong values for event
                "WHERE HA.Active = 1 AND IFNULL(HA.ActionType, '') != 'DELETE' " +
                "AND  HA.ActivityID = '" + activityId + "'" +
                //"AND strftime('%Y-%m-%d %H:%M',AD.EndDate) <= strftime('%Y-%m-%d %H:%M',datetime('now','localtime'))" +
                "GROUP BY HA.ActivityID ORDER BY AD.EndDate DESC";

        Cursor cursor = db.rawQuery(query, null);
        ArrayList<String> activityDetails = new ArrayList<>();
        if (cursor != null && cursor.getCount() != 0) {
            if (!cursor.isLast()) {
                cursor.moveToNext();
                activityDetails.add(cursor.getString(0) == null ? "" : cursor.getString(0));
                activityDetails.add(cursor.getString(1) == null ? "" : cursor.getString(1));
                activityDetails.add(cursor.getString(2) == null ? "" : cursor.getString(2));
                activityDetails.add(cursor.getString(3) == null ? "" : cursor.getString(3));
                activityDetails.add(cursor.getString(4) == null ? "" : cursor.getString(4));
                activityDetails.add(cursor.getString(5) == null ? "" : cursor.getString(5));
                activityDetails.add(cursor.getString(6) == null ? "" : cursor.getString(6));
                activityDetails.add(cursor.getString(7) == null ? "" : cursor.getString(7));
                activityDetails.add(cursor.getString(8) == null ? "" : cursor.getString(8));
                activityDetails.add(cursor.getString(9) == null ? "" : cursor.getString(9));
                activityDetails.add(cursor.getString(10) == null ? "" : cursor.getString(10));
                activityDetails.add(cursor.getString(11) == null ? "" : cursor.getString(11));
                activityDetails.add(cursor.getString(12) == null ? "" : cursor.getString(12));
                activityDetails.add(cursor.getString(13) == null ? "" : cursor.getString(13));
                activityDetails.add(cursor.getString(14) == null ? "" : cursor.getString(14));
                activityDetails.add(cursor.getString(15) == null ? "" : cursor.getString(15));
                activityDetails.add(cursor.getString(16) == null ? "" : cursor.getString(16));
                activityDetails.add(cursor.getString(17) == null ? "" : cursor.getString(17));
                activityDetails.add(cursor.getString(18) == null ? "" : cursor.getString(18));
                activityDetails.add(cursor.getString(19) == null ? "" : cursor.getString(19));
                activityDetails.add(cursor.getString(20) == null ? "" : cursor.getString(20));
                activityDetails.add(cursor.getString(21) == null ? "" : cursor.getString(21));
                activityDetails.add(cursor.getString(22) == null ? "" : cursor.getString(22));
                activityDetails.add(cursor.getString(23) == null ? "" : cursor.getString(23));
                activityDetails.add(cursor.getString(24) == null ? "" : cursor.getString(24)); // TODO actionType

            }
        }
        cursor.close();

        return activityDetails;
    }

    public ArrayList<String> getTaskDetails(String activityId, String activityOwner, String hk_UUID) {
        String query = "SELECT HA.ActivityID,HA.activityTitle, HA.activityOwner, HA.blockCalendar, HA.activityStatus, HA.allowOtherToModify AS CanEdit, " +
                "MIN(AD.StartTime) AS StartTime, MAX(AD.EndTime) AS EndTime  , CASE WHEN HA.ActivityOwner = (SELECT HK_UUID FROM Users) THEN 'You' ELSE IFNULL(DC.DisplayName,IFNULL(UC.DisplayName,UC.PhoneNumber)) END AS DisplayName , " +
                "HA.QuickbloxGroupID , HA.QuickbloxRoomJID ," +
                "HA.invitationStatus,HA.ActivityNotes," +
                "HA.userActivityStatus," +
                "HA.countRSVP,HA.activityRSVP,HA.reminder,   AD.StartDate AS StartDate,  AD.EndDate AS EndDate, " +
                "HA.unreadCount ," +
                "HA.address, " +
                "CASE WHEN HA.activityOwner = '" + activityOwner + "'" + " THEN 'Owner' " +
                "WHEN IFNULL(AU.InvitationStatus,'') = 'Pending' THEN 'Pending' " +
                "WHEN IFNULL(AU.InvitationStatus,'') = 'Yes' THEN 'Yes' END Status, " +
                "CASE WHEN  strftime('%Y-%m-%d %H:%M',IFNULL(ST.SnoozeDate,AD.EndDate)) <= strftime('%Y-%m-%d %H:%M',datetime('now','localtime'))  THEN 'OD' ELSE 'FE' END AS EventType, " +
                "IFNULL(HAD.ColumnValue,'') AS LatLong" +
                ",HA.actionType" +
                ",ST.SnoozeDate " + // added this line to get latLong values for event
                "FROM HKActivities HA " +// added ",HA.actionType " by Raj at line number 2105 to read the action type of respective activity on 02-3-2017
                "LEFT OUTER JOIN UserContacts UC ON HA.ActivityOwner = UC.HK_UUID " +// added ",ST.SnoozeDate  " by Raj at line number 2170 to read the snoozeDate of activity on 21-3-2017
                "LEFT OUTER JOIN ActivityUsers AU ON AU.ActivityID = HA.ActivityID AND AU.HK_UUID = '" + hk_UUID + "'" +
                "LEFT OUTER JOIN SnoozeTasks ST ON ST.ActivityTaskID = HA.ActivityID " +
                "LEFT OUTER JOIN DeviceContacts DC ON UC.PhoneNumber = DC.PhoneNumber " +
                "LEFT OUTER JOIN ActivityDates AD ON HA.ActivityID =  AD.ActivityID " +
                "LEFT OUTER JOIN HKActivitiesDetails HAD ON HAD.ActivityID = HA.ActivityID AND HAD.ColumnName = 'latLong'" + // added this line to get latLong values for event
                "WHERE HA.Active = 1 AND IFNULL(HA.ActionType, '') != 'DELETE' " +
                "AND  HA.ActivityID = '" + activityId + "'" +
                //"AND strftime('%Y-%m-%d %H:%M',AD.EndDate) <= strftime('%Y-%m-%d %H:%M',datetime('now','localtime'))" +
                "GROUP BY HA.ActivityID ORDER BY AD.EndDate DESC";

        Cursor cursor = db.rawQuery(query, null);
        ArrayList<String> activityDetails = new ArrayList<>();
        if (cursor != null && cursor.getCount() != 0) {
            if (!cursor.isLast()) {
                cursor.moveToNext();
                activityDetails.add(cursor.getString(0) == null ? "" : cursor.getString(0));
                activityDetails.add(cursor.getString(1) == null ? "" : cursor.getString(1));
                activityDetails.add(cursor.getString(2) == null ? "" : cursor.getString(2));
                activityDetails.add(cursor.getString(3) == null ? "" : cursor.getString(3));
                activityDetails.add(cursor.getString(4) == null ? "" : cursor.getString(4));
                activityDetails.add(cursor.getString(5) == null ? "" : cursor.getString(5));
                activityDetails.add(cursor.getString(6) == null ? "" : cursor.getString(6));
                activityDetails.add(cursor.getString(7) == null ? "" : cursor.getString(7));
                activityDetails.add(cursor.getString(8) == null ? "" : cursor.getString(8));
                activityDetails.add(cursor.getString(9) == null ? "" : cursor.getString(9));
                activityDetails.add(cursor.getString(10) == null ? "" : cursor.getString(10));
                activityDetails.add(cursor.getString(11) == null ? "" : cursor.getString(11));
                activityDetails.add(cursor.getString(12) == null ? "" : cursor.getString(12));
                activityDetails.add(cursor.getString(13) == null ? "" : cursor.getString(13));
                activityDetails.add(cursor.getString(14) == null ? "" : cursor.getString(14));
                activityDetails.add(cursor.getString(15) == null ? "" : cursor.getString(15));
                activityDetails.add(cursor.getString(16) == null ? "" : cursor.getString(16));
                activityDetails.add(cursor.getString(17) == null ? "" : cursor.getString(17));
                activityDetails.add(cursor.getString(18) == null ? "" : cursor.getString(18));
                activityDetails.add(cursor.getString(19) == null ? "" : cursor.getString(19));
                activityDetails.add(cursor.getString(20) == null ? "" : cursor.getString(20));
                activityDetails.add(cursor.getString(21) == null ? "" : cursor.getString(21));
                activityDetails.add(cursor.getString(22) == null ? "" : cursor.getString(22));
                activityDetails.add(cursor.getString(23) == null ? "" : cursor.getString(23));
                activityDetails.add(cursor.getString(24) == null ? "" : cursor.getString(24));// TODO actionType
                activityDetails.add(cursor.getString(25) == null ? "" : cursor.getString(25));// TODO get the snooze time of respective activity

            }
        }
        cursor.close();

        return activityDetails;
    }

    public ArrayList<ActivityDetails> getEventForCalendar(String hk_UUID, String selectedDate) {
//        ArrayList<ActivityDetailsDao> detailsArray = new ArrayList<ActivityDetailsDao>();
        ArrayList<ActivityDetails> detailsArray = new ArrayList<>();
        String query = "SELECT HA.ActivityID,HA.activityTitle, HA.activityOwner, HA.blockCalendar, HA.activityStatus, HA.allowOtherToModify AS CanEdit, " +
                " MIN(AD.StartTime) AS StartTime, MAX(AD.EndTime) AS EndTime,CASE WHEN HA.ActivityOwner = (SELECT HK_UUID FROM Users) THEN 'You' ELSE IFNULL(DC.DisplayName,IFNULL(UC.DisplayName,UC.PhoneNumber)) END AS DisplayName , " +
                " HA.QuickbloxGroupID , HA.QuickbloxRoomJID ,  AD.StartDate AS StartDate, IFNULL(ST.SnoozeDate,AD.EndDate) AS EndDate ," +
                " HA.unreadCount ," +
                "  HA.address,HA.reminder, " +
                " CASE WHEN HA.activityOwner = '" + hk_UUID + "' THEN 'Owner' " +
                " WHEN IFNULL(AU.InvitationStatus,'') = 'Pending' THEN 'Pending' " +
                " WHEN IFNULL(AU.InvitationStatus,'') = 'Yes' THEN 'Yes' END Status, HA.invitesToActivity " +
                " FROM HKActivities HA " +
                " LEFT OUTER JOIN UserContacts UC ON HA.ActivityOwner = UC.HK_UUID " +
                " LEFT OUTER JOIN ActivityUsers AU ON AU.ActivityID = HA.ActivityID AND AU.HK_UUID = '" + hk_UUID + "'" +
                " LEFT OUTER JOIN DeviceContacts DC ON UC.PhoneNumber = DC.PhoneNumber " +
                " LEFT OUTER JOIN ActivityDates AD ON HA.ActivityID =  AD.ActivityID " +
                " LEFT OUTER JOIN SnoozeTasks ST ON HA.ActivityID = ST.ActivityTaskID" +
//                " WHERE HA.Active = 1 AND IFNULL(HA.ActionType,'') != 'DELETE' AND blockCalendar = '0' " +
//                " AND HA.invitationStatus IN ('Pending', 'Yes')" +
                " WHERE HA.ActionType != 'DELETE' AND blockCalendar = '1' " +
                " AND strftime('%Y-%m-%d' ,AD.StartDate) = '" + selectedDate + "'" +
                " AND HA.invitationStatus != 'Delete'" +
                " AND HA.invitationStatus = 'Yes'" +
                " GROUP BY HA.ActivityID  ORDER BY AD.StartDate ASC";
                /*" AND HA.ActivityOwner = CASE WHEN HA.ActivityStatus = 'inActive' THEN '" + hk_UUID + "' ELSE HA.ActivityOwner END " +
                " GROUP BY HA.ActivityID  ORDER BY AD.StartDate ASC";*/

        Cursor cursor = db.rawQuery(query.toString(), null);
        if (cursor != null) {
            if (cursor.getCount() != 0) {
                for (int i = 0; i <= cursor.getCount(); i++) {
                    if (!cursor.isLast()) {
                        cursor.moveToNext();

                        ActivityDetails activityDetailsDao = new ActivityDetails();
                        activityDetailsDao.setActivityID(cursor.getString(0));
                        activityDetailsDao.setActivityTittle(cursor.getString(1));
                        activityDetailsDao.setActivityOwner(cursor.getString(2));
                        activityDetailsDao.setBlockCalendar(cursor.getString(3));
                        activityDetailsDao.setActivityStatus(cursor.getString(4));
                        activityDetailsDao.setCanEdit(cursor.getString(5));
                        activityDetailsDao.setStartTime(UtilityHelper.getTime(cursor.getString(11)));
                        activityDetailsDao.setEndTime(UtilityHelper.getTime(cursor.getString(12)));
                        activityDetailsDao.setDisplayName(cursor.getString(8));
                        activityDetailsDao.setQuickbloxGroupID(cursor.getString(9));
                        activityDetailsDao.setQuickbloxRoomJID(cursor.getString(10));
                        activityDetailsDao.setStartDate(cursor.getString(11));
                        activityDetailsDao.setEndDate(cursor.getString(12));
                        activityDetailsDao.setUnReadCount(cursor.getInt(13));
                        activityDetailsDao.setAddress(cursor.getString(14));
                        activityDetailsDao.setReminder(cursor.getString(15));
                        activityDetailsDao.setStatus(cursor.getString(16));
                        activityDetailsDao.setInviteesToActivity(cursor.getInt(17));

                        detailsArray.add(activityDetailsDao);
                    }
                }
            }

        }
        cursor.close();
//        db.close();
        return detailsArray;
    }

    public String getValueOfUser(String hkuuid, String column) {
        String query = "Select " + column + " from " + TABLE_USER_CONTACTS + " where HK_UUID='" + hkuuid + "'";
        Log.i(TAG, "user query " + query);
        String value = "";
        Cursor cur = db.rawQuery(query, null);
        cur.moveToFirst();
        if (cur.getCount() > 0) {
            value = cur.getString(cur.getColumnIndex(column));
        }
        cur.close();
        return value;
    }

    public int getActivityUsersCount(String activityId) {
        String query = "Select * from " + TABLE_ACTIVITY_USERS + " WHERE activityID='" + activityId + "'AND invitationStatus != 'No' AND userActivityStatus !='inActive'";
        int value = 0;
        Cursor cur = db.rawQuery(query, null);
        cur.moveToFirst();
        value = cur.getCount();
        cur.close();
        return value;
    }

    /*public ArrayList<SelectedContactObject> getActivityUsers(String activityId) {
        ArrayList<SelectedContactObject> values = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_ACTIVITY_USERS + " WHERE activityID='" + activityId + "' AND ActionType != 'REMOVE' AND invitationStatus != 'Delete'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            if (cursor.getCount() != 0) {
                while (cursor.moveToNext()) {
                    SelectedContactObject user = new SelectedContactObject(Parcel.obtain());
                    String hkuuid = cursor.getString(cursor.getColumnIndex("hK_UUID"));
                    String invitationStatus = cursor.getString(cursor.getColumnIndex("invitationStatus"));
                    String accept = cursor.getString(cursor.getColumnIndex("accept"));
                    String phone = cursor.getString(cursor.getColumnIndex("phoneNumber"));
                    String countRSVP = cursor.getString(cursor.getColumnIndex("countRSVP"));
                    String quickBloxId = cursor.getString(cursor.getColumnIndex("QuickBloxID"));
                    String displayName = getValueOfUser(hkuuid, "DisplayName");
                    String hkId = getValueOfUser(hkuuid, "HK_ID");
                    String lastReadTime = cursor.getString(cursor.getColumnIndex("deliveredTime"));
                    String userActivityStatus = cursor.getString(cursor.getColumnIndex("userActivityStatus"));

                    user.setName(displayName);
                    user.setNumber(phone);
                    user.setHkID(hkId);
                    user.setInvitationStatus(invitationStatus);
                    user.setHkUUID(hkuuid);
                    user.setRSVPCount(countRSVP);
                    user.setQuickBloxID(quickBloxId);
                    user.setUserActivityStatus(accept);
                    user.setDeliveredTime(lastReadTime);
                    user.setUserActivityStatus(userActivityStatus);
                    values.add(user);
                }
            }
            cursor.close();
        }
        return values;
    }*/


    public ArrayList<SelectedContactObject> getActivityUsers(String activityId) {
        ArrayList<SelectedContactObject> values = new ArrayList<>();

        /*String query = "SELECT AU.*, IFNULL(CONTACTNAME,DISPLAYNAME) AS DISPLAYNAME, '1' AS TMP FROM " +
                "USERCONTACTS UC INNER JOIN ActivityUsers AU ON AU.HK_UUID =(SELECT CREATEDBY FROM ActivityUsers WHERE activityID='" + activityId + "') AND" +
                " UC.HK_UUID=AU.HK_UUID AND AU.INVITATIONSTATUS='Yes' AND AU.activityID='" + activityId + "'" +
                " LEFT JOIN USER_PHONE_CONTACTS UPC ON UPC.HKUUID=UC.HK_UUID" +
                " UNION" +
                " SELECT AU.*, IFNULL(CONTACTNAME,DISPLAYNAME) AS DISPLAYNAME, '2' AS TMP FROM " +
                "USERCONTACTS UC INNER JOIN ActivityUsers AU ON UC.HK_UUID=AU.HK_UUID AND AU.INVITATIONSTATUS='Yes' AND AU.activityID='" + activityId + "'" +
                " AND AU.CREATEDBY <> AU.HK_UUID" +
                " LEFT JOIN USER_PHONE_CONTACTS UPC ON UPC.HKUUID=UC.HK_UUID" +
                " UNION" +
                " SELECT AU.*,IFNULL(CONTACTNAME,DISPLAYNAME) AS DISPLAYNAME,'3' AS TMP FROM " +
                "USERCONTACTS UC INNER JOIN ActivityUsers AU ON UC.HK_UUID=AU.HK_UUID AND INVITATIONSTATUS='Pending' AND AU.activityID='" + activityId + "'" +
                " LEFT JOIN USER_PHONE_CONTACTS UPC ON UPC.HKUUID=UC.HK_UUID" +
                " UNION" +
                " SELECT AU.*,IFNULL(CONTACTNAME,DISPLAYNAME) AS DISPLAYNAME,'4' AS TMP FROM " +
                "USERCONTACTS UC INNER JOIN ActivityUsers AU ON UC.HK_UUID=AU.HK_UUID AND INVITATIONSTATUS='No' AND AU.activityID='" + activityId + "'" +
                " LEFT JOIN USER_PHONE_CONTACTS UPC ON UPC.HKUUID=UC.HK_UUID" +
                " ORDER BY TMP ASC,DISPLAYNAME ASC";*/

        String query = "SELECT AU.*, IFNULL(CONTACTNAME,DISPLAYNAME) AS DISPLAYNAME, " +
                "CASE WHEN AU.HK_UUID = AU.CreatedBy THEN 1 " +
                "WHEN AU.InvitationStatus = 'Yes' AND AU.HK_UUID != AU.CreatedBy THEN 2 " +
                "WHEN AU.InvitationStatus = 'Pending' AND IFNULL(AU.HK_UUID,'') != ''  THEN 3 " +
                "WHEN AU.InvitationStatus = 'No' AND IFNULL(AU.HK_UUID,'') = '' THEN 4 " +
                "ELSE 5 END AS TMP " +
                "FROM ActivityUsers AU " +
                "LEFT OUTER JOIN UserContacts UC ON UC.HK_UUID = AU.HK_UUID " +
                "LEFT OUTER JOIN USER_PHONE_CONTACTS UPC ON UPC.ContactNo = AU.PhoneNumber " +
                "WHERE AU.activityID = '" + activityId + "' AND AU.InvitationStatus != 'Delete' AND  userActivityStatus != 'inActive'" +
                " ORDER BY TMP ASC, DISPLAYNAME ASC";


        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            if (cursor.getCount() != 0) {
                while (cursor.moveToNext()) {
                    SelectedContactObject user = new SelectedContactObject(Parcel.obtain());
                    String hkuuid = cursor.getString(cursor.getColumnIndex("hK_UUID"));
                    String invitationStatus = cursor.getString(cursor.getColumnIndex("invitationStatus"));
                    String accept = cursor.getString(cursor.getColumnIndex("accept"));
                    String phone = cursor.getString(cursor.getColumnIndex("phoneNumber"));
                    String countRSVP = cursor.getString(cursor.getColumnIndex("countRSVP"));
                    String quickBloxId = cursor.getString(cursor.getColumnIndex("QuickBloxID"));
                    String displayName = getValueOfUser(hkuuid, "DisplayName");
                    String hkId = getValueOfUser(hkuuid, "HK_ID");
                    String lastReadTime = cursor.getString(cursor.getColumnIndex("deliveredTime"));
                    String userActivityStatus = cursor.getString(cursor.getColumnIndex("userActivityStatus"));

                    user.setName(displayName);
                    user.setNumber(phone);
                    user.setHkID(hkId);
                    user.setInvitationStatus(invitationStatus);
                    user.setHkUUID(hkuuid);
                    user.setRSVPCount(countRSVP);
                    user.setQuickBloxID(quickBloxId);
                    user.setUserActivityStatus(accept);
                    user.setDeliveredTime(lastReadTime);
                    user.setUserActivityStatus(userActivityStatus);
                    values.add(user);
                }
            }
            cursor.close();
        }
        return values;
    }


    public ArrayList<SelectedContactObject> getEventAcceptedUsers(String activityId) {
        ArrayList<SelectedContactObject> values = new ArrayList<>();

        String query = "SELECT AU.*, IFNULL(CONTACTNAME,DISPLAYNAME) AS DISPLAYNAME, " +
                "CASE WHEN AU.HK_UUID = AU.CreatedBy THEN 1 " +
                "WHEN AU.InvitationStatus = 'Yes' AND AU.HK_UUID != AU.CreatedBy THEN 2 " +
                "WHEN AU.InvitationStatus != 'Pending' AND IFNULL(AU.HK_UUID,'') != ''  THEN 3 " +
                "WHEN AU.InvitationStatus != 'No' AND IFNULL(AU.HK_UUID,'') = '' THEN 4 " +
                "ELSE 5 END AS TMP " +
                "FROM ActivityUsers AU " +
                "LEFT OUTER JOIN UserContacts UC ON UC.HK_UUID = AU.HK_UUID " +
                "LEFT OUTER JOIN USER_PHONE_CONTACTS UPC ON UPC.ContactNo = AU.PhoneNumber " +
                "WHERE AU.activityID = '" + activityId + "' AND AU.InvitationStatus = 'Yes' AND  userActivityStatus != 'inActive'" +
                " ORDER BY TMP ASC, DISPLAYNAME ASC";


        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            if (cursor.getCount() != 0) {
                while (cursor.moveToNext()) {
                    SelectedContactObject user = new SelectedContactObject(Parcel.obtain());
                    String hkuuid = cursor.getString(cursor.getColumnIndex("hK_UUID"));
                    String invitationStatus = cursor.getString(cursor.getColumnIndex("invitationStatus"));
                    String accept = cursor.getString(cursor.getColumnIndex("accept"));
                    String phone = cursor.getString(cursor.getColumnIndex("phoneNumber"));
                    String countRSVP = cursor.getString(cursor.getColumnIndex("countRSVP"));
                    String quickBloxId = cursor.getString(cursor.getColumnIndex("QuickBloxID"));
                    String displayName = getValueOfUser(hkuuid, "DisplayName");
                    String hkId = getValueOfUser(hkuuid, "HK_ID");
                    String lastReadTime = cursor.getString(cursor.getColumnIndex("deliveredTime"));
                    String userActivityStatus = cursor.getString(cursor.getColumnIndex("userActivityStatus"));

                    user.setName(displayName);
                    user.setNumber(phone);
                    user.setHkID(hkId);
                    user.setInvitationStatus(invitationStatus);
                    user.setHkUUID(hkuuid);
                    user.setRSVPCount(countRSVP);
                    user.setQuickBloxID(quickBloxId);
                    user.setUserActivityStatus(accept);
                    user.setDeliveredTime(lastReadTime);
                    user.setUserActivityStatus(userActivityStatus);
                    values.add(user);
                }
            }
            cursor.close();
        }
        return values;
    }


    // TODO 19-07-2017 getting HKID for activity with no QUICKBLOX GROUPID based on activity IDS.

    public ArrayList<String> getActivityUsersHK_ID(String activityId) {
        ArrayList<String> values = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_ACTIVITY_USERS + " WHERE activityID='" + activityId + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            if (cursor.getCount() != 0) {
                while (cursor.moveToNext()) {
                    String hkuuid = cursor.getString(cursor.getColumnIndex("hK_UUID"));
                    String hkId = getValueOfUser(hkuuid, "HK_ID");
                    values.add(hkId);
                }
            }
            cursor.close();
        }
        return values;
    }

    public ArrayList<ActivityDetailsDao> getTaskForCalendar(String hk_UUID, String selectedDate) {

        ArrayList<ActivityDetailsDao> detailsArray = new ArrayList<ActivityDetailsDao>();

        String query = "SELECT HA.ActivityID,HA.activityTitle, HA.activityOwner, HA.blockCalendar, HA.activityStatus, HA.allowOtherToModify AS CanEdit, " +
                " MIN(AD.StartTime) AS StartTime, MAX(AD.EndTime) AS EndTime,CASE WHEN HA.ActivityOwner = (SELECT HK_UUID FROM Users) THEN 'You' ELSE IFNULL(DC.DisplayName,IFNULL(UC.DisplayName,UC.PhoneNumber)) END AS DisplayName , " +
                " HA.QuickbloxGroupID , HA.QuickbloxRoomJID ,  AD.StartDate AS StartDate, IFNULL(ST.SnoozeDate,AD.EndDate) AS EndDate ," +
                " HA.unreadCount , " +
                " HA.address,HA.reminder, " +
                " CASE WHEN HA.activityOwner = '" + hk_UUID + "' THEN 'Owner' " +
                " WHEN IFNULL(AU.InvitationStatus,'') = 'Pending' THEN 'Pending' " +
                " WHEN IFNULL(AU.InvitationStatus,'') = 'Yes' THEN 'Yes' END Status, " +
                " HA.userActivityStatus,  " +
                " CASE WHEN HA.ActivityStatus = 'Completed' THEN HA.ActivityStatus ELSE " +
                " CASE WHEN strftime('%Y-%m-%d %H:%M', IFNULL(ST.SnoozeDate, AD.EndDate)) <= strftime('%Y-%m-%d %H:%M', datetime('now', 'localtime')) " +
                " THEN 'OD' ELSE 'FE' END END AS EventType, HA.invitesToActivity " +
                " FROM HKActivities HA " +
                " LEFT OUTER JOIN UserContacts UC ON HA.ActivityOwner = UC.HK_UUID " +
                " LEFT OUTER JOIN ActivityUsers AU ON AU.ActivityID = HA.ActivityID AND AU.HK_UUID = '" + hk_UUID + "'" +
                " LEFT OUTER JOIN DeviceContacts DC ON UC.PhoneNumber = DC.PhoneNumber " +
                " LEFT OUTER JOIN ActivityDates AD ON HA.ActivityID =  AD.ActivityID " +
                " LEFT OUTER JOIN SnoozeTasks ST ON HA.ActivityID = ST.ActivityTaskID" +
//                " WHERE HA.Active = 1 AND IFNULL(HA.ActionType,'') != 'DELETE' AND blockCalendar = '0' " +
//                " AND HA.invitationStatus IN ('Pending', 'Yes')" +
                " WHERE HA.ActionType != 'DELETE' AND blockCalendar = '0' " +
                " AND strftime('%Y-%m-%d' ,AD.StartDate) = '" + selectedDate + "'" +
                " AND HA.invitationStatus != 'Delete'" +
                " GROUP BY HA.ActivityID  ORDER BY AD.StartDate ASC";
                /*" AND HA.ActivityOwner = CASE WHEN HA.ActivityStatus = 'inActive' THEN '" + hk_UUID + "' ELSE HA.ActivityOwner END " +
                " GROUP BY HA.ActivityID  ORDER BY AD.StartDate ASC";*/

        Cursor cursor = db.rawQuery(query.toString(), null);
        if (cursor != null) {
            if (cursor.getCount() != 0) {
                for (int i = 0; i <= cursor.getCount(); i++) {
                    if (!cursor.isLast()) {
                        cursor.moveToNext();

                        ActivityDetailsDao activityDetailsDao = new ActivityDetailsDao();
                        activityDetailsDao.setActivityID(cursor.getString(0));
                        activityDetailsDao.setActivityTitle(cursor.getString(1));
                        activityDetailsDao.setActivityOwner(cursor.getString(2));
                        activityDetailsDao.setBlockCalendar(cursor.getString(3));
                        activityDetailsDao.setActivityStatus(cursor.getString(4));
                        activityDetailsDao.setCanEdit(cursor.getString(5));
                        activityDetailsDao.setStartTime(cursor.getString(6));
                        activityDetailsDao.setEndTime(cursor.getString(7));
                        activityDetailsDao.setDisplayName(cursor.getString(8));
                        activityDetailsDao.setQuickbloxGroupID(cursor.getString(9));
                        activityDetailsDao.setQuickbloxRoomJID(cursor.getString(10));
                        activityDetailsDao.setStartDate(cursor.getString(11));
                        activityDetailsDao.setEndDate(cursor.getString(12));
                        activityDetailsDao.setUnreadCount(cursor.getInt(13));
                        activityDetailsDao.setAddress(cursor.getString(14));
                        activityDetailsDao.setReminder(cursor.getString(15));
                        activityDetailsDao.setStatus(cursor.getString(16));
                        activityDetailsDao.setUserActivityStatus(cursor.getString(17));
                        activityDetailsDao.setEventType(cursor.getString(18));
                        activityDetailsDao.setInvitiesToActivity(cursor.getInt(19));
                        detailsArray.add(activityDetailsDao);
                    }
                }
            }

        }
        cursor.close();
        return detailsArray;
    }

    public ArrayList<String> getActivityDetailsFromNotification(String activityId, String activityOwner, String hk_UUID) {

        ArrayList<String> result = new ArrayList<>();
        String query = "SELECT HA.ActivityID,HA.activityTitle, HA.activityOwner, HA.blockCalendar, HA.activityStatus, HA.reminder, HA.address, HA.allowOtherToModify AS CanEdit, " +
                "MIN(AD.StartTime) AS StartTime, MAX(AD.EndTime) AS EndTime  , CASE WHEN HA.ActivityOwner = (SELECT HK_UUID FROM Users) THEN 'You' ELSE IFNULL(DC.DisplayName,IFNULL(UC.DisplayName,UC.PhoneNumber)) END AS DisplayName , " +
                "HA.QuickbloxGroupID , HA.QuickbloxRoomJID ,HA.invitationStatus,HA.ActivityNotes,HA.countRSVP,HA.activityRSVP,   AD.StartDate AS StartDate,  AD.EndDate AS EndDate, " +
                "HA.unreadCount , " +
                "CASE WHEN HA.activityOwner = '" + activityOwner + "' THEN 'Owner' " +
                "WHEN IFNULL(AU.InvitationStatus,'') = 'Pending' THEN 'Pending' " +
                "WHEN IFNULL(AU.InvitationStatus,'') = 'Yes' THEN 'Yes' END Status, " +
                "CASE WHEN  strftime('%Y-%m-%d %H:%M',AD.EndDate) <= strftime('%Y-%m-%d %H:%M',datetime('now','localtime'))  THEN 'OD' ELSE 'FE' END AS EventType " +
                "FROM HKActivities HA " +
                "LEFT OUTER JOIN UserContacts UC ON HA.ActivityOwner = UC.HK_UUID " +
                "LEFT OUTER JOIN ActivityUsers AU ON AU.ActivityID = HA.ActivityID AND AU.HK_UUID = '" + hk_UUID + "'" +
                "LEFT OUTER JOIN DeviceContacts DC ON UC.PhoneNumber = DC.PhoneNumber " +
                "LEFT OUTER JOIN ActivityDates AD ON HA.ActivityID =  AD.ActivityID " +
                "WHERE HA.Active = 1 AND IFNULL(HA.ActionType, '') != 'DELETE' " +
                "AND  HA.ActivityID = '" + activityId + "' GROUP BY HA.ActivityID";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.getCount() != 0) {
            if (!cursor.isLast()) {
                cursor.moveToNext();
                result.add(cursor.getString(0));
                result.add(cursor.getString(1));
                result.add(cursor.getString(2));
                result.add(cursor.getString(3));
                result.add(cursor.getString(4));
                result.add(cursor.getString(5));
                result.add(cursor.getString(6));
                result.add(cursor.getString(7));
                result.add(cursor.getString(8));
                result.add(cursor.getString(9));
                result.add(cursor.getString(10));
                result.add(cursor.getString(11));
                result.add(cursor.getString(12));
                result.add(cursor.getString(13));
                result.add(cursor.getString(14));
                result.add(cursor.getString(15));
                result.add(cursor.getString(16));
                result.add(cursor.getString(17));
                result.add(cursor.getString(18));
                result.add(cursor.getString(19));
                result.add(cursor.getString(20));
                result.add(cursor.getString(21));
            }
        }
        cursor.close();
        return result;
    }

    public ArrayList<ActivityDetails> getShortEventDataList(String completedTask) {
        ArrayList<ActivityDetails> ad = new ArrayList<>();
        String query = "";
        if (completedTask.equalsIgnoreCase("Completed Events")) {
            query = "Select ActivityDateID, activityID, startDate, endDate from " + TABLE_ACTIVITY_DATES + " where startDate < strftime('%Y-%m-%d',datetime('now','localtime')) ORDER BY startDate ASC";
        } else {
            query = "Select ActivityDateID, activityID, startDate, endDate from " + TABLE_ACTIVITY_DATES + " where startDate >= strftime('%Y-%m-%d',datetime('now','localtime')) ORDER BY startDate ASC";
        }
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        int totalDates = cursor.getCount();
        for (int i = 0; i < totalDates; i++) {
            String query1 = "select activityTitle,activityOwnerName,address,activityOwner, invitationStatus,QuickbloxGroupID,QuickbloxRoomJID,unreadCount,invitesToActivity from " + TABLE_HKACTIVITIES + " where activityID='" + cursor.getString(1) + "' and blockCalendar='1' and activityStatus='Active' and invitationStatus='Yes'";
            Cursor cursor1 = db.rawQuery(query1, null);
            cursor1.moveToFirst();
            if (cursor1.getCount() > 0) {

                ActivityDetails activity = new ActivityDetails();
                activity.setActivityDateID(cursor.getString(0));
                activity.setActivityID(cursor.getString(1));
                activity.setStartDate(cursor.getString(2));
                activity.setStartTime(UtilityHelper.getTime(cursor.getString(2)));
                activity.setEndDate(cursor.getString(3));
                activity.setEndTime(UtilityHelper.getTime(cursor.getString(3)));
                activity.setActivityTittle(cursor1.getString(0));
                if (cursor1.getString(1) != null)
                    activity.setDisplayName(cursor1.getString(1).trim().equals("") ? displayNameByHKUUID(db, cursor1.getString(3)) : cursor1.getString(1));
                activity.setAddress(cursor1.getString(2));

                activity.setActivityOwner(cursor1.getString(3));
                activity.setInvitationStatus(cursor1.getString(4));
                activity.setQuickbloxGroupID(cursor1.getString(5));
                activity.setQuickbloxRoomJID(cursor1.getString(6));
                activity.setUnReadCount(cursor1.getInt(7));
                activity.setInviteesToActivity(cursor1.getInt(8));
                if (completedTask.equalsIgnoreCase("Completed Events")) {
                    activity.setActivityStatus("Completed");
                }
                log.v("Prakash", ": databaseHelper  >>>>> " + cursor1.getString(0));
                log.v("Prakash", ":  databaseHelper invitees count >>>>> " + cursor1.getInt(8));
                cursor1.moveToNext();
                ad.add(activity);
            }
            cursor1.close();
            cursor.moveToNext();
        }
        cursor.close();
        return ad;
    }

    public int getEventListCount(String completedTask) {
        int count = 0;
        String query = "";
        if (completedTask.equalsIgnoreCase("Completed Events")) {
            query = "Select ActivityDateID, activityID, startDate, endDate from " + TABLE_ACTIVITY_DATES + " where startDate < strftime('%Y-%m-%d',datetime('now','localtime')) ORDER BY startDate ASC";
        } else {
            query = "Select ActivityDateID, activityID, startDate, endDate from " + TABLE_ACTIVITY_DATES + " where startDate >= strftime('%Y-%m-%d',datetime('now','localtime')) ORDER BY startDate ASC";
        }
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        int totalDates = cursor.getCount();
        for (int i = 0; i < totalDates; i++) {
            String query1 = "select activityTitle,activityOwnerName,address,activityOwner, invitationStatus,QuickbloxGroupID,QuickbloxRoomJID,unreadCount,invitesToActivity from " + TABLE_HKACTIVITIES + " where activityID='" + cursor.getString(1) + "' and blockCalendar='1' and activityStatus='Active'";
            Cursor cursor1 = db.rawQuery(query1, null);
            cursor1.moveToFirst();
            if (cursor1.getCount() > 0) {
                count = count + cursor1.getCount();
            }
            cursor1.close();
            cursor.moveToNext();
        }
        cursor.close();
        return count;
    }

    public void createInvitation(Bundle bundel) {
        ContentValues contentValues = new ContentValues();

        contentValues.put("notificationId", bundel.getString("notificationId") == null ? "" : bundel.getString("notificationId"));
        contentValues.put("notificaitonText", bundel.getString("notificaitonText") == null ? "" : bundel.getString("notificaitonText"));
        contentValues.put("SendFrom", bundel.getString("createdBy") == null ? "" : bundel.getString("createdBy"));
        contentValues.put("actionType", bundel.getString("actionType") == null ? "" : bundel.getString("actionType"));
        contentValues.put("dateCreated", bundel.getString("dateCreated") == null ? "" : bundel.getString("dateCreated"));
        contentValues.put("objectId", bundel.getString("objectId") == null ? "" : bundel.getString("objectId"));
        contentValues.put("objectType", bundel.getString("objectType") == null ? "" : bundel.getString("objectType"));
        contentValues.put("pendingInvCount", bundel.getString("pendingInvCount") == null ? "" : bundel.getString("pendingInvCount"));
        contentValues.put("lastUpdated", bundel.getString("lastUpdated") == null ? "" : bundel.getString("lastUpdated"));
        contentValues.put("isPending", ""/*bundel.getString("pendingInvCount")*/);
        db.insertWithOnConflict(TABLE_NOTIFICATION, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void createActivityUsers(Bundle bundle) {

        if (bundle.getParcelableArrayList("hkUsersList") != null) {
            ArrayList<HKUsers> HKUsersList = bundle.getParcelableArrayList("hkUsersList");
            Log.i(TAG, "activityDatesList size = " + HKUsersList.size());
            for (int i = 0; i < HKUsersList.size(); i++) {

                HKUsers hkUser = HKUsersList.get(i);


                ContentValues contentValues = new ContentValues();

                contentValues.put("activityID", bundle.getString("activityID") == null ? "" : bundle.getString("activityID"));
                if (hkUser.gethK_UUID() == null || hkUser.gethK_UUID().equalsIgnoreCase("")) {
                    contentValues.put("hK_UUID", " ");
                } else {
                    contentValues.put("hK_UUID", hkUser.gethK_UUID());
                }
                contentValues.put("phoneNumber", hkUser.getPhoneNumber());
                contentValues.put("countRSVP", hkUser.getCountRSVP());
                contentValues.put("invitationStatus", hkUser.getInvitationStatus());
                contentValues.put("userActivityStatus", hkUser.getUserActivityStatus());
                contentValues.put("QuickBloxID", hkUser.getQuickBloxID());
                contentValues.put("ActionType", bundle.getString("actionType") == null ? "" : bundle.getString("actionType"));
                contentValues.put("dateModified", bundle.getString("dateModified") == null ? "" : bundle.getString("dateModified"));
                contentValues.put("createdBy", bundle.getString("createdBy") == null ? "" : bundle.getString("createdBy"));
                contentValues.put("modifiedBy", bundle.getString("modifiedBy") == null ? "" : bundle.getString("modifiedBy"));
                contentValues.put("deliveredTime", hkUser.getDeliveredTime() == null ? "" : hkUser.getDeliveredTime());

                // As QBUSer is deleted on CONFLICT_REPLACE
                String query = "Select QBUserID,deviceUserName from " + TABLE_USER_CONTACTS + " where phoneNumber='" + hkUser.getPhoneNumber() + "'";
                Cursor cursor = db.rawQuery(query, null);
                if (cursor != null && cursor.moveToFirst()) {
                    contentValues.put("QBUserID", cursor.getString(0));
                    contentValues.put("deviceUserName", cursor.getString(1));
                }
                cursor.close();

                db.insertWithOnConflict(TABLE_ACTIVITY_USERS, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
            }
        } else {

            ArrayList<String> phoneNumbersList = bundle.getStringArrayList("phoneNumber");
            ArrayList<String> phoneHKUUIDList = bundle.getStringArrayList("phoneHKUUID");

            if (bundle.getStringArrayList("phoneHKUUID") != null) {

                for (int i = 0; i < phoneNumbersList.size(); i++) {
                    String QuickBloxID = "";
                    ContentValues contentValues = new ContentValues();
                    try {
                        if (phoneHKUUIDList.get(i) != null && !phoneHKUUIDList.get(i).equalsIgnoreCase("")) {
                            String query = "Select QuickBloxID,deviceUserName from " + TABLE_USER_CONTACTS + " where HK_UUID='" + phoneHKUUIDList.get(i) + "'";
                            Cursor cursor = db.rawQuery(query, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                QuickBloxID = cursor.getString(0);
                                contentValues.put("deviceUserName", cursor.getString(1));
                                contentValues.put("hK_UUID", phoneHKUUIDList.get(i));
                            }
                            cursor.close();
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }

                    contentValues.put("ActivityUserID", bundle.getString("activityUserID") == null ? "" : bundle.getString("activityUserID"));
                    contentValues.put("activityID", bundle.getString("activityID") == null ? "" : bundle.getString("activityID"));
                    contentValues.put("phoneNumber", phoneNumbersList.get(i) == null ? "" : phoneNumbersList.get(i));
                    contentValues.put("invitationStatus", "Pending");
                    contentValues.put("countRSVP", " ");
                    contentValues.put("userActivityStatus", bundle.getString("userActivityStatus") == null ? "" : bundle.getString("userActivityStatus"));
                    contentValues.put("QuickBloxID", QuickBloxID == null ? "" : QuickBloxID);
                    contentValues.put("ActionType", bundle.getString("actionType") == null ? "" : bundle.getString("actionType"));
                    contentValues.put("dateModified", bundle.getString("dateModified") == null ? "" : bundle.getString("dateModified"));
                    contentValues.put("createdBy", bundle.getString("createdBy") == null ? "" : bundle.getString("createdBy"));
                    contentValues.put("modifiedBy", bundle.getString("modifiedBy") == null ? "" : bundle.getString("modifiedBy"));

                    db.insertWithOnConflict(TABLE_ACTIVITY_USERS, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                }

            }

        }
    }

    public void createActivityOwner(Bundle bundle) {

        if (bundle.getParcelable("activityOwnerDetails") != null) {
            ActivityOwnerDetails ownerDetails = bundle.getParcelable("activityOwnerDetails");
            Log.i(TAG, "activityOwnerDetails = " + ownerDetails.toString());

            ContentValues contentValues = new ContentValues();
            contentValues.put("activityID", bundle.getString("activityID") == null ? "" : bundle.getString("activityID"));
            if (ownerDetails.gethK_UUID() == null || ownerDetails.gethK_UUID().equalsIgnoreCase("")) {
                contentValues.put("hK_UUID", " ");
            } else {
                contentValues.put("hK_UUID", ownerDetails.gethK_UUID());
            }
            contentValues.put("phoneNumber", ownerDetails.getPhone());
            contentValues.put("countRSVP", " ");
            contentValues.put("invitationStatus", "Yes");
            contentValues.put("userActivityStatus", "Active");
            contentValues.put("QuickBloxID", ownerDetails.getQuickBloxID());
            contentValues.put("ActionType", bundle.getString("actionType") == null ? "" : bundle.getString("actionType"));
            contentValues.put("dateModified", bundle.getString("dateModified") == null ? "" : bundle.getString("dateModified"));
            contentValues.put("createdBy", bundle.getString("createdBy") == null ? "" : bundle.getString("createdBy"));
            contentValues.put("modifiedBy", bundle.getString("modifiedBy") == null ? "" : bundle.getString("modifiedBy"));
            db.insertWithOnConflict(TABLE_ACTIVITY_USERS, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    public void updateActivity(Bundle bundle) {

        String dateCreated = bundle.getString("dateCreated");
        Log.i(TAG, "dateCreated VALUE = " + dateCreated.toString());
        //SimpleDateFormat simpleDateFormatCreated = new SimpleDateFormat("yyyy-MM-dd hh:mm:sss aaa");
        SimpleDateFormat simpleDateFormatCreated = new SimpleDateFormat("yyyy-MM-dd hh:mm:sss", Locale.getDefault());
        if (dateCreated != null) {
            try {
                Date newDate = simpleDateFormatCreated.parse(dateCreated);

                simpleDateFormatCreated = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String date = simpleDateFormatCreated.format(newDate);
                dateCreated = date;

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        String dateModified = bundle.getString("dateCreated");
        Log.i(TAG, "dateModified VALUE = " + dateModified.toString());
        //SimpleDateFormat simpleDateFormatModified = new SimpleDateFormat("yyyy-MM-dd hh:mm aaa");
        SimpleDateFormat simpleDateFormatModified = new SimpleDateFormat("yyyy-MM-dd hh:mm:sss", Locale.getDefault());
        if (dateModified != null) {
            try {
                Date newDate = simpleDateFormatModified.parse(dateModified);

                simpleDateFormatModified = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String date = simpleDateFormatModified.format(newDate);
                dateModified = date;

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        ContentValues contentValues = new ContentValues();

        contentValues.put("activityID", bundle.getString("activityID") == null ? "" : bundle.getString("activityID"));
        contentValues.put("activityTitle", bundle.getString("activityTitle") == null ? "" : bundle.getString("activityTitle"));
        contentValues.put("active", bundle.getString("active") == null ? "" : bundle.getString("active"));
        contentValues.put("activityOwner", bundle.getString("activityOwner") == null ? "" : bundle.getString("activityOwner"));

        contentValues.put("blockCalendar", bundle.getString("blockCalendar") == null ? "" : bundle.getString("blockCalendar"));
        contentValues.put("allowOtherToModify", bundle.getString("allowOtherToModify") == null ? "" : bundle.getString("allowOtherToModify"));
        contentValues.put("activityStatus", bundle.getString("activityStatus") == null ? "" : bundle.getString("activityStatus"));
        contentValues.put("dateCreated", dateCreated == null ? "" : dateCreated);
        contentValues.put("dateModified", dateModified == null ? "" : dateModified);
        contentValues.put("createdBy", bundle.getString("createdBy") == null ? "" : bundle.getString("createdBy"));
        contentValues.put("modifiedBy", bundle.getString("modifiedBy") == null ? "" : bundle.getString("modifiedBy"));

        contentValues.put("QuickbloxGroupID", bundle.getString("QuickbloxGroupID") == null ? "0" : bundle.getString("QuickbloxGroupID"));
        contentValues.put("QuickbloxRoomJID", " ");
        contentValues.put("PendingRoomJID", " ");
        contentValues.put("countRSVP", bundle.getString("countRSVP") == null ? "" : bundle.getString("countRSVP"));
        contentValues.put("reminder", bundle.getString("reminder") == null ? "" : bundle.getString("reminder"));
        contentValues.put("activityNotes", bundle.getString("activityNotes") == null ? "" : bundle.getString("activityNotes"));
        contentValues.put("userActivityStatus", bundle.getString("userActivityStatus") == null ? "" : bundle.getString("userActivityStatus"));
        contentValues.put("address", bundle.getString("address") == null ? "" : bundle.getString("address"));
        contentValues.put("invitationStatus", bundle.getString("invitationStatus") == null ? "" : bundle.getString("invitationStatus"));

        db.insertWithOnConflict(TABLE_HKACTIVITIES, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void updateActivityUsers(Bundle bundle) {

        if (bundle.getParcelableArrayList("hkUsersList") != null) {
            ArrayList<HKUsers> HKUsersList = bundle.getParcelableArrayList("hkUsersList");
            Log.i(TAG, "activityDatesList size = " + HKUsersList.size());


            for (int i = 0; i < HKUsersList.size(); i++) {

                HKUsers hkUser = HKUsersList.get(i);

                ContentValues contentValues = new ContentValues();

                contentValues.put("activityID", bundle.getString("activityID") == null ? "" : bundle.getString("activityID"));
                if (hkUser.gethK_UUID() == null || hkUser.gethK_UUID().equalsIgnoreCase("")) {
                    contentValues.put("hK_UUID", " ");
                } else {
                    contentValues.put("hK_UUID", hkUser.gethK_UUID());
                }
                contentValues.put("phoneNumber", hkUser.getPhoneNumber());
                contentValues.put("invitationStatus", hkUser.getInvitationStatus());
                contentValues.put("userActivityStatus", hkUser.getUserActivityStatus());
                contentValues.put("QuickBloxID", hkUser.getQuickBloxID());
                contentValues.put("countRSVP", hkUser.getCountRSVP());
                contentValues.put("ActionType", bundle.getString("actionType") == null ? "" : bundle.getString("actionType"));
                contentValues.put("dateModified", bundle.getString("dateModified") == null ? "" : bundle.getString("dateModified"));
                contentValues.put("createdBy", bundle.getString("createdBy") == null ? "" : bundle.getString("createdBy"));
                contentValues.put("modifiedBy", bundle.getString("modifiedBy") == null ? "" : bundle.getString("modifiedBy"));

                db.insertWithOnConflict(TABLE_ACTIVITY_USERS, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);

            }
        } else {

            ArrayList<String> phoneNumbersList = bundle.getStringArrayList("phoneNumber");
            ArrayList<String> phoneHKUUIDList = bundle.getStringArrayList("phoneHKUUID");
            if (bundle.getStringArrayList("phoneHKUUID") != null) {
                for (int i = 0; i < phoneNumbersList.size(); i++) {
                    ContentValues contentValues = new ContentValues();
                    try {
                        if (phoneHKUUIDList.get(i) != null) {
                            contentValues.put("hK_UUID", phoneHKUUIDList.get(i));
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }

                    contentValues.put("ActivityUserID", bundle.getString("ActivityUserID") == null ? "" : bundle.getString("ActivityUserID"));
                    contentValues.put("activityID", bundle.getString("activityID") == null ? "" : bundle.getString("activityID"));
                    contentValues.put("phoneNumber", phoneNumbersList.get(i));
                    contentValues.put("invitationStatus", "Pending");
                    contentValues.put("userActivityStatus", "Active");
                    contentValues.put("QuickBloxID", bundle.getString("quickBloxID") == null ? "" : bundle.getString("quickBloxID"));
                    contentValues.put("ActionType", bundle.getString("actionType") == null ? "" : bundle.getString("actionType"));
                    contentValues.put("dateModified", bundle.getString("dateModified") == null ? "" : bundle.getString("dateModified"));
                    contentValues.put("createdBy", bundle.getString("createdBy") == null ? "" : bundle.getString("createdBy"));
                    contentValues.put("modifiedBy", bundle.getString("modifiedBy") == null ? "" : bundle.getString("modifiedBy"));

                    db.insertWithOnConflict(TABLE_ACTIVITY_USERS, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                }

            }

        }
    }

    public void updateActivityDetails(Bundle bundle) {

        ContentValues contentValues = new ContentValues();

        contentValues.put("activityDetailID", bundle.getString("activityDetailID"));
        contentValues.put("activityID", bundle.getString("activityID"));
        contentValues.put("columnName", bundle.getString("columnName"));
        contentValues.put("columnValue", bundle.getString("columnValue"));
        contentValues.put("actionType", bundle.getString("actionType"));
        contentValues.put("dateCreated", bundle.getString("dateCreated"));
        contentValues.put("dateModified", bundle.getString("dateModified"));
        contentValues.put("createdBy", bundle.getString("createdBy"));
        contentValues.put("modifiedBy", bundle.getString("modifiedBy"));

        db.insertWithOnConflict(TABLE_HKACTIVITIES_DETAILS, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void updateActivityDates(Bundle bundle) {

        if (bundle.getParcelableArrayList("activityDatesList") != null) {
            ArrayList<ActivityDates> activityDatesList = bundle.getParcelableArrayList("activityDatesList");
            Log.i(TAG, "activityDatesList size = " + activityDatesList.size());
            for (int i = 0; i < activityDatesList.size(); i++) {

                ActivityDates activityDates = activityDatesList.get(i);


                String startDate = activityDates.getStartDate();
                SimpleDateFormat simpleDateFormatCreated = new SimpleDateFormat("EEE, MMM dd, yyyy | hh:mm aaa", Locale.getDefault());
                if (startDate != null && startDate.length() > 2) {
                    try {
                        Date newDate = simpleDateFormatCreated.parse(startDate);

                        simpleDateFormatCreated = new SimpleDateFormat("yyyy-MM-dd k:mm", Locale.getDefault());
                        String date = simpleDateFormatCreated.format(newDate);
                        startDate = date;

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                String startTime = activityDates.getStartTime();
                SimpleDateFormat simplestartTime = new SimpleDateFormat("EEE, MMM dd, yyyy | hh:mm aaa", Locale.getDefault());
                if (startTime != null && startTime.length() > 2) {
                    try {
                        Date newDate = simplestartTime.parse(startTime);

                        simplestartTime = new SimpleDateFormat("hh:mm aaa", Locale.getDefault());
                        String date = simplestartTime.format(newDate);
                        startTime = date;

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                String endDate = activityDates.getEndDate();
                SimpleDateFormat simpleDateFormatModified = new SimpleDateFormat("EEE, MMM dd, yyyy | hh:mm aaa", Locale.getDefault());
                if (endDate != null && endDate.length() > 2) {
                    try {
                        Date newDate = simpleDateFormatModified.parse(endDate);

                        simpleDateFormatModified = new SimpleDateFormat("yyyy-MM-dd k:mm", Locale.getDefault());
                        String date = simpleDateFormatModified.format(newDate);
                        endDate = date;

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                String endTime = activityDates.getEndTime();
                SimpleDateFormat simpleEndTime = new SimpleDateFormat("EEE, MMM dd, yyyy | hh:mm aaa", Locale.getDefault());
                if (endTime != null && endTime.length() > 2) {
                    try {
                        Date newDate = simpleEndTime.parse(endTime);

                        simpleEndTime = new SimpleDateFormat("hh:mm aaa", Locale.getDefault());
                        String date = simpleEndTime.format(newDate);
                        endTime = date;

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                ContentValues contentValues = new ContentValues();


                contentValues.put("activityDateID", bundle.getString("activityDateID") == null ? "" : bundle.getString("activityDateID"));
                contentValues.put("activityID", bundle.getString("activityID") == null ? "" : bundle.getString("activityID"));
                contentValues.put("startDate", startDate == null ? "" : startDate);
                contentValues.put("startTime", startTime == null ? "" : startTime);
                contentValues.put("endDate", endDate == null ? "" : endDate);
                contentValues.put("endTime", endTime == null ? "" : endTime);
                contentValues.put("actionType", bundle.getString("actionType") == null ? "" : bundle.getString("actionType"));
                contentValues.put("dateModified", bundle.getString("dateModified") == null ? "" : bundle.getString("dateModified"));
                contentValues.put("createdBy", bundle.getString("createdBy") == null ? "" : bundle.getString("createdBy"));
                contentValues.put("modifiedBy", bundle.getString("modifiedBy") == null ? "" : bundle.getString("modifiedBy"));

                db.insertWithOnConflict(TABLE_ACTIVITY_DATES, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);

            }
        }
    }

    public void createPendingInvitation(Bundle bundel) {
        ContentValues contentValues = new ContentValues();

        contentValues.put("Active", bundel.getString("active") == null ? "" : bundel.getString("active"));
        contentValues.put("activityId", bundel.getString("activityId") == null ? "" : bundel.getString("activityId"));
        contentValues.put("activityStatus", bundel.getString("activityStatus") == null ? "" : bundel.getString("activityStatus"));
        contentValues.put("activityOwner", bundel.getString("activityOwner") == null ? "" : bundel.getString("activityOwner"));
        contentValues.put("hK_UUID", bundel.getString("hK_UUID") == null ? "" : bundel.getString("hK_UUID"));
        contentValues.put("OwnerDisplayName", bundel.getString("actOwnerDisplayName") == null ? "" : bundel.getString("actOwnerDisplayName"));
        contentValues.put("userActivityStatus", bundel.getString("userActivtyStatus") == null ? "" : bundel.getString("userActivtyStatus"));
        contentValues.put("activityTitle", bundel.getString("activityTitle") == null ? "" : bundel.getString("activityTitle"));
        contentValues.put("invitationStatus", bundel.getString("invitationStatus") == null ? "" : bundel.getString("invitationStatus"));
        contentValues.put("lastUpdated", bundel.getString("lastUpdated") == null ? "" : bundel.getString("lastUpdated"));
        contentValues.put("DateCreated", bundel.getString("DateCreated") == null ? "" : bundel.getString("DateCreated"));

        db.insertWithOnConflict(TABLE_PENDINGINVITATIONS, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public String getActivityOwnerIDBasedOnActivityID(String activityID) {
        String activityOwner = "";
        String query = "SELECT activityOwner FROM HKActivities WHERE ActivityID = '" + activityID + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() != 0) {
            if (!cursor.isLast()) {
                cursor.moveToNext();
                activityOwner = cursor.getString(0);
            }
        }
        cursor.close();
        return activityOwner;
    }

    public String getActivityDateIDBasedOnActivityID(String activityID) {
        String activityDateID = "";
        String query = "SELECT ActivityDateID FROM ActivityDates WHERE ActivityID = '" + activityID + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() != 0) {
            if (!cursor.isLast()) {
                cursor.moveToNext();
                activityDateID = cursor.getString(0);
                Log.i(TAG, "activityDateID = " + activityDateID);
            }
        }
        cursor.close();
        return activityDateID;
    }

    public RSVPCount getActivityRSVPCount(String activityID, String hK_uuid) {

        RSVPCount countsData = new RSVPCount();

        String query = "Select 'NotResponded' AS ColumnName, COUNT(1) " +
                " from ActivityUsers where activityId =  '" + activityID + "'AND ActionType!='REMOVE' AND LTRIM(RTRIM(invitationStatus)) = 'Pending' AND LTRIM(RTRIM(ActionType)) != 'REMOVE'" +
                " UNION " +
                " Select 'No', count(invitationStatus) from ActivityUsers where activityId = '" + activityID + "' AND  LTRIM(RTRIM(invitationStatus)) = 'No'  " +
                " UNION " +
                " Select 'RSVP', SUM(countRSVP) from ActivityUsers Where activityId =  '" + activityID + "' " +
                " UNION " +
                " Select 'userRSVP', CASE WHEN LTRIM(RTRIM(countRSVP)) = '' THEN '1' ELSE LTRIM(RTRIM(countRSVP)) END AS countRSVP from ActivityUsers Where activityId = '" + activityID + "' AND hK_UUID = '" + hK_uuid + "' " +
                " UNION " +
                "Select 'Yes', count(invitationStatus) from ActivityUsers " +
                " Where activityId = '" + activityID + "' and  invitationStatus = 'Yes' UNION SELECT 'total',COUNT(1) from ActivityUsers where activityId = '" + activityID + "' AND userActivityStatus != 'inActive' AND  ActionType !='REMOVE' AND invitationStatus != 'Delete'";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() > 0) {
            for (int i = 0; i <= cursor.getCount(); i++) {
                if (!cursor.isLast()) {
                    cursor.moveToNext();
                    if (i == 0) {
                        countsData.setNoCount(cursor.getInt(1));
                    }
                    if (i == 1) {
                        countsData.setNotRespondedCount(cursor.getInt(1));
                    }
                    if (i == 2) {
                        countsData.setTotalRSVPCount(cursor.getInt(1));
                    }
                    if (i == 3) {
                        countsData.setYesCount(cursor.getInt(1));
                    }
                    if (i == 4) {
                        countsData.setTotalNoOfInvitees(cursor.getInt(1));
                    }
                    if (i == 5) {
                        countsData.setUserRSVPCount(cursor.getInt(1));
                    }

                }
            }
            Log.i(TAG, "contactsList : " + countsData.getNoCount() + countsData.getNotRespondedCount() + countsData.getRSVPCount() + countsData.getTotalCount() + countsData.getUserRSVPCount() + countsData.getYesCount());
        }
        cursor.close();

        return countsData;
    }

    public void insertSnoozeDateNTime(String activityID, Date newDate) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String reportDate = df.format(newDate);
        Log.i(TAG, "Report Date: " + reportDate);

        ContentValues contentValues = new ContentValues();
        contentValues.put("activityTaskID", activityID == null ? "" : activityID);
        contentValues.put("SnoozeDate", reportDate == null ? "" : reportDate);

        db.insertWithOnConflict(TABLE_SNOOZE_TASKS, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public Boolean deleteNotificationBasedOnNotificationID(String notificationID) {
        String query = "delete from " + TABLE_NOTIFICATION + " where notificationId='" + notificationID + "'";
        db.execSQL(query);
        return true;
    }

    public Boolean deleteDataFromNotificationTable() {
        String query = "DELETE FROM " + TABLE_NOTIFICATION + "";
        db.execSQL(query);
        return true;
    }

    public ActivityDetails getActivityDetailsBasedOnActvityID(String activityId) {
        String query = "SELECT  * FROM HKActivities WHERE activityId= '" + activityId + "'";
        Cursor cursor = db.rawQuery(query, null);
        ActivityDetails activityDetails = new ActivityDetails();
        if (cursor != null && cursor.moveToFirst()) {
            activityDetails.setActivityID(cursor.getString(1));
            activityDetails.setActivityTittle(cursor.getString(2));
            activityDetails.setActivityStatus(cursor.getString(3));
            activityDetails.setActivityOwner(cursor.getString(4));
            activityDetails.setDisplayName(cursor.getString(5));
            activityDetails.setBlockCalendar(cursor.getString(7));
            activityDetails.setStartDate(cursor.getString(10));
            activityDetails.setQuickbloxGroupID(cursor.getString(14));
            activityDetails.setQuickbloxRoomJID(cursor.getString(15));

            Log.i(TAG, "ActivityDetails Group Id:--- " + cursor.getString(1));
            activityDetails.setGroupID(getGroupID(cursor.getString(1)));
            cursor.close();
        }

        return activityDetails;
    }

    String displayNameByHKUUID(SQLiteDatabase db, String hkUUID) {
        String result = "";

        String query = "SELECT DisplayName FROM UserContacts where HK_UUID='" + hkUUID + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            result = cursor.getString(0);
        }
        cursor.close();
        return result;
    }

    public ContactObject getUserFromContactNumber(String contactNumber) {
        ContactObject result = null;
        String query = "SELECT * FROM UserContacts where PhoneNumber='" + contactNumber + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            result = new ContactObject();
            result.setHkUUID(cursor.getString(1));
            result.setNumber(cursor.getString(2));
            result.setName(cursor.getString(3));
            result.setHkID(cursor.getString(4));
            result.setInvitationStatus(cursor.getString(5));
            result.setImage(cursor.getString(6));
            result.setQuickBlockID(cursor.getString(7));
            result.setQBUserID(cursor.getString(8));
            cursor.close();
        }
        return result;
    }

    public ArrayList<String> getOwnerDetails(String HK_UUID) {
        ArrayList<String> result = new ArrayList<>();

        String query = "Select HK_ID, PhotoPath, DisplayName, QuickBloxID,PhoneNumber from  UserContacts WHERE HK_UUID ='" + HK_UUID + "'";
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            result.add(cursor.getString(0));
            result.add(cursor.getString(1));
            result.add(cursor.getString(2));
            result.add(cursor.getString(3));
            result.add(cursor.getString(4));

            cursor.moveToNext();
        }
        return result;
    }

    public String getQBGroupIDBasedOnActivityID(String activityID) {
        String qbGroupID = "";
        String query = "Select QuickbloxGroupID from  HKActivities " + "WHERE activityID = " + "'" + activityID + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            qbGroupID = cursor.getString(0);
            cursor.close();
        }
        return qbGroupID;
    }

    public ArrayList<String> getQBUserToAddForActivity(String activityID) {
        ArrayList<String> values = new ArrayList<>();

        String buildSQL = "SELECT QBUserID FROM " + TABLE_ACTIVITY_USERS + " where (actionType='ADD' OR actionType='REINVITE') and QBUserID is not null and activityID='" + activityID + "'";
        Cursor cursor = db.rawQuery(buildSQL, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                values.add(cursor.getString(0));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return values;
    }

    public ArrayList<String> getQBUserToDelForActivity(String activityID) {
        ArrayList<String> values = new ArrayList<>();
        String buildSQL = "SELECT QBUserID FROM " + TABLE_ACTIVITY_USERS + " where actionType='REMOVE' and QBUserID is not null and activityID='" + activityID + "'";
        Cursor cursor = db.rawQuery(buildSQL, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                values.add(cursor.getString(0));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return values;
    }

    public void updatedMsgOfActivity(String activityID, String updatedMsg) {
        ContentValues cv = new ContentValues();
        cv.put("updatedMsg", updatedMsg);
        db.update(TABLE_HKACTIVITIES, cv, "activityID='" + activityID + "'", null);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setReminder(String activityID, String startTime, String reminder) {

        final int requestCode = Constants.ALARM_REQUEST_CODE;

        Intent intent = new Intent(context, NotificationReceiverActivity.class);
        intent.putExtra("activityID", activityID);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager am = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
        am.cancel(pendingIntent);

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = null;
        try {
            date = formatter.parse(startTime);
            long dateInLong = date.getTime();
            long reminderInLong = date.getTime();
            Log.i(TAG, "date " + dateInLong + " ====== " + new java.util.Date().getTime());

            if (reminder.equals("10 MINUTES"))
                reminderInLong = dateInLong - (10 * 60 * 1000);
            else if (reminder.equals("15 MINUTES"))
                reminderInLong = dateInLong - (15 * 60 * 1000);
            else if (reminder.equals("30 Min"))
                reminderInLong = dateInLong - (30 * 60 * 1000);
            else if (reminder.equals("1 HOUR"))
                reminderInLong = dateInLong - (60 * 60 * 1000);
            else if (reminder.equals("2 HOURS"))
                reminderInLong = dateInLong - (2 * 60 * 60 * 1000);
            else if (reminder.equals("1 DAY"))
                reminderInLong = dateInLong - (24 * 60 * 60 * 1000);
            else if (reminder.equals("2 DAYS"))
                reminderInLong = dateInLong - (2 * 24 * 60 * 60 * 1000);
            else if (reminder.equals("5 DAYS"))
                reminderInLong = dateInLong - (5 * 24 * 60 * 60 * 1000);
            if (reminderInLong != dateInLong) {
                Log.i(TAG, "alarm reminderInLong != dateInLong: ");
                am.set(AlarmManager.RTC_WAKEUP, reminderInLong, pendingIntent);
            } else if (reminderInLong < System.currentTimeMillis()) {
                Log.i(TAG, "alarm reminderInLong > System.currentTimeMillis(): ");
//                am.set(AlarmManager.RTC_WAKEUP, dateInLong, pendingIntent);
            } else {
                Log.i(TAG, "alarm Else ==== == = = : ");
                am.set(AlarmManager.RTC_WAKEUP, dateInLong, pendingIntent);
            }

        } catch (ParseException e) {
            e.printStackTrace();
            Log.i(TAG, "alarm ParseException alarm not set  : " + e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setReminderNew(String activityID, String startTime, String reminder) {

        final int requestCode = Constants.ALARM_REQUEST_CODE;

        Intent intent = new Intent(context, NotificationReceiverActivity.class);
        intent.putExtra("activityID", activityID);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager am = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
        am.cancel(pendingIntent);

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = null;
        try {
            date = formatter.parse(startTime);
            long dateInLong = date.getTime();
            long reminderInLong = date.getTime();
            Log.i(TAG, "date " + dateInLong + " ====== " + new java.util.Date().getTime());

            switch (reminder) {
                case "5 Mins":
                    reminderInLong = dateInLong - (5 * 60 * 1000);
                    break;
                case "10 Mins":
                    reminderInLong = dateInLong - (10 * 60 * 1000);
                    break;
                case "15 Mins":
                    reminderInLong = dateInLong - (15 * 60 * 1000);
                    break;
                case "30 Mins":
                    reminderInLong = dateInLong - (30 * 60 * 1000);
                    break;
                case "1 HOUR":
                    reminderInLong = dateInLong - (60 * 60 * 1000);
                    break;
            }

            if (reminderInLong != dateInLong) {
                Log.i(TAG, "alarm reminderInLong != dateInLong: ");
                am.set(AlarmManager.RTC_WAKEUP, reminderInLong, pendingIntent);
            } else if (reminderInLong < System.currentTimeMillis()) {
                Log.i(TAG, "alarm reminderInLong > System.currentTimeMillis(): ");
//                am.set(AlarmManager.RTC_WAKEUP, dateInLong, pendingIntent);
            } else {
                Log.i(TAG, "alarm Else ==== == = = : ");
                am.set(AlarmManager.RTC_WAKEUP, dateInLong, pendingIntent);
            }

        } catch (ParseException e) {
            e.printStackTrace();
            Log.i(TAG, "alarm ParseException alarm not set  : " + e);
        }
    }

    public ArrayList<String> getAllActivityIDs() {
        ArrayList<String> allActivityIDs = new ArrayList<>();
        String query = "Select activityID from  HKActivities ";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                allActivityIDs.add(cursor.getString(0));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return allActivityIDs;
    }

    public ActivityDetails getActivityDetailsBasedOnQBGroupID(String qbGroupId) {

        String query = "SELECT  * FROM HKActivities WHERE QuickbloxGroupID= '" + qbGroupId + "'";
        Cursor cursor = db.rawQuery(query, null);
        ActivityDetails activityDetails = new ActivityDetails();
        if (cursor != null && cursor.moveToFirst()) {
            activityDetails.setActivityID(cursor.getString(1));
            activityDetails.setActivityOwner(cursor.getString(4));
            activityDetails.setBlockCalendar(cursor.getString(7));
            cursor.close();
        }
        return activityDetails;
    }

    public int getNotificationUnReadCount() {
        int notificationCount = 0;
        String query = "SELECT isRead FROM Notification WHERE isRead = 0";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            notificationCount = cursor.getCount();
        }
        return notificationCount;
    }

    public String getUserNameByHK_UUID(String HK_UUID) {
        String userName = "";
        String query = "SELECT displayName FROM UserContacts WHERE HK_UUID = '" + HK_UUID + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                userName = cursor.getString(0);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return userName;
    }
    // TODO save user to DB with type REMOVE/INVITE to add/delete member from APPLOZIC group.

    public void saveUser(String UniqueID, Integer appLoxicGroupID, String hk_Id, String actionType, String actionPerformed) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("UniqueID", UniqueID);// TODO applozic groupID to add or remove user to/from group.
        contentValues.put("AppLoxicGroupID", appLoxicGroupID);// TODO applozic groupID to add or remove user to/from group.
        contentValues.put("HK_ID", hk_Id);
        contentValues.put("ActionPerformed", actionPerformed);
        contentValues.put("Type", actionType);// TODO applozic id to add or remove user to/from group.
        db.insertWithOnConflict(TABLE_REMOVE_INVITE_APL_GROUP, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void saveUserWithDBasParams(String UniqueID, Integer appLoxicGroupID, String hk_Id, String actionType, String actionPerformed) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("UniqueID", UniqueID);// TODO applozic groupID to add or remove user to/from group.
        contentValues.put("AppLoxicGroupID", appLoxicGroupID);// TODO applozic groupID to add or remove user to/from group.
        contentValues.put("HK_ID", hk_Id);
        contentValues.put("ActionPerformed", actionPerformed);
        contentValues.put("Type", actionType);// TODO actionType add or remove user to/from group.
        db.insertWithOnConflict(TABLE_REMOVE_INVITE_APL_GROUP, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    }

    /*public void updateUser(AppLozicAddOrRemoveUser appLozicAddOrRemoveUser) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("UniqueID", appLozicAddOrRemoveUser.UniqueID);// TODO applozic groupID to add or remove user to/from group.
        contentValues.put("AppLoxicGroupID", appLozicAddOrRemoveUser.AppLoxicGroupID);// TODO applozic groupID to add or remove user to/from group.
        contentValues.put("HK_ID", appLozicAddOrRemoveUser.HK_ID);
        contentValues.put("ActionPerformed", appLozicAddOrRemoveUser.ActionPerformed);
        contentValues.put("Type", appLozicAddOrRemoveUser.Type);// TODO applozic id to add or remove user to/from group.
        db.insertWithOnConflict(TABLE_REMOVE_INVITE_APL_GROUP, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    }*/

    /*public ArrayList<AppLozicAddOrRemoveUser> getAllUsersToAddOrRemoveFromAPPLOZICGrroup() {
        ArrayList<AppLozicAddOrRemoveUser> list = new ArrayList();
        String query = "SELECT * FROM " + TABLE_REMOVE_INVITE_APL_GROUP + " WHERE ActionPerformed='NO'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            if (cursor.getCount() != 0) {
                while (cursor.moveToNext()) {
                    AppLozicAddOrRemoveUser user = new AppLozicAddOrRemoveUser();
                    user.UniqueID = cursor.getString(cursor.getColumnIndex("UniqueID"));
                    user.AppLoxicGroupID = cursor.getString(cursor.getColumnIndex("AppLoxicGroupID"));
                    user.HK_ID = cursor.getString(cursor.getColumnIndex("HK_ID"));
                    user.ActionPerformed = cursor.getString(cursor.getColumnIndex("ActionPerformed"));
                    user.Type = cursor.getString(cursor.getColumnIndex("Type"));
                    list.add(user);
                }
            }
            cursor.close();
        }
        return list;
    }*/

    public void addOrUpdateContact(ServiceContactObject deviceContact) {

        ServiceContactObject contactObject = deviceContact;
        ContentValues values1 = new ContentValues();
        values1.put("contactName", (String) contactObject.contactName);
        values1.put("contactNo", (String) contactObject.contactNo);
        values1.put("image", (String) contactObject.image);
        values1.put("hkID", (String) contactObject.hkID);
        values1.put("hkUUID", (String) contactObject.hkUUID);

        db.insertWithOnConflict(USER_PHONE_CONTACTS, null, values1, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void addUpdateContact(ServiceContactObject deviceContact) {

        ServiceContactObject contactObject = deviceContact;
        ContentValues values1 = new ContentValues();
        values1.put("contactName", (String) contactObject.contactName);
        values1.put("contactNo", (String) contactObject.contactNo);
        values1.put("image", (String) contactObject.image);
        values1.put("hkID", (String) contactObject.hkID);
        values1.put("hkUUID", (String) contactObject.hkUUID);

        db.insertWithOnConflict(USER_PHONE_CONTACTS, null, values1, SQLiteDatabase.CONFLICT_IGNORE);

    }

    public boolean isExists(String Table_Name, String where_condition) {
        try {
            Cursor c = db.rawQuery("Select * from " + Table_Name + " " + where_condition, null);
            if (c.moveToFirst()) {
                return true;
            }
            c.close();
        } catch (Exception e) {

        }
        return false;
    }

    public ServiceContactObject getUser(String phoneNumber) {
        ServiceContactObject result = new ServiceContactObject();
        String query = "SELECT * FROM " + USER_PHONE_CONTACTS + " WHERE contactNo='" + phoneNumber + "'";
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            result.contactName = cursor.getString(1);
            result.contactNo = cursor.getString(2);
            cursor.moveToNext();
        }
        return result;
    }

    public ServiceContactObject getContactName(final String phoneNumber, Context context) {
        ServiceContactObject result = new ServiceContactObject();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                result.contactName = cursor.getString(0);
                if (phoneNumber.contains("+91")) {
                    result.contactNo = "+91" + phoneNumber;
                } else {
                    result.contactNo = phoneNumber;
                }
            }
            cursor.close();
        }
        return result;
    }

    public ArrayList<SelectedContactObject> getNonHKUsers(String activityID) {
        ArrayList<SelectedContactObject> values = new ArrayList<>();
        String query = "SELECT phoneNumber FROM " + TABLE_ACTIVITY_USERS + " WHERE hK_UUID is null AND activityID='" + activityID + "'";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            if (cursor.getCount() != 0) {
                while (cursor.moveToNext()) {
                    SelectedContactObject user = new SelectedContactObject(Parcel.obtain());
                    user.setNumber(cursor.getString(cursor.getColumnIndex("phoneNumber")));
                    values.add(user);
                }
            }
            cursor.close();
        }
        return values;
    }

    public void saveNewHkUsers(List<NonHKContact> hkContacts, String activityID) {

        ActivityDetails activityDetails = getActivityDetailsBasedOnActvityID(activityID);

        for (NonHKContact hkContact : hkContacts) {
            // TODO inserting NonHKContact in to db from list of tasks obtained
            ContentValues values = new ContentValues();
            values.put("HK_UUID", hkContact.hK_UUID);
            values.put("HK_ID", hkContact.hKID);
            values.put("DisplayName", hkContact.displayName);
            values.put("PhotoPath", hkContact.photo);
            values.put("QuickBloxID", hkContact.quickBloxID);
            values.put("PhoneNumber", hkContact.phone);
            values.put("active", hkContact.active);

            db.insertWithOnConflict(TABLE_USER_CONTACTS, null, values, SQLiteDatabase.CONFLICT_REPLACE);

            ContentValues contentValues = new ContentValues();
            contentValues.put("deviceUserName", hkContact.displayName);
            contentValues.put("hK_UUID", hkContact.hK_UUID);
            contentValues.put("ActivityUserID", " ");
            contentValues.put("activityID", activityID);
            contentValues.put("phoneNumber", hkContact.phone);
            contentValues.put("invitationStatus", "Pending");
            contentValues.put("countRSVP", " ");
            contentValues.put("userActivityStatus", "Active");
            contentValues.put("QuickBloxID", " ");
            contentValues.put("ActionType", "ADD");
            contentValues.put("dateModified", activityDetails.getStartDate());
            contentValues.put("createdBy", activityDetails.getActivityOwner());
            contentValues.put("modifiedBy", activityDetails.getActivityOwner());

            db.insertWithOnConflict(TABLE_ACTIVITY_USERS, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);

            ContentValues userPHContactValues = new ContentValues();
            userPHContactValues.put("hkUUID", hkContact.hK_UUID);
            userPHContactValues.put("hkID", hkContact.hKID);
            userPHContactValues.put("contactName", hkContact.displayName);
            userPHContactValues.put("image", hkContact.photo);
            userPHContactValues.put("contactNo", hkContact.phone);

            if (isExists(USER_PHONE_CONTACTS, "where contactNo = '" + hkContact.phone + "'")) {
                db.update(USER_PHONE_CONTACTS, userPHContactValues, "contactNo=?", new String[]{hkContact.phone});
            } else {
            }
        }
    }

    public void updateLongUrlOnActivityUser(String activityID, String phoneNumber, String longUrl) {
        ContentValues cv = new ContentValues();
        cv.put("longUrl", longUrl);
        db.update(TABLE_ACTIVITY_USERS, cv, "activityID='" + activityID + "' AND phoneNumber='" + phoneNumber + "'", null);
    }


    public boolean isOpen() {
        try {
            return db.isOpen();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public DataBaseHelper open() throws SQLException {
        try {
            boolean isExist = dataHelper.checkDataBase();
            if (isExist == false) {
                db = dataHelper.getWritableDatabase();
            }
            db = dataHelper.getWritableDatabase();
        } catch (Exception e) {
        }
        return this;
    }


    public ArrayList<String> getOverDueActivityIDS() {
        ArrayList<String> stringArrayList = new ArrayList<>();
        String query = "Select HA.ActivityID " +
                " FROM HKActivities HA " +
                " INNER JOIN ActivityDates AD ON HA.ActivityID = AD.ActivityID " +
                " WHERE strftime('%Y-%m-%d', AD.EndDate) < DATE('now') ";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() > 0) {
            for (int i = 0; i <= cursor.getCount(); i++) {
                if (!cursor.isLast()) {
                    cursor.moveToNext();
                    stringArrayList.add(cursor.getString(0));
                }
            }
        }
        cursor.close();
        return stringArrayList;
    }

    public ArrayList<String> getOverDueActivityIDSAfter48Hrs() {
        ArrayList<String> stringArrayList = new ArrayList<>();
        String query = "Select DISTINCT HA.ActivityID  FROM HKActivities HA " +
                " INNER JOIN ActivityDates AD ON HA.ActivityID = AD.ActivityID " +
                " LEFT OUTER JOIN ActivityUsers AU ON HA.ActivityID = AU.ActivityID AND AU.HK_UUID = '%@' " +
                " WHERE EndDate <= DateTime('Now', 'LocalTime', '-2 Day') AND " +
                " ( HA.blockCalendar = 1 OR (HA.blockCalendar = 0 AND AU.UserActivityStatus = 'Completed'))";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() > 0) {
            for (int i = 0; i <= cursor.getCount(); i++) {
                if (!cursor.isLast()) {
                    cursor.moveToNext();
                    stringArrayList.add(cursor.getString(0));
                }
            }
        }
        cursor.close();
        return stringArrayList;
    }

    public boolean isDBDeleted() {

        this.db.beginTransaction();
        try {
            this.db.delete(TABLE_ACTIVITY_ATTACHMENTS, null, null);
            this.db.delete(TABLE_ACTIVITY_DATES, null, null);
            this.db.delete(TABLE_ACTIVITY_USERS, null, null);
            this.db.delete(TABLE_ACTIVITY_USERS_TEMP, null, null);
            this.db.delete(TABLE_HKACTIVITIES, null, null);
            this.db.delete(TABLE_HKACTIVITIES_DETAILS, null, null);
            this.db.delete(TABLE_HKACTIVITIES_FOR_CHAT_ICON, null, null);
            this.db.delete(TABLE_NOTIFICATION, null, null);
            this.db.delete(TABLE_PENDINGINVITATIONS, null, null);
            this.db.delete(TABLE_QB_USER, null, null);
            this.db.delete(TABLE_SNOOZE_TASKS, null, null);
            this.db.delete(TABLE_USERS, null, null);
            this.db.delete(TABLE_REMOVE_INVITE_APL_GROUP, null, null);
            this.db.setTransactionSuccessful();
            this.db.endTransaction();
            this.db.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public void updateFromDIB(String actID) {
        String query = "UPDATE HKActivities SET QuickbloxGroupID ='" + " "/*channel.getClientGroupId()*/ + "' WHERE activityID = '" + actID + "'";
        String query1 = "UPDATE HKActivities SET ApplozicGroupCreated ='YES' WHERE activityID = '" + actID + "'";
        db.execSQL(query);
        db.execSQL(query1);
    }
}

